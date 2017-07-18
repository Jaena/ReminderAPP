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
     * Called to add new file's name in database when recording starts.
     *
     * @param fileName    file's name. Format is yy-MM-dd hh:mm:ss.
     */
    public void insert(String fileName) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fileName", fileName);
        db.insert(tableName, null, values);
    }

//    public void insert(String fileName, String alarmTime) {
//        db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("fileName", fileName);
//        values.put("alarmTime", alarmTime);
//        db.insert(tableName, null, values);
//    }

    /**
     * Called to update alarm time in database.
     *
     * @param fileName    file's name. Format is yy-MM-dd hh:mm:ss.
     * @param alarmTime   time to alarm
     */
    public void update (String fileName, String alarmTime) {
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("alarmTime", alarmTime);
        db.update(tableName, values, "fileName=?", new String[]{fileName});
    }

    /**
     * Called to delete file in database.
     *
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
     * Called to get all fime name to play the records
     *
     * @return String[] it is going to use playing records
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
     * Called to get the last file name.
     * Because this program insert file name in database earlyer than finish new recording file so it need to get the last file name.
     *
     * @return String it is use to know newest recording file
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


}
