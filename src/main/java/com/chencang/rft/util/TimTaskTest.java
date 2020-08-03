package com.chencang.rft.util;

import lombok.SneakyThrows;

import java.util.TimerTask;

public class TimTaskTest extends TimerTask {


    @SneakyThrows
    @Override
    public void run() {
        boolean is = FileUtil.isIEStart();
        if (is){
            FileUtil.excuteCMDBatFile("taskkill /f /im iexplore.exe ");
            this.cancel();
        }
    }
}
