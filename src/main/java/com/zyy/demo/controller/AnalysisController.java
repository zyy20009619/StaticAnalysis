package com.zyy.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyy.demo.entities.SingelCollect;
import com.zyy.demo.services.IAnalysisClassFile;
import com.zyy.demo.services.IAnalysisComtFile;
import com.zyy.demo.services.IAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class AnalysisController {
    @Autowired
    private IAnalysisService iAnalysisService;
    @Autowired
    private IAnalysisComtFile iAnalysisComtFile;
    @Autowired
    private IAnalysisClassFile iAnalysisClassFile;

    @RequestMapping(value = "getSCFile")
    @ResponseBody
    public String getSCFile(@RequestBody JSONObject jsonParams) {
        Map<String, String> serviceMsg = (Map<String, String>) jsonParams.get("servicenames_services_url");
        List<String> projectsurl = (List<String>) jsonParams.get("projects_url");
        int index = 0;
        for (Map.Entry<String, String> entry : serviceMsg.entrySet()) {
            SingelCollect.init();
            iAnalysisService.analysisSCFile(entry.getKey(), entry.getValue(), projectsurl.get(index));
            index++;
        }
        return "OK";
    }

    @RequestMapping("getAPIFile")
    @ResponseBody
    public String getAPIFile(@RequestBody JSONObject jsonParam) {
        Map<String, String> apiMsg = (Map<String, String>) jsonParam.get("servicenames_apis_url");
        List<String> projectsurl = (List<String>) jsonParam.get("projects_url");
        List<String> type = (List<String>) jsonParam.get("type");
        int index = 0;
        for (Map.Entry<String, String> entry : apiMsg.entrySet()) {
            SingelCollect.init();
            iAnalysisService.analysisAPIFile(entry.getKey(), entry.getValue(), projectsurl.get(index), type.get(index));
            index++;
        }
        return "OK";
    }

    @RequestMapping("getCmtFile")
    @ResponseBody
    public String getCmtFile(@RequestBody JSONObject jsonParams) {
        List<String> projectsurl = (List<String>) jsonParams.get("urls");
        for (String projectpath : projectsurl) {
            iAnalysisComtFile.analysisComtFile(projectpath);
        }
        return "OK";
    }

    @RequestMapping("getStructdepFile")
    @ResponseBody
    public String getStructdepFile(@RequestBody JSONObject jsonParams) {
        List<String> projectsurl = (List<String>) jsonParams.get("urls");
        List<String> projectname = (List<String>) jsonParams.get("names");
        for (int index = 0; index < projectname.size(); index++) {
            System.out.println(projectname.get(index));
            iAnalysisClassFile.analysisStructdep(projectname.get(index), projectsurl.get(index));
        }
        return "OK";
    }
}
