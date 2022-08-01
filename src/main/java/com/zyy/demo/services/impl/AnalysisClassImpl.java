package com.zyy.demo.services.impl;

import com.zyy.demo.entities.SingelCollect;
import com.zyy.demo.services.IAnalysisClassFile;
import com.zyy.demo.utils.FileUtil;
import org.springframework.stereotype.Service;
import util.SingleCollect;
import yyInterface.Impl;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalysisClassImpl implements IAnalysisClassFile {
    public static Map<String, Integer> classInfo = new HashMap();
    public static Map<String, Map<String, Integer>> struct = new HashMap();

    @Override
    public void analysisStructdep(String name, String url) {
        classInfo.clear();
        struct.clear();
        Impl.clear();
        // 读取项目下的类信息和类之间的依赖信息
        Impl.identify(url, name);
        classInfo.putAll(Impl.getClassInfo());
        for (Map.Entry<String, Map<String, Integer>> entry1 : Impl.getStructure().entrySet()) {
            if (!classInfo.containsKey(entry1.getKey())) {
                continue;
            }
            Map<String, Integer> temp = new HashMap<>();
            for (Map.Entry<String, Integer> entry2 : entry1.getValue().entrySet()) {
                if (classInfo.containsKey(entry2.getKey())) {
                    temp.put(entry2.getKey(), entry2.getValue());
                }
            }
            if (temp.size() != 0) {
                struct.put(entry1.getKey(), temp);
            }
        }

//        System.out.println(struct);
        // 将结果写入structuredep文件
//        FileUtil.writeToCSV(url, "class");
        FileUtil.writeToCSV(url, "structdep");
    }
}