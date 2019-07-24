package com.xiaoxie.ffmpeg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_command;
    private Button btn_invoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed_command = findViewById(R.id.ed_command);
        btn_invoke = findViewById(R.id.btn_invoke);
        btn_invoke.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_invoke) {
        }
    }
}
