package zou.dahua.branch.weight.window;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.prohua.universal.UniversalAdapter;
import com.prohua.universal.UniversalViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;
import zou.dahua.branch.R;
import zou.dahua.branch.bean.MediaEntity;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.event.RefreshServicePlayEvent;
import zou.dahua.branch.event.ServiceNextMusicEvent;
import zou.dahua.branch.event.ServicePlayLocationMusicEvent;
import zou.dahua.branch.event.ServicePlayMusicEvent;
import zou.dahua.branch.event.ShowAnimViewEvent;
import zou.dahua.branch.util.DisplayUtil;
import zou.dahua.branch.view.HomeMainFragment;


/**
 * @author Administrator
 */
public class PlayMenuListPopWindow extends PopupWindow {

    private List<MediaEntity> mediaEntityList;

    private UniversalAdapter universalAdapter;

    private RecyclerView recyclerView;

    private LinearLayout haveNull;

    public void fitPopupWindowOverStatusBar(boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(this, needFullScreen);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 弹出选择照片和拍照
     *
     * @param mContext 上下文环境
     * @param parent   点击触发的控件
     */
    public PlayMenuListPopWindow(final Activity mContext, final SupportFragment supportFragment, View parent) {

        super(mContext);
        //不加低版本会报空指针异常

        EventBus.getDefault().post(new ShowAnimViewEvent(true));

        final View view = View.inflate(mContext, R.layout.play_list_window_layout, null);

        setAnimationStyle(R.style.popupWindow_default);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        fitPopupWindowOverStatusBar(true);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        update();

        LinearLayout linearLayout = view.findViewById(R.id.GLin);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.height = DisplayUtil.getMobileHeight(mContext) / 3 * 2;
        linearLayout.setLayoutParams(layoutParams);

        view.findViewById(R.id.outer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        haveNull = view.findViewById(R.id.haveNull);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                EventBus.getDefault().post(new ShowAnimViewEvent(false));
            }
        });

        recyclerView = view.findViewById(R.id.recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mediaEntityList = CoreApplication.musicBean().getMediaEntityList();

        universalAdapter = new UniversalAdapter(mContext, mediaEntityList,
                R.layout.local_music_list_item_window_layout, 0, 0);

        universalAdapter.setOnBindItemView(new UniversalAdapter.OnBindItemView() {
            @Override
            public void onBindItemViewHolder(UniversalViewHolder universalViewHolder, final int i) {
                universalViewHolder.setText(R.id.songName, mediaEntityList.get(i).title);
                universalViewHolder.setText(R.id.songNameEr, mediaEntityList.get(i).artist);
                if (CoreApplication.musicBean().getLocation() == i) {
                    universalViewHolder.vbi(R.id.moreRight).setVisibility(View.VISIBLE);
                    if (CoreApplication.musicBean().isPlaying()) {
                        universalViewHolder.setImgRes(R.id.moreRight, R.mipmap.ic_player_home_pause);
                    } else {
                        universalViewHolder.setImgRes(R.id.moreRight, R.mipmap.ic_player_home_play);
                    }
                } else {
                    universalViewHolder.vbi(R.id.moreRight).setVisibility(View.GONE);
                }

                universalViewHolder.vbi(R.id.delRight).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (i != CoreApplication.musicBean().getLocation()) {
                            if (i > CoreApplication.musicBean().getLocation()) {
                                mediaEntityList.remove(i);
                                refresh();
                            } else {
                                mediaEntityList.remove(i);
                                CoreApplication.musicBean().setLocation(CoreApplication.musicBean().getLocation() - 1);
                                refresh();
                            }
                        } else {
                            mediaEntityList.remove(i);
                            CoreApplication.musicBean().setLocation(CoreApplication.musicBean().getLocation() - 1);
                            EventBus.getDefault().post(new ServiceNextMusicEvent());
                            EventBus.getDefault().post(new RefreshServicePlayEvent());
                            refresh();
                        }
                        if(mediaEntityList.size() == 0) {
                            haveNull.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        universalAdapter.setOnBindItemClick(new UniversalAdapter.OnBindItemClick() {
            @Override
            public void onBindItemClick(View view, int i) {
                if (CoreApplication.musicBean().getLocation() != i) {
                    CoreApplication.musicBean().setLocation(i);
                    EventBus.getDefault().post(new ServicePlayLocationMusicEvent());
                    EventBus.getDefault().post(new RefreshServicePlayEvent());
                } else {
                    EventBus.getDefault().post(new ServicePlayMusicEvent());
                    EventBus.getDefault().post(new RefreshServicePlayEvent());
                }
                universalAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(universalAdapter);

        if (CoreApplication.musicBean().getMediaEntityList().size() == 0) {
            haveNull.setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        universalAdapter.notifyDataSetChanged();
    }
}
