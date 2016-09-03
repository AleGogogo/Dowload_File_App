package xiaomeng.bupt.com.donload_file_app.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.dao.query.Query;
import xiaomeng.bupt.com.donload_file_app.MainActivity;
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
         mHelper = mManager.getHelper();
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {

        db = mHelper.getWritableDatabase();
        ThreadInfoDao dao =  mManager.initThreadInfo(db);
        dao.insert(threadInfo);
        db.close();
    }

    @Override
    public void deleteThread(long id) {
        db = mHelper.getWritableDatabase();
        ThreadInfoDao dao =  mManager.initThreadInfo(db);
        dao.deleteByKey(id);
        db.close();
    }

    @Override
    public void upDateThread(String url, long id, int finished) {
        ArrayList<ThreadInfo> list = new ArrayList<>();
        db = mHelper.getReadableDatabase();
        mDao = mManager.initThreadInfo(db);
        Query<ThreadInfo> query = mDao.queryBuilder()
                .where(ThreadInfoDao.Properties.Url.eq(url),ThreadInfoDao.Properties.Id.eq(id))
                .orderAsc(ThreadInfoDao.Properties.Id)
                .build();
        list = (ArrayList<ThreadInfo>) query.list();
        ThreadInfo threadInfo = list.get(0);
        threadInfo.setFinished(finished);
        mDao.insert(threadInfo);

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
