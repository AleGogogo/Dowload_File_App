package xiaomeng.bupt.com.donload_file_app.DB;

import java.util.ArrayList;

import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfoDao;

/**
 * Created by LYW on 2016/9/1.
 */
public interface ThreadDAO {
    void insertThread(ThreadInfo threadInfo);
    void deleteThread(String url);
    void upDateThread(String url,long id,int finished);
    ArrayList<ThreadInfo> getThreads(String url);
    boolean isExist(String url,long thread_id);
}