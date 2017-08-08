package com.google.cloud.android.reminderapp;

/**
 * Created by 박재성 on 2017-08-02.
 */

public class ContentAnalysis {

    public String Analysis(String target) {

        String contentValue = "";
        String regex ="";
        System.out.println("target : " + target);
        contentValue = extractManager(target);

        //정규식
        contentValue = contentValue.replaceAll("([0-9]?[0-9])년(까지|날까지|날|에|뒤에|후에|후|뒤|쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])월(까지|날까지|달|달에|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])일(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])시반(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])시간(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])시(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])분(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])초(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("([0-9]?[0-9])주(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");

        contentValue = contentValue.replaceAll("(월|화|수|목|금|토|일)요일(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("(다?다음주)(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");
        contentValue = contentValue.replaceAll("(오전쯤(에)?|오후쯤(에)?)","");
        contentValue = contentValue.replaceAll("(오전에|오후에)","");
        contentValue = contentValue.replaceAll("(오전|오후)","");
        contentValue = contentValue.replaceAll("(다다음날|다음날|내일|내일모레|내일모레글피|오늘|자정|정오|명일|명월|금일|작일|모레|낼모레|익일)(까지|날까지|날|에|뒤에|후에|후|뒤쯤에|쯤)?","");

        return contentValue;
    }
        public String extractManager(String searchTarget) {

            searchTarget = searchTarget.replaceAll(" ", "");
            searchTarget = searchTarget.replaceAll("한시", "1시");
            searchTarget = searchTarget.replaceAll("두시", "2시");
            searchTarget = searchTarget.replaceAll("세시", "3시");
            searchTarget = searchTarget.replaceAll("네시", "4시");
            searchTarget = searchTarget.replaceAll("다섯시", "5시");
            searchTarget = searchTarget.replaceAll("여섯시", "6시");
            searchTarget = searchTarget.replaceAll("일곱시", "7시");
            searchTarget = searchTarget.replaceAll("여덟시", "8시");
            searchTarget = searchTarget.replaceAll("아홉시", "9시");
            searchTarget = searchTarget.replaceAll("열시", "10시");
            searchTarget = searchTarget.replaceAll("열한시", "11시");
            searchTarget = searchTarget.replaceAll("열두시", "12시");

            searchTarget = searchTarget.replaceAll("새벽((([0-9])?[0-9])시)(([0-9])?[0-9]분)?(반)?", "오전");
            searchTarget = searchTarget.replaceAll("아침((([0-9])?[0-9])시)(([0-9])?[0-9]분)?(반)?", "오전");
            searchTarget = searchTarget.replaceAll("저녁((([0-9])?[0-9])시)(([0-9])?[0-9]분)?(반)?", "오후");
            searchTarget = searchTarget.replaceAll("낮((([0-9])?[0-9])시)(([0-9])?[0-9]분)?(반)?", "오후");
            searchTarget = searchTarget.replaceAll("밤1시", "오전1시");
            searchTarget = searchTarget.replaceAll("밤12시", "오전0시");
            searchTarget = searchTarget.replaceAll("밤((([0-9])?[0-9])시)(([0-9])?[0-9]분)?(반)?", "오후");

            searchTarget = searchTarget.replaceAll("삼일", "3일");
            searchTarget = searchTarget.replaceAll("사일", "4일");
            searchTarget = searchTarget.replaceAll("오일", "5일");


            searchTarget = searchTarget.replaceAll("넷이", "4시");
            searchTarget = searchTarget.replaceAll("메시", "4시");

            searchTarget = searchTarget.replaceAll("하루", "1일");
            searchTarget = searchTarget.replaceAll("이틀", "2일");
            searchTarget = searchTarget.replaceAll("사흘", "3일");
            searchTarget = searchTarget.replaceAll("나흘", "4일");
            searchTarget = searchTarget.replaceAll("닷새", "5일");
            searchTarget = searchTarget.replaceAll("엿새", "6일");
            searchTarget = searchTarget.replaceAll("이레", "7일");

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
            searchTarget = searchTarget.replaceAll("있따", "있다가");

            searchTarget = searchTarget.replaceAll("일주", "1주");
            searchTarget = searchTarget.replaceAll("이주", "2주");
            searchTarget = searchTarget.replaceAll("삼주", "3주");
            searchTarget = searchTarget.replaceAll("사주", "4주");
            searchTarget = searchTarget.replaceAll("오주", "5주");
            searchTarget = searchTarget.replaceAll("이번주(까지|에|날)?", "");

            return searchTarget;
    }
}
