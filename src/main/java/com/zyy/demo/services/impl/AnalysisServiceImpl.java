package com.zyy.demo.services.impl;

import com.zyy.demo.services.IAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class AnalysisServiceImpl implements IAnalysisService{
	private DealSCAndAPIFile dealSCAndAPIFile = new DealSCAndAPIFile();

	/**
	 * 输入一个工程的路径，扫描其下面所有的路径，抽取其中的微服务及其下面的类
	 */
	public void analysisSCFile(String projectpath) {
		if (dealSCAndAPIFile.readAllPaths(projectpath, "sc")) {
			/** 将抽取的结果读取到csv文件中,存在原项目路径下*/
			dealSCAndAPIFile.writeToCSV( projectpath, "sc");
		}
	}

	/**
	 * 输入一个路径，遍历其下面每个微服务将其api文件及其接口方法抽出
	 */
	public void analysisAPIFile(String projectpath) {
		if (dealSCAndAPIFile.readAllPaths(projectpath, "api")) {
			/** 将抽取的结果读取到csv文件中,存在原项目路径下*/
			dealSCAndAPIFile.writeToCSV(projectpath, "api");
		}
	}
}
