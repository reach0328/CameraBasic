package com.android.jh.camerabasic;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //권한 요청 코드
    private final int REQ_PERMISSION = 100;
    // 카메라 요청 코드
    private final int REQ_CAMERA = 101;
    Button btnCamera;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidget();
        setListener();
        checkPermission();
    }

    private void init() {
        //프로그램 실행
    }
    private void checkPermission() {
        //버전 체크해서 마시멜로우(6.0)보다 낮으면 런타임 권한 체크를 하지않는다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PermissionControl.checkPermssion(this,REQ_PERMISSION)){
                init();
            }
        } else {
            init();
        }
    }

    // 위젯 세팅
    private void setWidget() {
        imgView = (ImageView) findViewById(R.id.imageView);
        btnCamera = (Button) findViewById(R.id.btnCamera);
    }

    // 리스너 세팅
    private void setListener() {
        btnCamera.setOnClickListener(clickListener);
    }

    // 리스너 정의
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnCamera : // 카메라 버튼 동작

                    break;
            }
        }
    };

    //권한체크 후 콜백< 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_PERMISSION){
            //배열에 넘긴 런타임 권한을 체크해서 승인이 됐으면
            if(PermissionControl.onCheckedResult(grantResults)) {
                init();
            } else {
                Toast.makeText(this,"권한을 사용하지 않으시면 프로그램을 실행시킬수 없습니다",Toast.LENGTH_SHORT).show();
                finish();
                // 선택 1.종료, 2. 권한체크 다시물어보기
                //PermissionControl.checkPermssion(this,REQ_PERMISSION);
            }
        }
    }
}