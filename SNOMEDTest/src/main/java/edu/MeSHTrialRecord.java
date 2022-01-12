package edu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeSHTrialRecord {

    String nctid;
    String field;
    String term;
    String domain;
    String conceptID;
    String conceptName;
    String CTgovText;
    String start_date;
    String completion_date;
    String enrollment;
    List<String> bucket;
    List<String> bucketConceptName;

    public MeSHTrialRecord(String nctid) {
        this.nctid = nctid;
    }

    public MeSHTrialRecord(String nctid, String term) {
        this.nctid = nctid;
        this.term = term;
    }

    public MeSHTrialRecord(String nctid, String field, String term, String domain, String conceptID, String conceptName, String CTgovText, String start_date, String completion_date, String enrollment) {
        this.nctid = nctid;
        this.field = field;
        this.term = term;
        this.domain = domain;
        this.conceptID= conceptID;
        this.conceptName = conceptName;
        this.CTgovText = CTgovText;
        this.start_date = start_date;
        this.completion_date = completion_date;
        this.enrollment = enrollment;
        this.bucket = new ArrayList<>();
        this.bucketConceptName = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TrialRecord{" +
                "nctid='" + nctid + '\'' +
                ", field='" + field + '\'' +
                ", term='" + term + '\'' +
                ", domain='" + domain + '\'' +
                ", conceptID='" + conceptID + '\'' +
                ", conceptName='" + conceptName + '\'' +
                ", CTgovText='" + CTgovText + '\'' +
                ", start_date='" + start_date + '\'' +
                ", completion_date='" + completion_date + '\'' +
                ", enrollment='" + enrollment + '\'' +
                ", bucket='" + bucket + '\'' +
                ", bucketConceptName='" + bucketConceptName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeSHTrialRecord that = (MeSHTrialRecord) o;
        return nctid.equals(that.nctid) && Objects.equals(bucket, that.bucket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nctid, bucket);
    }

    public String[] convertToStringList() {
        return new String[]{nctid,
                field,
                term,
                domain,
                conceptName,
                CTgovText,
                start_date,
                completion_date,
                enrollment,
                String.join(" || ", bucket),
                String.join(" || ", bucketConceptName)
        };
    }

    public String[] convertToStringReformat() {
        return new String[]{
                nctid,
                start_date,
                completion_date,
                enrollment,
                String.join(" || ", bucket)
        };
    }

    public List<String> getBucket() {
        return bucket;
    }

    public void setBucket(List<String> bucket) {
        this.bucket = bucket;
    }

    public void addBucket(String bucketName) {
        boolean contains = this.bucket.contains(bucketName);
        if(!contains)
            this.bucket.add(bucketName);
    }

    public List<String> getBucketConceptName() {
        return bucketConceptName;
    }

    public void setBucketConceptName(List<String> bucketConceptNameList) {
        this.bucketConceptName = bucketConceptNameList;
    }

    public void addBucketConceptName(String bucketConceptName) {

        boolean contains = this.bucketConceptName.contains(bucketConceptName);
        if(!contains)
            this.bucketConceptName.add(bucketConceptName);
    }

    public String getNctid() {
        return nctid;
    }

    public void setNctid(String nctid) {
        this.nctid = nctid;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getConceptID() {
        return conceptID;
    }

    public void setConceptID(String conceptID) {
        this.conceptID = conceptID;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getCTgovText() {
        return CTgovText;
    }

    public void setCTgovText(String CTgovText) {
        this.CTgovText = CTgovText;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }


    public static MeSHTrialRecord copyTrialRecord(MeSHTrialRecord record){
        MeSHTrialRecord acopy = new MeSHTrialRecord(record.nctid, record.term);
        acopy.field = record.field;
        acopy.domain = record.domain;
        acopy.conceptID = record.conceptID;
        acopy.conceptName = record.conceptName;
        acopy.CTgovText = record.CTgovText;
        acopy.start_date = record.start_date;
        acopy.completion_date = record.completion_date;
        acopy.enrollment = record.enrollment;
        acopy.bucket = new ArrayList<>();
        acopy.bucketConceptName = new ArrayList<>();
        return acopy;
    }

}

