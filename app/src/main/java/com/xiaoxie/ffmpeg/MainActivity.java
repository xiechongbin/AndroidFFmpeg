package com.xiaoxie.ffmpeg;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xiaoxie.ffmpeglib.FFmpegJniBridge;
import com.xiaoxie.ffmpeglib.OnCmdExecListner;


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
            // String cmd = ed_command.getText().toString();
            String cmd = "ffmpeg -y -i /storage/emulated/0/in.mp4 -b 2097k -r 30 -vcodec libx264 -preset superfast /storage/emulated/0/a.mp4";
            //String cmd = "ffmpeg -i /storage/emulated/0/in.mp4 /storage/emulated/0/out.mp4";
            Log.d("cmds", cmd);
            if (TextUtils.isEmpty(cmd)) {
                Log.d("cmds", "cmd is null");
                return;
            }
            final String[] commands = cmd.split(" ");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //FFmpegJniBridge.invokeCommands(commands);
                }
            }).start();
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource("/storage/emulated/0/in.mp4");
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (commands.length > 0) {
                FFmpegJniBridge.invokeCommandSync(commands, mediaPlayer.getDuration(), new OnCmdExecListner() {
                    @Override
                    public void onSuccess() {
                        Log.d("ddddcccc", "执行成功");
                    }

                    @Override
                    public void onFailure() {
                        Log.d("ddddcccc", "执行失败");
                    }

                    @Override
                    public void onProgress(float progress) {
                        Log.d("ddddcccc", "执行进度:" + progress);
                    }
                });
            }
        }
    }
}
