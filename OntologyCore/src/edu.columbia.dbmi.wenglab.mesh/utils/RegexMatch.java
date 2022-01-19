package edu.columbia.dbmi.wenglab.mesh.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatch {

    private static String REGEX_HEADING = "MH = (.+)$";
    private static String REGEX_NUMBER = "MN = (.+)$";
    private static String REGEX_UNIQUE_ID = "UI = D(.+)$";

    private static String REGEX_ENTRY = "(PRINT )?ENTRY = (.+)$";
    private static String REPLACE = "-";

    public static void main(String[] args) {

//        Pattern p = Pattern.compile(REGEX_HEADING);
        Pattern p = Pattern.compile(REGEX_NUMBER);
//        String input = "MH = Abortion, Habitual";
//        String input = "MN = Abortion, Habitual";
        String input = "UI = D0000001";
        // get a matcher object
        Matcher m = p.matcher(input);

        while(m.find()) {
            System.out.println(m.group(0));
            System.out.println(m.group(1));
        }

        List<String> meshHeading = getMeshHeading(input);
        System.out.println("meshHeading = " + meshHeading);

        List<String> meshNumber = getMeshNumber(input);
        System.out.println("meshHeading = " + meshNumber);

        List<String> meshUniqueID = getMeshUniqueID(input);
        System.out.println("meshUniqueID = " + meshUniqueID);

    }

    public static List<String> getMeshHeading(String input){
        Pattern p = Pattern.compile(REGEX_HEADING);

        // get a matcher object
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<>();
        while(m.find()){
            results.add(m.group(1));
        }
        return results;
    }



    public static List<String> getMeshNumber(String input){
        Pattern p = Pattern.compile(REGEX_NUMBER);

        // get a matcher object
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<>();
        while(m.find()){
            results.add(m.group(1));
        }
        return results;
    }

    public static List<String> getMeshUniqueID(String input){
        Pattern p = Pattern.compile(REGEX_UNIQUE_ID);

        // get a matcher object
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<>();
        while(m.find()){
            results.add(m.group(1));
        }
        return results;
    }

    public static List<String> getMeshEntry(String input){
        Pattern p = Pattern.compile(REGEX_ENTRY);

        // get a matcher object
        Matcher m = p.matcher(input);
        List<String> results = new ArrayList<>();
        while(m.find()){
            results.add(m.group(2));
        }
        return results;
    }


}
