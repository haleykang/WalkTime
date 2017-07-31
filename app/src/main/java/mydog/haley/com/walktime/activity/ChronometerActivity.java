package mydog.haley.com.walktime.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mydog.haley.com.walktime.R;
import mydog.haley.com.walktime.db.WalkTimeOpenHelper;
import mydog.haley.com.walktime.domain.WalkTimeVO;

// 시간 기록 클래스
public class ChronometerActivity extends AppCompatActivity {

    private static final String TAG = "**ChronometerActivity**";

    // 산책 버튼 상수
    private static final int INIT = 0;
    private static final int RUN = 1;
    private int mStatus = 0; // 초기 상태는 INIT

    private Context mContext = this;

    private Chronometer mChronometer;
    private FloatingActionButton mPlayBt;

    // 산책 시작 시간
    private long mBaseTime;
    // 산책 종료 시간
    private long mStopTime;

    // 데이터 베이스 저장을 위한 데이터
    // 산책 기록 날짜
    private Date mToday;
    private String mStart;
    private String mEnd;
    private long mWalkTime;

    // 데이터베이스
    private WalkTimeOpenHelper mWalkTimeDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer);

        Log.v(TAG, "Call onCreate()");
        mPlayBt = (FloatingActionButton)findViewById(R.id.play_bt);
        mChronometer = (Chronometer)findViewById(R.id.walk_time_watch);

        // Chronometer 포맷 HH:MM:SS 형태로 변경
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long tempTime = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                chronometer.setText(getWatchFormat(tempTime));
            }
        });


        // 데이터베이스 오픈 -> onStart() 함수로 이동
        /*    mWalkTimeDBHelper = new WalkTimeOpenHelper(mContext);
        try {
            mWalkTimeDBHelper.open();

        } catch(SQLException e) {
            Log.e(TAG, "Error : Open WalkTimeDBHelper", e);
        }*/

    }

    public void onPlayClick(View v) {


        switch(mStatus) {
            // 1. 초기 상태일 때 버튼 클릭
            case INIT:
                Log.v(TAG, "Click - Status : INIT");
                // 1)산책을 시작합니다 Toast 메세지
                Toast.makeText(mContext, "산책을 시작합니다.", Toast.LENGTH_SHORT).show();

                // 2)Chronometer 실행
                // 2-1)Chronometer 베이스 타임을 현재 시간으로 지정
                mChronometer.setBase(SystemClock.elapsedRealtime()); // -> 시작 타임
                mBaseTime = mChronometer.getBase();
                Log.v(TAG, "Base Time : " + mBaseTime);
                // 2-2) Chronometer 시작
                mChronometer.start();
                // 2-3) 시작한 시간 저장
                mStart = getCurrentTime();
                Log.v(TAG, "Start Time : " + mStart);
                // 3)버튼 src -> ic_stop 모양으로 변경
                mPlayBt.setImageResource(R.drawable.ic_stop);

                // 4)mStatus = RUN 상태로 변경
                mStatus = RUN;
                break;
            // 2. 동작 상태일 때 버튼 클릭
            case RUN:
                Log.v(TAG, "Click - Status : RUN");


                setDialog();

                break;
        }

    }

    public void setDialog() {

        // 추가 사항 - 산책 시간이 1분 미만일 경우 산책 기록 안함

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        long tempTime = (SystemClock.elapsedRealtime() - mBaseTime) / 1000;
        Log.v(TAG, "TempTime : " + tempTime);

        // 재시작을 어떻게 해야할지.. 우선 이동할 메인 페이지 구현 후 만들기
    /*    if(tempTime < 60) {
            // 산책 시간이 1분 미만인 경우!
            builder.setTitle("산책 종료");
            builder.setMessage("1분 미만의 산책은 기록되지 않습니다.\n산책을 그만할까요?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.v(TAG, "산책시간 1분 미만 - 산책 기록 없음");
                    // 1) 크로노미터 STOP
                    mChronometer.stop();
                    // 2) 산책 종료 토스트
                    Toast.makeText(mContext, "산책을 종료하였습니다.", Toast.LENGTH_SHORT).show();
                    // 3) 만약 이동할 메인 페이지가 있다면 그 페이지로 이동(아직 구현 안함)

                    // 4) 버튼 src -> ic_play 모양으로 변경
                    mPlayBt.setImageResource(R.drawable.ic_play);
                    // 5) mStatus = INIT 상태로 변경
                    mStatus = INIT;
                    // 6) 크로노미터 00:00:00으로 초기화 -> 안되네?
                    mChronometer.setFormat(String.valueOf("00:00:00"));


                }
            });
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.v(TAG, "Continue");
                    dialogInterface.cancel();


                }
            });

        } else {*/

        builder.setTitle("산책 종료");
        builder.setMessage("산책을 그만할까요?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(TAG, "Finish - Recording WalkTime.");
                // 0) Chronometer Stop
                mChronometer.stop();
                // 1) 산책 중지 시간 저장
                mStopTime = SystemClock.elapsedRealtime();
                Log.v(TAG, "Stop Time : " + mStopTime);
                // 2) End 시간 저장
                mEnd = getCurrentTime();
                Log.v(TAG, "End Time : " + mEnd);
                // 3) 총 산책 시간 저장
                mWalkTime = (mStopTime - mBaseTime) / 1000;
                Log.v(TAG, "총 산책 시간 : " + mWalkTime + "초");
                // 4) 버튼 src -> ic_play 모양으로 변경
                mPlayBt.setImageResource(R.drawable.ic_play);
                // 5) mStatus = INIT 상태로 변경
                mStatus = INIT;
                // 6) 데이터베이스에 산책 내용 저장
                openDB();
                WalkTimeVO walkTimeVO = new WalkTimeVO(mStart, mEnd, mWalkTime);
                mWalkTimeDBHelper.insertTime(walkTimeVO);
                mWalkTimeDBHelper.close();

                // 7) 리스트 페이지로 이동 / 또는 메인 페이지로 이동
                startActivity(new Intent(mContext, TimeListActivity.class));

            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(TAG, "Cancel - Stop Recording.");
                // 1) 다이얼로그 창 취소
                dialogInterface.cancel();
                   /* // 2) Chronometer 재시작
                    mChronometer.start();*/
            }
        });
    /*}*/


        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private String getWatchFormat(long time) {

        int h = (int)(time / 3600);
        int m = (int)(time - h * 3600) / 60;
        int s = (int)(time - h * 3600 - m * 60);
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";

        return hh + ":" + mm + ":" + ss;
    }

    private String getCurrentTime() {

        Date date = new Date(System.currentTimeMillis());

        SimpleDateFormat timeFormat = new SimpleDateFormat("aa hh:mm", Locale.getDefault());

        return timeFormat.format(date);
    }

    private void openDB() {

        // 데이터 베이스 오픈
        mWalkTimeDBHelper = new WalkTimeOpenHelper(mContext);
        try {
            mWalkTimeDBHelper.open();

        } catch(SQLException e) {
            Log.e(TAG, "Error : Open WalkTimeDBHelper", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "Call onStart()");

        // 사용할 때 위치로 변경 -> 홈 화면으로 이동했다가 다시 앱으로 들어올 때마다 DB가 오픈됨
        /* // 데이터 베이스 오픈
        mWalkTimeDBHelper = new WalkTimeOpenHelper(mContext);
        try {
            mWalkTimeDBHelper.open();

        } catch(SQLException e) {
            Log.e(TAG, "Error : Open WalkTimeDBHelper", e);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Call onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "Call onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "Call onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "Call onRestart()");
        // 재시작 했을 때 크로노미터 00:00:00으로 셋팅
        mChronometer.setText(String.valueOf("00:00:00"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWalkTimeDBHelper.close();
        Log.v(TAG, "Call onDestroy()");
    }
}
