/*
Language: Java
IDE: IntellJ, Visual Studio Code, netbeans
- - - - - - - - - - - - - - - - - - - -
Group: Randy Vo, Eric McMahon,  Joe Mugo
Date: 04/2/2025
Class: CS 4500-001
- - - - - - - - - - - - - - - - - - - -
[Program Description:]
Assignment: Small Group Project 2
Description: This Java program reads a CSV file provided by the user and extracts the following information:
1. ** Extract Species (Names) **:
    - Reads the first line of the CSV file to get the names and number of names (abundance count).
    - Writes the column names to a file named 'Species.txt'.

2. ** Extracts Dates**:
    - Reads the dates from the first column of each subsequent line in the CSV file.
    - Writes the dates to a file named 'DatedData.txt'.

3. **Processes Numeric Data**:
    - Reads the numeric data from the remaining columns of each line.
    - Converts positive numbers to '1' and zero to '0'.
    - Writes the converted data to a file named 'PresentAbsent.txt'.
- - - - - - - - - - - - - - - - - - - -
[Central Data Structures]
    - ArrayList: resizable array
    - HashMap: data structure that stores key-value pairs
- - - - - - - - - - - - - - - - - - - -
[Sources:]
   - BufferedWriter: https://www.programiz.com/java-programming/bufferedwriter, https://www.geeksforgeeks.org/io-bufferedwriter-class-methods-java/
   - BufferedReader: https://www.programiz.com/java-programming/bufferedreader, https://www.geeksforgeeks.org/bufferedreader-class-in-java/
   - FileReader: https://www.geeksforgeeks.org/file-reader-java-with-examples/
   - FileWriter: https://www.geeksforgeeks.org/filewriter-class-in-java-with-examples/
   - IOException: https://www.geeksforgeeks.org/handle-an-ioexception-in-java/
   - FileNotFoundException: https://docs.oracle.com/javase/8/docs/api/java/io/FileNotFoundException.html
   - toLowerCase().endsWith(".csv") : https://stackoverflow.com/questions/26794275/how-do-i-ignore-case-when-using-startswith-and-endswith-in-java
   - split(“,“) function: https://www.geeksforgeeks.org/split-string-java-examples/
   - pathname: https://stackoverflow.com/questions/1693020/how-to-specify-filepath-in-java
- - - - - - - - - - - - - - - - - - - -
[Major Revision Dates]
    - 03/21/2025
    - 03/31/2025
- - - - - - - - - - - - - - - - - - - -
[How to Run:]
Requirements:
    Java 17+
1. Open project in NetBeans
2. Add ".CSV" file to the same root directory as the java program.
3. Run the Java program by pressing the run button/green triangle above.
4. Follow the on-screen instructions to enter the name of the CSV file.
5. The program will process the CSV file and generate the output files:
    - 'Species.txt'
    - 'DatedData.txt'
    - 'PresentAbsent.txt'
- - - - - - - - - - - - - - - - - - - -
*/

import java.io.*;
import java.util.*;

public class SG2 {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello, this program will ask the user for the name of the CSV file.\nThe program will then read the CSV file, line by line, and extract the following:\nPlease press ENTER to continue");
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("Please input a file");
        String fileUser = consoleScanner.nextLine();
        validateInput(fileUser, consoleScanner);
        scanner.close();
    }

    public static void promptUserEnterKey(Scanner s) {
        System.out.println("Press Enter to continue...");
        s.nextLine();
    }

    /**
     * Validates file being provided
     *
     * @param fileUser string that represents file name
     * @param consoleScanner scanner object to real file
     */
    public static void validateInput(String fileUser, Scanner consoleScanner) throws IOException {
        while (true) {
            fileUser = fileUser.trim();
            if (fileUser.length() < 4 || !fileUser.toLowerCase().endsWith(".csv")) {
                System.out.println("Please input a valid CSV file name ending with .CSV");
            } else {
                File file = new File(fileUser);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("File not found. Please input another file:");
                } else {

                    processCSV(file);
                    return;
                }
            }
            // Ask for input again
            fileUser = consoleScanner.nextLine();
        }
    }


    public static boolean processCSV(File file) throws IOException {

        // Used for keeping track of what line the program is reading
        int lineNumber = 1;
        int numSpecies = 0;
        int numDates = 0;
        double max = 0;
        Scanner scanner = new Scanner(System.in);
        // PA Data Structure to hold PresentAbsent Data
        /*
        This data structure is a list of list,
        the outer list holds another list that represents rows in PresentAbsent,
        the inside list holds each item in that row (date & numbers)
         */
        List<List<String>> PA = new ArrayList<>();

        if (!file.exists() || !file.isFile()) {
            System.out.println("Error: File not found or incorrect directory.");
            return false;
        }

        // BufferedWriters for DatedData, PresentAbsent and Species
        BufferedWriter speciesWriter = new BufferedWriter(new FileWriter("Species.txt"));
        BufferedWriter dateWriter = new BufferedWriter(new FileWriter("DatedData.txt"));
        BufferedWriter presentAbsentWriter = new BufferedWriter(new FileWriter("PresentAbsent.txt"));

        //Reads file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();
            char commaCheck = line.charAt(0);
            if (commaCheck != ',' || line == null) {
                System.out.println("The first character in the file must be ','.");
                return false;
            }

            //Only first line is read for species
            FileWriter species = new FileWriter("Species.txt");
            String[] speciesList = line.substring(1).split(",");
            numSpecies = speciesList.length;
            System.out.println(Arrays.toString(speciesList));
            for (String specie : speciesList)
            {
                speciesWriter.write(specie);
                speciesWriter.newLine();
            }
            speciesWriter.close();
            species.close();
            PA.add(List.of(speciesList));

            //Read the rest of the lines until the line is empty or null
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                List<String> lineHolder = new ArrayList<>();
                double highestAbd = 0;
                List<Integer> highestAbdIndex = new ArrayList<>();
                if (line.trim().isEmpty())
                {
                    break;
                }

                //splits line by commas
                String[] dateList = line.split(",");
                if (dateList.length > 0)
                {
                    //writes only date in DatedData
                    if (dateList[0].matches("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/\\d{4}$")) {
                        dateWriter.write(dateList[0]);
                        dateWriter.newLine();
                        lineHolder.add(dateList[0]);
                    } else {
                        System.out.println("The dates must be in MM/DD/YYYY format at line " + lineNumber);
                        return false;
                    }

                    // validates that numbers provided are equal to species column
                    // numbers provided should be equal to species provided minus 1 (date column)
                    if ((dateList.length - 1) != numSpecies) {
                        System.out.println("Row count is invalid at line: " + lineNumber);
                        System.out.println("Line Content: " + line);

                        promptUserEnterKey(scanner);
                        return false;
                    }

                    for (int x = 1; x < dateList.length; x++) {
                        // checks if number provided contains alphabet character
                        if (dateList[x].matches(".*[a-zA-Z].*")) {
                            System.out.println("Content provided contains alphabet character at line: " + lineNumber);
                            System.out.println("Invalid Content: " + dateList[x]);
                            System.out.println("Line Content: " + line);

                            promptUserEnterKey(scanner);
                            return false;
                        }
                        // checks if number provided is real number
                        if (!dateList[x].matches("^[-+]?(?:\\d+(\\.\\d*)?|\\d+/\\d+)$")) {
                            System.out.println("Content provided not a valid number at line: " + lineNumber);
                            System.out.println("Invalid Content: " + dateList[x]);
                            System.out.println("Line Content: " + line);

                            promptUserEnterKey(scanner);
                            return false;
                        }
                        // checks if number provided is a fraction
                        if (dateList[x].contains("/")) {
                            System.out.println("Content provided is a fraction at line: " + lineNumber);
                            System.out.println("Invalid Content: " + dateList[x]);
                            System.out.println("Line Content: " + line);

                            promptUserEnterKey(scanner);
                            return false;
                        }
                        // checks if number provided is negative
                        double dateListValue = Double.parseDouble(dateList[x]);
                        if (dateListValue < 0) {
                            System.out.println("Content provided is negative at line: " + lineNumber);
                            System.out.println("Content: " + dateList[x]);
                            System.out.println("Line Content: " + line);

                            promptUserEnterKey(scanner);
                            return false;
                        }
                        // abundance count logic
                        if (dateListValue == 0) {
                            lineHolder.add("0");
                        }
                        if (dateListValue > 0) {
                            lineHolder.add("1");
                        }
                        // Track highest abundance value and corresponding column indices
                        if (dateListValue > highestAbd) {
                            highestAbd = dateListValue;
                            highestAbdIndex.clear();
                            highestAbdIndex.add(x);
                        } else if (dateListValue == highestAbd) {
                            highestAbdIndex.add(x);
                        }
                    }
                    System.out.print(dateList[0] + " had the highest abundance of " + highestAbd + " in species: ");
                    List<String> highestSpecies = new ArrayList<>();
                    for (int index : highestAbdIndex) {
                        highestSpecies.add(speciesList[index-1]); // Get species name based on column index
                    }
                    System.out.println(String.join(", ", highestSpecies));
                    // if numbers are valid then add to dates found count
                    numDates++;
                    PA.add(lineHolder);
                }
            }
            presentAbsentWriter.write(",");
            for (List<String> row : PA) {
                presentAbsentWriter.write(String.join(",", row));
                presentAbsentWriter.newLine();
            }
            /*
            Output a message to the screen announcing how many different species (names) were found
            in the file, and how many different dates were found. The user should be prompted to push ENTER to
            continue the program
             */
            System.out.println("Number of different species(names) found: " + numSpecies);
            System.out.println("Number of dates found: " + numDates);
            promptUserEnterKey(scanner);
        }
        dateWriter.close();
        presentAbsentWriter.close();

        // Generate Report
        /*
        HashMap uses number state '0' or '1' as key to get unique values
        When key does not exist, it is added to HashMap
        When key exist, the date is added to the list associated to the key
         */
        Map<String, List<String>> reportDate = new HashMap<>();
        int p = 0;
        for (List<String> row : PA) {
            // skip species row
            if (p != 0) {
                // key is numbers provided
                StringBuilder key = new StringBuilder();
                for (int r = 1; r < row.size(); r++) {
                    key.append(row.get(r));
                }
                if (!reportDate.containsKey(key.toString())) {
                    reportDate.put(key.toString(), new ArrayList<>(List.of(row.get(0))));
                } else {
                    List<String> dates = reportDate.get(key.toString());
                    dates.add(row.get(0));
                }
            }
            p++;
        }

        for (String key : reportDate.keySet()) {
            int maxAbSize = reportDate.get(key).size();
            if (maxAbSize > 1) {
                System.out.print(key + " occurs " + maxAbSize + " times: ");
                System.out.println(String.join(", ", reportDate.get(key)));
            }
        }

        System.out.println("Thank you for using the program all of its' tasks have been completed. Please press Enter to end the program.");
        scanner.nextLine();
        scanner.close();
        return true;
    }
}
