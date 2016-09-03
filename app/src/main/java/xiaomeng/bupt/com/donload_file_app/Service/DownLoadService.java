package xiaomeng.bupt.com.donload_file_app.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;


/**
 * Created by LYW on 2016/9/1.
 */
public class DownLoadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/downloads";
    public static final int MSG_INIT  = 100;
    private   DownLoadTask mTask;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.d("TAG", "点击 start :" + fileInfo.getName());
            //初始化线程
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            {
                //停止下载
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                Log.d("TAG", "点击 stop :" + fileInfo.getName());
                if (mTask!=null){
                    mTask.isPause = true;
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT:
                    //这是主线程么？
                    FileInfo file = (FileInfo) msg.obj;
                    Log.d("TAG", "初始化数据完成 ");
                    //开始下载
                    mTask = new DownLoadTask(DownLoadService.this,file);
                    mTask.download();
                    break;
                default:
            }
        }
    };

    /**
     * 线程初始化
     */
    class InitThread extends Thread {
        private FileInfo mFileInfo = null;
        HttpURLConnection connection = null;
        RandomAccessFile rsf = null;
        public InitThread(FileInfo fileInfo) {
            mFileInfo = fileInfo;
        }

        public void run() {
            try {
                URL url = new URL(mFileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                int length = -1;
                if (connection.getResponseCode() == 200) {
                    length = connection.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir, mFileInfo.getName());
                rsf = new RandomAccessFile(file,"rwd");
                rsf.setLength(length);
                //这个有啥用
                mFileInfo.setLength(length);
                Message message = handler.obtainMessage();
                message.obj = mFileInfo;
                message.what= MSG_INIT;
                handler.sendMessage(message);
                //或者
                //handler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    connection.disconnect();
                    rsf.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}