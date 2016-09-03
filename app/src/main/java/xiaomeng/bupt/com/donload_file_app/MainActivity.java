package xiaomeng.bupt.com.donload_file_app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.DB.ThreadInfoManager;
import xiaomeng.bupt.com.donload_file_app.Service.DownLoadService;

public class MainActivity extends Activity {

    private Button mStartButton;
    private Button mStopButton;
    private ProgressBar mProgress;
    private TextView mDownloadFileName;
    private FileInfo mFileInfo;
    public static ThreadInfoManager threadInfoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        iniDate();
        iniListener();

    }


    private void initView() {
        mDownloadFileName = (TextView)findViewById(R.id.id_textView);
        mProgress = (ProgressBar)findViewById(R.id.id_progressBar);
        mStartButton =(Button)findViewById(R.id.id_button_start);
        mStopButton = (Button)findViewById(R.id.id_button_stop);

    }
    private void iniDate() {
        mFileInfo = new FileInfo(1,"微博图片",
                "http://ww4.sinaimg.cn/thumb180/4b4fcb71jw1eiiw88vtwnj20xc18gk2h.jpg",
                0,0,0);
        mDownloadFileName.setText(mFileInfo.getName());
        mProgress.setMax(100);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownLoadService
                .ACTION_UPDATE);
        registerReceiver(broadcastReceiver,intentFilter);
    }
    private void iniListener() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownLoadService
                        .class);
                intent.setAction(DownLoadService.ACTION_START);
                intent.putExtra("fileInfo",mFileInfo);
                startService(intent);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownLoadService
                        .class);
                intent.setAction(DownLoadService.ACTION_STOP);
                intent.putExtra("fileInfo",mFileInfo);
                startService(intent);
            }
        });
    }

     //发送广播来告诉Activity更新UI
    BroadcastReceiver broadcastReceiver  = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (DownLoadService.ACTION_UPDATE.equals(intent.getAction())) {
                 int finished = intent.getIntExtra("finished", 0);
                 mProgress.setProgress(finished);
             }
         }
     };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
