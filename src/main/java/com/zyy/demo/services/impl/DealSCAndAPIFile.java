package com.zyy.demo.services.impl;

import com.zyy.demo.entities.SingelCollect;
import com.zyy.demo.entities.MethodMessage;

import java.io.*;
import java.util.*;

public class DealSCAndAPIFile {
    /**
     * 存储微服务及其下面所有服务类的集合
     */
    public static Map<String, List<String>> service2class = new HashMap<>();
    /**
     * 存储微服务与其每个接口类及接口类下面的方法的集合
     */
    public static Map<String, Map<String, List<MethodMessage>>> service2api = new HashMap<>();

    //获取SC文件所需信息
    public static boolean getSCMessage(String servicesname, String scspath) {
        service2class.clear();
        List<String> servicename = Arrays.asList(servicesname.split(";"));
        List<String> scpath = Arrays.asList(scspath.split(";"));
        int index = 0;
        for (String name : servicename) {
            System.out.println(name);
            List<String> classnames = new ArrayList<String>();
            Map<String, List<MethodMessage>> methodMap = new HashMap<>();
            traverseFolders(classnames, methodMap, new File(scpath.get(index)), "default", "sc");
            service2class.put(name, classnames);
            index++;
        }
        if (service2class.size() != 0) {
            return true;
        }
        return false;
    }

    //获取API文件所需信息
    public static boolean getAPIMessage(String servicesname, String apispath, String type) {
        service2api.clear();
        List<String> servicename = Arrays.asList(servicesname.split(";"));
        List<String> apipath = Arrays.asList(apispath.split(";"));
        List<String> filetypes = Arrays.asList(type.split(";"));
        int index = 0;
        for (String name : servicename) {
            List<String> classnames = new ArrayList<String>();
            Map<String, List<MethodMessage>> methodMap = new HashMap<>();
            if (apipath.get(index).equals("none")) {
                index++;
                continue;
            }
            List<String> apipaths = Arrays.asList(apipath.get(index).split(","));
            for (String path : apipaths) {
                traverseFolders(classnames, methodMap, new File(path), filetypes.get(index), "api");
            }
            service2api.put(name, methodMap);
            index++;
        }
        if (service2api.size() != 0) {
            return true;
        }
        return false;
    }

    private static void traverseFolders(List<String> classnames, Map<String, List<MethodMessage>> methodMap, File basePathfile, String filetype, String type) {
        if (basePathfile.listFiles() == null) {
            return;
        }
        for (File file : Arrays.asList(basePathfile.listFiles())) {
            traverseFolders(classnames, methodMap, file, filetype, type);
            if (file.getName().contains(".java") && !file.getName().contains("Test")) {
                if (type.equals("api")) {
                    if (filetype.equals("controller")) {
                        if (!file.getName().toLowerCase().contains("controller")) {
                            continue;
                        }
                        SingelCollect.setFlag("controller");
                    } else if (filetype.equals("interface")) {
                        SingelCollect.setFlag("interface");
                    } else if (filetype.equals("servlet")) {
                        if (!file.getName().toLowerCase().contains("servlet")) {
                            continue;
                        }
                        SingelCollect.setFlag("servlet");
                    } else if (filetype.equals("resource")) {
                        if (!file.getName().toLowerCase().contains("resource")) {
                            continue;
                        }
                        SingelCollect.setFlag("resource");
                    } else if (filetype.equals("rest")) {
                        if (!file.getName().toLowerCase().contains("rest")) {
                            continue;
                        }
                        SingelCollect.setFlag("rest");
                    } else if (filetype.equals("endpoint")) {
                        if (!file.getName().toLowerCase().contains("endpoint")) {
                            continue;
                        }
                        SingelCollect.setFlag("endpoint");
                    }
                } else {
                    SingelCollect.setFlag("sc");
                }
                ClassParser.getMethodMessage(file.getPath());
                classnames.add(SingelCollect.getClassname());
                if (SingelCollect.getMethods().size() != 0) {
                    List<MethodMessage> methods = new ArrayList<>();
                    methods.addAll(SingelCollect.getMethods());
                    methodMap.put(SingelCollect.getClassname(), methods);
                    SingelCollect.init();
                    SingelCollect.clear();
                }
            }
        }
    }
}
