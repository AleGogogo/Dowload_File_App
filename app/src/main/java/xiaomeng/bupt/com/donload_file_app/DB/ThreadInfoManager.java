package xiaomeng.bupt.com.donload_file_app.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import xiaomeng.bupt.com.donload_file_app.greendao.DaoMaster;
import xiaomeng.bupt.com.donload_file_app.greendao.DaoSession;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfoDao;

/**
 * Created by LYW on 2016/9/2.
 */
public class ThreadInfoManager {
    public static SQLiteDatabase db;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static DaoMaster.DevOpenHelper helper;
    public static ThreadInfoDao threadInfoDao;
    private static ThreadInfoManager threadInfoManager;

    public static String DB_NAME = "threadInfo_db";

    private ThreadInfoManager(Context mContext){
         helper = new DaoMaster.DevOpenHelper(mContext,
                DB_NAME,null);
    }

    public static   ThreadInfoManager getInstance(Context mContext){
        if (threadInfoManager == null){
            threadInfoManager  = new ThreadInfoManager(mContext);
        }
        return threadInfoManager;
    }

     public ThreadInfoDao initThreadInfo(SQLiteDatabase db){
          daoMaster = new DaoMaster(db);
         daoSession = daoMaster.newSession();
         threadInfoDao = daoSession.getThreadInfoDao();
         return threadInfoDao;
     }
     public  DaoMaster.DevOpenHelper getHelper(){
         return helper;
     }
}
