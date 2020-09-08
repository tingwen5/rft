package com.chencang.rft.util;

import lombok.SneakyThrows;

import java.util.TimerTask;

public class TimTaskTest2 extends TimerTask {

    @SneakyThrows
    @Override
    public void run() {
        boolean is2 = FileUtil.isIEStart("seqedit.exe");
        if (is2){
            FileUtil.excuteCMDBatFile("taskkill /f /im seqedit.exe");
            this.cancel();
        }
    }
}
