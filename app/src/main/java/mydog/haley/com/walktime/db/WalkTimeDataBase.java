package mydog.haley.com.walktime.db;

import android.provider.BaseColumns;

/**
 * WalkTime 테이블 생성
 */

public class WalkTimeDataBase {

    // 데이터베이스 호출에 사용할 생성자
    public static final class CreateDB implements BaseColumns {

        public static final String CODE = "code";
        public static final String START = "start";
        public static final String END = "end";
        public static final String WALK_TIME = "walk_time";
        public static final String REG_DATE = "reg_date";
        public static final String _TABLENAME = "walktime";

        public static final String _CREATE = "create table " + _TABLENAME + "("
                + CODE + " integer primary key autoincrement,"
                + START + " time,"
                + END + " time,"
                + WALK_TIME + " integer,"
                + REG_DATE + " date default (date('now','localtime'))"
                + ")";

    }


}
