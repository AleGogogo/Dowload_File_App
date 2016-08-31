package xiaomeng.bupt.com.donload_file_app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import xiaomeng.bupt.com.donload_file_app.Bean.FileInfo;

public class MainActivity extends Activity {

    private Button mStartButton;
    private Button mStopButton;
    private ProgressBar mProgress;
    private TextView mDownloadFileName;
    private FileInfo mFileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        iniDate();
    }

    private void iniDate() {

    }

    private void initView() {
        mDownloadFileName = (TextView)findViewById(R.id.id_textView);
        mProgress = (ProgressBar)findViewById(R.id.id_progressBar);
        mStartButton =(Button)findViewById(R.id.id_button_start);
        mStopButton = (Button)findViewById(R.id.id_button_stop);

    }
}
