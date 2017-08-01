package mydog.haley.com.walktime.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mydog.haley.com.walktime.R;
import mydog.haley.com.walktime.WalkTimeAdapter;
import mydog.haley.com.walktime.db.WalkTimeOpenHelper;
import mydog.haley.com.walktime.domain.WalkTimeVO;

// 산책 기록 관리 리스트 뷰
/*
1. 산책 커스텀 리스트 뷰
2. selectAllTime() 메소드로 전체 리스트 가져오기
3. deleteTime() 메소드로 선택한 산책 기록 삭제
4. 주간/월간 기록 보기
 */

public class TimeListActivity extends AppCompatActivity {

    private static final String TAG = "**TimeListActivity**";
    private Context mContext;
    private WalkTimeOpenHelper mDBHelper;
    private Cursor mCursor;
    private WalkTimeVO mWalkTimeVO;
    private ArrayList<WalkTimeVO> mArrayList;
    // CustomAdapter
    private WalkTimeAdapter mAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_list);
        Log.v(TAG, "Call onCreate()");

        mContext = this;
        mListView = (ListView)findViewById(R.id.time_list);

        // 데이터베이스 생성 & 오픈
        mDBHelper = new WalkTimeOpenHelper(mContext);
        try {
            mDBHelper.open();
            Log.v(TAG, "Open DataBase");
        } catch(SQLException e) {
            Log.e(TAG, "Exception - DateBase open", e);
        }

        // ArrayList 초기화
        mArrayList = new ArrayList<WalkTimeVO>();
        // 테이블에 있는 데이터를 가져와서 ArrayList에 저장 -> 메소드
        addList();

        // 값을 제대로 가져왔는지 확인하기 위해 로그
        for(WalkTimeVO i : mArrayList) {
            Log.v(TAG, "////////////////////////////");
            Log.v(TAG, "Code : " + i.getCode());
            Log.v(TAG, "Start Time : " + i.getStart());
            Log.v(TAG, "End Time : " + i.getEnd());
            Log.v(TAG, "RegDate : " + i.getReg_date());
            Log.v(TAG, "WalkTime : " + i.getWalk_time());
            Log.v(TAG, "////////////////////////////");
        }

        // 리스트뷰에 사용할 커스텀 어댑터 초기화 & 어댑터 설정
        mAdapter = new WalkTimeAdapter(mArrayList, mContext);
        mListView.setAdapter(mAdapter);

        // 리스트 뷰 버튼 클릭 이벤트
        setOnClick();


    }


    // Cursor를 이용해 테이블의 내용을 WalkTimeVO에 저장 후 ArrayList로 Add하는 메소드
    private void addList() {

        mCursor = null;
        // DB에 있는 모든 값을 가져옴
        mCursor = mDBHelper.selectAllTime();
        // 컬럼 갯수 확인
        Log.v(TAG, "Count : " + mCursor.getCount());

        while(mCursor.moveToNext()) {
            // WalkTimeVO에 가져온 값을 입력
            mWalkTimeVO = new WalkTimeVO(
                    mCursor.getInt(mCursor.getColumnIndex("code")),
                    mCursor.getString(mCursor.getColumnIndex("start")),
                    mCursor.getString(mCursor.getColumnIndex("end")),
                    mCursor.getLong(mCursor.getColumnIndex("walk_time")),
                    mCursor.getString(mCursor.getColumnIndex("reg_date"))
            );

            // 테이블의 값을 저장하고 있는 mWalkTimeVO를 ArrayList에 저장
            mArrayList.add(mWalkTimeVO);
        }

        // Cursor close
        mCursor.close();

    }

    // 삭제 버튼 클릭
    private void setOnClick() {

        // 커스텀 리스트 아이템 내의 버튼 클릭 시 처리할 이벤트
        mAdapter.setOnListButtonClick(new WalkTimeAdapter.onListButtonClick() {
            @Override
            public void onDeleteClick(WalkTimeVO vo) {

                final int code = vo.getCode();
                final int position = mAdapter.getmPosition();
                Log.v(TAG, "View Code : " + code);
                Log.v(TAG, "View Position : " + position);

                // 삭제 여부 확인 다이얼로그
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("산책 기록 삭제")
                        .setMessage("산책 기록을 삭제하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Log.v(TAG, "Delete WalkTime");
                                Log.v(TAG, "삭제 코드 : " + code + ", 삭제 포지션 : " + position);

                                boolean result = mDBHelper.deleteTime(code);
                                Log.v(TAG, "Delete Result : " + result);

                                if(result) {
                                    // 정상적으로 값을 삭제했다면 리스트에도 삭제
                                    mArrayList.remove(position);
                                    // 어댑터에 ArrayList 다시 셋팅
                                    mAdapter.setArrayList(mArrayList);
                                    // 어댑터에 값이 변경된것을 알림
                                    mAdapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(TimeListActivity.this,
                                            "삭제하려는 데이터를 다시 확인해주세요",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }


    @Override
    protected void onDestroy() {

        Log.v(TAG, "Call onDestroy()");
        mDBHelper.close();
        Log.v(TAG, "Close DataBase");
        super.onDestroy();
    }
}
