package zou.dahua.branch.weight;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.florent37.viewanimator.ViewAnimator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;
import zou.dahua.branch.R;
import zou.dahua.branch.event.ShowAnimViewEvent;

/**
 * 全局基类
 *
 * @author Deep
 * @date 2017/11/10 0010
 */

public abstract class BaseSupportFragment extends SupportFragment {

    /**
     * Fragment父视图对象
     */
    protected View superView;
    /**
     * 对话框对象(私有)
     */
    private Dialog loadingDialog;

    /**
     * 显示对话框调用次数(私有)
     */
    private int isShow = 0;

    /**
     * 设置视图资源
     *
     * @return id
     */
    protected abstract int getFragmentViewId();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    @BindView(R.id.windowBefore)
    View windowBefore;

    /**
     * 显示等待框
     *
     * @param str 内容
     */
    protected void showDia(String str) {
        if (isShow == 0) {
            loadingDialog = DialogMaker.showCommenWaitDialog(getActivity(), str, null, false, "");
        } else {
            if (loadingDialog != null) {
                loadingDialog.setTitle(str);
            }
        }
        isShow++;
    }

    /**
     * 隐藏等待框
     */
    protected void disDia() {
        if (isShow == 1) {
            loadingDialog.dismiss();
        }
        isShow--;
    }

    /**
     * 封装初始化视图
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        superView = inflater.inflate(R.layout.base_fragment, container, false);

        View childView = inflater.inflate(getFragmentViewId(), container, false);

        ((LinearLayout) superView.findViewById(R.id.content)).addView(childView);

        // 默认使用 ButterKnife
        ButterKnife.bind(this, superView);

        initView();

        return superView;
    }

    public void showAnimView() {
        ViewAnimator.animate(windowBefore).alpha(1.0f).duration(200).start();
    }

    public void hideAnimView() {
        ViewAnimator.animate(windowBefore).alpha(0.0f).duration(200).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideSoftInput();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShowAnimViewEvent event) {
        if(event.isShow()) {
            showAnimView();
        } else {
            hideAnimView();
        }
    }
}
