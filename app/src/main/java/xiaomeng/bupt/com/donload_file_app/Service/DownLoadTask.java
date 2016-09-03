package xiaomeng.bupt.com.donload_file_app.Service;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.DB.ThreadDAOImPl;
import xiaomeng.bupt.com.donload_file_app.DB.ThreadInfoManager;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfoDao;

/**
 * Created by LYW on 2016/9/2.
 */
public class DownLoadTask {
    private FileInfo mFileInfo;
    private Context mContext;
    private ThreadDAOImPl imPlement ;
    public   boolean isPause;

    public DownLoadTask(Context mContext,FileInfo mFileInfo){
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        imPlement = new ThreadDAOImPl(mContext);
    }
     public void download(){
         ArrayList<ThreadInfo> list = imPlement.getThreads(mFileInfo.getUrl());
         ThreadInfo threadInfo = null;
         if (list.size()==0){
            threadInfo = new ThreadInfo((long) 0, 0, mFileInfo.getLength(), 0, mFileInfo
                     .getUrl());
         }else{
             threadInfo = list.get(0);
         }
             new DownLoadThread(threadInfo).start();

     }

    class DownLoadThread extends Thread{
        private ThreadInfo mThreadInfo = null;
        private HttpURLConnection connection;
        private int mFinished;
        InputStream in = null;
        RandomAccessFile rsf = null;
        public DownLoadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }
        public void run(){
            //向数据库插入数据信息
            if(!(imPlement.isExist(mThreadInfo.getUrl(),mThreadInfo.getId()))){
                imPlement.insertThread(mThreadInfo);
                Log.d("TAG", "数据库中不存在这条信息，请求插入！");
            }else
                try {
                    //设置下载信息，位置
                    URL url = new URL(mThreadInfo.getUrl());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    int start = mThreadInfo.getStart()+mThreadInfo.getFinished();
                    //设置下载范围
                    connection.setRequestProperty("Range","byte = "+start+"-"+mThreadInfo.getEnd());
                    //设置文件写入位置
                    //初始化的时候不是已经建目录了么？
                    File file = new File (DownLoadService.DOWNLOAD_PATH,mFileInfo.getName());
                    RandomAccessFile rsf = new RandomAccessFile(file,"rwd");
                    rsf.seek(start);
                    Intent intent = new Intent();
                    mFinished += mThreadInfo.getFinished();
                    //开始下载
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                        int length = -1;
                        byte[] bytes = new byte[1024 * 4];
                        InputStream in = connection.getInputStream();
                        long currentTime = System.currentTimeMillis();
                        while ((length = in.read(bytes))!= -1){
                            rsf.write(bytes);
                            //把进度发送广播传给Activity
                            mFinished += length;
                            if ((System.currentTimeMillis()-currentTime)>=1000) {
                                intent.putExtra("finished", mFinished*100/mFileInfo.getLength());
                                mContext.sendBroadcast(intent);
                            }

                            if (isPause){
                                mThreadInfo.setFinished(mFinished);
                                imPlement.upDateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),
                                        mThreadInfo.getFinished());
                                return;
                            }
                        }
                        imPlement.deleteThread(mThreadInfo.getId());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                    finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (rsf!=null) {
                               rsf.close();
                            }
                            if (connection!= null) {
                               connection.disconnect();
                            }
                        }catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }
                }
        }
    }
