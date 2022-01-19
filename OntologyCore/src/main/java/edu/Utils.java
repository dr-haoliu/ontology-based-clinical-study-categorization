package main.java.edu;



import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {

    public static void main(String[] args) {

//        readDataFromCommaSeperator("C:\\Users\\Hao\\Desktop\\Simona\\Classification_1005.csv");
        readDataFromCommaSeperator("C:\\Users\\Hao\\Desktop\\Simona\\Classification_1009.csv");

        readDataFromCustomSeperator("C:\\Users\\Hao\\Desktop\\Simona\\ten_trials_top_condition.tsv");


    }


    public static List<String[]> readDataFromCommaSeperator(String file)
    {

        List<String[]> allData = new ArrayList<>();
        try {
            // Create an object of file reader class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvParser object with
            // seperator comma
            CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

            // create csvReader object with parameter
            // filereader and parser
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withCSVParser(parser)
                    .withSkipLines(1)
                    .build();

            // Read all data at once
            allData = csvReader.readAll();

            // Print Data.
            for (String[] row : allData) {
                for (String cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allData;
    }


    public static List<String[]> readDataFromCustomSeperator(String file)
    {
        List<String[]> allData = new ArrayList<>();
        try {
            // Create an object of file reader class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvParser object with
            // custom seperator semi-colon
            CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();

            // create csvReader object with parameter
            // filereader and parser
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withCSVParser(parser)
                    .build();

            // Read all data at once
            allData = csvReader.readAll();

            // Print Data.
            for (String[] row : allData) {
                for (String cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allData;
    }

    public static List<String[]> readDataFromCustomSeperator(String file, int skipLineIndex)
    {
        List<String[]> allData = new ArrayList<>();
        try {
            // Create an object of file reader class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvParser object with
            // custom seperator semi-colon
            CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

            // create csvReader object with parameter
            // filereader and parser
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(skipLineIndex)
                    .withCSVParser(parser)
                    .build();

            // Read all data at once
            allData = csvReader.readAll();

            // Print Data.
            for (String[] row : allData) {
                for (String cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allData;
    }


}
