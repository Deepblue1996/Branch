package zou.dahua.branch.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.prohua.universal.UniversalAdapter;
import com.prohua.universal.UniversalViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import zou.dahua.branch.R;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.event.RefreshServicePlayEvent;
import zou.dahua.branch.event.ServiceNextMusicEvent;
import zou.dahua.branch.event.ServicePlayLocationMusicEvent;
import zou.dahua.branch.event.ServicePlayLocationOneMusicEvent;
import zou.dahua.branch.event.ServicePlayMusicEvent;
import zou.dahua.branch.weight.BaseSupportFragment;
import zou.dahua.branch.weight.MusicBar;
import zou.dahua.branch.weight.TitleBar;
import zou.dahua.branch.weight.window.PlayMenuListPopWindow;

/**
 * 本地音乐列表
 * <p>
 * Created by Deep on 2018/4/20 0020.
 */

public class LocalMusicListFragment extends BaseSupportFragment {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.musicBar)
    MusicBar musicBar;
    @BindView(R.id.windowBefore)
    View windowBefore;
    @BindView(R.id.haveNull)
    LinearLayout haveNull;

    private UniversalAdapter universalAdapter;

    private PlayMenuListPopWindow playMenuListPopWindow;

    /**
     * 懒加载
     */
    public static LocalMusicListFragment newInstance() {

        Bundle args = new Bundle();

        LocalMusicListFragment fragment = new LocalMusicListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected int getFragmentViewId() {
        return R.layout.local_music_list_fragment;
    }

    @Override
    protected void initView() {
        titleBar.setBack(R.mipmap.ic_back, "本地音乐", new TitleBar.BackListener() {
            @Override
            public void back() {
                EventBus.getDefault().post(new RefreshServicePlayEvent());
                pop();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        universalAdapter = new UniversalAdapter(getContext(), HomeMainFragment.mediaEntityList,
                R.layout.local_music_list_item_layout, R.layout.local_music_list_header_layout, 0);
        universalAdapter.setOnBindItemView(new UniversalAdapter.OnBindItemView() {
            @Override
            public void onBindItemViewHolder(UniversalViewHolder universalViewHolder, int i) {
                if (CoreApplication.musicBean().getSong() != null
                        && CoreApplication.musicBean().getSong().id == HomeMainFragment.mediaEntityList.get(i).id) {
                    universalViewHolder.vbi(R.id.playInfoImg).setVisibility(View.VISIBLE);
                    if (CoreApplication.musicBean().isPlaying()) {
                        universalViewHolder.setImgRes(R.id.playInfoImg, R.mipmap.ic_player_home_pause);
                    } else {
                        universalViewHolder.setImgRes(R.id.playInfoImg, R.mipmap.ic_player_home_play);
                    }
                } else {
                    universalViewHolder.vbi(R.id.playInfoImg).setVisibility(View.GONE);
                }
                universalViewHolder.setText(R.id.songName, HomeMainFragment.mediaEntityList.get(i).title);
                universalViewHolder.setText(R.id.songNameEr, HomeMainFragment.mediaEntityList.get(i).artist);
            }
        });
        universalAdapter.setOnBindHeaderView(new UniversalAdapter.OnBindHeaderView() {
            @Override
            public void onBindHeaderViewHolder(UniversalViewHolder universalViewHolder, int i) {
                universalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CoreApplication.musicBean().setMediaEntityList(HomeMainFragment.mediaEntityList);
                        musicBar.initState(CoreApplication.musicBean());
                        EventBus.getDefault().post(new ServicePlayMusicEvent());
                        EventBus.getDefault().post(new RefreshServicePlayEvent());
                    }
                });
            }
        });
        universalAdapter.setOnBindItemClick(new UniversalAdapter.OnBindItemClick() {
            @Override
            public void onBindItemClick(View view, int i) {
                for (int j = 0; j < CoreApplication.musicBean().getMediaEntityList().size(); j++) {
                    if (CoreApplication.musicBean().getMediaEntityList().get(j).id
                            == HomeMainFragment.mediaEntityList.get(i).id) {
                        if (CoreApplication.musicBean().getSong() != null &&
                                CoreApplication.musicBean().getSong().id != HomeMainFragment.mediaEntityList.get(i).id) {
                            CoreApplication.musicBean().setLocation(j);
                            EventBus.getDefault().post(new ServicePlayLocationMusicEvent());
                            EventBus.getDefault().post(new RefreshServicePlayEvent());
                        } else {
                            EventBus.getDefault().post(new ServicePlayMusicEvent());
                            EventBus.getDefault().post(new RefreshServicePlayEvent());
                        }
                        return;
                    }
                }
                // 播放一首
                EventBus.getDefault().post(new ServicePlayLocationOneMusicEvent(HomeMainFragment.mediaEntityList.get(i)));
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });
        recycler.setAdapter(universalAdapter);

        if(HomeMainFragment.mediaEntityList.size() == 0) {
            haveNull.setVisibility(View.VISIBLE);
        }

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

        musicBar.setOnNextListener(new MusicBar.OnNextListener() {
            @Override
            public void click() {
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }
        });

        musicBar.setOnMenuListener(new MusicBar.OnMenuListener() {
            @Override
            public void click() {
                playMenuListPopWindow = new PlayMenuListPopWindow(_mActivity, LocalMusicListFragment.this, getView());
            }
        });

        musicBar.setOnTouchListener(new MusicBar.OnTouchListener() {
            @Override
            public void click() {
                start(PlayMusicFragment.newInstance());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RefreshServicePlayEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshServicePlayEvent event) {
        musicBar.initState(CoreApplication.musicBean());
        universalAdapter.notifyDataSetChanged();
        if (playMenuListPopWindow != null) {
            playMenuListPopWindow.refresh();
        }
    }
}
