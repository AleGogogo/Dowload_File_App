package xiaomeng.bupt.com.donload_file_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;
import xiaomeng.bupt.com.donload_file_app.Service.DownLoadService;
import xiaomeng.bupt.com.donload_file_app.greendao.ThreadInfo;

/**
 * Created by LYW on 2016/9/6.
 */
public class DownLoadAdapter extends BaseAdapter {
    private ArrayList<FileInfo> mData;
    private Context mContext;
    public DownLoadAdapter(Context context,ArrayList<FileInfo> list) {
        mData = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        final FileInfo mFile = mData.get(position);
        ViewHolder holder = null;
        if (contentView == null) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.downloaditerm, null);
            //每次更新UI几乎不变的代码放这里
            holder = new ViewHolder();
            holder.mDownloadFileName = (TextView)contentView.findViewById(R.id.id_textView);
            holder.mProgress = (ProgressBar)contentView.findViewById(R.id.id_progressBar);
            holder.mStartButton = (Button)contentView. findViewById(R.id.id_button_start);
            holder.mStopButton = (Button)contentView. findViewById(R.id.id_button_stop);
            holder.mProgress.setMax(100);
            holder.mDownloadFileName.setText(mFile.getName());
            holder.mStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DownLoadService
                            .class);
                    intent.setAction(DownLoadService.ACTION_START);
                    intent.putExtra("fileInfo", mFile);
                    mContext.startService(intent);
                }
            });
            holder.mStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DownLoadService
                            .class);
                    intent.setAction(DownLoadService.ACTION_STOP);
                    intent.putExtra("fileInfo", mFile);
                    mContext.startService(intent);
                }
            });
            contentView.setTag(holder);
        }else {
            holder = (ViewHolder) contentView.getTag();
        }
        //更新变化的代码放这里
        holder.mProgress.setProgress(mFile.getFinished());
            return contentView;

    }

        public void updateProgress(int id, int finished){
            FileInfo fileInfo = mData.get(id);
            fileInfo.setFinished(finished);
            notifyDataSetChanged();
    }

     class ViewHolder{
          Button mStartButton;
          Button mStopButton;
          ProgressBar mProgress;
          TextView mDownloadFileName;
    }
}
