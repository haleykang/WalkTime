package mydog.haley.com.walktime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mydog.haley.com.walktime.domain.WalkTimeVO;

/**
 * WALKTIME 테이블 - 데이터 베이스 관리 클래스
 */

public class WalkTimeOpenHelper {

    private static final String TAG = "**WalkTimeDBHelper**";

    private static final String DATABASE_NAME = "walktime";

    private static final int DATABASE_VERSION = 1;

    public static SQLiteDatabase mDB;

    private DBHelper mDBHelper;

    private Context mContext;


    // DB Open 헬퍼
    private class DBHelper extends SQLiteOpenHelper {

        // 생성자
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // 최초 DB 생성시 한 번 호출
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            Log.v(TAG, "데이터베이스 생성");

            sqLiteDatabase.execSQL(WalkTimeDataBase.CreateDB._CREATE);

            Log.v(TAG, "walktime 테이블 생성");

        }

        // DATABASE_VERSION 업그레이드 됐을 때 DB & 테이블 재 생성
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            Log.v(TAG, "데이터베이스 업그레이드 " + i + " -> " + i1);
            Log.v(TAG, "walktime 테이블 삭제");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
                    + WalkTimeDataBase.CreateDB._TABLENAME);
            Log.v(TAG, "데이터베이스 onCreate() 재시작");
            onCreate(sqLiteDatabase);

        }

    } // end of DBHelper

    //// 기본 데이터베이스 함수////
    // 1. WalkTimeDBHelper 생성자
    public WalkTimeOpenHelper(Context mContext) {
        this.mContext = mContext;
    }

    // 2. 데이터베이스 open 하는 메소드
    public WalkTimeOpenHelper open() throws SQLException {
        Log.v(TAG, "Open DataBase");
        mDBHelper = new DBHelper(mContext);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    // 3. 데이터베이스 close 메소드
    public void close() {
        Log.v(TAG, "Close DataBase");
        mDB.close();
    }

    //// 실제 사용할 CRUD 함수 ////
    // 1. 새로운 산책 기록 입력 함수
    public long insertTime(WalkTimeVO vo) {
        Log.v(TAG, "insertTime() - 시간 기록 저장");

        // ContentValues에 값 저장 - start/end/walk_time
        ContentValues values = new ContentValues();
        values.put(WalkTimeDataBase.CreateDB.START, vo.getStart());
        values.put(WalkTimeDataBase.CreateDB.END, vo.getEnd());
        values.put(WalkTimeDataBase.CreateDB.WALK_TIME, vo.getWalk_time());

        // 새로운 값 추가 후 결과 반환 -> 아마 영향 받은 모든 행의 숫자?
        return mDB.insert(WalkTimeDataBase.CreateDB._TABLENAME, null, values);
    }

    // 2. 산책 기록 수정 함수 -> 사실 필요 없지만 관리를 위해서 우선 생성
    public boolean updateTime(WalkTimeVO vo) {
        Log.v(TAG, "updateTime() - 시간 기록 수정");

        ContentValues values = new ContentValues();
        values.put(WalkTimeDataBase.CreateDB.START, vo.getStart());
        values.put(WalkTimeDataBase.CreateDB.END, vo.getEnd());
        values.put(WalkTimeDataBase.CreateDB.WALK_TIME, vo.getWalk_time());

        return mDB.update(WalkTimeDataBase.CreateDB._TABLENAME,
                values, "code=" + vo.getCode(), null) > 0;

    }

    // 3. 산책 기록 삭제 - 코드 값 기준
    public boolean deleteTime(int code) {
        Log.v(TAG, "deleteTime() - 시간 기록 삭제");
        Log.v(TAG, "Delete Code : " + code);

        return mDB.delete(WalkTimeDataBase.CreateDB._TABLENAME, "code=" + code, null) > 0;
    }

    // 4. 모든 산책 기록 삭제
    public boolean deleteAll() {
        Log.v(TAG, "deleteAll() - 모든 시간 기록 삭제");
        return mDB.delete(WalkTimeDataBase.CreateDB._TABLENAME, null, null) > 0;
    }

    // 5. 조회 기능
    // 5-1) 모든 데이터 조회
    public Cursor selectAllTime() {
        Log.v(TAG, "selectAllTime() - 모든 시간 기록 조회");
        // 조회 정렬 기준 설정
        String orderBy = WalkTimeDataBase.CreateDB.CODE + " DESC";
        return mDB.query(WalkTimeDataBase.CreateDB._TABLENAME, null, null, null, null, null, orderBy);
    }

    // 5-2) 선택한 하나의 기록만 조회 - 산책 시간 기록에서는 사실 필요없음
    public Cursor selectOneTime(int code) {
        Log.v(TAG, "selectOneTime() - 시간 기록 조회");
        Log.v(TAG, "Select Code : " + code);

        // 코드 기준으로 테이블에서 값을 찾아오기
        Cursor cursor = mDB.query(WalkTimeDataBase.CreateDB._TABLENAME, null,
                "code=" + code, null, null, null, null);

        // 받아온 컬럼이 null이 아니고, 가져온 갯수가 0이 아닌 경우 제일 첫 번째로 커서 보내기
        if(cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    // 5-3) 오늘 실행한 산책 시간 조회
    public Long getSumDay(String date) {

        long time = -1;

        String sql = "select sum(walk_time) from walktime "
                + "where date = " + date;
        Cursor cursor = mDB.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            time = cursor.getLong(0);
        }
        cursor.close();

        return time; // -1이 넘어오면 제대로 안된거
    }


    // 5-4) 한 달 산책 시간 조회
    public Long getSumMonth(String date) {

        long time = -1;
        String sql = "select sum(walk_time) from walktime "
                + "where reg_date between date('" + date + "', 'start of month') "
                + "and date('" + date + "', 'start of month','+1 month','-1 day')";

        Cursor cursor = mDB.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            time = cursor.getLong(0);
        }
        cursor.close();
        return time;

    }


}
