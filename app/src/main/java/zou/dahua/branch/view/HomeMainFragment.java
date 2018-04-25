package zou.dahua.branch.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import zou.dahua.branch.R;
import zou.dahua.branch.bean.MediaEntity;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.event.RefreshServicePlayEvent;
import zou.dahua.branch.event.ServiceNextMusicEvent;
import zou.dahua.branch.event.ServicePlayMusicEvent;
import zou.dahua.branch.service.ListenMusicService;
import zou.dahua.branch.util.MusicUtil;
import zou.dahua.branch.util.ToastUtil;
import zou.dahua.branch.weight.BaseSupportFragment;
import zou.dahua.branch.weight.MusicBar;
import zou.dahua.branch.weight.TitleBar;
import zou.dahua.branch.weight.window.PlayMenuListPopWindow;

/**
 * 主页
 * <p>
 * Created by Deep on 2018/4/20 0020.
 */

public class HomeMainFragment extends BaseSupportFragment {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.musicBar)
    MusicBar musicBar;
    @BindView(R.id.localMusicCount)
    TextView localMusicCount;
    @BindView(R.id.localTouchLin)
    LinearLayout localTouchLin;

    /**
     * 再点一次退出程序时间设置
     */
    private static final long WAIT_TIME = 2000L;
    private static long TOUCH_TIME = 0;

    public static List<MediaEntity> mediaEntityList;

    private PlayMenuListPopWindow playMenuListPopWindow;

    /**
     * 懒加载
     */
    public static HomeMainFragment newInstance() {

        Bundle args = new Bundle();

        HomeMainFragment fragment = new HomeMainFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int getFragmentViewId() {
        return R.layout.home_main_fragment;
    }

    @Override
    protected void initView() {
        titleBar.setTitle("本地");

        titleBar.setBack(R.mipmap.ic_player_home_more, new TitleBar.BackListener() {
            @Override
            public void back() {

            }
        });
        titleBar.setMore(R.mipmap.ic_player_home_search, new TitleBar.MoreListener() {
            @Override
            public void more() {

            }
        });

        localMusicCount.setText("(" + 0 + ")");

        RxPermissions permissions = new RxPermissions(_mActivity);
        permissions.request(Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            ToastUtil.show("申请失败，将无法使用部分功能");
                        } else {
                            mediaEntityList = MusicUtil.getAllMediaList(getContext(), "");
                            localMusicCount.setText("(" + mediaEntityList.size() + ")");
                        }
                    }

                    @Override
                    public void onError(java.lang.Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });

        localTouchLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(LocalMusicListFragment.newInstance());
            }
        });

        musicBar.initState(CoreApplication.musicBean());

        musicBar.setOnProgress(new MusicBar.OnProgress() {
            @Override
            public void progress(float pro) {
                CoreApplication.musicBean().setImgProgress(pro);
            }
        });

        musicBar.setOnPlayListener(new MusicBar.OnPlayListener() {
            @Override
            public void click() {
                if (!musicBar.playState()) {
                    musicBar.playSong();
                    CoreApplication.musicBean().setPlaying(true);
                } else {
                    musicBar.pauseSong();
                    CoreApplication.musicBean().setPlaying(false);
                }
                EventBus.getDefault().post(new ServicePlayMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });

        musicBar.setOnMenuListener(new MusicBar.OnMenuListener() {
            @Override
            public void click() {
                playMenuListPopWindow = new PlayMenuListPopWindow(_mActivity, HomeMainFragment.this, getView());
            }
        });

        musicBar.setOnNextListener(new MusicBar.OnNextListener() {
            @Override
            public void click() {
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });

        musicBar.setOnTouchListener(new MusicBar.OnTouchListener() {
            @Override
            public void click() {
                start(PlayMusicFragment.newInstance());
            }
        });

        Intent it = new Intent(_mActivity, ListenMusicService.class);
        _mActivity.startService(it);
    }

    /**
     * 处理回退事件
     *
     * @return true
     */
    @Override
    public boolean onBackPressedSupport() {

        // LogoFragment
        if (_mActivity.getSupportFragmentManager().getBackStackEntryCount() > 2) {
            pop();
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                _mActivity.finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                // TODO: UI提示
                ToastUtil.show("再按一次退出");
            }
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RefreshServicePlayEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshServicePlayEvent event) {
        musicBar.initState(CoreApplication.musicBean());
        if (playMenuListPopWindow != null) {
            playMenuListPopWindow.refresh();
        }
    }
}
