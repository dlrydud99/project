package edu.skku.finalproject.studyhelper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences pref;
    private String prevStudyName;
    private String prevMyName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 기존 로그인 정보 가져오기 */
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        prevStudyName = pref.getString("studyName",null);
        prevMyName = pref.getString("myName",null);
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}

/* 기존에 들어갔던 스터디 정보 가져와 메인 바로 띄우는 코드 */
//        if (prevStudyName!= null){
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("studyName",prevStudyName);
//            intent.putExtra("myName", prevMyName);
//            Toast.makeText(this,"기존에 진행중이던 "+ prevMyName +"스터디로 입장합니다.",Toast.LENGTH_LONG ).show();
//            startActivity(intent);
//            finish();
//        }else{
//            Intent intent = new Intent(this, StartActivity.class);
//            startActivity(intent);
//            finish();
//        }