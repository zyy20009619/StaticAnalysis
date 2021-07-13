package com.zyy.demo.entities;

public class FileSimilarity {
    private String file1Index = "";
    private String file2Index = "";
    private double similarityScore = 0;

    public FileSimilarity(String file1Index, String file2Index, double similarityScore) {
        this.file1Index = file1Index;
        this.file2Index = file2Index;
        this.similarityScore = similarityScore;
    }

    public String getFile1Index() {
        return file1Index;
    }

    public void setFile1Index(String file1Index) {
        this.file1Index = file1Index;
    }

    public String getFile2Index() {
        return file2Index;
    }

    public void setFile2Index(String file2Index) {
        this.file2Index = file2Index;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }
}
