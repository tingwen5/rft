package com.chencang.rft.util;

import java.util.Date;
import java.util.Timer;


/**
 * 定时任务管理器
 *
 * @author dyh
 */
public class TimerManager {

    /**
     * 单例模式
     */
    private static TimerManager timerManager = null;

    public TimerManager() {
    }

    public static TimerManager getInstance() {
        if (timerManager == null) {
            timerManager = new TimerManager();
        }
        return timerManager;
    }

    /**
     * 定时器
     */
    private Timer timer = new Timer("homePageTimer");

    /**
     * 定时任务
     */
    private TimTaskTest timerTask = null;

    /**
     * 启动定时任务
     */
    public void startTimerTask() {
        timer.purge();
        timerTask = new TimTaskTest();
        timer.schedule(timerTask, new Date(), 10000);
    }

    /**
     * 定时任务取消
     */
    public void stopTimerTask() {
        timerTask.cancel();
        timerTask = null;//如果不重新new，会报异常
    }

}