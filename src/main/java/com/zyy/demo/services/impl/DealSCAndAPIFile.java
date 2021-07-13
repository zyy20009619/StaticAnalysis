package com.zyy.demo.services.impl;

import com.zyy.demo.entities.SingelCollect;
import com.zyy.demo.entities.MethodMessage;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class DealSCAndAPIFile {
    /**
     * 存储微服务及其下面所有服务类的集合
     */
    Map<String, List<String>> service2class = new HashMap<>();
    /**
     * 存储微服务与其每个接口类及接口类下面的方法的集合
     */
    Map<String, Map<String, List<MethodMessage>>> service2api = new HashMap<>();

    public boolean readAllPaths(String projectpath, String type) {
        // 取到info.txt的内容（包含微服务名和controller路径信息）:微服务与src/main/java路径的对应
        Map<String, String> allPaths = readInfoPath(projectpath + File.separator + "info.txt", type);

        //遍历info.txt中的所有路径
        if (allPaths.size() != 0) {
            for (Map.Entry<String, String> entry: allPaths.entrySet()) {
                List<String> classnames = new ArrayList<String>();
                Map<String, List<MethodMessage>> methodMap = new HashMap<>();
                File services = new File(entry.getValue());
                traverseFolders(classnames, methodMap, services, entry.getValue(), type);
                if (type.equals("sc")) {
                    service2class.put(entry.getKey(), classnames);
                } else if (type.equals("api")) {
                    service2api.put(entry.getKey(), methodMap);
                } else if (type.equals("senman")) {
                    SingelCollect.getService2class().put(entry.getKey(), classnames);
                }
            }
            return true;
        }
        return false;
    }

    private void traverseFolders(List<String> classnames, Map<String, List<MethodMessage>> methodMap, File srcpath, String basePath, String type) {
        if (srcpath.listFiles() == null) {
            return;
        }
        for (File file : Arrays.asList(srcpath.listFiles())) {
            traverseFolders(classnames, methodMap, file, basePath, type);
            if (file.getName().contains(".java")) {
                String filename = file.getAbsolutePath().replace(basePath, "").replaceAll("\\\\", ".");
                String classname = filename.substring(1, filename.length() - 5);
                if (type.equals("api")) {
                    ClassParser.getMethodMessage(file.getPath());
                    methodMap.put(classname, SingelCollect.getMethods());
                } else if (type.equals("sc") || type.equals("senman")) {
                    classnames.add(classname);
                }
            }
        }
    }

    private Map<String, String> readInfoPath(String infopath, String type) {
        Map<String, String> resultpaths = new HashMap<>();
        File infoFile = new File(infopath);
        if (infoFile.exists()) {
            try {
                getAllPaths(type, resultpaths, infoFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultpaths;
    }

    private void getAllPaths(String type, Map<String, String> resultpaths, File infoFile) throws IOException {
        Pattern pattern = Pattern.compile("^[1-9]d*$");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(infoFile));
        String line = "";
        int servicenum = 0;
        while((line = bufferedReader.readLine()) != null) {
            if (pattern.matcher(line).find()) {  // 如果是数字行，取出微服务数量
                servicenum = Integer.valueOf(line);
            } else if ((type.equals("sc") || type.equals("senman")) && servicenum != 0) {  //如果不是数字行，那么将所有微服务的路径存入paths中
                String[] temps = line.split("\\\\");
                resultpaths.put(temps[temps.length - 1], infoFile.getParent() + File.separator + line + File.separator + "src\\main\\java");
                servicenum--;
            } else if (type.equals("api")){ //如果当前需要提取api文件，那么将api的路径与前面的微服务路径进行拼接
                String[] temps = line.split("\\\\");
                resultpaths.put(temps[0], infoFile.getParent() + File.separator + line);
            }
        }
    }


    public void writeToCSV(String projectpath, String type) {
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
            } else {
                writeAPIFile(out);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAPIFile(Writer out) {
        service2api.forEach((servicename, classMessages) -> {
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

    private void writeSCFile(Writer out) throws IOException {
        for (Map.Entry<String, List<String>> entry : service2class.entrySet()) {
            for (String classname : entry.getValue()) {
                out.write("contain," + entry.getKey() + "," + classname + "\n");
            }
        }
    }
}
