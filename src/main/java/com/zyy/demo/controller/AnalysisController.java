package com.zyy.demo.controller;

import com.zyy.demo.services.IAnalysisComtFile;
import com.zyy.demo.services.IAnalysisSemanticFile;
import com.zyy.demo.services.IAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AnalysisController {
    @Autowired
    private IAnalysisService iAnalysisService;
    @Autowired
    private IAnalysisComtFile iAnalysisComtFile;
    @Autowired
    private IAnalysisSemanticFile iAnalysisSemanticFile;

    @RequestMapping("getSCFile")
    @ResponseBody
    public void getSCFile(@RequestParam("projectpath")String projectpath) {
        iAnalysisService.analysisSCFile(projectpath);
    }

    @RequestMapping("getAPIFile")
    @ResponseBody
    public void getAPIFile(@RequestParam("projectpath")String projectpath) {
        iAnalysisService.analysisAPIFile(projectpath);
    }

    @RequestMapping("getComtFile")
    @ResponseBody
    public void getComtFile(@RequestParam("projectpath")String projectpath) {
        iAnalysisComtFile.analysisComtFile(projectpath);
    }

//    @RequestMapping("getSemanticFile")
//    @ResponseBody
//    public void getSemanticFile(@RequestParam("projectpath")String projectpath) {
//        iAnalysisSemanticFile.analysisSemanticFile(projectpath);
//    }
}
