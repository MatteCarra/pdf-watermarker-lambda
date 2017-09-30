package com.github.mattecarra;

/**
 * Created by matteo on 09/04/17.
 */
public class WatermarkInput {
    private String s3InputBucket;
    private String s3InputFileKey;
    private String s3OutputBucket;
    private String s3OutputKey;
    private String user;
    private String licenseText;

    public String getS3InputBucket() {
        return s3InputBucket;
    }

    public void setS3InputBucket(String s3InputBucket) {
        this.s3InputBucket = s3InputBucket;
    }

    public String getS3OutputBucket() {
        return s3OutputBucket;
    }

    public void setS3OutputBucket(String s3OutputBucket) {
        this.s3OutputBucket = s3OutputBucket;
    }

    public String getS3InputFileKey() {
        return s3InputFileKey;
    }

    public void setS3InputFileKey(String s3InputFileKey) {
        this.s3InputFileKey = s3InputFileKey;
    }

    public String getS3OutputKey() {
        return s3OutputKey;
    }

    public void setS3OutputKey(String s3OutputKey) {
        this.s3OutputKey = s3OutputKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLicenseText() {
        return licenseText;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }
}
