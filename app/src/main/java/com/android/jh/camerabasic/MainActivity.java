package com.android.jh.camerabasic;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    // 권한 요청 코드
    private final int REQ_PERMISSION = 100;
    // 카메라 요청 코드
    private final int REQ_CAMERA = 101;
    // 겔러리 요청 코드
    private final int REQ_GALLERY = 102;

    Button btnCamera, btnGallery;
    ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 위젯을 세팅
        setWidget();
        // 버튼 관련 컨트롤러 활성화 처리
        buttonDisable();
        // 리스너 계열을 등록
        setListener();
        // 권한 처리
        checkPermission();
    }

    private void init() {
        //프로그램 실행
        // 권한 처리가 통과 되었을 때 만 버튼을 활성화 시켜준다
        buttonEnable();
    }

    private void checkPermission() {
        //버전 체크해서 마시멜로우(6.0)보다 낮으면 런타임 권한 체크를 하지않는다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermssion(this, REQ_PERMISSION)) {
                init();
            }
        } else {
            init();
        }
    }

    private void buttonDisable() {
        btnCamera.setEnabled(false);
    }

    private void buttonEnable() {
        btnCamera.setEnabled(true);
    }

    // 위젯 세팅
    private void setWidget() {
        imgView = (ImageView) findViewById(R.id.imageView);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGallery = (Button) findViewById(R.id.btnGallery);
    }

    // 리스너 세팅
    private void setListener() {
        btnCamera.setOnClickListener(clickListener);
        btnGallery.setOnClickListener(clickListener);
    }

    Uri fileUri = null;
    // 리스너 정의
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.btnCamera: // 카메라 버튼 동작
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 롤리팝 이상 버전에서는 코드를 반영해야 한다.
                    // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    // --- 여기 까지 컨텐트 uri 강제세팅 ---
                    startActivityForResult(intent, REQ_CAMERA);
                    break;
                case R.id.btnGallery://겔러리에서 이미지 불러오기
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");//외부 저장소에 있는 이미지만 가져오기 위한 필터리
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CAMERA);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("Camera", "resultCode===============================" + resultCode);
        switch (requestCode) {
            case REQ_GALLERY:
                if(resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    Glide.with(this)
                            .load(fileUri)
                            .into(imgView);
                }
                break;
            case REQ_CAMERA :
                if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) { // 사진 확인처리됨 RESULT_OK = -1
                    // 롤리팝 체크
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        Log.i("Camera", "data.getData()===============================" + data.getData());
                        fileUri = data.getData();
                    }
                    Log.i("Camera", "fileUri===============================" + fileUri);
                    if (fileUri != null) {
                        // 글라이드로 이미지 세팅하면 자동으로 사이즈 조절
                        Glide.with(this)
                                .load(fileUri)
                                .into(imgView);
                    } else {
                        Toast.makeText(this, "사진파일이 없습니다", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // resultCode 가 0이고 사진이 찍혔으면 uri 가 남는데
                    // uri 가 있을 경우 삭제처리...
                }
                break;
        }
    }

    //권한체크 후 콜백< 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 됐으면
            if (PermissionControl.onCheckedResult(grantResults)) {
                init();
            } else {
                Toast.makeText(this, "권한을 사용하지 않으시면 프로그램을 실행시킬수 없습니다", Toast.LENGTH_SHORT).show();
                //finish();
                // 선택 1.종료, 2. 권한체크 다시물어보기
                //PermissionControl.checkPermssion(this,REQ_PERMISSION);
            }
        }
    }
}