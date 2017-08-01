package mydog.haley.com.walktime.db;

import android.provider.BaseColumns;

/**
 * Diary DataBase 생성
 */

public class DiaryDataBase {

    // 데이터베이스 호출에 사용할 생성자
    public static final class DiaryDB implements BaseColumns {

        public static final String CODE = "code";
        public static final String PHOTO = "photo";
        public static final String CONTENT = "content";
        public static final String TODAY = "today";
        public static final String _DIARYTABLE = "diary";

        public static final String _CREATE = "create table " + _DIARYTABLE
                + CODE + " integer autoincrement,"
                + PHOTO + " blob,"
                + CONTENT + " text,"
                + TODAY + " date default (date('now','localtime'))"
                + ")";
    }

}
