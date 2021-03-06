package zou.dahua.branch.weight;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * 全局基类(支持侧滑)
 * @author Deep
 * @date 2017/11/10 0010
 */

public abstract class BaseSwipeSupportFragment extends SwipeBackFragment {

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
     * @return id
     */
    protected abstract int getFragmentViewId();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 显示等待框
     * @param str 内容
     */
    protected void showDia(String str) {
        if(isShow == 0) {
            loadingDialog = DialogMaker.showCommenWaitDialog(getActivity(), str, null, false, "");
        } else {
            if(loadingDialog != null) {
                loadingDialog.setTitle(str);
            }
        }
        isShow ++;
    }

    /**
     * 隐藏等待框
     */
    protected void disDia() {
        if(isShow == 1) {
            loadingDialog.dismiss();
        }
        isShow--;
    }

    /**
     * 封装初始化视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        superView = inflater.inflate(getFragmentViewId(), container, false);

        // 默认使用 ButterKnife
        ButterKnife.bind(this, superView);

        initView();

        return attachToSwipeBack(superView);
    }

    /**
     * 侧滑默认横向动画
     * @return
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        // return new DefaultHorizontalAnimator();
        // 设置无动画
        // return new DefaultNoAnimator();
        // 设置自定义动画
        // return new FragmentAnimator(enter,exit,popEnter,popExit);

        // 默认竖向(和安卓5.0以上的动画相同)

        return new DefaultHorizontalAnimator();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hideSoftInput();
    }
}
