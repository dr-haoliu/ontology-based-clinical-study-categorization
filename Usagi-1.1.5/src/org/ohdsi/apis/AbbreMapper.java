package org.ohdsi.apis;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbbreMapper {
    public static Map<String, String> extract(String input) {
        // assume that pattern is long form (short form)
        // 1. extract the short form and corresponding long form candidates
        Pattern short_form = Pattern.compile("\\((.*?)\\)");
        Pattern long_form = Pattern.compile("(\\)|^)(.*?)\\(");

        Matcher m_short = short_form.matcher(input);
        Matcher m_long = long_form.matcher(input);

        List<String> short_list = new ArrayList<>();
        List<String> initial_long = new ArrayList<>();

        while(m_short.find()) {
            short_list.add(m_short.group(1).trim());
        }

        while(m_long.find()) {
            initial_long.add(m_long.group(2).trim());
        }

        if(short_list.size() != initial_long.size()) {
            return null;
        }

        List<String> long_list = new ArrayList<>();

        //2. shorten the initial_long
        for(int i = 0; i < short_list.size(); i++) {
            int length = short_list.get(i).length();
            int word_num = Math.min(length + 5, length * 2);
            String[] temp = initial_long.get(i).split(" ");
            if(temp.length <= word_num) {
                long_list.add(String.join(" ", temp));
            } else {
                String[] shorten = Arrays.copyOfRange(temp, temp.length - 1 - word_num, temp.length);
                long_list.add(String.join(" ", shorten));
            }
        }

        Map<String, String> result = new LinkedHashMap<>();
        //3. select the best long_form
        for(int i = 0; i < short_list.size(); i++) {
            result.put(short_list.get(i), findBestLongForm(short_list.get(i), long_list.get(i)));
        }
        return result;
    }

    // this part of code is from the paper by A.S. Schwartz & M.A. Hearst:
    // "A Simple Algorithm for Identifying Abbreviation Definitions in Biomedical Text"
    public static String findBestLongForm(String shortForm, String longForm) {
        int sIndex;
        int lIndex;
        char currChar;

        sIndex = shortForm.length() - 1;
        lIndex = longForm.length() - 1;

        for(; sIndex >= 0; sIndex--) {
            currChar = Character.toLowerCase(shortForm.charAt(sIndex));
            if(!Character.isLetterOrDigit(currChar)) {
                continue;
            }
            while(
                    ((lIndex >= 0) && (Character.toLowerCase(longForm.charAt(lIndex)) != currChar))
                            || ((sIndex == 0) && (lIndex > 0) && (Character.isLetterOrDigit(longForm.charAt(lIndex - 1))))
            )
                lIndex--;
            if (lIndex < 0)
                return null;
            lIndex--;
        }

        lIndex = longForm.lastIndexOf(" ", lIndex) + 1;
        return longForm.substring(lIndex);
    }

    public static String cleanAbbrev(String input){
        String output = input;
        Map<String, String> extract = extract(input);
        if (extract == null) return input;
        if (extract.size()>0){
            for (String key: extract.keySet()) {
                System.out.println("cleanAbbrev: key = " + key);
                output = output.replace("("+key+")","");
                System.out.println("cleanAbbrev: output = " + output);
            }
        }
        output = output.replaceAll("\\s+", " ");
        return output;
    }

    // test
    public static void main(String[] args) {
        String input = "hello (H) foo (F)";
        System.out.println(extract(input));

        input = "Human papilloma virus (HPV) infection";
        System.out.println(extract(input));

        input = "Human Papillomavirus (HPV) Infection";
        System.out.println(extract(input));

        System.out.println(cleanAbbrev(input));

        input = "Human Papillomavirus Infection";
        System.out.println(extract(input));

        input = "Chronic Hepatitis C Virus (HCV) Infection";
        System.out.println(extract(input));

        System.out.println(cleanAbbrev(input));
    }

}