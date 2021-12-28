package org.ohdsi.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMatcher {

    public static void main( String args[] ){
        
        

        // 按指定模式在字符串查找
        String line = "Non-squamous Non-small cell lung cancer";

        List<String> result = matchNegation(line);
        result.forEach(t-> System.out.println("t = " + t));
        
        String pattern = "(Non-\\b\\w+)\\s+";
//        String pattern = "Non-(\\b\\w+)\\s+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        while (m.find()) {
            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
//            System.out.println("Found value: " + m.group(2) );
//            System.out.println("Found value: " + m.group(3) );
        }
    }
    
    public static List<String> matchNegation(String line){
        List<String> result = new ArrayList<>();

        String pattern = "(Non-\\b\\w+)\\s+";
//        String pattern = "Non-(\\b\\w+)\\s+";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }
    
}
