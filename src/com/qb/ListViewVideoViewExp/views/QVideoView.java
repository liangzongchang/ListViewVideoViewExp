package com.qb.ListViewVideoViewExp.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.qb.ListViewVideoViewExp.R;
import com.qb.ListViewVideoViewExp.bean.VideoInfo;
import com.qb.ListViewVideoViewExp.utils.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 视频播放视图
 * @author qubian
 * @data 2015年11月5日
 * @email naibbian@163.com
 */
public class QVideoView extends RelativeLayout implements View.OnClickListener
{
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
    private Context mContext;
    private QVideoViewListener listener;
    private static int VIDEOWIDTH = 400;
    private boolean isFullScreen =false;
    private AudioManager mAudioManager;
    private Toast volumnToast;
    private VolumnView volumnView;

    public QVideoView(Context context) {
        super(context);
        mContext=context;
    }

    public QVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public QVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }

    public void initView(VideoInfo info,QVideoViewListener listener)
    {
        this.mVideoInfo= info;
        this.listener=listener;
        initView();
        initListener();
        initData();

    }
    private void initView()
    {
        videoImage=(ImageView) findViewById(R.id.video_image);
        videoNameText=(TextView)findViewById(R.id.video_name_text);
        videoPlayBtn=(ImageButton)findViewById(R.id.video_play_btn);
        mProgressBar=(ProgressBar)findViewById(R.id.progressbar);
        mVideoView= (FullScreenVideoView) findViewById(R.id.videoview);
        mPlayTime = (TextView) findViewById(R.id.play_time);
        mDurationTime = (TextView) findViewById(R.id.total_time);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mTopView = findViewById(R.id.top_layout);
        mBottomView = findViewById(R.id.bottom_layout);
    }

    private void initData() {
        mHandler =  new VideoHander();
        videoNameText.setText(mVideoInfo.getTitle());
        videoImage.setImageResource(mVideoInfo.getImgResouce());
        mVideoView.setVideoPath(mVideoInfo.getVideoUrl());
    }

    private void initListener() {
        mVideoView.requestFocus();
        videoPlayBtn.setOnClickListener(this);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        findViewById(R.id.exp_btn).setOnClickListener(this);
        width = DensityUtil.getWidthInPx(mContext);
        height = DensityUtil.getHeightInPx(mContext);
        threshold = DensityUtil.dip2px(mContext, 18);
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
                videoPlayBtn.setImageResource(R.drawable.video_btn_down);
                mPlayTime.setText("00:00");
                mSeekBar.setProgress(0);
            }
        });
        mVideoView.setOnTouchListener(mTouchListener);
    }

    private void setVideoSize() {

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VIDEOWIDTH);
            this.setLayoutParams(lp);
        }else
        {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.setLayoutParams(lp);
        }
    }

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(mContext,
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
            Animation animation1 = AnimationUtils.loadAnimation(mContext,
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
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.option_entry_from_top);
            mTopView.startAnimation(animation);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(mContext,
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

    class VideoHander extends Handler
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
//            if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }else
//            {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
            isFullScreen =!isFullScreen;
            // 横竖切屏，让事件到activity中去
            if(listener!=null)
            {
                listener.onclick(view);
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
                    Log.i("AAA","move");
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
//                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
//                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        if (deltaX > 0) {
                            forward(absDeltaX);
                        } else if (deltaX < 0) {
                            backward(absDeltaX);
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
    private void backward(float delataX) {
        if(!isFullScreen)
        {
            return;
        }
        int current = mVideoView.getCurrentPosition();
        int backwardTime = (int) (delataX / width * mVideoView.getDuration());
        int currentTime = current - backwardTime;
        mVideoView.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideoView.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void forward(float delataX) {
        if(!isFullScreen)
        {
            return;
        }
        int current = mVideoView.getCurrentPosition();
        int forwardTime = (int) (delataX / width * mVideoView.getDuration());
        int currentTime = current + forwardTime;
        mVideoView.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideoView.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void volumeDown(float delatY) {
        if(!isFullScreen)
        {
            return;
        }
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / height * max * 3);
        int volume = Math.max(current - down, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        showVolumnView(transformatVolume);
    }

    private void volumeUp(float delatY) {
        if(!isFullScreen)
        {
            return;
        }
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) ((delatY / height) * max * 3);
        int volume = Math.min(current + up, max);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        showVolumnView(transformatVolume);
    }

    public void showVolumnView(float progress) {
        if (volumnToast == null) {
            volumnToast = new Toast(mContext);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.inc_video_volumn, null);
            volumnView = (VolumnView) layout.findViewById(R.id.volumnView);
            volumnToast.setView(layout);
            volumnToast.setGravity(Gravity.BOTTOM, 0, 100);
            volumnToast.setDuration(Toast.LENGTH_SHORT);
        }
        volumnView.setProgress(progress);
        volumnToast.show();
    }
    public interface QVideoViewListener{
        public  void onclick(View view);
    }

}