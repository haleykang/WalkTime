package mydog.haley.com.walktime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import mydog.haley.com.walktime.R;

// 페이지 이동을 위해서 생성한 메인 페이지

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    // 바텀 네비게이션 --> 계속 쓸거면 따로 클래스 만들어서 쓰자
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(MainActivity.this, ChronometerActivity.class));
                    return true;
                case R.id.navigation_list:
                    startActivity(new Intent(MainActivity.this, TimeListActivity.class));

                    return true;
                case R.id.navigation_diary:
                    startActivity(new Intent(MainActivity.this, DiaryActivity.class));
                    return true;
            }
            return false;
        }

    };

}
