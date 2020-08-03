package com.chencang.rft.controller;

import com.chencang.rft.config.RftConfig;
import com.chencang.rft.util.FileUtil;
import com.chencang.rft.util.TimerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@RestController
public class RftController {

    @Autowired
    RftConfig rftConfig;

    @PostMapping("/autoTest")
    public void AutoTest() {
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
