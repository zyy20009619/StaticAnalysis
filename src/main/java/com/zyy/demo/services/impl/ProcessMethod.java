package com.zyy.demo.services.impl;

import com.zyy.demo.entities.MethodMessage;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.List;

public class ProcessMethod {
    public void dealMethod(MethodDeclaration node, List<MethodMessage> methods) {
        MethodMessage methodMessage = new MethodMessage();

        methodMessage.setName(node.getName().toString());
        if (node.parameters().size() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            node.parameters().forEach(input -> {
                String para[] = input.toString().split(" ");
                String type = "";
                if (para.length == 3) {
                    type = para[1];
                } else if (para.length == 4) {
                    type = para[2];
                } else {
                    type = para[0];
                }
                if(type.contains("<")) {
                    type = type.split("<")[0];
                }
                stringBuilder.append(type).append(";");
            });
            methodMessage.setInputType(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
        }
        String returnType = node.getReturnType2() != null ? node.getReturnType2().toString().split("<")[0] : "";
        methodMessage.setOuputType(returnType);
        methods.add(methodMessage);
    }
}
