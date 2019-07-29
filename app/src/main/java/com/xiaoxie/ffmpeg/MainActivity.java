package com.xiaoxie.ffmpeg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xiaoxie.ffmpeglib.VideoCutEditor;
import com.xiaoxie.ffmpeglib.interfaces.OnCmdExecListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
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
            String inputPath = "/storage/emulated/0/in.mp4";
            String outputPath = "/storage/emulated/0/out.mp4";
            // String cmd = ed_command.getText().toString();
            // String cmd = "ffmpeg -y -i /storage/emulated/0/in.mp4 -b 2097k -r 30 -vcodec libx264 -preset superfast /storage/emulated/0/a.mp4";
            //String cmd = "ffmpeg -i /storage/emulated/0/in.mp4 /storage/emulated/0/out.mp4";
            String cmd = "ffmpeg -i /storage/emulated/0/in.mp4 -ss 3 -c copy -to 10 /storage/emulated/0/cout.mp4";
            Log.d(TAG, cmd);
            if (TextUtils.isEmpty(cmd)) {
                Log.d(TAG, "cmd is null");
                return;
            }
            final String[] commands = cmd.split(" ");

            if (commands.length > 0) {
//                FFmpegJniBridge.invokeCommandSync(commands, 10000, new OnCmdExecListener() {
//                    @Override
//                    public void onSuccess(String result) {
//                        Log.d(TAG, "执行成功");
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        Log.d(TAG, "执行失败");
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//                        Log.d(TAG, "执行进度:" + progress);
//                    }
//                });
            }

            VideoCutEditor.doCutVideoWithEndTime(inputPath, outputPath, 3, 10, true, true, new OnCmdExecListener() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "执行成功");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "执行失败");
                }

                @Override
                public void onProgress(float progress) {
                    Log.d(TAG, "执行进度:" + progress);
                }
            });
        }
    }
}
