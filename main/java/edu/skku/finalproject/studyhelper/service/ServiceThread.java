package edu.skku.finalproject.studyhelper.service;

import android.os.Handler;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import static edu.skku.finalproject.studyhelper.activity.MainActivity.thisWeekStudy;

class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    public boolean isTenMinute = false;

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }
    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }
    public void run(){
        while(isRun){
            if(thisWeekStudy != null){
                String studyTime = thisWeekStudy.getDate();
                try {
                    Date studyTimeParsed = sdf.parse(studyTime) ;

                    Date currenTime = new Date(System.currentTimeMillis());

                    Log.d("현재 스터디 시간: ",studyTimeParsed.toString());
                    Log.d("현재 시간: ",currenTime.toString());
                    int compareResult = studyTimeParsed.compareTo(currenTime);
                    Log.d("compareResult",Integer.toString(compareResult));
                    switch (compareResult){
                        case -1: // 스터디 시간 지남
                            handler.sendEmptyMessage(-1);
                            stopForever();
                            break;
                        case 0:
                        case 1: // 아직 스터디 시간 x or 스터디시간
                            long diffTime = (studyTimeParsed.getTime() - currenTime.getTime()) / 1000; // 스터디까지 남은 시간초
                            if (diffTime  < 600 && diffTime > 598 ){ // 10분전
                                Log.d("스터디 10분전: ","");
                                handler.sendEmptyMessage(1);
                            }else if(diffTime  < 600){
                                handler.sendEmptyMessage(2);
                            }
                            break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            try{
                Thread.sleep(1000);
            }catch (Exception e) {}
        }
    }
}
