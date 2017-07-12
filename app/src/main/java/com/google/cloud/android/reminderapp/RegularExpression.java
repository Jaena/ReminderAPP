package com.google.cloud.android.reminderapp;

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

    public int curYear, curMonth, curDay, curHour, curMinute; //현재 날짜
    public int calYear, calMonth, calDay, calHour, calMinute; //계산 후 알람 날짜

    String curA = new String(); //curA : 오전 / 오후
    String calA = new String(); //calA : 오전 / 오후

    public String curDayOfWeek;
    public int[] days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    boolean isNextDay; //다음날로 넘어가게 하는 함수인지 확인
    boolean isTime; //시간을 말한 정규식인지 확인

    // 내일|낼|명일|모레|글피|익일|명일
    public RegularExpression() {
        hMap = new HashMap<String, Integer>();
        wMap = new HashMap<String, Integer>();
        hMap.put("오늘", 0);
        hMap.put("내일", 1);
        hMap.put("낼", 1);
        hMap.put("명일", 1);
        hMap.put("익일", 1);
        hMap.put("명일", 1);
        hMap.put("모레", 2);
        hMap.put("글피", 3);
        hMap.put("내일모레", 2);
        hMap.put("낼모레", 2);
        hMap.put("내일모래", 2);
        hMap.put("모래", 2);
        hMap.put("낼모래", 2);

        hMap.put("다음주", 1);
        hMap.put("다다음주", 2);

        hMap.put("다음날", 1);
        hMap.put("담날", 1);
        hMap.put("다다음날", 2);

        wMap.put("일요일", 1);
        wMap.put("월요일", 2);
        wMap.put("화요일", 3);
        wMap.put("수요일", 4);
        wMap.put("목요일", 5);
        wMap.put("금요일", 6);
        wMap.put("토요일", 7);
    }

    public String Analysis(String target) {
        isNextDay = false;
        isTime = false;

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:hh:mm:ss:a");
        String tempTime = sdf.format(date);
        String temp[] = tempTime.split(":");

        //현재 날짜를 초기화해준다.
        curYear = Integer.parseInt(temp[0]);
        curMonth = Integer.parseInt(temp[1]);
        curDay = Integer.parseInt(temp[2]);
        curHour = Integer.parseInt(temp[3]);
        curMinute = Integer.parseInt(temp[4]);
        //curSecond = Integer.parseInt(temp[5]);

        System.out.println("cur : " + curYear + " " + curMonth + " " + curDay + " " + curHour + " " + curMinute + " ");
        curA = temp[6];
        calA = temp[6];

        //현재 요일을 가져온다.
        cal.set(Calendar.YEAR, curYear);
        cal.set(Calendar.MONTH, curMonth);
        cal.set(Calendar.DATE, curDay);

        switch (cal.get(Calendar.DAY_OF_WEEK)-1) {
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
            case 0:
                curDayOfWeek = "토요일";
                break;
        }

        System.out.println("cur : " + curDayOfWeek);

        //24시간 기준으로 오전과 오후를 나눈다
        if (curA.equals("오전") && curHour == 12) {
            curHour = 0; //0시
        }
        if (curA.equals("오후")) {
            curHour += 12;
        }

        //계산값을 지금 현재 날짜로 초기화후
        calYear = curYear;
        calMonth = curMonth;
        calDay = curDay;
        calHour = curHour;
        calMinute = curMinute;

        System.out.println("cur : " + curYear + " " + curMonth + " " + curDay + " " + curHour + " " + curMinute + " ");

        //정규식 표현식에서 계산 값 추출
        extractManager(target);

        String calTime = calYear + "년 " + calMonth + "월 " + calDay + "일 " + calHour + "시 " + calMinute + "분 ";

        //추출한 표현값 리턴
        return calTime;
    }

    public boolean extractManager(String searchTarget) {
        searchTarget = searchTarget.replaceAll(" ", "");
        searchTarget = searchTarget.replaceAll("새벽", "오전");
        searchTarget = searchTarget.replaceAll("아침", "오전");
        searchTarget = searchTarget.replaceAll("저녁", "오후");
        searchTarget = searchTarget.replaceAll("낮", "오후");
        searchTarget = searchTarget.replaceAll("밤1시", "오전1시");
        searchTarget = searchTarget.replaceAll("밤12시", "오전0시");
        searchTarget = searchTarget.replaceAll("밤", "오후");

        searchTarget = searchTarget.replaceAll("삼일", "3일");
        searchTarget = searchTarget.replaceAll("사일", "4일");
        searchTarget = searchTarget.replaceAll("오일", "5일");

        searchTarget = searchTarget.replaceAll("세시", "3시");
        searchTarget = searchTarget.replaceAll("네시", "4시");
        searchTarget = searchTarget.replaceAll("메시", "4시");
        searchTarget = searchTarget.replaceAll("다섯시", "5시");

        searchTarget = searchTarget.replaceAll("하루", "1");
        searchTarget = searchTarget.replaceAll("이틀", "2");
        searchTarget = searchTarget.replaceAll("사흘", "3");
        searchTarget = searchTarget.replaceAll("나흘", "4");
        searchTarget = searchTarget.replaceAll("닷새", "5");
        searchTarget = searchTarget.replaceAll("엿새", "6");
        searchTarget = searchTarget.replaceAll("이레", "7");

        searchTarget = searchTarget.replaceAll("반시간", "30분");
        searchTarget = searchTarget.replaceAll("자정", "오전0시");
        searchTarget = searchTarget.replaceAll("정오", "오후12시");

        searchTarget = searchTarget.replaceAll("일주일", "1주");
        searchTarget = searchTarget.replaceAll("일날", "일"); //금요일 날, 수요일 날.. 등등
        searchTarget = searchTarget.replaceAll("담날", "다음날");
        searchTarget = searchTarget.replaceAll("담주", "다음주");

        searchTarget = searchTarget.replaceAll("있따가", "있다가");
        searchTarget = searchTarget.replaceAll("이따가", "있다가");
        searchTarget = searchTarget.replaceAll("이다가", "있다가");
        searchTarget = searchTarget.replaceAll("이따", "있다가");

        searchTarget = searchTarget.replaceAll("일주", "1주");
        searchTarget = searchTarget.replaceAll("이주", "2주");
        searchTarget = searchTarget.replaceAll("삼주", "3주");
        searchTarget = searchTarget.replaceAll("사주", "4주");
        searchTarget = searchTarget.replaceAll("오주", "5주");

        String regex = new String();
        while (true) {

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분"; // 다다음주 월요일 오전/오후 1시 30분
            if (extract15(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반"; // 다다음주 월요일 오전/오후 1시 반
            if (extract16(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시"; //다다음주 월요일 1시
            if (extract17(searchTarget, regex)) break;

            //잘 안쓸 것 같은 정규식
            //regex = "다? ?다음 ?주 ?(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분"; // 다다음주 ~월 ~일 오전/오후/1시 30분
            //if (extract24(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분"; //월요일 오전/오후 1시 30분
            if (extract18(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반"; //월요일 오전/오후 1시 반
            if (extract19(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요 ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시"; // 월요일 오전/오후 1시
            if (extract20(searchTarget, regex)) break;

            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분 ?(월|화|수|목|금|토|일) ?요 ?일"; //오전/오후 1시 30분 월요일
            if (extract21(searchTarget, regex)) break;

            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반 ?(월|화|수|목|금|토|일) ?요 ?일"; //오전/오후 1시 반 월요일
            if (extract22(searchTarget, regex)) break;

            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?(월|화|수|목|금|토|일) ?요 ?일"; //오전/오후 1시 월요일
            if (extract23(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분"; // 7월 5일 오전/오후 1시 30분
            if (extract24(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반"; // 7월 5일 오전/오후 1시 반
            if (extract28(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시"; // 7월 5일 오전/오후 1시
            //regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일 ?([1-2]?[0-9]) ?시";
            if (extract25(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월 ?([1-3]?[0-9]) ?일"; // 7월 4일
            if (extract8(searchTarget, regex)) break;

            regex = "(1?[0-9]) ?월"; // 8월
            if (extract9(searchTarget, regex)) break;

            regex = "([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9]) ?분"; // 7일 오전/오후 1시 30분
            if (extract26(searchTarget, regex)) break;

            regex = "([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반";  //7일 오전/오후 1시 반
            if (extract29(searchTarget, regex)) break;

            regex = "([1-3]?[0-9]) ?일 ?(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시"; //7일 오전/오후 1시
            if (extract27(searchTarget, regex)) break;

            regex = "([0-9]+) ?시간 ?([0-9]+) ?분 ?(후|뒤|있다가)"; // 5시간 30분 있다가 알려줘
            if (extract1(searchTarget, regex)) break;

            regex = "([0-9]+) ?시간 ?반 ?(후|뒤|있다가)"; // 5시간 반 있다가 알려줘
            if (extract30(searchTarget, regex)) break;

            regex = "([0-9]+) ?시 ?간? ?만? ?(후|뒤|있다가)"; // 5시간 있다가 알려줘
            if (extract2(searchTarget, regex)) {
                return true; //break;
            }

            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?([1-5]?[0-9])분"; //오전/오후 1시 30분
            if (extract3(searchTarget, regex)) {
                break;
            }

            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시 ?반"; //오전 오후 1시 반
            if (extract4(searchTarget, regex)) {
                break;
            }
            regex = "(오 ?전|오 ?후)? ?([1-2]?[0-9]) ?시"; //오전 오후 1시
            if (extract5(searchTarget, regex)) {
                break;
            }
            regex = "([0-9]+) ?분 ?(후|뒤|있다가)"; // 5분뒤에
            if (extract6(searchTarget, regex)) {
                return true; //break;
            }

            regex = "(^| )([0-9]+) ?주 ?(후|뒤|있다가)"; //3주뒤에
            if (extract7(searchTarget, regex)) {
                return true; //break;
            }

            regex = "(^| )([0-9]+) ?일 ?(후|뒤|있다가)"; // 5일 뒤에
            if (extract11(searchTarget, regex)) {
                return true; //break;
            }

            regex = "([1-3]?[0-9]) ?일"; // 3일 미팅
            if (extract10(searchTarget, regex)) break;

            regex = "다? ?다음 ?주 ?(월|화|수|목|금|토|일) ?요 ?일"; //다음주 토요일
            if (extract13(searchTarget, regex)) break;

            regex = "(월|화|수|목|금|토|일) ?요 ?일"; //토요일
            if (extract14(searchTarget, regex)) break;

            regex = "(다? ?다음 ?주)"; //다음주
            if (extract12(searchTarget, regex)) break;

            break;
        }

        regex = "[다다음날|다음날|내일|낼|명일|모레|내일모레|내일모래|낼모레|낼모래|모래|글피|익일|명일]+";
        int ret = extract100(searchTarget, regex);
        if (ret > 0) {
            //System.out.println("DDDD" + isNextDay);
            isNextDay = true;
            calDay += ret;
            int day_num = days[calMonth];
            calMonth += calDay == day_num ? 0 : calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;

            if (!isTime) {
                calHour = 8;
                calMinute = 0;
            }
        }

        //오늘이라고 말할 경우
        regex = "(오늘)+";
        extract102(searchTarget, regex);

        //오전 오후를 식별해주는 정규식
        regex = "오전|오후";
        extract101(searchTarget, regex);

        return true;
    }

    public boolean extract24(String searchTarget, String regex) { //월 일 시 분
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract24");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("월|일|시|분");

            // 시간표현 초기화
            calMonth = Integer.parseInt(temp[0]);
            calDay = Integer.parseInt(temp[1]);
            calHour = Integer.parseInt(temp[2]);
            calMinute = Integer.parseInt(temp[3]);

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curMonth * 43200 + curDay * 1440 + curHour * 60 + curMinute >= calMonth * 43200 + calDay * 1440 + calHour * 60 + calMinute)
                atTime(curYear + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
            else
                atTime(curYear, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
        }
        return isExtracted;
    }

    public boolean extract25(String searchTarget, String regex) { //월 일 시
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract25");

            isNextDay = true;
            isExtracted = true;
            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("월|일|시");

            // 시간표현 초기화
            calMonth = Integer.parseInt(temp[0]);
            calDay = Integer.parseInt(temp[1]);
            calHour = Integer.parseInt(temp[2]);

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curMonth * 720 + curDay * 24 + curHour >= calMonth * 720 + calDay * 24 + calHour)
                atTime(curYear + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), 0);
            else
                atTime(curYear, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), 0);
        }

        return isExtracted;
    }

    public boolean extract26(String searchTarget, String regex) { //일 시 분
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract26");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("일|시|분");

            // 시간표현 초기화
            calDay = Integer.parseInt(temp[0]);
            calHour = Integer.parseInt(temp[1]);
            calMinute = Integer.parseInt(temp[2]);

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curDay * 1440 + curHour * 60 + curMinute >= calDay * 1440 + calHour * 60 + calMinute)
                atTime(curYear, curMonth + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
            else
                atTime(curYear, curMonth, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
        }

        return isExtracted;
    }

    public boolean extract27(String searchTarget, String regex) { //일 시
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract27");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("일|시");
            System.out.println("temp : " + temp[0] + temp[1]);

            // 시간표현 초기화
            calDay = Integer.parseInt(temp[0]);
            calHour = Integer.parseInt(temp[1]);

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curDay * 24 + curHour >= calDay * 24 + calHour)
                atTime(curYear, curMonth + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 0);
            else
                atTime(curYear, curMonth, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 0);
        }

        return isExtracted;
    }

    public boolean extract28(String searchTarget, String regex) { //월 일 시 반
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract28");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("월|일|시");

            // 시간표현 초기화
            calMonth = Integer.parseInt(temp[0]);
            calDay = Integer.parseInt(temp[1]);
            calHour = Integer.parseInt(temp[2]);
            calMinute = 30;

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curMonth * 43200 + curDay * 1440 + curHour * 60 + curMinute >= calMonth * 43200 + calDay * 1440 + calHour * 60 + calMinute)
                atTime(curYear + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), 30);
            else
                atTime(curYear, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), 30);
        }

        return isExtracted;
    }

    public boolean extract29(String searchTarget, String regex) { //일 시 반
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract29");

            isNextDay = true;
            isExtracted = true;
            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.replaceAll(" ", "").split("일|시");

            // 시간표현 초기화
            calMinute = curMonth;
            calDay = Integer.parseInt(temp[0]);
            calHour = Integer.parseInt(temp[1]);
            calMinute = 30;

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (curMonth * 43200 + curDay * 1440 + curHour * 60 + curMinute >= calMonth * 43200 + calDay * 1440 + calHour * 60 + calMinute)
                atTime(curYear, calMonth + 1, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 30);
            else
                atTime(curYear, calMonth, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 30);
        }

        return isExtracted;
    }

    public boolean extract1(String searchTarget, String regex) { //~시간 ~분 후|뒤|있다가
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract1");
            isExtracted = true;
            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            temp = result.replaceAll(" ", "").split("시간|분");

            addTime(0, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
            //System.out.println("test : " + calHour + " " + calMinute);

            if(calHour < 12)
                calA = "오전";
        }

        return isExtracted;
    }


    public boolean extract30(String searchTarget, String regex) { //~시간 ~반 후|뒤|있다가
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract30");
            isExtracted = true;
            result = matcher.group(0);
//            temp = result.trim().split("시간|분");
//            addTime(0, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            temp = result.replaceAll(" ", "").split("시간");
            addTime(0, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);

            if(calHour < 12)
                calA = "오전";
        }

        return isExtracted;
    }


    public boolean extract2(String searchTarget, String regex) { //~시간 후
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract2");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("시간");
            //addTime(0, Integer.parseInt(temp[0].trim()), 0);
            addTime(0, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);

            if(calHour < 12)
                calA = "오전";
        }
        return isExtracted;
    }

    public boolean extract3(String searchTarget, String regex) { //~시 ~분
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract3");
            isExtracted = true;
            isTime = true;
            result = matcher.group(0);
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.split("시|분");

            //atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
        }
        return isExtracted;
    }

    public boolean extract4(String searchTarget, String regex) { //~시 반
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract4");
            isExtracted = true;
            isTime = true;
            result = matcher.group(0);
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.split("시");
            //atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), 30);
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);
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
            System.out.println("extract5");
            isExtracted = true;
            isTime = true;
            result = matcher.group(0);
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");
            temp = result.split("시");
            // atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].trim()), 0);
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);
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
            System.out.println("extract6");
            isExtracted = true;
            isTime = true;
            result = matcher.group(0);
            temp = result.split("분");
            //addTime(0, 0, Integer.parseInt(temp[0].trim()));
            addTime(0, 0, Integer.parseInt(temp[0].replaceAll(" ", "")));
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
            System.out.println("extract7");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("주");
            // addTime(7*Integer.parseInt(temp[0].trim()), 0, 0);
            addTime(7 * Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0);
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
            System.out.println("extract8");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월|일");

            // 시간표현 초기화
            calMonth = Integer.parseInt(temp[0]);
            calDay = Integer.parseInt(temp[1]);
            calHour = 8;
            calMinute = 0;

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            //atTime(curYear, Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()), curHour, curMinute);
            if (curMonth * 720 + curDay * 24 + curHour >= calMonth * 720 + calDay * 24 + calHour)
                calYear = curYear + 1; // 이전을 얘기한다면
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
            System.out.println("extract9");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("월");
            // atTime(curYear, Integer.parseInt(temp[0].trim()), 1, 0, 0); //그 달의 1일 0시 0분
            atTime(curYear, Integer.parseInt(temp[0].replaceAll(" ", "")), 1, 0, 0); //그 달의 1일 0시 0분
            if (curMonth >= Integer.parseInt(temp[0].replaceAll(" ", "")))
                calYear = curYear + 1; // 이전을 얘기한다면

            calHour = 8;
            calMinute = 0; //working time의 초기 시간으로 설정
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
            System.out.println("extract10");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            //atTime(curYear, curMonth, Integer.parseInt(temp[0].trim()), 0, 0); //그 달, 그 일의 0시 0분
            atTime(curYear, curMonth, Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0); //그 달, 그 일의 0시 0분
            if (curDay >= Integer.parseInt(temp[0].replaceAll(" ", "")))
                calMonth = curMonth + 1; // 이전을 얘기한다면

            calHour = 8;
            calMinute = 0; //working time의 초기 시간으로 설정
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
            System.out.println("extract11");
            isExtracted = true;
            result = matcher.group(0);
            temp = result.split("일");
            //addTime(Integer.parseInt(temp[0].trim()), 0, 0);
            addTime(Integer.parseInt(temp[0].replaceAll(" ", "")), 0, 0);
        }
        return isExtracted;
    }

    public int extract100(String searchTarget, String regex) { //[오늘|다음날|다다음날|다다음날|내일|낼|명일|모레|글피|익일|명일]+
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        int maxi = 0;
        while (matcher.find()) {
            System.out.println("extract100");
            String match = matcher.group(0);
            Integer num = 0;
            num = hMap.get(match);
            if (num == null) num = 0;

            if (maxi < num) maxi = num;
        }
        return maxi;
    }

    public boolean extract12(String searchTarget, String regex) { //다음주, 다다음주
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;
        String result = "";
        String[] temp = new String[4];
        while (matcher.find()) {
            System.out.println("extract12");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            addTime(hMap.get(result) * 7, 0, 0);
            calHour = 8;
            calMinute = 0; //working time의 초기 시간으로 설정
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
            System.out.println("extract13");
            isNextDay = true;
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

            System.out.println("주: " + week + " 요일 : " + dayofweek + " 더해야할 일수 : ");
            int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
            calDay += calweekday;

            calHour = 8;
            calMinute = 0; //working time의 초기 시간으로 설정
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
            isNextDay = true;
            System.out.println("extract14");
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            String dayofweek = result;

            calHour = 8;
            calMinute = 0; //working time의 초기 시간으로 설정

            if (wMap.get(curDayOfWeek) < wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                System.out.println("요일을 하고 있다 " + result );
                //System.out.println("test : " + calweekday + " " + curHour + " " + calHour);
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                System.out.println("test22 : " + calweekday + " " + curHour + " " + calHour);
                calDay += calweekday;
            }

        }
        return isExtracted;
    }


    public boolean extract101(String searchTarget, String regex) { //오전, 오후
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";

        while (matcher.find()) { //같은 형식의 시간표현이 여러개인 경우 가장 마지막 시간 표현 사용
            System.out.println("extract101");
            isExtracted = true;
            result = matcher.group(0);
        }

        if (result.equals("오후")) { //오후
            calA = "오후";
            if (calHour < 12)
                calHour += 12;

            if (calHour * 60 + calMinute < curHour * 60 + curMinute && !isNextDay) {
                System.out.println("IS NEXT DAY : " + isNextDay);
                calDay += 1;
                int day_num = days[calMonth];
                calMonth += calDay / day_num;
                calDay = calDay % day_num == 0 ? day_num : calDay % day_num;

                calYear += calMonth / 12;
                calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
            }
        } else if (result.equals("오전") || calA.equals("오전")) { //오전
            calA = "오전";
            if (calHour * 60 + calMinute < curHour * 60 + curMinute && !isNextDay) {
                System.out.println("IS NEXT DAY : " + isNextDay);
                calDay += 1;
                int day_num = days[calMonth];
                calMonth += calDay / day_num;
                calDay = calDay % day_num == 0 ? day_num : calDay % day_num;

                calYear += calMonth / 12;
                calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
            }
        } else { //오전, 오후가 입력되지 않았다면, working time(8am - 8pm, 8 - 20) 범위내에서 처리
            System.out.println("extract200");
            if (calHour < 12) { //12이상이면 오후로 정해진 것. 1 ~ 11은 오전/오후 둘 다 가능
                calA = "오전";
                if (calHour < 8) { //0 ~ 7
                    calHour += 12;
                    calA = "오후";


                }
            }

            System.out.println("test : " + calYear + " " + calMonth + " " + calDay + " " + calHour + " " + calMinute + !isNextDay);
            System.out.println("test : " + curYear + " " + curMonth + " " + curDay + " " + curHour + " " + curMinute + !isNextDay);

            if (calHour * 60 + calMinute <= curHour * 60 + curMinute && !isNextDay && (calYear * 365 + calMonth * 30 + calDay) <= (curYear * 365 + curMonth * 30 + curDay)) {
                System.out.println("extract201 : " + calMonth + " " + calDay + " " + calHour + " " + calMinute);
                System.out.println("extract201");
                System.out.println("IS NEXT DAY : " + isNextDay);
                calDay += 1;
                int day_num = days[calMonth];
                calMonth += calDay == day_num ? 0 : calDay / day_num;
                calDay = calDay % day_num == 0 ? day_num : calDay % day_num;

                calYear += calMonth / 12;
                calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
            }
        }

        return isExtracted;
    }

    public boolean extract15(String searchTarget, String regex) { //다/다음주 ~요일 오전/오후 ~요일 ~시 ~분

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";

        String[] temp = new String[4];

        while (matcher.find()) {
            isExtracted = true;
            isNextDay = true;
            System.out.println("extract15");

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시|분");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));

                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                //System.out.println(dayofweek + " " + wMap.get(dayofweek) + " " + curDayOfWeek + " " + wMap.get(curDayOfWeek) + "calweekday :" + calweekday);
                calDay += calweekday;
            } else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시|분");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));

                System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

        }
        return isExtracted;
    }

    public boolean extract16(String searchTarget, String regex) { //다/다음주 ~요일 ~시 반
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            isExtracted = true;
            isNextDay = true;
            System.out.println("In extract16");

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            } else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
        }
        return isExtracted;
    }

    public boolean extract17(String searchTarget, String regex) { //다/다음주 ~요일 ~시

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("In extract17");
            // System.out.println("In matcher.find()");
            isNextDay = true;
            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            if (result.substring(0, 3).equals("다다음")) {
                week = result.substring(0, 4);
                dayofweek = result.substring(4, 7);
                result = result.substring(7);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            } else if (result.substring(0, 3).equals("다음주")) {
                week = result.substring(0, 3);
                dayofweek = result.substring(3, 6);
                result = result.substring(6);

                temp = result.split("시");
                atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);

                //System.out.println("test : "+ Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (hMap.get(week) * 7 + wMap.get(dayofweek) - 1));
                int calweekday = hMap.get(week) * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
        }
        return isExtracted;
    }

    public boolean extract18(String searchTarget, String regex) { //~요일 ~시 ~분

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("In extract18");
            // System.out.println("In matcher.find()");
            isExtracted = true;

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(0, 3);
            result = result.substring(3);
            temp = result.split("시|분");

            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour * 60 + curMinute <= calHour * 60 + calMinute) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                //System.out.println("요일을 하고 있다 " + result );
                System.out.println("test : " + calweekday + " " + curHour + " " + calHour);
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                //System.out.println("test : " + calweekday + " " + curHour + " " + calHour);
                calDay += calweekday;
            }

            //System.out.println("test333 : " +  curHour + " " + calHour);
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

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {

            isNextDay = true;
            System.out.println("extract19");
            // System.out.println("In matcher.find()");

            isExtracted = true;
            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(0, 3);
            result = result.substring(3);
            temp = result.split("시");

            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 30);

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour <= calHour) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
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

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract20");
            // System.out.println("In matcher.find()");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(0, 3);
            result = result.substring(3);
            temp = result.split("시");

            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 0);

            //시간바꿔주기 위한것
            regex = "오전|오후";
            extract101(searchTarget, regex);

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour <= calHour) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
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

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract21");
            // System.out.println("In matcher.find()");
            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(result.length() - 3);
            result = result.substring(0, result.length() - 3);

            temp = result.split("시|분");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), Integer.parseInt(temp[1].replaceAll(" ", "")));
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + Integer.parseInt(temp[1].replaceAll(" ", "")));

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour * 60 + curMinute <= calHour * 60 + calMinute) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
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

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;

        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract22");
            // System.out.println("In matcher.find()");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(result.length() - 3);
            result = result.substring(0, result.length() - 3);
            temp = result.split("시");

            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 30);
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 30);

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour <= calHour) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;
            }

            int day_num = days[curMonth];
            calMonth += calDay / day_num;
            calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
            calYear += calMonth / 12;
            calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;
        }
        return isExtracted;
    }

    public boolean extract23(String searchTarget, String regex) { // ~시 ~요일

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        boolean isExtracted = false;
        String result = "";
        String week = "";
        String dayofweek = "";
        String[] temp = new String[4];

        while (matcher.find()) {
            System.out.println("extract23");
            // System.out.println("In matcher.find()");

            isNextDay = true;
            isExtracted = true;

            result = matcher.group(0).replaceAll(" ", "");
            result = result.replaceAll("오전", "");
            result = result.replaceAll("오후", "");

            dayofweek = result.substring(result.length() - 3);
            result = result.substring(0, result.length() - 3);
            temp = result.split("시");
            atTime(curYear, curMonth, curDay, Integer.parseInt(temp[0].replaceAll(" ", "")), 0);
            System.out.println("test : " + Integer.parseInt(temp[0].replaceAll(" ", "")) + " " + 0);

            if (wMap.get(curDayOfWeek) <= wMap.get(dayofweek) && curHour <= calHour) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (0 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 0 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
                calDay += calweekday;

            } else if (wMap.get(curDayOfWeek) >= wMap.get(dayofweek)) {
                //int calweekday = (-1 * (wMap.get(curDayOfWeek) - 1) + (1 * 7 + wMap.get(dayofweek) - 1));
                int calweekday = 1 * 7 + (wMap.get(dayofweek) - (wMap.get(curDayOfWeek)));
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

    public boolean extract102(String searchTarget, String regex) { //오늘

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);

        while (matcher.find()) {
            System.out.println("extract102");
            // System.out.println("In matcher.find()");
            isNextDay = true;
        }
        return true;
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
        calMonth += calDay == day_num ? 0 : calDay / day_num;
        calDay = calDay % day_num == 0 ? day_num : calDay % day_num;
        calYear += calMonth / 12;
        calMonth = calMonth % 12 == 0 ? 12 : calMonth % 12;

        if (calHour >= 12) calA = "오후";
    }

    public void atTime(int y, int M, int d, int h, int m) {
        System.out.println("hour : " + h + " " + "minute : " + m);
        calYear = y;
        calMonth = M;
        calDay = d;
        calHour = h;
        calMinute = m;

        if (calHour >= 12) calA = "오후";
    }
}