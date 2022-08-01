package com.zyy.demo.utils;

import com.zyy.demo.entities.FilePair;
import com.zyy.demo.services.impl.AnalysisClassImpl;
import com.zyy.demo.services.impl.AnalysisComtImpl;
import com.zyy.demo.services.impl.DealSCAndAPIFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static void writeToCSV(String projectpath, String type) {
        String fileName = projectpath + File.separator + type + ".csv";
        File file = new File(fileName);
        // 创建文件，如果存在删除重新创建；创建好之后将抽取的数据读入
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            Writer out = new FileWriter(file);
            if (type.equals("sc")) {
                writeSCFile(out);
            } else if (type.equals("api")) {
                writeAPIFile(out);
            } else if (type.equals("cmt")) {
                writeCmtFile(out);
            } else if (type.equals("class")) {
                writeClassFile(out);
            } else if (type.equals("structdep")) {
                writeStructdepFile(out);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeCmtFile(Writer out) {
        for (FilePair filePair : AnalysisComtImpl.cochanges) {
            try {
                // 过滤掉其中不是src/main/java和单元测试的类，防止报错
                if (filePair.getFile1().contains("Test") || filePair.getFile2().contains("Test") || getPackagename(filePair.getFile1()) == " " || getPackagename(filePair.getFile2()) == "") {
                    continue;
                }
                out.write(getPackagename(filePair.getFile1()) + "," + getPackagename(filePair.getFile2()) + "," + filePair.getCount() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getPackagename(String filepath) {
        System.out.println(filepath);
        if (filepath.split("java").length < 2) {
            return "";
        }
        String path = filepath.split("java")[1];
        String packagename = path.replaceAll("/", ".");
//        System.out.println(packagename.substring(1, packagename.length() - 1));
        return packagename.substring(1, packagename.length() - 1);
    }

    private static void writeAPIFile(Writer out) {
        DealSCAndAPIFile.service2api.forEach((servicename, classMessages) -> {
            classMessages.forEach((classname, methods) -> {
                methods.forEach(methodMessage -> {
                    try {
                        out.write(servicename + "," + classname + "." + methodMessage.getName() + "," + methodMessage.getInputType() + "," + methodMessage.getOuputType() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private static void writeSCFile(Writer out) throws IOException {
        int index = 1;
        for (Map.Entry<String, List<String>> entry : DealSCAndAPIFile.service2class.entrySet()) {
            for (String classname : entry.getValue()) {
                if (classname.equals("")) {
                    continue;
                }
                out.write("contain," + entry.getKey() + "," + classname + "," + index + "\n");
                index++;
            }
        }
    }

    private static void writeClassFile(Writer out) throws IOException {
        for (Map.Entry<String, Integer> entry : AnalysisClassImpl.classInfo.entrySet()) {
            out.write(entry.getValue() - 1 + "," + entry.getKey() + "\n");
        }
    }

    private static void writeStructdepFile(Writer out) throws IOException {
        for (Map.Entry<String, Map<String, Integer>> entry : AnalysisClassImpl.struct.entrySet()) {
            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                out.write(entry.getKey() + "," + entry1.getKey() + "," + entry1.getValue() + "\n");
            }
        }
    }
}
