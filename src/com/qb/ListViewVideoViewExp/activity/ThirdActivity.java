package com.qb.ListViewVideoViewExp.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import com.qb.ListViewVideoViewExp.R;
import com.qb.ListViewVideoViewExp.bean.VideoInfo;
import com.qb.ListViewVideoViewExp.views.QVideoView;

/**
 * Created by Administrator on 2015/11/6.
 */
public class ThirdActivity extends Activity implements QVideoView.QVideoViewListener{
    QVideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        videoView= (QVideoView) findViewById(R.id.qvideoview);
        videoView.initView((VideoInfo) getIntent().getExtras().get("videoInfo"),this);
    }

    @Override
    public void onclick(View view) {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
