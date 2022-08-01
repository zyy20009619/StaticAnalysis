package com.zyy.demo.entities;

import java.util.*;

public class SingelCollect {
    public static List<MethodMessage> methods = new LinkedList<>();
    public static Map<String, List<String>> service2class = new HashMap<>();
    public static List<String> identifierListOneFile = new ArrayList<>();
    public static String classname = "";
    public static String packagename = "";
    public static String Flag = "";

    public static void init() {
        classname = "";
        packagename = "";
    }

    public static String getClassname() {
        return classname;
    }

    public static void setClassname(String classname) {
        SingelCollect.classname = classname;
    }

    public static String getPackagename() {
        return packagename;
    }

    public static void setPackagename(String packagename) {
        SingelCollect.packagename = packagename;
    }

    public static void clear() {
        SingelCollect.getMethods().clear();
    }

    public static String getFlag() {
        return Flag;
    }

    public static void setFlag(String flag) {
        Flag = flag;
    }

    public static List<MethodMessage> getMethods() {
        return methods;
    }

    public static void setMethods(List<MethodMessage> methods) {
        SingelCollect.methods = methods;
    }

    public static Map<String, List<String>> getService2class() {
        return service2class;
    }

    public static void setService2class(Map<String, List<String>> service2class) {
        SingelCollect.service2class = service2class;
    }

    public static List<String> getIdentifierListOneFile() {
        return identifierListOneFile;
    }

    public static void setIdentifierListOneFile(List<String> identifierListOneFile) {
        SingelCollect.identifierListOneFile = identifierListOneFile;
    }

    public static void clearIdentifierListOneFile() {
        SingelCollect.identifierListOneFile.clear();
    }
}
