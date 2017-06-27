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
    //추출해서 나온 값들
        int calSec = -1;
        int calMin = -1;
        int calHour= -1;
        int calDay = -1;
        int calMonth = -1;
        int calYear = -1;

    //현재 날짜를 넣어줄 값들
        int curYear = 0;
        int curMonth = 0;
        int curDay=0;
        int curHour=0;
        int curMin=0;
        int curSec=0;

    //요일, 다양한 표현들 추출한 값들
    String dayOfWeek = null;

    int tempMin =0;

    //구글에서 보내준 문자값을 넣어줄 변수 값
    String statement;

    //분석하기
    public String Anylis(String target){
        statement = target; // 구글 서버에서 보내주는 문자값

        System.out.println("정규식 : " + target);

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

        dayOfWeek = dayOfWeekPattern();
        calMin = MinutePattern();
        calSec = SecondPattern();

        String curTime ="현재 시간: "+curMin+" 분 "+curSec+" 초\n";
        String calTime = "알람 시간: "+dayOfWeek+ " "+ calMin+" 분 "+calSec+" 초";

        reset();
        return curTime+ calTime;
    }

    public String dayOfWeekPattern()
    {
        Pattern pattern = Pattern.compile("[월]?[화]?[수]?[목]?[금]?[토]?[일]?+[요]+[일]");//여기에 정규표현식을 적습니다.
        Matcher matcher = pattern.matcher(statement);
        if(matcher.find())
        {
            String match = matcher.group(0);
            System.out.println("요일 테스트 : " + match);
            return match;
        }
        return null;
    }


    public int SecondPattern()
    {
        Pattern pattern = Pattern.compile("[1-9]\\d*+[초]");//여기에 정규표현식을 적습니다.
        Matcher matcher = pattern.matcher(statement);

        while(true){
            if (matcher.find()) {
                String match = matcher.group(0);
                System.out.println("정규식 짜른거" + match);
                System.out.println(match + "\n");
                String temp2[] = match.split("초");
                System.out.println("초다"+temp2[0] + "\n");
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
