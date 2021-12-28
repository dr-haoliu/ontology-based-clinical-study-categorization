package org.ohdsi.apis;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AcronymDict {

    private HashMap<String, String> dict;

    public HashMap<String, String> getDict() {
        return dict;
    }

    public void setDict(HashMap<String, String> dict) {
        this.dict = dict;
    }

    public AcronymDict(HashMap<String, String> dict) {
        this.dict = dict;
    }

    public AcronymDict(String file) {
        HashMap<String, String> dict = new HashMap();
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
            List<String[]> allData = csvReader.readAll();

            // Print Data.
            for (String[] row : allData) {
                if(row.length==3){
//                    System.out.println(row[0]);
                    dict.put(row[0], row[1]);
                    for (String cell : row) {
                        System.out.print(cell + "\t");
                    }
                    System.out.println();
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.dict = dict;

    }


    public void AddtoDict(String key, String value){
        dict.put(key, value);
    }

    @Override
    public String toString() {
        String mapAsString = dict.keySet().stream()
                .map(key -> key + "=" + dict.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }

    public static void main(String[] args) {

        String file = "data/AcronymDict_v1.txt";
        AcronymDict dict = new AcronymDict(file);
        System.out.println("dict.toString() = " + dict.toString());

    }

}

