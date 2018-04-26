package zou.dahua.branch.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.github.mmin18.widget.RealtimeBlurView;
import com.lzx.musiclibrary.manager.MusicManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import zou.dahua.branch.R;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.event.RefreshServicePlayEvent;
import zou.dahua.branch.event.ServiceLastMusicEvent;
import zou.dahua.branch.event.ServiceNextMusicEvent;
import zou.dahua.branch.event.ServicePlayMusicEvent;
import zou.dahua.branch.util.DisplayUtil;
import zou.dahua.branch.util.ImageUtil;
import zou.dahua.branch.util.PreciseCompute;
import zou.dahua.branch.util.TimerToUtil;
import zou.dahua.branch.weight.BaseSupportFragment;
import zou.dahua.branch.weight.MineLinearInterpolator;
import zou.dahua.branch.weight.MusicBar;
import zou.dahua.branch.weight.window.PlayMenuListPopWindow;

/**
 * 音乐界面
 * Created by Deep on 2018/4/24 0024.
 */

public class PlayMusicFragment extends BaseSupportFragment {

    @BindView(R.id.backgroundBg)
    ImageView backgroundBg;
    @BindView(R.id.realtimeBlur)
    RealtimeBlurView realtimeBlur;
    @BindView(R.id.barHeight)
    View barHeight;
    @BindView(R.id.nowSongName)
    TextView nowSongName;
    @BindView(R.id.nowSongNameEr)
    TextView nowSongNameEr;
    @BindView(R.id.playerMenu)
    ImageView playerMenu;
    @BindView(R.id.playerPlay)
    ImageView playerPlay;
    @BindView(R.id.playerLast)
    ImageView playerLast;
    @BindView(R.id.playerNext)
    ImageView playerNext;
    @BindView(R.id.backLin)
    ImageView backLin;
    @BindView(R.id.progressBg)
    RelativeLayout progressBg;
    @BindView(R.id.progress)
    View progress;
    @BindView(R.id.progressText)
    TextView progressText;
    @BindView(R.id.progressAllText)
    TextView progressAllText;

    private PlayMenuListPopWindow playMenuListPopWindow;

    private float rotation = 0;

    private ViewAnimator viewAnimator;
    private MusicBar.OnProgress onProgress;

    private Timer timer;

    /**
     * 懒加载
     */
    public static PlayMusicFragment newInstance() {

        Bundle args = new Bundle();

        PlayMusicFragment fragment = new PlayMusicFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int getFragmentViewId() {
        return R.layout.play_music_fragment;
    }

    @Override
    protected void initView() {
        ImageUtil.show(getContext(), R.mipmap.ic_launcher_round, backgroundBg);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) barHeight.getLayoutParams();
        layoutParams.height = DisplayUtil.getStatusBarHeight(getContext());
        barHeight.setLayoutParams(layoutParams);

        if (CoreApplication.musicBean().isPlaying()) {
            playerPlay.setImageResource(R.mipmap.ic_player_home_pause);
        } else {
            playerPlay.setImageResource(R.mipmap.ic_player_home_play);
        }

        nowSongName.setText(CoreApplication.musicBean().getSongName());
        nowSongNameEr.setText(CoreApplication.musicBean().getSongerName());

        playerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CoreApplication.musicBean().isPlaying()) {
                    playSong();
                } else {
                    pauseSong();
                }
                EventBus.getDefault().post(new ServicePlayMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });

        playerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMenuListPopWindow = new PlayMenuListPopWindow(_mActivity, PlayMusicFragment.this, getView());
            }
        });
        playerLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ServiceLastMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });
        playerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });

        rotation = CoreApplication.musicBean().getImgProgress();
        backgroundBg.setRotation(CoreApplication.musicBean().getImgProgress());

        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realtimeBlur.setVisibility(View.GONE);
                pop();
            }
        });

        if (CoreApplication.musicBean().getSong() != null) {
            progressText.setText(TimerToUtil.songDateToString(MusicManager.get().getProgress()));
            progressAllText.setText(TimerToUtil.songDateToString(CoreApplication.musicBean().getSong().duration));
        }
    }

    private void playSong() {

        CoreApplication.musicBean().setPlaying(true);

        playerPlay.setImageResource(R.mipmap.ic_player_home_pause);

        if (viewAnimator != null) {
            viewAnimator.cancel();
            viewAnimator = null;
        }

        viewAnimator = ViewAnimator
                .animate(backgroundBg)
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
                        rotation = backgroundBg.getRotation() % 360;
                        backgroundBg.setRotation(rotation);
                    }
                }).start();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (CoreApplication.musicBean().isPlaying()) {
                            // ----精准到后六位

                            progressBg.measure(0, 0);

                            RelativeLayout.LayoutParams layoutParams =
                                    (RelativeLayout.LayoutParams) progress.getLayoutParams();
                            // 总长度百分比分配
                            double one = PreciseCompute.div(progressBg.getWidth(), 100, 6);

                            // 总时间百分比分配
                            double two = PreciseCompute.div(MusicManager.get().getDuration(), 100, 6);

                            // 进度占几个百分比
                            double three = PreciseCompute.div(MusicManager.get().getProgress(), two, 6);

                            // 百分比乘以分配，得出长度
                            double five = PreciseCompute.mul(three, one);

                            // 四舍五入到整数
                            double six = PreciseCompute.round(five, 0);

                            layoutParams.width = (int) six;

                            progress.setLayoutParams(layoutParams);

                            progressText.setText(TimerToUtil.songDateToString(MusicManager.get().getProgress()));
                            progressAllText.setText(TimerToUtil.songDateToString(CoreApplication.musicBean().getSong().duration));
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private void pauseSong() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        CoreApplication.musicBean().setPlaying(false);

        if (viewAnimator != null) {
            playerPlay.setImageResource(R.mipmap.ic_player_home_play);
            viewAnimator.cancel();
            viewAnimator = null;
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        realtimeBlur.setVisibility(View.VISIBLE);

        if (CoreApplication.musicBean().isPlaying()) {
            playSong();
        } else {
            pauseSong();
        }

        ViewAnimator.animate(realtimeBlur).alpha(0.0f, 1.0f).duration(200).start();
    }

    @Override
    public boolean onBackPressedSupport() {
        realtimeBlur.setVisibility(View.GONE);
        return super.onBackPressedSupport();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshServicePlayEvent event) {

        if (CoreApplication.musicBean().isPlaying()) {
            playSong();
        } else {
            pauseSong();
        }

        nowSongName.setText(CoreApplication.musicBean().getSongName());
        nowSongNameEr.setText(CoreApplication.musicBean().getSongerName());

        if (playMenuListPopWindow != null) {
            playMenuListPopWindow.refresh();
        }
    }

}
