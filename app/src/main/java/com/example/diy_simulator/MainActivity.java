package com.example.diy_simulator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

/*
    //가장 먼저, FirebaseStorage 인스턴스를 생성한다
    // getInstance() 파라미터에 들어가는 값은 firebase console에서
    //storage를 추가하면 상단에 gs:// 로 시작하는 스킴을 확인할 수 있다

    //위에서 생성한 FirebaseStorage 를 참조하는 storage를 생성한다

    // 위의 저장소를 참조하는 images폴더안의 space.jpg 파일명으로 지정하여
    // 하위 위치를 가리키는 참조를 만든다
    */
// Create a storage reference from our app

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.move_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TapFramgment1_Home.class  ));
            }
        });
    }
}