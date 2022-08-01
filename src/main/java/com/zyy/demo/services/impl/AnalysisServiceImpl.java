package com.zyy.demo.services.impl;

import com.zyy.demo.services.IAnalysisService;
import com.zyy.demo.utils.FileUtil;
import org.springframework.stereotype.Service;

@Service
public class AnalysisServiceImpl implements IAnalysisService {

	/**
	 * 输入一个工程的路径，扫描其下面所有的路径，抽取其中的微服务及其下面的类
	 */
	public void analysisSCFile(String servicenames, String servicespath, String projectpath) {
		if (DealSCAndAPIFile.getSCMessage(servicenames, servicespath)) {
			/** 将抽取的结果读取到csv文件中,存在原项目路径下*/
			FileUtil.writeToCSV( projectpath, "sc");
		}
	}

	@Override
	public void analysisAPIAndSCFile(String servicenames, String servicespath, String projectpath, String type) {

	}

	/**
	 * 输入一个路径，遍历其下面每个微服务将其api文件及其接口方法抽出
	 */
	public void analysisAPIFile(String servicenames, String servicespath, String projectpath, String type) {
		if (DealSCAndAPIFile.getAPIMessage(servicenames, servicespath, type)) {
			/** 将抽取的结果读取到csv文件中,存在原项目路径下*/
			FileUtil.writeToCSV(projectpath, "api");
		}
	}
}
