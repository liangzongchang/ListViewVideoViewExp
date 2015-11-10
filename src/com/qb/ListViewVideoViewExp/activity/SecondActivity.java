package com.qb.ListViewVideoViewExp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.qb.ListViewVideoViewExp.utils.QMediaController;
import com.qb.ListViewVideoViewExp.R;
import com.qb.ListViewVideoViewExp.bean.VideoInfo;
import com.qb.ListViewVideoViewExp.utils.DensityUtil;
import com.qb.ListViewVideoViewExp.views.FullScreenVideoView;
import com.qb.ListViewVideoViewExp.views.QVideoView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/11/5.
 */
public class SecondActivity  extends Activity implements View.OnClickListener{

    private VideoInfo mVideoInfo;
    private FullScreenVideoView mVideoView;
    private float width;
    private float height;
    private View mTopView;
    private View mBottomView;
    private ImageView videoImage;
    private TextView videoNameText;
    private ImageButton videoPlayBtn;
    private ProgressBar mProgressBar;
    private TextView mPlayTime;
    private TextView mDurationTime;
    private SeekBar mSeekBar;
    private static final int HIDE_TIME = 5000;
    private static final int Video_TIME = 500;
    private Timer mTimer;
    private boolean isFirstPlay=true;
    private VideoHander mHandler;
    private RelativeLayout qVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mVideoInfo= (VideoInfo) getIntent().getExtras().get("videoInfo");
        initView();
        initListener();
        initData();
    }

    private void initData() {
        mHandler =  new VideoHander();
    }

    private void initListener() {
        findViewById(R.id.exp_btn).setOnClickListener(this);
        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);
        threshold = DensityUtil.dip2px(this, 18);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(hideRunnable);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    int time = progress * mVideoView.getDuration() / 100;
                    mVideoView.seekTo(time);
                }
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.GONE);
//               setVideoSize();


                mVideoView.start();
//                if (playTime != 0) {
//                    mVideoView.seekTo(playTime);
//                }
                isFirstPlay=false;
                mHandler.removeCallbacks(hideRunnable);
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
                mDurationTime.setText(formatTime(mVideoView.getDuration()));
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                }, 0, Video_TIME);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                mPlay.setImageResource(R.drawable.video_btn_down);
                mPlayTime.setText("00:00");
                mSeekBar.setProgress(0);
            }
        });
        mVideoView.setOnTouchListener(mTouchListener);
    }

    private void setVideoSize() {

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400);
            qVideoView.setLayoutParams(lp);
        }else
        {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            qVideoView.setLayoutParams(lp);
        }
    }

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mTopView.setVisibility(View.GONE);
                }
            });
            mTopView.startAnimation(animation);

            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mBottomView.setVisibility(View.GONE);
                }
            });
            mBottomView.startAnimation(animation1);
            videoPlayBtn.setVisibility(View.INVISIBLE);
        } else {
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_top);
            mTopView.startAnimation(animation);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_bottom);
            mBottomView.startAnimation(animation1);
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
            videoPlayBtn.setVisibility(View.VISIBLE);
        }
    }
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            showOrHide();
        }
    };

    class VideoHander extends  Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mVideoView.getCurrentPosition() > 0) {
                        mPlayTime.setText(formatTime(mVideoView.getCurrentPosition()));
                        int progress = mVideoView.getCurrentPosition() * 100 / mVideoView.getDuration();
                        mSeekBar.setProgress(progress);
                        if (mVideoView.getCurrentPosition() > mVideoView.getDuration() - 100) {
                            mPlayTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                        mSeekBar.setSecondaryProgress(mVideoView.getBufferPercentage());
                    } else {
                        mPlayTime.setText("00:00");
                        mSeekBar.setProgress(0);
                    }

                    break;
                case 2:
                    showOrHide();
                    break;

                default:
                    break;
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }
    private void initView() {
        videoImage=(ImageView) findViewById(R.id.video_image);
        videoNameText=(TextView)findViewById(R.id.video_name_text);
        videoPlayBtn=(ImageButton)findViewById(R.id.video_play_btn);
        videoPlayBtn.setOnClickListener(this);
        mProgressBar=(ProgressBar)findViewById(R.id.progressbar);
        mVideoView= (FullScreenVideoView) findViewById(R.id.videoview);
        mPlayTime = (TextView) findViewById(R.id.play_time);
        mDurationTime = (TextView) findViewById(R.id.total_time);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mVideoView.requestFocus();
        mVideoView.setVideoPath(mVideoInfo.getVideoUrl());
        mTopView = findViewById(R.id.top_layout);
        mBottomView = findViewById(R.id.bottom_layout);
        qVideoView= (RelativeLayout) findViewById(R.id.qvideoview);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = DensityUtil.getWidthInPx(this);
            width = DensityUtil.getHeightInPx(this);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = DensityUtil.getWidthInPx(this);
            height = DensityUtil.getHeightInPx(this);
        }
        super.onConfigurationChanged(newConfig);
    }
    private void setViewGone()
    {
        videoPlayBtn.setVisibility(View.INVISIBLE);
        videoImage.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==videoPlayBtn.getId())
        {
            if(mVideoView.isPlaying())
            {
                mVideoView.pause();
                videoPlayBtn.setImageResource(R.drawable.ic_play_video);
            }else
            {
                mVideoView.start();
                videoPlayBtn.setImageResource(R.drawable.video_btn_on);
                videoPlayBtn.setVisibility(View.GONE);
                if(isFirstPlay)
                {
                    mVideoView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    setViewGone();
                }
            }


        }else  if(view.getId()==R.id.exp_btn)
        {
            if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }else
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
            setVideoSize();
        }


    }

    private class AnimationImp implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }

    }


    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float deltaY = y - mLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    // 声音调节标识
                    boolean isAdjustAudio = false;
                    if (absDeltaX > threshold && absDeltaY > threshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < threshold && absDeltaY > threshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > threshold && absDeltaY < threshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
//                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
//                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
//                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
//                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        if (deltaX > 0) {
//                            forward(absDeltaX);
                        } else if (deltaX < 0) {
//                            backward(absDeltaX);
                        }
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold
                            || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = (int) 0;
                    if (isClick) {
                        showOrHide();
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }

    };

}
