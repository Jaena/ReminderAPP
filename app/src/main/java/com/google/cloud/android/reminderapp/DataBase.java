package com.google.cloud.android.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBase {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    String dbName = "record.db";
    String tableName = "record";


    public DataBase(Context c)
    {
        helper = new MySQLiteOpenHelper(c, dbName, null, 1);
    }

    /**
     * VoiceRecorder에서 파일 이름을 인자로 받아 디비에 저장하기 위해 사용
     *
     * @param fileName    알람이 생성된 시간을 가지고 있는 파일의 이름이다.  yy-MM-dd hh:mm:ss의 형식을 사용하였다.
     */
    public void insert(String fileName) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fileName", fileName);
        db.insert(tableName, null, values);
    }

    //fileName과 alarmTime, 인식된 text를 같이 insert하는 함수.
    public void insert(String fileName, String alarmTime,String text) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fileName", fileName);
        values.put("alarmTime", alarmTime);
        values.put("text",text);
        db.insert(tableName, null, values);
    }


//    /**
//     * 녹음 파일의 이름을 가지고 알람 시간을 데이터베이스에 업데이트 시켜준다.
//     * MainActivity 등에서 호출된다.
//     *
//     * @param fileName    알람이 생성된 시간을 가지고 있는 파일의 이름이다. yy-MM-dd hh:mm:ss. 몇 분 뒤 알람해줘 등과 같은 알람일 경우 현재 시간을 파악하기 위해 사용한다.
//     * @param alarmTime   알람이 울릴 시간으로 String으로 저장된다.  yy:MM:dd:hh:mm의 형식으로 저장된다.
//     * @param text 음성 입력한 text가 저장된다.
//     */
//    public void update (String fileName, String alarmTime, String text) {
//        db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("alarmTime", alarmTime);
//        values2.put("text",text);
//        db.update(tableName, values, "fileName=?", new String[]{fileName});
//    }

    /**
     * 아직 사용되진 않았지만 후에 파일을 지울 때 사용할 예정이다.
     * file의 이름을 인자로 받아 디비에서 검색한 뒤, 일치하는 것이 있을 경우 지운다
     *
     *@TODO 일치하는 것이 없을 때의 처리 안함
     * @param fileName    file's name. Format is yy-MM-dd hh:mm:ss.
     */
    public void delete (String fileName) {
        db = helper.getWritableDatabase();
        db.delete(tableName, "fileName=?", new String[]{fileName});
        Log.i("db1", fileName + "정상적으로 삭제 되었습니다.");
    }

//    //연 월 일 시 분 초 각각 받는 함수 만들고, 일단 예비용 연도 반환 함수
//    public int getYear(String attName)//Attribute Name
//    {
//        db = helper.getReadableDatabase();
//        String SQL ="SELECT "+attName+" FROM "+tableName+";";
//        Cursor c = db.rawQuery(SQL,null);
//        String date="";
//        while (c.moveToNext()) {
//            date = c.getString(0);
//        }
//        String temp[] = date.split("-",1);
//        int year = Integer.parseInt(temp[0]);
//        return year;
//    }//쓰면 안됨, where이 구체적으로 안잡힘

    /**
     *  녹음 파일을 재생할 때, 파일의 이름을 가지고 찾으므로 모든 파일의 이름을 불러오는 역할을 한다.
     *
     * @return 파일의 이름이 String이므로 String[]를 리턴한다.
     */

    public String[] getAllFileName(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT fileName FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
        }
        return temp;
    }

    /**
     * 가장 끝의 파일, 즉 최신파일을 가져오기 위해 사용된다.
     * VoiceRecorder에서 파일을 저장한뒤, 메인에서 구글 STT 서버로 보내기 위해서는 가장 최신 파일이 필요한데,
     * 이를 인자로 넘겨주지 않고 디비에 저장된 가장 끝 파일의 이름을 가져온 뒤 보내도록 했다.
     * 기기는 구글 STT서버에서 받은 텍스트를 정규식을 통해 분석할 때 까지 녹음을 하지 않으므로 문제가 없다.
     *
     * @return 가장 끝의 파일 이름 String을 리턴한다.
     */

    public String getLastFileName(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT fileName FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
        }
        return temp[num-1];
    }

    public String getLastText(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT text FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
        }
        return temp[num-1];
    }

    public String getLastAlarmText(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT alarmTime FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
        }
        return temp[num-1];
    }
    public String[] getAllAlarmTime(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT alarmTime FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
            System.out.println("test : " + temp[i]);
        }
        return temp;
    }

    public String[] getAllContent(){
        db = helper.getReadableDatabase();
        String SQL ="SELECT text FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        String temp [] = new String[num];
        for(int i=0;i<num;i++){
            c.moveToNext();
            temp[i] = c.getString(0);
            System.out.println("test : " + temp[i]);
        }
        return temp;
    }

    public int getAllPlayListNum()
    {
        db = helper.getReadableDatabase();
        String SQL ="SELECT alarmTime FROM "+tableName+";";
        Cursor c = db.rawQuery(SQL,null);
        int num = c.getCount();
        return num;
    }
}
