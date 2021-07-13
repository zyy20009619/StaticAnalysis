package com.zyy.demo.entities;

public class FilePair {
    public String file1;
    public String file2;
    public String count;

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public FilePair(String file1, String file2, String count) {
        this.file1 = file1;
        this.file2 = file2;
        this.count = count;
    }
}
