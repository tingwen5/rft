package com.chencang.rft.util;

import lombok.SneakyThrows;

import java.util.TimerTask;

public class TimTaskTest extends TimerTask {


    @SneakyThrows
    @Override
    public void run() {
        boolean is = FileUtil.isIEStart("iexplore.exe");
        if (is){
            System.out.println("检测到ie程序，启动进程关闭命令");
            FileUtil.excuteCMDBatFile("taskkill /f /im iexplore.exe ");
            this.cancel();
        }
    }
}
