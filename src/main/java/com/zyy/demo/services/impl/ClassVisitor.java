package com.zyy.demo.services.impl;

import com.zyy.demo.entities.MethodMessage;
import com.zyy.demo.entities.SingelCollect;
import org.eclipse.jdt.core.dom.*;

import java.util.LinkedList;
import java.util.List;

public class ClassVisitor extends ASTVisitor {
    ProcessMethod processMethod = new ProcessMethod();
    List<MethodMessage> methods = new LinkedList<>();

    @Override
    public boolean visit(MethodDeclaration node) {
        processMethod.dealMethod(node, methods);
        SingelCollect.setMethods(methods);
        System.out.println("Method:\t" + node.getName());
        SingelCollect.getIdentifierListOneFile().add(node.getName().toString());
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        System.out.println("Class:\t" + node.getName());
        return super.visit(node);
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        System.out.println("PackageDeclaration:\t" + node.getName());
        SingelCollect.getIdentifierListOneFile().add(node.getName().toString());
        return super.visit(node);
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        System.out.println("ImportDeclaration:\t" + node.getName());
        SingelCollect.getIdentifierListOneFile().add(node.getName().toString());
        return super.visit(node);
    }

//    @Override
//    public boolean visit(VariableDeclarationStatement node) {
//        System.out.println("VariableDeclarationStatement:\t" + node.fragments());
//        return super.visit(node);
//    }

    @Override
    public boolean visit(FieldDeclaration node) {
        node.fragments().forEach(fragment -> {
            SingelCollect.getIdentifierListOneFile().add(fragment.toString());
        });
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        SingelCollect.getIdentifierListOneFile().add(node.getName().getFullyQualifiedName());
//        System.out.println("SingleVariableDeclaration:\t" + node.getName().getFullyQualifiedName());
        return super.visit(node);
    }

    public boolean visit(ReturnStatement node){
        System.out.println("ReturnStatement:\t" + node.getExpression());
        return super.visit(node);
    }
}
