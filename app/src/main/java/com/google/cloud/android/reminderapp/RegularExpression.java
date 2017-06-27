package com.google.cloud.android.reminderapp;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaena on 2017-06-26.
 */

public class RegularExpression {

    public HashMap<String, Integer> hMap;
    public int curYear, curMonth, curDay, curHour, curMinute;
    public int calYear, calMonth, calDay, calHour, calMinute;;

    // 내일|낼|명일|모레|글피|익일|명일
    public RegularExpression() {
        hMap = new HashMap<String, Integer>();
        hMap.put("내일", 1); hMap.put("낼", 1); hMap.put("명일", 1);
        hMap.put("익일", 1); hMap.put("명일", 1);
        hMap.put("모레", 2); hMap.put("글피", 3);
    }

    public String Analysis(String target){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:hh:mm:ss");
        String tempTime = sdf.format(date);
        String temp[] = tempTime.split(":");

        curYear= Integer.parseInt(temp[0]);
        curMonth= Integer.parseInt(temp[1]);
        curDay= Integer.parseInt(temp[2]);
        curHour= Integer.parseInt(temp[3]);
        curMinute= Integer.parseInt(temp[4]);

        calYear = 0; calMonth = 0; calDay = 0; calHour = 0; calMinute = 0;


        extractManager(target);
        String curTime = "현재 시간: "+ curYear + "년 " + curMonth + "월 "+ curDay + "일 " + curHour + "시 " + curMinute + "분\n";
        String calTime = "알람 시간: "+ calYear + "년 " + calMonth + "월 "+ calDay + "일 " + calHour + "시 " + calMinute + " 분";

        return curTime + calTime;
    }

    public boolean extractManager(String searchTarget) {
        String regex = new String();

        while(true) {
            regex = "([0-9]+) ?시간 ?([0-9]+) ?분 ?(후|뒤|있다가)";
            if(extract1(searchTarget, regex)) break;

            regex = "([0-9]+) ?시 ?간? ?만? ?(후|뒤|있다가)";
            if(extract2(searchTarget, regex)) break;

            regex = "([1-2]?[0-9])시 ?([1-5]?[0-9])분";
            if(extract3(searchTarget, regex)) break;

            regex = "([1-2]?[0-9])시 ?반";
            if(extract4(searchTarget, regex)) break;

            regex = "([1-2]?[0-9])시";
            if(extract5(searchTarget, regex)) break;

            regex = "([0-9]+) ?분 ?(후|뒤|있다가)";
            if(extract6(searchTarget, regex)) break;

            regex = "(^| )([0-9]+)주 ?(후|뒤|있다가)";
            if(extract7(searchTarget, regex)) break;

            regex = "(1?[0-9])월 ?([1-3]?[0-9])일";
            if(extract8(searchTarget, regex)) break;

            regex = "(1?[0-9])월";
            if(extract9(searchTarget, regex)) break;

            regex = "(^| )([0-9]+) ?일 ?(후|뒤|있다가)";
            if(extract11(searchTarget, regex)) break;


            regex = "([1-3]?[0-9])일";
            if(extract10(searchTarget, regex)) break;

            break;
        }

        regex = "[내일|낼|명일|모레|글피|익일|명일]+";
        calDay += extract100(searchTarget, regex);

        return true;
    }

    public boolean extract1(String searchTarget, String regex) { //~시간 ~분 후|뒤|있다가
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.trim().split("시간|분");
            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            System.out.println("what ? " + Integer.parseInt(temp[0].trim()) + " " + Integer.parseInt(temp[1].trim()));
            //System.out.println(matcher.group(0));
            System.out.println("IN TEST");
        }
        System.out.println("TESTTEST");

        return isExtracted;
    }


    public boolean extract2(String searchTarget, String regex) { //~시간 후
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시간");
            addTime(0, Integer.parseInt(temp[0].trim()), 0);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract3(String searchTarget, String regex) { //~시 ~분
        System.out.println("In extract3");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시|분");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract4(String searchTarget, String regex) { //~시반
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), 30);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract5(String searchTarget, String regex) { //~시
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), 0);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract6(String searchTarget, String regex) { //~분 후|뒤|있다가
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("분");
            addTime(0, 0, Integer.parseInt(temp[0].trim()));
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract7(String searchTarget, String regex) { //~주 후
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("주");
            addTime(7*Integer.parseInt(temp[0].trim()), 0, 0);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract8(String searchTarget, String regex) { //~월 ~일
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월|일");
            atTime(curYear, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()), curHour, curMinute);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract9(String searchTarget, String regex) { //~월
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월");
            atTime(curYear, Integer.parseInt(temp[0].trim()), 1, 0, 0); //그 달의 1일 0시 0분
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract10(String searchTarget, String regex) { //~일
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            atTime(curYear, curMonth, Integer.parseInt(temp[0].trim()), 0, 0); //그 달, 그 일의 0시 0분
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract11(String searchTarget, String regex) { //~일 후(뒤)
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while(matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            addTime(Integer.parseInt(temp[0].trim()), 0, 0);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public int extract100(String searchTarget, String regex) { //[내일|낼|명일|모레|글피|익일|명일]+
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
//       boolean isExtracted = false;
        int maxi=0;
        while(matcher.find()) {
            String match = matcher.group(0);
            Integer num = 0;
            num = hMap.get(match);
            if(num == null) num = 0;

            if(maxi < num) maxi = num;
            //addTime(maxi, 0, 0); - addTime은 curTime에 더하는 것이므로
            //여기서 더해봤자 아무의미가 마지막에 추가적으로 더해주자.
//          isExtracted = true;
            //System.out.println(matcher.group(0));
        }
//       return isExtracted;
        return maxi;
    }

    public void addTime(int d, int h, int m) {
        System.out.println(d + " " + h + " " + m );

        calMinute = curMinute + m;
        calHour = curHour + h;
        calDay = curDay + d;
        System.out.println("val hey : " + calDay + " " + calHour + " " + calMinute);
        calHour += calMinute/60;
        calMinute = calMinute % 60;
        calDay += calHour/24;
        calHour = calHour%24;


        //calMonth - 김원교수님 뵙기전까지 해야함 일단 6월 27일 5시이후에생각해보자
        /*calMonth += calDay/31;
        calDay
        calYear = curYear*/
    }

    public void atTime(int y, int M, int d, int h, int m) {
        System.out.println("hour : " + h + " " + "minute : " + m);
        calYear = y;
        calMonth = M;
        calDay = d;
        calHour = h;
        calMinute = m;
    }
}