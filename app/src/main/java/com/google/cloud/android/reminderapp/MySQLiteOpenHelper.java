package com.google.cloud.android.reminderapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
/**
 * SQLite 쿼리를 통해 record라는 이름의 table을 만든다.
 * fileName과 alarmTime이라는 것은 text라고 하는 타입으로 지정되었는데
 * 그 이유는 후에 디비 파일을 컴퓨터로 추출해서 볼 때, 지원하는 글자 형식의 타입이
 * 텍스트 타입이기 때문이다.
 * String을 사용하여 insert 와 select를 했을 때 문제가 없었던 걸로 보아 유사한 타입인 것 같다.
 */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table record (fileName text, alarmTime text, text text);";//아이디 지우고  ㅇㅇ 저장할 것 만들고
        db.execSQL(sql);
    }
    /**
     * 위와 마찬가지로 컴퓨터에서 혹시 디비 파일을 수정했을 때
     * 컴퓨터 파일을 반영하기 위해 사용하였다.
 */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "drop table if exists record";
        db.execSQL(sql);
        onCreate(db);
    }
}
