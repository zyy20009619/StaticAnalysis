package com.zyy.demo.services.impl;

import com.zyy.demo.entities.FilePair;
import com.zyy.demo.services.IAnalysisComtFile;
import com.zyy.demo.utils.FileUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AnalysisComtImpl implements IAnalysisComtFile {
    public static List<FilePair> cochanges = new ArrayList<FilePair>();
    // 阈值，暂设为1，考虑到有的项目比较小
    int Threshold = 1;

    public void analysisComtFile(String basepath) {
        // 对从git上取下来的文件先做预处理
        firstHandle(basepath);
        // 从处理完的文件中提取共变信息
        getCoChangeMessage(basepath);
    }

    private void firstHandle(String basepath) {
        String filepath = basepath + File.separator + "master.txt";
        List<String> lineList = new ArrayList<>();
        Pattern pattern1 = Pattern.compile("(^commit)|(^M)|(^R)|(^A)|(^D)");
        Pattern pattern2 = Pattern.compile("(^commit)");
        Pattern pattern3 = Pattern.compile("(^M)|(^R)|(^A)|(^D)");
        Pattern pattern4 = Pattern.compile("(.java$)");
        boolean firstLineflag = true;  //判断是否是第一行
        int beforeline = 1;  //判断前一行是不是commit
        String beforeLine = "";  //存储前一行
        int i = 0;

        File file = new File(filepath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                // 如果是第一行且是commit行
                if (firstLineflag && pattern2.matcher(line).find()) {
                    beforeLine = line;
                    beforeline = 1;
                    firstLineflag = false;
                } else if (!firstLineflag && pattern1.matcher(line).find()) {
                    // 如果不是第一行,且是提交信息相关行
                    //  如果当前行是commit行，前一行不是commit行,添加前一行
                    if (pattern2.matcher(line).find()) {
                        if (beforeline == 0) {
                            lineList.add(beforeLine);
                            beforeLine = line;
                        } else {
                            // 当前行是commit行，前一行是commit行
                            beforeLine = line;
                        }
                        beforeline = 1;
                        i = i + 1;
                    } else if (pattern4.matcher(line).find() && beforeline == 1) {
                        lineList.add(beforeLine);
                        lineList.add(String.valueOf(i));
                        beforeline = 0;
                        beforeLine = line;
                    } else if (pattern4.matcher(line).find() && beforeline == 0) {
                        lineList.add(beforeLine);
                        beforeline = 0;
                        beforeLine = line;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 如果最后一行是不是commit行，就添加到lineList
        if (pattern3.matcher(beforeLine).find() && pattern4.matcher(beforeLine).find()) {
            lineList.add(beforeLine);
        }
        File outputfile = new File(file.getParentFile() + File.separator + "masterIndex.txt");
        try {
            outputfile.createNewFile();
            FileWriter out = new FileWriter(outputfile);
            for (String s : lineList) {
                out.write(s + "\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getCoChangeMessage(String basepath) {
        cochanges.clear();
        Map<String, List<String>> fileChange = new HashMap<String, List<String>>();
        List<String> fileList = new ArrayList<>();
        String coChangePath = basepath + File.separator + "cmt.csv";
        String masterIndexPath = basepath + File.separator + "masterIndex.txt";

        // 获取一次commit中超过10个文件提交的id（因为很多项目比较小，更新频次也不是很多，所以就暂时不删除超过10的提交id）
        List<String> MoreFileCommit = new ArrayList<String>();
//        getMoreFileList(masterIndexPath, MoreFileCommit);
        // 获取所有添加的文件路径以及其出现的commit的id
        handleHistory(masterIndexPath, fileChange, fileList);
        // 删除cochange中的出现的重复id和超过10个文件的id
        Map<String, List<String>> commit = handelSameCommit(MoreFileCommit, fileChange);
        findCoChanges(commit, fileList);
        // 将结果读入cmt.csv中
        FileUtil.writeToCSV(basepath, "cmt");
    }

    private void getMoreFileList(String masterIndexPath, List<String> MoreFileCommit) {
        Pattern pattern1 = Pattern.compile("(^commit)");
        Pattern pattern2 = Pattern.compile("(^M)|(^R)|(^A)|(^D)");
        String beforecommit = "";
        int fileOneCommit = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(masterIndexPath)));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                // 如果是commit和time行
                if (pattern1.matcher(line).find()) {
                    if (fileOneCommit > 10) {
                        MoreFileCommit.add(beforecommit);
                    }
                    ///////要记着去除字符串前后的\n啊啊啊啊啊
                    String tmp = line.replaceAll("\n", "").split(" ", 2)[1];
                    String id = tmp.split("\\(")[0];
                    beforecommit = id;
                    fileOneCommit = 0;
                }
                // 如果是文件信息行
                if (pattern2.matcher(line).find()) {
                    fileOneCommit = fileOneCommit + 1;
                }
            }
            // 最后一个commit
            if (fileOneCommit > 10) {
                MoreFileCommit.add(beforecommit);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHistory(String masterIndexPath, Map<String, List<String>> fileChange, List<String> fileList) {
        int delFileNum = 0;

        Pattern pattern1 = Pattern.compile("(^commit)");
        Pattern pattern2 = Pattern.compile("(^M)|(^A)");
        Pattern pattern3 = Pattern.compile("(^R)");
        Pattern pattern4 = Pattern.compile("(^0-9)");
        Pattern pattern5 = Pattern.compile("(^D)");

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(masterIndexPath)));
            String line = "";
            String tmp = "";
            String id = "";
            String time = "";
            String index = "";
            while ((line = bufferedReader.readLine()) != null) {
                // 如果是commit和time行
                if (pattern1.matcher(line).find()) {
                    tmp = line.replaceAll("\n", "").split(" ", 2)[1];
                    id = tmp.split("\\(")[0];
                    time = tmp.split("\\(")[1];
                    time = time.replace(")", "");
                }
                //如果是index行
                if (pattern4.matcher(line).find()) {
                    index = line.replaceAll("\n", "");
                }
                // 如果是文件信息行M或 A
                if (pattern2.matcher(line).find()) {
                    String fileName = line.replaceAll("\n", "").split("\t")[1];
                    if (fileChange.containsKey(fileName)) {
                        fileChange.get(fileName).add(id);
                    } else {
                        List<String> commitList = new ArrayList<String>();
                        commitList.add(id);
                        fileChange.put(fileName, commitList);
                        fileList.add(fileName);
                    }
                }
                // 如果是文件信息行R
                if (pattern3.matcher(line).find()) {
                    String fileNewName = line.replaceAll("\n", "").split("\t", 3)[2];
                    String fileName = line.replaceAll("\n", "").split("\t", 3)[1];

                    if (fileChange.containsKey(fileName)) {
                        List<String> commitList = fileChange.remove(fileName);
                        commitList.add(id);
                        fileList.remove(fileName);

                        if (fileChange.containsKey(fileNewName)) {
                            fileChange.get(fileNewName).addAll(commitList);
                        } else {
                            fileChange.put(fileNewName, commitList);
                            fileList.add(fileNewName);
                        }
                    } else {
                        if (fileChange.containsKey(fileNewName)) {
                            fileChange.get(fileNewName).add(id);
                        } else {
                            List<String> commitList = new ArrayList<>();
                            commitList.add(id);
                            fileChange.put(fileNewName, commitList);
                            fileList.add(fileNewName);
                        }
                    }
                }
                // 如果是文件信息行D
                if (pattern5.matcher(line).find()) {
                    delFileNum = delFileNum + 1;
                    String fileName = line.replaceAll("\n", "").split("\t")[1];
                    if (fileChange.containsKey(fileName)) {
                        fileChange.remove(fileName);
                    }
                    if (fileList.contains(fileName)) {
                        fileList.remove(fileName);
                    }
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<String>> handelSameCommit(List<String> MoreFileCommit, Map<String, List<String>> fileChange) {
        Map<String, List<String>> tempcommit = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> entry : fileChange.entrySet()) {
            List<String> commitList = new ArrayList<String>();
            for (String comit : entry.getValue()) {
                if (!commitList.contains(comit) && !MoreFileCommit.contains(comit)) {
                    commitList.add(comit);
                }
            }
            tempcommit.put(entry.getKey(), commitList);
        }
        return tempcommit;
    }

    private void findCoChanges(Map<String, List<String>> commit, List<String> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
            List<String> commitList1 = commit.get(fileList.get(i));
            for (int j = i + 1; j < fileList.size(); j++) {
                List<String> commitList2 = commit.get(fileList.get(j));
                int count = 0;
                for (String commit1 : commitList1) {
                    for (String commit2 : commitList2) {
                        if (commit1.equals(commit2)) {
                            count++;
                        }
                    }
                }
                if (count >= Threshold) {
                    cochanges.add(new FilePair(fileList.get(i), fileList.get(j), String.valueOf(count)));
                }
            }
        }
        System.out.println(cochanges.size());
    }
}
