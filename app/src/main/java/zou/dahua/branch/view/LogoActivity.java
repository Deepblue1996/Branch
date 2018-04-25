package zou.dahua.branch.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.gyf.barlibrary.ImmersionBar;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.prohua.roundlayout.RoundAngleFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import zou.dahua.branch.R;
import zou.dahua.branch.bean.EveryDayBean;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.util.ImageUtil;
import zou.dahua.branch.util.RxCountDown;
import zou.dahua.branch.util.ToastUtil;

public class LogoActivity extends AppCompatActivity {

    private final static int LOGO_SECOND = 3;

    @BindView(R.id.logoImg)
    ImageView logoImg;
    @BindView(R.id.startScreen)
    ImageView startScreen;
    @BindView(R.id.timeCountText)
    TextView timeCountText;
    @BindView(R.id.everyDayText)
    TextView everyDayText;
    @BindView(R.id.timeLin)
    RoundAngleFrameLayout timeLin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_activity_layout);

        // 默认使用 ButterKnife
        ButterKnife.bind(this);

        // 状态栏透明和间距处理
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                // 原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();

        ImageUtil.show(getBaseContext(), R.mipmap.ic_launcher, startScreen);

        getData();
    }

    private void getData() {
        Dove.fly(CoreApplication.jobTask().getEveryDay(), new Dover<EveryDayBean>() {
            @Override
            public void call(@NonNull Disposable disposable) {
            }

            @Override
            public void don(Disposable disposable, @NonNull EveryDayBean everyDayBean) {
                ImageUtil.show(getBaseContext(), everyDayBean.getPicture2(), logoImg);
                everyDayText.setText(everyDayBean.getNote());

                RxCountDown.countdown(LOGO_SECOND).doOnSubscribe(new Consumer<Disposable>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        ViewAnimator.animate(timeLin).alpha(1.0f).duration(200).start();
                        timeCountText.setText("Start");
                    }
                }).subscribe(new Observer<Integer>() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onError(Throwable e) {
                        timeCountText.setText("Error");
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {
                        timeCountText.setText("Go");
                        startActivity(new Intent(LogoActivity.this, CoreActivity.class));
                        finish();
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        timeCountText.setText("" + integer + "s");
                    }
                });
            }

            @Override
            public void die(Disposable disposable, @NonNull Throwable throwable) {
                ToastUtil.show(throwable.getMessage());
            }

            @Override
            public void end(Disposable disposable) {
            }
        });
    }
}
