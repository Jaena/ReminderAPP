package com.google.cloud.android.reminderapp;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaena on 2017-06-26.
 */

public class RegularExpression {

    public HashMap<String, Integer> hMap;
    public HashMap<String, Integer> wMap;
    public int curYear, curMonth, curDay, curHour, curMinute;
    public int calYear, calMonth, calDay, calHour, calMinute;
    public String curDayOfWeek;
    public int[] days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    // 내일|낼|명일|모레|글피|익일|명일
    public RegularExpression() {
        hMap = new HashMap<String, Integer>();
        wMap = new HashMap<String, Integer>();

        hMap.put("내일", 1);
        hMap.put("낼", 1);
        hMap.put("명일", 1);
        hMap.put("익일", 1);
        hMap.put("명일", 1);
        hMap.put("모레", 2);
        hMap.put("글피", 3);
        hMap.put("다음주", 1);
        hMap.put("다다음주", 2);

        wMap.put("일요일", 1);
        wMap.put("월요일", 2);
        wMap.put("화요일", 3);
        wMap.put("수요일", 4);
        wMap.put("목요일", 5);
        wMap.put("금요일", 6);
        wMap.put("토요일", 7);

    }

    public String Analysis(String target) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:hh:mm:ss");
        String tempTime = sdf.format(date);
        String temp[] = tempTime.split(":");

        curYear = Integer.parseInt(temp[0]);
        curMonth = Integer.parseInt(temp[1]);
        curDay = Integer.parseInt(temp[2]);
        curHour = Integer.parseInt(temp[3]);
        curMinute = Integer.parseInt(temp[4]);


        cal.set(Calendar.YEAR, curYear);
        cal.set(Calendar.MONTH, curMonth);
        cal.set(Calendar.DATE, curDay);

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                curDayOfWeek = "일요일";
                break;
            case 2:
                curDayOfWeek = "월요일";
                break;
            case 3:
                curDayOfWeek = "화요일";
                break;
            case 4:
                curDayOfWeek = "수요일";
                break;
            case 5:
                curDayOfWeek = "목요일";
                break;
            case 6:
                curDayOfWeek = "금요일";
                break;
            case 7:
                curDayOfWeek = "토요일";
                break;
        }
        //System.out.println("현재 요일 : "+ curDayOfWeek);
        calYear = 0;
        calMonth = 0;
        calDay = 0;
        calHour = 0;
        calMinute = 0;


        extractManager(target);
        String curTime = "현재 시간: " + curYear + "년 " + curMonth + "월 " + curDay + "일 " + curHour + "시 " + curMinute + "분\n";
        String calTime = "알람 시간: " + calYear + "년 " + calMonth + "월 " + calDay + "일 " + calHour + "시 " + calMinute + " 분";

        return curTime + calTime;
    }

    public boolean extractManager(String searchTarget) {
        String regex = new String();

        while (true) {

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분";
            if (extract15(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?반";
            if (extract16(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시";
            if (extract17(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분";
            if (extract18(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?반";
            if (extract19(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시";
            if (extract20(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분 ?(월|화|수|목|금|토|일) ?요일";
            if (extract21(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시 ?반 ?(월|화|수|목|금|토|일) ?요일";
            if (extract22(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시 ?(월|화|수|목|금|토|일) ?요일";
            if (extract23(searchTarget, regex)) break;


            regex = "([0-9]+) ?시간 ?([0-9]+) ?분 ?(후|뒤|있다가)";
            if (extract1(searchTarget, regex)) break;

            regex = "([0-9]+) ?시 ?간? ?만? ?(후|뒤|있다가)";
            if (extract2(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9])? 분";
            // if(extract15(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시 ?반";
            //if(extract16(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일 ?([1-2]?[0-9]) ?시";
            //if(extract17(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시 ?([1-5]?[0-9])분";
            if (extract3(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시 ?반";
            if (extract4(searchTarget, regex)) break;

            regex = "([1-2]?[0-9]) ?시";
            if (extract5(searchTarget, regex)) break;

            regex = "([0-9]+) ?분 ?(후|뒤|있다가)";
            if (extract6(searchTarget, regex)) break;

            regex = "(^| )([0-9]+)주 ?(후|뒤|있다가)";
            if (extract7(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일";
            if (extract8(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월";
            if (extract9(searchTarget, regex)) break;

            regex = "(^| )([0-9]+) ?일 ?(후|뒤|있다가)";
            if (extract11(searchTarget, regex)) break;

            regex = "([1-3]?[0-9]) ?일";
            if (extract10(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요일";
            if (extract13(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요일";
            if (extract14(searchTarget, regex)) break;

            regex = "(다? ?다음 ?주)";
            if (extract12(searchTarget, regex)) break;
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.replaceAll(" ", "").split("시간|분");
            addTime(0, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
            System.out.println("what ? " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시간");
            addTime(0, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);
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
        while (matcher.find()) {
            System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시|분");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("분");
            addTime(0, 0, Integer.parseInt(temp[0].replaceAll(" ", "")));
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("주");
            addTime(7 * Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0);
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월|일");
            atTime(curYear, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")), curHour, curMinute);

            if (curMonth >= Integer.parseInt(temp[0].replaceAll(" ", "")) && curDay > Integer.parseInt(temp[1].replaceAll(" ", "")))
                calYear = curYear + 1; // 이전을 얘기한다면
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월");
            atTime(curYear, Integer.parseInt(temp[0].replaceAll(" ", "")), 1, 0, 0); //그 달의 1일 0시 0분
            if (curMonth >= Integer.parseInt(temp[0].replaceAll(" ", "")))
                calYear = curYear + 1; // 이전을 얘기한다면
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            atTime(curYear, curMonth, Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0); //그 달, 그 일의 0시 0분
            if (curDay >= Integer.parseInt(temp[0].replaceAll(" ", "")))
                calMonth = curMonth + 1; // 이전을 얘기한다면
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
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            addTime(Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0);
            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public int extract100(String searchTarget, String regex) { //[내일|낼|명일|모레|글피|익일|명일]+
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
//       boolean isExtracted = false;
        int maxi = 0;
        while (matcher.find()) {
            String match = matcher.group(0);
            Integer num = 0;
            num = hMap.get(match);
            if (num == null) num = 0;

            if (maxi < num) maxi = num;
            //addTime(maxi, 0, 0); - addTime은 curTime에 더하는 것이므로
            //여기서 더해봤자 아무의미가 마지막에 추가적으로 더해주자.
//          isExtracted = true;
            //System.out.println(matcher.group(0));
        }
//       return isExtracted;
        return maxi;
    }

    public boolean extract12(String searchTarget, String regex) { //다음주, 다다음주
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            //System.out.println("extract12 : " + result);
            addTime(hMap.get(result) * 7, 0, 0);

        }
        return isExtracted;
    }

    public boolean extract13(String searchTarget, String regex) { //다음주, 다다음주 월,화~일요일
        //System.out.println("ddd " + searchTarget);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        //System.out.println("dddd");
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            //System.out.println("요일 "+result);
            if (result.length() == 7) {
                week = result.substring(0, 4); //다다음주
                dayofweek = result.substring(4); //월~일요일
            } else if (result.length() == 6) {
                week = result.substring(0, 3); //다음주
                dayofweek = result.substring(3); //월~일요일
            }

            // System.out.println("주: " + week + " 요일 : " + dayofweek);
            int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
            addTime(calweekday, 0, 0);
        }
        return isExtracted;
    }

    public boolean extract14(String searchTarget, String regex) { //일요일,월요일. 혹시 현재 화요일인데 월요일이라고하면 다음주가됨
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        // System.out.println("요일을 하고 있다");
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            if (wMap.get(curDayOfWeek) < wMap.get(result)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(result) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                addTime(calweekday, 0, 0);
            } else if (wMap.get(curDayOfWeek) >= wMap.get(result)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(result) - 1));
                addTime(calweekday, 0, 0);
            }
        }
        return isExtracted;
    }


    public boolean extract15(String searchTarget, String regex) { //다/다음주 ~요일 ~시 ~분
        System.out.println("In extract15");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시|분");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));

                System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시|분");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));

                System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract16(String searchTarget, String regex) { //다/다음주 ~요일 ~시 반
        System.out.println("In extract16");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract17(String searchTarget, String regex) { //다/다음주 ~요일 ~시
        System.out.println("In extract17");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract18(String searchTarget, String regex) { //~요일 ~시 ~분
        System.out.println("In extract18");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

                dayofweek = result.substring(0, 3);
                result = result.substring(3);
                temp = result.split("시|분");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
                System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract19(String searchTarget, String regex) { //~요일 ~시 ~반
        System.out.println("In extract19");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            dayofweek = result.substring(0, 3);
            result = result.substring(3);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")),30);
            System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 30);

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract20(String searchTarget, String regex) { //~요일 ~시
        System.out.println("In extract20");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            dayofweek = result.substring(0, 3);
            result = result.substring(3);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")),0);
            System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 0);

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract21(String searchTarget, String regex) { // ~시 ~분 ~요일
        System.out.println("In extract21");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            dayofweek = result.substring(result.length()-3);
            result = result.substring(0,result.length()-3);
            temp = result.split("시|분");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
            System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract22(String searchTarget, String regex) { // ~시 ~반 ~요일
        System.out.println("In extract22");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            dayofweek = result.substring(result.length()-3);
            result = result.substring(0,result.length()-3);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);
            System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 30);

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }

    public boolean extract23(String searchTarget, String regex) { // ~시 ~반 ~요일
        System.out.println("In extract23");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            // System.out.println("In matcher.find()");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");

            dayofweek = result.substring(result.length()-3);
            result = result.substring(0,result.length()-3);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")),0);
            System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " +0);

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                //System.out.println("요일을 하고 있다 " + result );
                calDay += calweekday;
            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

            //System.out.println("test : " +result);


            //System.out.println(matcher.group(0));
        }
        return isExtracted;
    }



    public void addTime(int d, int h, int m) {
        System.out.println(d + " " + h + " " + m);

        calMinute = curMinute + m;
        calHour = curHour + h;
        calDay = curDay + d;
        calMonth = curMonth;
        calYear = curYear;
        System.out.println("val hey : " + calDay + " " + calHour + " " + calMinute);
        calHour += calMinute / 60;
        calMinute = calMinute % 60;
        calDay += calHour / 24;
        calHour = calHour % 24;

        int day_num = days[curMonth];
        calMonth += calDay / day_num;
        calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
        calYear += calMonth / 12;
        calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
    }


    //calMonth - 김원교수님 뵙기전까지 해야함 일단 6월 27일 5시이후에생각해보자
        /*calMonth += calDay/31;
        calDay
        calYear = curYear*/

    public void atTime(int y, int M, int d, int h, int m) {
        System.out.println("hour : " + h + " " + "minute : " + m);
        calYear = y;
        calMonth = M;
        calDay = d;
        calHour = h;
        calMinute = m;
    }
}