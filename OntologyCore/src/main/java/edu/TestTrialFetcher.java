package main.java.edu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestTrialFetcher {


    public static void main( String[] args )
    {

        String spath= "data/2021_04 Vivli NCT_ID studies all.txt";

        String outputFile = "data/output/output_2021_04_Vivli_NCT_ID_studies.csv";

        String allnctids= FileUtil.readFile(spath);
        String[] nctids=allnctids.split("\n");
        List<String> ids=new ArrayList<String>();
        for(String i:nctids){
            if(i.trim().length() >0)
                ids.add(i.trim());
        }

        List<String[]> outputList = new ArrayList<>();
        String[] header={"nctid", "field", "term", "domain", "conceptID", "conceptName", "CTgovText", "start_date", "completion_date", "enrollment"};
        outputList.add(header);

        TestTrialFetcher stf=new TestTrialFetcher();


        for(String id: ids) {
            System.out.println("=====================================");
            System.out.println("Processing nct id = " + id);
            if(id.equalsIgnoreCase("NCT03315559")){
                System.out.println("here break");
            }

            List<String[]> cs =stf.csvfetchCommonFieldsByNCTID(id) ;

            outputList.addAll(cs);


        }

        CSVUtil.writeToCSV(outputFile, outputList);
    }


    public  List<String[]> csvfetchCommonFieldsByNCTID(String nctid) {
        List<String[]> sb=new ArrayList<>();

        try {
            String url="https://clinicaltrials.gov/ct2/show/"+nctid+"?/displayxml=true";
            Document doc = Jsoup.connect(url).get();

            String str_start_date ="";
            String str_completion_date ="";
            String str_enrollment ="";

            Elements start_date = doc.getElementsByTag("start_date");
            if (start_date.size() >=1)
                str_start_date = start_date.get(0).text().trim();

            Elements completion_date = doc.getElementsByTag("completion_date");
            if(completion_date.size() >=1)
                str_completion_date = completion_date.get(0).text().trim();

            Elements enrollment = doc.getElementsByTag("enrollment");
            if(enrollment.size() >=1)
                str_enrollment = enrollment.get(0).text().trim();


            Elements official_title = doc.getElementsByTag("official_title");
            for(Element element: official_title){
                System.out.println("official_title = " + element.text().trim());
                String[] row = {nctid, "official_title", element.text().trim(), "Condition", "", "", element.text().trim(), str_start_date, str_completion_date, str_enrollment};
                sb.add(row);
            }

//            Elements brief_summary = doc.getElementsByTag("brief_summary");
//            for(Element element: brief_summary){
//                System.out.println("brief_summary = " + element.getElementsByTag("textblock").text());
//                List<Paragraph> pas = processTerms(element.getElementsByTag("textblock").text());
//                sb.addAll(formatConditionTerms(nctid,"brief_summary",pas));
//            }
//
//            Elements detailed_description = doc.getElementsByTag("detailed_description");
//            for(Element element: detailed_description){
//                System.out.println("detailed_description = " + element.text().trim());
//                List<Paragraph> pas = processTerms(element.text().trim());
//                sb.addAll(formatConditionTerms(nctid,"detailed_description",pas));
//
//            }

            Elements content = doc.getElementsByTag("condition");
            for(Element element: content){
                String text = element.childNode(0).toString().trim();

//                text = text.replace(",","");
                text = reverseOrderOnComma(text);
                System.out.println("condition = " + text);


                String[] row = {nctid, "condition_field", text, "Condition", "", "", text, str_start_date, str_completion_date, str_enrollment};
                sb.add(row);

            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }



    public String reverseOrderOnComma(String str){
        String result = str;
        List<String> aList = Arrays.asList(str.split(","));
        aList.replaceAll(String::trim);
        if(aList.size()>1){
            System.out.println("Before Comma Reversed: " + aList);
            Collections.reverse(aList);
            System.out.println("After Comma Reversed: ");
            System.out.println(aList);
            result = String.join(" ", aList);
        }else {
            return result;
        }

        return result;
    }

}