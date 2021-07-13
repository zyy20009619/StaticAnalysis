package com.zyy.demo.services.impl;

import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ClassParser extends ASTVisitor {
    public static void getMethodMessage(String filepath) {
        visitAST(filepath);
    }

    private static void visitAST(String filepath) {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(new File(filepath).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String(bytes, StandardCharsets.UTF_8);

        // JLS：Java编程规范
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(s.toCharArray());
        // K_COMPILATION_UNIT表示需要解析的代码类型为Java文件
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        // 将源代码转化为AST
        org.eclipse.jdt.core.dom.CompilationUnit cu = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);

        // 访问AST树
        ClassVisitor visitor = new ClassVisitor();
        cu.accept(visitor);
    }

    public static void visitIdenfitier(String path) {
        visitAST(path);
    }
}