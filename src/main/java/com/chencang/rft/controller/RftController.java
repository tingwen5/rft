package com.chencang.rft.controller;

import com.alibaba.fastjson.JSONObject;
import com.chencang.rft.config.RftConfig;
import com.chencang.rft.util.FileUtil;
import com.chencang.rft.util.JenkinsScraper;
import com.chencang.rft.util.TimerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
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
                if (is){
                    System.out.println("检测到cmd程序正在运行，方法返回");
                    return;
                }
                String result = FileUtil.readTxt(rftConfig.getPolarionTxt()+"/scriptName.txt");
                List<String> tbList = Arrays.asList(result.split("\\n"));
                tbList.removeAll(Collections.singleton(null));
                String[] scriptNames = tbList.get(2).split(":");
                String scriptName = scriptNames[1];
                String[] name = scriptName.substring(1, scriptName.length() - 1).split(",");
                for (String str : name){
                    FileUtil.excuteCMDBatFile(rftConfig.getRftCompile()+str.trim()+"/"+str.trim()+"_Script");
                    log.info("编译："+str+"_Script");
                }
                FileUtil.excuteCMDBatFile(rftConfig.getRftCmd());
                String code = jenkinsScraper.scrape(pathHttp, rftConfig.getJusername(), rftConfig.getJpassword());
                log.info(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String lpath = rftConfig.getlPaht();
            String result = FileUtil.readTxt(lpath+"/scriptName.txt");
            List<String> tbList = Arrays.asList(result.split("\\n"));
            tbList.removeAll(Collections.singleton(null));
            String[] scriptNames = tbList.get(2).split(":");
            String scriptName = scriptNames[1];
            String[] name = scriptName.substring(1, scriptName.length() - 1).split(",");
            try {
                for (String n : name){
                    String cmd = "@echo off\n";
                    cmd+="cls\n";
                    cmd+="set seqName =\n";
                    cmd+="cd "+lpath+"\\"+n+"\n";
                    cmd+="echo a>a.xml\n";
                    cmd+="del *.xml /s /a h\n";
                    cmd+="for /f %%a in ('dir /s /b *.seq') do ( set seqName=%%a)\n";
                    cmd+="\"C:\\Progra~2\\National Instruments\\TestStand 2016\\Bin\\SeqEdit.exe\" /runEntryPoint \"Single Pass\" \"%seqName%\" /quit\n";
                    FileUtil.excuteCMDBatFile(cmd);
                }
                String c = "@echo off\n";
                c+="cd "+lpath+"\n";
                c+="cd..\n";
                c+="C:\\Progra~1\\WinRAR\\winrar.exe a -o+ -r -s -ibck labviewWorkspace.rar labviewWorkspace\n";
                c+="rmdir /s/q labviewWorkspace\n";
                FileUtil.excuteCMDBatFile(c);
                String code = jenkinsScraper.scrape(pathHttp, rftConfig.getJusername(), rftConfig.getJpassword());
                log.info(code);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
