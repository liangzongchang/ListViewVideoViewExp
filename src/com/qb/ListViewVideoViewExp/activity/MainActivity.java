package com.qb.ListViewVideoViewExp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.qb.ListViewVideoViewExp.utils.QMediaController;
import com.qb.ListViewVideoViewExp.R;
import com.qb.ListViewVideoViewExp.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    private ListView mListView;
    private List<VideoInfo> videoList;
    private VideoAdapter adapter;
    private int currentIndex=-1;
    private VideoView mVideoView;
    private QMediaController mMediaController;
    private int playPosition=-1;
    private boolean isPaused=false;
    private boolean isPlaying=false;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        initView();
        initData();
        setVideoScroll();
    }

    private void initView() {
        mListView=(ListView) findViewById(R.id.video_listview);
        findViewById(R.id.top_btn).setOnClickListener(this);
        findViewById(R.id.top_btn2).setOnClickListener(this);
    }

    private void initData()
    {
        String url1="http://ht-mobile.cdn.turner.com/nba/big/teams/kings/2014/12/12/HollinsGlassmov-3462827_8382664.mp4";
        String url2="http://ht-mobile.cdn.turner.com/nba/big/teams/kings/2014/12/12/VivekSilverIndiamov-3462702_8380205.mp4";
        videoList =new ArrayList<VideoInfo>();
        VideoInfo video;
        for(int i=0;i<20;i++){
            if(i%2==0){
                video=new VideoInfo("AAAAA"+i,url1,R.drawable.video2);
            }else{
                video=new VideoInfo("BBBBB"+i,url2,R.drawable.video1);
            }
            videoList.add(video);
        }
        adapter = new VideoAdapter(videoList);
        mListView.setAdapter(adapter);
    }

    private void setVideoScroll()
    {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if ((currentIndex < firstVisibleItem || currentIndex > mListView.getLastVisiblePosition()) && isPlaying) {
                    Log.i("Url:", "mVideoView：" + mVideoView.toString());
                    playPosition = mVideoView.getCurrentPosition();
                    mVideoView.pause();
                    mVideoView.setMediaController(null);
                    isPaused = true;
                    isPlaying = false;
                    Log.i("Url:", "位置：" + playPosition);
                }
            }
        });
        /*		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentIndex=position;
				adapter.notifyDataSetChanged();
			}
		});*/
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.top_btn)
        {
            Intent intent= new Intent(this,SecondActivity.class);
            intent.putExtra("videoInfo",videoList.get(1));
            startActivity(intent);
        }else if(view.getId()==R.id.top_btn2)
        {
            Intent intent= new Intent(this,ThirdActivity.class);
            intent.putExtra("videoInfo",videoList.get(1));
            startActivity(intent);
        }
    }

    class VideoAdapter extends BaseAdapter {
        private List list;
        public VideoAdapter(List videoList)
        {
            this.list=videoList;
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return  createView(position,convertView);
        }
    }

    protected View createView(int position, View convertView) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bindData(position);
        return holder.view;
    }

    public class ViewHolder{
        public ImageView videoImage;
        public TextView videoNameText;
        public ImageButton videoPlayBtn;
        public ProgressBar mProgressBar;
        public View view;
        public ViewHolder()
        {
            view =LayoutInflater.from(mContext).inflate(R.layout.listview_videoitem, null);
            videoImage=(ImageView) view.findViewById(R.id.video_image);
            videoNameText=(TextView)view.findViewById(R.id.video_name_text);
            videoPlayBtn=(ImageButton)view.findViewById(R.id.video_play_btn);
            mProgressBar=(ProgressBar) view.findViewById(R.id.progressbar);
            view.setTag(this);
        }
        public void bindData(final int position)
        {
            VideoInfo videoInfo = videoList.get(position);
            videoImage.setImageDrawable(getResources().getDrawable(videoInfo.getImgResouce()));
            videoNameText.setText(videoInfo.getTitle());

            videoPlayBtn.setVisibility(View.VISIBLE);
            videoImage.setVisibility(View.VISIBLE);
            videoNameText.setVisibility(View.VISIBLE);
            mMediaController = new QMediaController(MainActivity.this);

            if(currentIndex == position){
                videoPlayBtn.setVisibility(View.INVISIBLE);
                videoImage.setVisibility(View.INVISIBLE);
                videoNameText.setVisibility(View.INVISIBLE);

                if(isPlaying || playPosition==-1){
                    if(mVideoView!=null){
                        mVideoView.setVisibility(View.GONE);
                        mVideoView.stopPlayback();
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
                mVideoView=(VideoView) view.findViewById(R.id.videoview);
                mVideoView.setVisibility(View.VISIBLE);
                mMediaController.setAnchorView(mVideoView);
                mMediaController.setMediaPlayer(mVideoView);
                mVideoView.setMediaController(mMediaController);
                mVideoView.requestFocus();
                mProgressBar.setVisibility(View.VISIBLE);
                if(playPosition>0 && isPaused){
                    mVideoView.start();
                    isPaused=false;
                    isPlaying=true;
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    Log.i("Url:",videoInfo.getVideoUrl());
                    mVideoView.setVideoPath(videoInfo.getVideoUrl());
                    isPaused=false;
                    isPlaying=true;
                    Log.i("Url:", "播放新视频");
                }
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mVideoView!=null){
                            mVideoView.seekTo(0);
                            mVideoView.stopPlayback();
                            currentIndex=-1;
                            isPaused=false;
                            isPlaying=false;
                            mProgressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mProgressBar.setVisibility(View.GONE);
                        mVideoView.start();
                    }
                });

            }else{
                videoPlayBtn.setVisibility(View.VISIBLE);
                videoImage.setVisibility(View.VISIBLE);
                videoNameText.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            videoPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentIndex=position;
                    playPosition=-1;
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
