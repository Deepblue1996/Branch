package zou.dahua.branch.weight;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import zou.dahua.branch.R;
import zou.dahua.branch.bean.MusicStateBean;
import zou.dahua.branch.util.DisplayUtil;

/**
 * 音乐显示控件
 * <p>
 * Created by Deep on 2018/4/21 0021.
 */

public class MusicBar extends LinearLayout {

    private TextView nowSongName;
    private TextView nowSongNameEr;

    private View progress;

    private ImageView musicImg;

    private ImageView playerPlay;
    private ImageView playerNext;
    private ImageView playerMenu;

    private RelativeLayout touchAll;

    private Context context;

    private ViewAnimator viewAnimator;

    private OnPlayListener onPlayListener;
    private OnNextListener onNextListener;
    private OnMenuListener onMenuListener;
    private OnTouchListener onTouchListener;
    private OnProgress onProgress;

    private boolean isPlay = false;

    private float rotation = 0;

    public MusicBar(Context context) {
        super(context);
        init(context);
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MusicBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        this.context = context;

        View musicBarView = LayoutInflater.from(context).inflate(R.layout.music_bar_layout, null);

        nowSongName = musicBarView.findViewById(R.id.nowSongName);
        nowSongNameEr = musicBarView.findViewById(R.id.nowSongNameEr);
        progress = musicBarView.findViewById(R.id.progress);
        musicImg = musicBarView.findViewById(R.id.musicImg);
        playerPlay = musicBarView.findViewById(R.id.playerPlay);
        playerNext = musicBarView.findViewById(R.id.playerNext);
        playerMenu = musicBarView.findViewById(R.id.playerMenu);
        touchAll = musicBarView.findViewById(R.id.touchAll);

        playerPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlayListener != null) {
                    onPlayListener.click();
                }
            }
        });
        playerNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onNextListener != null) {
                    onNextListener.click();
                }
            }
        });
        playerMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuListener != null) {
                    onMenuListener.click();
                }
            }
        });
        touchAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTouchListener != null) {
                    onTouchListener.click();
                }
            }
        });

        addView(musicBarView);
    }

    public void initState(MusicStateBean musicStateBean) {
        setSongerName(musicStateBean.getSongerName());
        setSongName(musicStateBean.getSongName());
        setProgress(musicStateBean.getProgress());
        rotation = musicStateBean.getImgProgress();
        musicImg.setRotation(rotation);
        if (musicStateBean.isPlaying()) {
            playSong();
        } else {
            pauseSong();
        }
    }

    public void setSongName(String songName) {
        nowSongName.setText(songName);
    }

    public void setSongerName(String songerName) {
        nowSongNameEr.setText(songerName);
    }

    public void setProgress(int progressTime) {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) progress.getLayoutParams();
        layoutParams.width = DisplayUtil.getMobileWidth(context) / 100 * progressTime;
        progress.setLayoutParams(layoutParams);
    }

    public void playSong() {
        isPlay = true;

        playerPlay.setImageResource(R.mipmap.ic_player_home_pause);

        if (viewAnimator != null) {
            viewAnimator.cancel();
            viewAnimator = null;
        }

        viewAnimator = ViewAnimator
                .animate(musicImg)
                .rotation(rotation, rotation + 360)
                .duration(10000)
                .interpolator(new MineLinearInterpolator() {

                    @Override
                    public void onProgress(float nowProgress) {
                        if (onProgress != null) {
                            onProgress.progress(rotation + nowProgress);
                        }
                    }
                })
                .repeatCount(Animation.INFINITE)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        rotation = musicImg.getRotation() % 360;
                        musicImg.setRotation(rotation);
                    }
                }).start();
    }

    public void pauseSong() {
        isPlay = false;

        if (viewAnimator != null) {
            playerPlay.setImageResource(R.mipmap.ic_player_home_play);
            viewAnimator.cancel();
            viewAnimator = null;
        }
    }

    public boolean playState() {
        return isPlay;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    public void setOnNextListener(OnNextListener onNextListener) {
        this.onNextListener = onNextListener;
    }

    public void setOnMenuListener(OnMenuListener onMenuListener) {
        this.onMenuListener = onMenuListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnProgress(OnProgress onProgress) {
        this.onProgress = onProgress;
    }

    public interface OnTouchListener {
        void click();
    }

    public interface OnPlayListener {
        void click();
    }

    public interface OnNextListener {
        void click();
    }

    public interface OnMenuListener {
        void click();
    }

    public interface OnProgress {
        void progress(float pro);
    }
}
