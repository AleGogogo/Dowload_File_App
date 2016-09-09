package xiaomeng.bupt.com.donload_file_app.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.MainActivity;
import xiaomeng.bupt.com.donload_file_app.R;
import xiaomeng.bupt.com.donload_file_app.Service.DownLoadService;

/**
 * Created by LYW on 2016/9/9.
 */
public class NotificationUtils {
    private Context mContext;
    private FileInfo mFileInfo;
    private NotificationManager notificationManager;
    private Map<Integer,Notification> notifications;

     public NotificationUtils(Context context){
         mContext = context;
         notificationManager = (NotificationManager) context.getSystemService(Context.
                 NOTIFICATION_SERVICE);
         notifications = new HashMap<Integer, Notification>() ;

     }

    public  void showNotification(FileInfo fileInfo){
        if (notifications.containsKey(fileInfo.getId())) {
            Notification notification = new Notification();
            notification.tickerText = fileInfo.getName()+"开始下载";
            notification.when = System.currentTimeMillis();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            Intent intent = new Intent(mContext, MainActivity.class);
            PendingIntent pendingIntent =PendingIntent.getActivity(mContext,
                    0,intent,0);
            notification.contentIntent = pendingIntent;
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.notification);
            remoteViews.setTextViewText(R.id.id_notify_textView,fileInfo.getName());
            Intent startIntent = new Intent(mContext, DownLoadService.class);
            startIntent.setAction(DownLoadService.ACTION_START);
            startIntent.putExtra("fileinfo",fileInfo);
            PendingIntent startPi =PendingIntent.getActivity(mContext,
                    0,startIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.id_notify_button_start,
                    startPi);
            Intent stopIntent = new Intent(mContext, DownLoadService.class);
            startIntent.setAction(DownLoadService.ACTION_STOP);
            startIntent.putExtra("fileinfo",fileInfo);
            PendingIntent stopPi =PendingIntent.getActivity(mContext,
                    0,stopIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.id_notify_button_stop,
                    stopPi);
            notification.contentView = remoteViews;
            //发送通知
            notificationManager.notify(mFileInfo.getId(),notification);
            notifications.put(mFileInfo.getId(),notification);
        }

    }

    public void cancleNotification(int  id){

        notificationManager.cancel(id);
        notifications.remove(id);
    }

    public void updateNotification(int id,int progress){

        Notification notification = notifications.get(id);
//        RemoteViews remoteViews = notification.contentView;
        //判断是否是可用的notification
        if (notification != null) {
            notification.contentView.setProgressBar(R.id.id__notify_progressBar, 100,

                    progress, false);
            notificationManager.notify(id,notification);
        }

    }
}
