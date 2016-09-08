package xiaomeng.bupt.com.donload_file_app.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.DB.ThreadDAOImPl;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;

/**
 * Created by LYW on 2016/9/2.
 */
public class DownLoadTask {
    private static final String TAG = "DownLoadTask";
    private FileInfo mFileInfo;
    private Context mContext;
    private int mThreadCount = 1;
    private ThreadDAOImPl imPlementDao;
    public boolean isPause;
    private ArrayList<DownLoadThread> mThreadlist = null;
    public static ExecutorService executorService = Executors
            .newCachedThreadPool();

    public DownLoadTask(Context mContext, FileInfo mFileInfo,int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        imPlementDao = new ThreadDAOImPl(mContext);
    }

    public void download() {
        //从线程数据信息库取回的List
        ArrayList<ThreadInfo> list = imPlementDao.getThreads(mFileInfo.getUrl());

        if (list.size() == 0) {
            //设置每个线程的下载长度
            int length = mFileInfo.getLength()/mThreadCount;
            for (int i = 0; i <mThreadCount-1; i++) {
                ThreadInfo threadInfo = new ThreadInfo((long)i,i*length,
                        (i+1)*length-1,0,mFileInfo.getUrl());
                //避免程序无法平均分配长度
                if (i == mThreadCount-1){
                    threadInfo.setEnd(mFileInfo.getLength());
                }
                list.add(threadInfo);
                //向数据库插入数据信息(尽量把数据库的访问放在线程外面去做)
                imPlementDao.insertThread(threadInfo);
                Log.d(TAG, "插入了"+mThreadCount+"个线程");
            }

        }
        //创建一个thread队列集
           mThreadlist = new ArrayList<>();

            for (ThreadInfo threadInfo :
                    list) {
                DownLoadThread loadThread = new DownLoadThread(threadInfo);
                DownLoadTask.executorService.execute(loadThread);
                mThreadlist.add(loadThread);
            }



    }

    /**
     * 判断是否所有线程下载完毕
     */
    private synchronized void checkAllThreadFinished(){
        boolean allfinished = true;
        for (DownLoadThread thread:mThreadlist
             ) {
              if (!thread.isFinished){
                  allfinished = false;
              }
        }
           if (allfinished){
               //发送广播给Activity
               Intent intent = new Intent();
               intent.setAction(DownLoadService.ACTION_FINISHED);
               mFileInfo.setFinished(100);
               intent.putExtra("mfileInfo",mFileInfo);
               imPlementDao.deleteThread(mFileInfo.getUrl());
               mContext.sendBroadcast(intent);

           }
        
    }

    /**
     * 数据下载线程
     */
    class DownLoadThread extends Thread {
        private ThreadInfo mThreadInfo = null;
        private HttpURLConnection connection;
        private int mFinished;
        private boolean isFinished = false;


        public DownLoadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        public void run() {
            InputStream in = null;
            RandomAccessFile rsf = null;
            //向数据库插入数据信息(尽量把数据库的访问放在线程外面去做)

//            if (!(imPlement.isExist(mThreadInfo.getUrl(), mThreadInfo.getId()
//            ))) {
//                imPlement.insertThread(mThreadInfo);
//                Log.d("TAG", "数据库中不存在这条信息，请求插入！");
//            }
                try {
                    //设置下载信息，位置
                    URL url = new URL(mThreadInfo.getUrl());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    int start = mThreadInfo.getStart() + mThreadInfo
                            .getFinished();
                    //设置下载范围
                    connection.setRequestProperty("Range", "byte = " + start
                            + "-" + mThreadInfo.getEnd());
                    //设置文件写入位置
                    //初始化的时候不是已经建目录了么？
                    File file = new File(DownLoadService.DOWNLOAD_PATH,
                            mFileInfo.getName());
                     rsf = new RandomAccessFile(file, "rwd");
                    rsf.seek(start);
                    Intent intent = new Intent();
                    intent.setAction(DownLoadService.ACTION_UPDATE);
                    mFinished += mThreadInfo.getFinished();
                    //开始下载
                      if (connection.getResponseCode() == HttpURLConnection
                            .HTTP_OK) {
                        int length = -1;
                        byte[] bytes = new byte[1024 * 4];
                         in = connection.getInputStream();
                        long lastUpdateTime = System.currentTimeMillis();
                        while ((length = in.read(bytes)) != -1) {
                            rsf.write(bytes);
                            //把进度发送广播传给Activity
                            //累加整个文件的进度
                            mFinished += length;
                            //累加每个线程的进度
                            mThreadInfo.setFinished(mThreadInfo.getFinished()+length);
                            if ((System.currentTimeMillis() - lastUpdateTime) >= 10000) {
                                //计算百分比
                                int persent = (int) ((mFinished * 1f /
                                        mFileInfo.getLength()) * 100);
                                Log.d(TAG, "run: persent[" + persent + "]");
                                intent.putExtra("finished", persent);
                                intent.putExtra("id",mFileInfo.getId());
                                mContext.sendBroadcast(intent);
                                Log.d("TAG", "把进度利用广播的形势传过去啦！");
                                lastUpdateTime = System.currentTimeMillis();
                            }

                            if (isPause) {
                                imPlementDao.upDateThread(mThreadInfo.getUrl(),
                                        mThreadInfo.getId(),
                                        mThreadInfo.getFinished());
                                return;
                            }
                        }
                        //下载完成后最后发送百分之百
                          isFinished = true;
                          checkAllThreadFinished();

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (rsf != null) {
                            rsf.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
