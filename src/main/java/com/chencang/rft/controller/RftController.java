package com.chencang.rft.controller;

import com.alibaba.fastjson.JSONObject;
import com.chencang.rft.config.RftConfig;
import com.chencang.rft.util.FileUtil;
import com.chencang.rft.util.JenkinsScraper;
import com.chencang.rft.util.TimerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@ResponseBody
@RestController
public class RftController {

    @Autowired
    RftConfig rftConfig;

    @Autowired
    JenkinsScraper jenkinsScraper;

    @PostMapping("/autoTest")
    public void AutoTest(@RequestParam String plat) {
        String pathHttp = "";
        String strHttp = rftConfig.getHttpLog();
        JSONObject jsonObject = JSONObject.parseObject(strHttp);
        pathHttp = jsonObject.getString(plat);
        if (plat.contains("rft")) {
            try {
                boolean is = FileUtil.isIEStart("MTS_RFT.exe");
                if (is) {
                    System.out.println("检测到cmd程序正在运行，方法返回");
                    return;
                }
                List<String> tbList = FileUtil.readTxt(rftConfig.getPolarionTxt() + "/scriptName.txt");
                tbList.removeAll(Collections.singleton(null));
                String[] scriptNames = tbList.get(2).split(":");
                String scriptName = scriptNames[1];
                String[] name = scriptName.substring(1, scriptName.length() - 1).split(",");
                for (String str : name) {
                    FileUtil.excuteCMDBatFile(rftConfig.getRftCompile() + str.trim() + "/" + str.trim() + "_Script");
                    log.info("编译：" + str + "_Script");
                }
                FileUtil.excuteCMDBatFile(rftConfig.getRftCmd());
                String code = jenkinsScraper.scrape(pathHttp, rftConfig.getJusername(), rftConfig.getJpassword());
                log.info(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String lpath = rftConfig.getlPaht();
            List<String> tbList = FileUtil.readTxt(lpath + "/scriptName.txt");
            String[] scriptNames = tbList.get(1).split(":");
            String scriptName = scriptNames[1];
            String[] name = scriptName.substring(1, scriptName.length() - 1).split(",");
            try {
                for (String n : name) {
                    if (n.equals(""))
                        continue;
                    String cmd = "@echo off\n";
                    cmd += "cls\n";
                    cmd += "C:\n";
                    cmd += "set seqName =\n";
                    cmd += "cd " + lpath + "/" + n + "\n";
                    cmd += "echo a>a.xml\n";
                    cmd += "del *.xml /s /a h\n";
                    cmd += "for /f %%a in ('dir /s /b *.seq') do ( set seqName=%%a)\n";
                    cmd += rftConfig.getLabCmd() + "\"%seqName%\" /quit\n";
                    FileUtil.excuteCMDBatFile(cmd);
                }
                String c = "@echo off\n";
                c += "C:\n";
                c += "cd " + lpath + "\n";
                c += "cd..\n";
                c += "del labviewWorkspace.rar\n";
                c += "C:/Progra~1/WinRAR/winrar.exe a -o+ -r -s -ibck labviewWorkspace.rar labviewWorkspace\n";
                c += "rmdir /s/q labviewWorkspace\n";
                FileUtil.excuteCMDBatFile(c);
                String code = jenkinsScraper.scrape(pathHttp, rftConfig.getJusername(), rftConfig.getJpassword());
                log.info(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/txt")
    public void txtTestRun(@RequestParam String plat, @RequestParam String testRunId) {
        try {
            String path = "";
            if (plat.contains("rft"))
                path = rftConfig.getPolarionTxt();
            else {
                path = rftConfig.getlPaht();
                FileUtil.excuteCMDBatFile("C:\ncd "+path.substring(0,path.lastIndexOf("/"))+"\ntest.bat");
                FileUtil.excuteCMDBatFile("C:\ncd "+path.substring(0,path.lastIndexOf("/"))+"\nC:/Progra~1/WinRAR/winrar.exe x -o+ labviewWorkspace.rar");
            }
            List<String> scriptNames = FileUtil.readTxt(path + "/scriptName.txt");
            scriptNames.removeAll(Collections.singleton(null));
            String testRunNames = scriptNames.get(0);
            String testRunName = testRunNames.split(":")[1];
            FileUtil.autoReplaceStr(path + "/scriptName.txt", testRunName, testRunId);
            if(!plat.contains("rft"))
                FileUtil.excuteCMDBatFile("C:\ncd "+path.substring(0,path.lastIndexOf("/"))+"\nC:/Progra~1/WinRAR/winrar.exe a -o+ -r -s -ibck labviewWorkspace.rar labviewWorkspace\nrmdir /s/q labviewWorkspace");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
