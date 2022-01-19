package main.java.edu;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static void main(String[] args) {

        String output= "D:\\output.csv";
        //Writing data to a csv file
        String line1[] = {"id", "name", "salary", "start_date", "dept"};
        String line2[] = {"1", "Krishna", "2548", "2012-01-01", "IT"};
        String line3[] = {"2", "Vishnu", "4522", "2013-02-26", "Operations"};
        String line4[] = {"3", "Raja", "3021", "2016-10-10", "HR"};
        String line5[] = {"4", "Raghav", "6988", "2012-01-01", "IT"};
        List<String []> alist = new ArrayList<>();
        alist.add(line1);
        alist.add(line2);
        alist.add(line3);
        alist.add(line4);
        alist.add(line5);
        writeToCSV(output, alist);

        readCSV(output);

    }


    public static void writeToCSV(String outputFile, List<String []> content){

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(outputFile));
            //Writing data to a csv file
            writer.writeAll(content);
            //Flushing data from writer to file
            writer.flush();
            System.out.println("Data outputed");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<String[]> readCSV(String inputFile){

        List<String[]> strings = null;

        System.out.println("Start to read Data ");
        try {
            CSVReader reader = new CSVReader(new FileReader(inputFile));
            try {
                strings = reader.readAll();
//                for (String[] str : strings) {
//                    System.out.println("string 1 = " + str[0]);
//                    System.out.println("string 2 = " + str[1]);
//                    System.out.println("string 3 = " + str[2]);
//                    System.out.println("string 4 = " + str[3]);
//                }

            } catch (CsvException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;

    }


}
