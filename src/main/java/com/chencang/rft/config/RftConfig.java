package com.chencang.rft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RftConfig {

    @Value("${polarion.txt}")
    String polarionTxt;

    @Value("${rft.compile}")
    String rftCompile;

    @Value("${rft.cmd}")
    String rftCmd;

    @Value("${autoTest.username}")
    String jusername;

    @Value("${autoTest.password}")
    String jpassword;

    @Value("${autoTest.httpLog}")
    String httpLog;

    @Value("${labview.txt}")
    String lPaht;

    @Value("${labview.cmd}")
    String labCmd;

    public String getPolarionTxt() {
        return polarionTxt;
    }

    public String getRftCmd() {
        return rftCmd;
    }

    public String getRftCompile() {
        return rftCompile;
    }

    public String getJusername() {
        return jusername;
    }

    public String getJpassword() {
        return jpassword;
    }

    public String getHttpLog() {
        return httpLog;
    }

    public String getlPaht() {
        return lPaht;
    }

    public String getLabCmd() {
        return labCmd;
    }

}
