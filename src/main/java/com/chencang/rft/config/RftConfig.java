package com.chencang.rft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RftConfig {

    @Value("${polarion.txt}")
    String polarionTxt;

    @Value("${rft.sched}")
    String rftSched;

    @Value("${rft.cmd}")
    String rftCmd;

    @Value("${jenkins.username}")
    String jusername;

    @Value("${jenkins.password}")
    String jpassword;

    @Value("${jenkins.httpLog}")
    String httpLog;

    public String getPolarionTxt() {
        return polarionTxt;
    }

    public String getRftSched() {
        return rftSched;
    }

    public String getRftCmd() {
        return rftCmd;
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

}
