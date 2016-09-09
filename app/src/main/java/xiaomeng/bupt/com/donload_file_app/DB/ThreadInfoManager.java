package xiaomeng.bupt.com.donload_file_app.DB;

import android.content.Context;

import xiaomeng.bupt.com.donload_file_app.greendao.DaoMaster;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfoDao;

/**
 * Created by LYW on 2016/9/2.
 */
public class ThreadInfoManager {

    private static ThreadInfoManager sInstance;

    private  DaoMaster.DevOpenHelper mHelper;
    private  ThreadInfoDao threadInfoDao;
    //还全是public的。。。你在想什么啊。。。
    //..... 为啥全部都是static的啊？那应该是啥
    //不是啊  你用static是怎么想的啊？？？？？？
    //static又是什么意思啊！！！！！！
    //不用创建实例就可以用  那你下面写单例又是什么意思？哦也是
    //说实话  我不知道你创建这个manager的意义何在。我就先按照你
    //这个方式个该
    //你的mannger是打算多线程里面用的马？ 如果是，我就改成线程安全的方法是啊

    public static String DB_NAME = "threadInfo_db";


    private ThreadInfoManager(Context mContext){
         mHelper = new DaoMaster.DevOpenHelper(mContext,
                DB_NAME,null);
        initThreadDAO();
    }


    public  static    ThreadInfoManager getInstance(Context mContext){
        if (sInstance == null){
            synchronized (ThreadInfoManager.class){
                if (sInstance == null){
                    sInstance = new ThreadInfoManager(mContext);
                }
            }
        }
        return sInstance;
    }

     private ThreadInfoDao initThreadDAO(){
          DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());//这个智能new一次是吧
         //我到现在还是你根本没有理解面向对象
         threadInfoDao = daoMaster.newSession().getThreadInfoDao();
         //threadinfodao 这个是你要操作的对象，这个对象负责info映射到数据库
         //可这个init的操作是每次都需要的马？ 那db咋办，我当时就是不知道这个咋办才每次都initial
         //你可以这么想  daomaster这个是工厂，threadinfoda是负责映射的机器
         //你每次操作都需要建立一个工厂，再买点机器马？
         //难道不是简历一次后，直接使用马？那就建立两个？一个用了都操作用的一个用来写操作用的机器？
         //很显然，你直接用单例就好
         return threadInfoDao;
     }

    public  ThreadInfoDao getThreadInfoDao() {
        return threadInfoDao;
    }

    public  DaoMaster.DevOpenHelper getmHelper(){
         return mHelper;
     }
}
