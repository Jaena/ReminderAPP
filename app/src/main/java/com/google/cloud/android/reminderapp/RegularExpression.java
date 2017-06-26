package com.google.cloud.android.reminderapp;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaena on 2017-06-26.
 */

public class RegularExpression {
    int calSec = -1;
    int calMin = -1;
    int calHour= -1;
    int calDay = -1;
    int calMonth = -1;
    int calYear = -1;

    int curYear = 0;
    int curMonth = 0;
    int curDay=0;
    int curHour=0;
    int curMin=0;
    int curSec=0;

    int tempMin =0;
    String statement;

    public String Anaylis(String target){
        statement = target;

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:hh:mm:ss");
        String tempTime = sdf.format(date);
        String temp[] = tempTime.split(":");

        curYear= Integer.parseInt(temp[0]);
        curMonth= Integer.parseInt(temp[1]);
        curDay= Integer.parseInt(temp[2]);
        curHour= Integer.parseInt(temp[3]);
        curMin= Integer.parseInt(temp[4]);
        curSec = Integer.parseInt(temp[5]);

//        calYear;
//        calMonth;
//        calDay;
        //calHour = HourPattern();
        calMin = MinutePattern();
        calSec = SecondPattern();

        String curTime ="현재 시간: "+curMin+" 분 "+curSec+" 초\n";
        String calTime = "알람 시간: "+calMin+" 분 "+calSec+" 초";



        reset();
        return curTime+ calTime;
    }

    public int SecondPattern()
    {
        Pattern pattern = Pattern.compile("[1-9]\\d*+[초]");//여기에 정규표현식을 적습니다.
        Matcher matcher = pattern.matcher(statement);
        while(true){
            if (matcher.find()) {
                String match = matcher.group(0);
                System.out.println(match + "\n");
                String temp2[] = match.split("초");
                System.out.println(temp2[0] + "\n");
                int matchMin = Integer.parseInt(temp2[0]);
                calSec = curSec + matchMin;

                if(calSec>=60) {
                    tempMin = calSec / 60;
                    calSec = calSec%60;
                }
            }
            else {
                if(calSec<0)
                    calSec = curSec;
                break;
            }
            Log.d("Text",""+calSec);
        }
        return calSec;
    }

    public int MinutePattern()
    {
        Pattern pattern = Pattern.compile("[1-9]\\d*+[분]");//여기에 정규표현식을 적습니다.
        Matcher matcher = pattern.matcher(statement);
        while(true){
            if (matcher.find()) {
                String match = matcher.group(0);
                System.out.println(match + "\n");
                String temp2[] = match.split("분");
                System.out.println(temp2[0] + "\n");
                int matchMin = Integer.parseInt(temp2[0]);
                calMin = curMin + matchMin+tempMin;
                tempMin =0;
            }
            else {
                if(calMin<0)
                    calMin = curMin;
                break;
            }
            Log.d("Text",""+calMin);
        }
        return calMin;
    }

    public int HourPattern()
    {
        Pattern MinuatePattern = Pattern.compile("([오]+[후])|([오]+[전])|[1-9]\\d*+[시]");//여기에 정규표현식을 적습니다.
        Matcher matcher = MinuatePattern.matcher(statement);
        while(true){
            if (matcher.find()) {
                String match = matcher.group(0);
                System.out.println(match + "\n");
                String match2 = matcher.group(1);
                System.out.println(match2 + "\n");
                String temp2[] = match.split("[오후,오전]");
                System.out.println(temp2[0] + "\n");
                int matchMin = Integer.parseInt(temp2[0]);
                calMin = curMin + matchMin;
            }
            else {
                if(calMin<0)
                    calMin = curMin;
                break;
            }
            Log.d("Text",""+calMin);
        }
        return calMin;
    }

    public void reset()
    {
        calSec =-1;
        calMin = -1;
        calHour= -1;
        calDay = -1;
        calMonth = -1;
        calYear = -1;

        curYear = 0;
        curMonth = 0;
        curDay=0;
        curHour=0;
        curMin=0;
        curSec=0;
    }


}
