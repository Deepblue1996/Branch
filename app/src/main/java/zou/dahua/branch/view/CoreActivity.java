package zou.dahua.branch.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import zou.dahua.branch.R;

public class CoreActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_activity);

        // 默认使用 ButterKnife
        ButterKnife.bind(this);

        // 状态栏透明和间距处理
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                // 原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();

        // 竖屏显示
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        loadRootFragment(R.id.fl_container, HomeMainFragment.newInstance());
    }

}
