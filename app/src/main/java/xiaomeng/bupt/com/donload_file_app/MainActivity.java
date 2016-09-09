package xiaomeng.bupt.com.donload_file_app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.DB.ThreadInfoManager;
import xiaomeng.bupt.com.donload_file_app.Notification.NotificationUtils;
import xiaomeng.bupt.com.donload_file_app.Service.DownLoadService;

public class MainActivity extends Activity {
    private ListView mListView;
    private ArrayList<FileInfo> mData;
    private DownLoadAdapter mAdapter;
    public static ThreadInfoManager threadInfoManager;
    private NotificationUtils notificationUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        iniDate();


    }


    private void initView() {

        mListView  = (ListView)findViewById(R.id.id_listview);


    }

    private void iniDate() {

        mData = new ArrayList<>();

        FileInfo FileInfo1 = new FileInfo(0, "慕课下载1.apK",
                "http://www.imooc.com/mobile/imooc.apk",
                0, 0, 0);
        FileInfo FileInfo2 = new FileInfo(1, "慕课下载2.exe",
                "http://www.imooc.com/download/Activator.exe",
                0, 0, 0);
        FileInfo FileInfo3 = new FileInfo(2, "慕课下载3.exe",
                "http://www.imooc.com/download/iTunes64Setup.exe",
                0, 0, 0);
        FileInfo FileInfo4 = new FileInfo(3, "慕课下载4.exe",
                "http://www.imooc.com/download/BaiduPlayerNetSetup_100.exe",
                0, 0, 0);

        mData.add(FileInfo1);
        mData.add(FileInfo2);
        mData.add(FileInfo3);
        mData.add(FileInfo4);

        mAdapter = new DownLoadAdapter(this,mData);
        mListView.setAdapter(mAdapter);

        notificationUtils = new NotificationUtils(this);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownLoadService
                .ACTION_UPDATE);
        intentFilter.addAction(DownLoadService.ACTION_FINISHED);
        intentFilter.addAction(DownLoadService.ACTION_START);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    //发送广播来告诉Activity更新UI
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownLoadService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id",0);
                mAdapter.updateProgress(id,finished);
            }else if (DownLoadService.ACTION_FINISHED.equals(intent.getAction()))

            {
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("mfileInfo");
                mAdapter.updateProgress(fileInfo.getId(),fileInfo.getFinished());
                Toast.makeText(MainActivity.this,fileInfo.getName()+"下载完毕"
                        ,Toast.LENGTH_SHORT).show();
            }else if (DownLoadService.ACTION_START.equals(intent.getAction()))

            {
                FileInfo fileInfo = (FileInfo) intent.
                        getSerializableExtra("fileinfo");
                notificationUtils.showNotification(fileInfo);

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
