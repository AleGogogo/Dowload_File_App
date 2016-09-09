package xiaomeng.bupt.com.donload_file_app.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.dao.query.Query;
import xiaomeng.bupt.com.donload_file_app.greendao.DaoMaster;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfoDao;

/**
 * Created by LYW on 2016/9/2.
 */
public class ThreadDAOImPl implements ThreadDAO {
    private ThreadInfoManager mManager;
    private DaoMaster.DevOpenHelper mHelper;
    private ThreadInfoDao mDao;
    private SQLiteDatabase db;
    public ThreadDAOImPl(Context mContext){
         mManager = ThreadInfoManager.getInstance(mContext);
         mHelper = mManager.getmHelper();
        mDao = mManager.getThreadInfoDao();
    }

    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {

//        db = mHelper.getWritableDatabase();
//        ThreadInfoDao dao =  mManager.initThreadInfo(db);
        mDao.insert(threadInfo);
        db.close(); //这里为什么要选择close呢？
        //我看视频里每次操作完都要关闭
        //
    }

    @Override
    public synchronized void deleteThread(String url) {
        db = mHelper.getWritableDatabase();
        ThreadInfoDao dao =  mManager.initThreadInfo(db);
        //你仔细想想这里init是是不是每次都需要
        //这里需要每次都initial马？
        //需要把，因为 每次可写可读不一样啊，db类型不一样
        //看到错误提示了吧，你在某处关闭看数据库。是不是多线程，有的线程给他关了
        //java.lang.IllegalStateException: attempt to re-open an already-closed object:

        ArrayList<ThreadInfo> list = getThreads(url);
        for (ThreadInfo threadInfo :
                list) {
            dao.delete(threadInfo);
        }

        db.close();
    }

    @Override
    public synchronized void upDateThread(String url, long id, int finished) {
        ArrayList<ThreadInfo> list = new ArrayList<>();
        db = mHelper.getWritableDatabase();
        mDao = mManager.initThreadInfo(db);
        Query<ThreadInfo> query = mDao.queryBuilder()
                .where(ThreadInfoDao.Properties.Url.eq(url),ThreadInfoDao.Properties.Id.eq(id))
                .orderAsc(ThreadInfoDao.Properties.Id)
                .build();
        list = (ArrayList<ThreadInfo>) query.list();
        Log.d("TAG", "符合条件的线程数量为： "+list.size());
        ThreadInfo threadInfo = list.get(0);
        //你这里先query到原来已经存在的一行。目的是在暂停是更新字段

        //这里你没有进行数据有效性判读吧
        // list不一定会有数据啊 没有，不过错误不是这里吧，因为我debug的时候看到里面有数据啊，可是往下走错了
        threadInfo.setFinished(finished);
        //儿这里更新以后
//        mDao.insert(threadInfo);
        mDao.update(threadInfo);
        //再实施 哦哦我试试
        //你直接进行插入操作，由于原来表中已经有这么一行。在插入是key（threadinfo,id）肯定是相同
        //的。所以插入失败，你应该调用update方法
        // 因为你这里没有源码，我没有办法带你追踪这个异常
        //android.database.sqlite.SQLiteConstraintException: PRIMARY KEY must be unique (code 19)
        //直接跟你说原因把
        //
        //这个得看GreenDao的insert源码。默认肯定不让插入key相同的数据，是不覆盖的机制
        //这里不对，你有我的程序么，要不我给你运行一下，你看一下错，不运行看不出来
        //等下 我要先看依稀啊
        //运行一下
        //notifyAll();
    }

    @Override
    public ArrayList<ThreadInfo> getThreads(String url) {
        ArrayList<ThreadInfo> list = new ArrayList<>();
        db = mHelper.getReadableDatabase();
        mDao = mManager.initThreadInfo(db);
        Query<ThreadInfo> query = mDao.queryBuilder()
                .where(ThreadInfoDao.Properties.Url.eq(url))
                .orderAsc(ThreadInfoDao.Properties.Id)
                .build();
        list = (ArrayList<ThreadInfo>) query.list();
        Log.d("TAG", "查询找到符合要求的list!"+list.size());
        db.close();
        return list;
    }

    @Override
    public boolean isExist(String url, long thread_id) {
        boolean isExist = false;
        db = mHelper.getReadableDatabase();
        mDao = mManager.initThreadInfo(db);
        Query<ThreadInfo> query = mDao.queryBuilder()
                .where(ThreadInfoDao.Properties.Url.eq(url))
                .orderAsc(ThreadInfoDao.Properties.Id)
                .build();
        if (query.list().size() != 0)
        {
            isExist = true;
        }
        return isExist;
    }
}
