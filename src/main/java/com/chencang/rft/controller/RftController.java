package com.chencang.rft.controller;

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
        if (plat.contains("rft")) {
            String result = FileUtil.readTxt(rftConfig.getPolarionTxt());
            List<String> tbList = Arrays.asList(result.split("\\n"));
            tbList.removeAll(Collections.singleton(null));
            String[] scriptNames = tbList.get(2).split(":");
            String scriptName = scriptNames[1];
            String[] name = scriptName.substring(1, scriptName.length() - 1).split(",");
            String str = "";
            for (int i = 0; i < name.length; i++) {
                str += "\"" + name[i].trim() + "\"";
                if (i != name.length - 1)
                    str += ",";
            }
            try {
                FileUtil.autoReplaceStr(rftConfig.getRftSched(), "\"scriptNameIdentification\"", str);
                TimerManager tm = new TimerManager();
                tm.startTimerTask();
                FileUtil.excuteCMDBatFile(rftConfig.getRftCmd());
                FileUtil.autoReplaceStr(rftConfig.getRftSched(), str, "\"scriptNameIdentification\"");
                String code = jenkinsScraper.scrape(rftConfig.getHttpLog(), rftConfig.getJusername(), rftConfig.getJpassword());
                log.info(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String lpath = rftConfig.getlPaht();
            String result = FileUtil.readTxt(lpath+"\\scriptName.txt");
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
                c+="rmdir /s/q labviewWorkspace";
                FileUtil.excuteCMDBatFile(c);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}