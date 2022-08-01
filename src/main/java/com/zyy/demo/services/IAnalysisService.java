package com.zyy.demo.services;

public interface IAnalysisService {
    void analysisAPIFile(String servicenames, String servicespath, String projectpath, String type);

    void analysisSCFile(String servicenames, String servicespath, String projectpath);

    void analysisAPIAndSCFile(String servicenames, String servicespath, String projectpath, String type);
}
