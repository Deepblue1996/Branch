package zou.dahua.branch.weight;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import zou.dahua.branch.R;
import zou.dahua.branch.util.DisplayUtil;

/**
 * 自定义标题栏
 * Created by Deep on 2017/11/10 0010.
 */

public class TitleBar extends LinearLayout {

    private LinearLayout leftLinear;
    private LinearLayout rightLinear;
    private LinearLayout centerLinear;
    private RelativeLayout barLin;
    private ImageView viewLoading;
    private ImageView leftImageView;
    private ImageView rightImageView;
    private ImageView rightImageView2;
    private TextView leftTextView;
    private TextView rightTextView;
    private TextView titleTextView;
    private View statusBottomLineShow;

    private Animation mRefreshAnim;

    private Context mContext;

    private int openLoadingNum;

    public TitleBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View childView = mInflater.inflate(R.layout.title_bar_layout, null);

        LayoutParams linearParams = (LayoutParams) childView
                .findViewById(R.id.status)
                .getLayoutParams();
        //取控件textView当前的布局参数
        linearParams.height = DisplayUtil.getStatusBarHeight(getContext());
        childView.findViewById(R.id.status).setLayoutParams(linearParams);

        leftLinear = childView.findViewById(R.id.left_show);
        rightLinear = childView.findViewById(R.id.right_show);
        centerLinear = childView.findViewById(R.id.centerLinear);
        barLin = childView.findViewById(R.id.barLin);
        viewLoading = childView.findViewById(R.id.viewLoading);
        leftImageView = childView.findViewById(R.id.left_icon);
        leftTextView = childView.findViewById(R.id.left_text);
        rightImageView = childView.findViewById(R.id.right_icon);
        rightImageView2 = childView.findViewById(R.id.right_icon_2);
        rightTextView = childView.findViewById(R.id.right_text);
        statusBottomLineShow = childView.findViewById(R.id.status_bottom_line_show);

        titleTextView = childView.findViewById(R.id.title_text);

        mRefreshAnim = AnimationUtils.loadAnimation(getContext(), R.anim.diy_probar);

        addView(childView);

        openLoadingNum = 0;
    }

    public synchronized void stopLoading() {

        openLoadingNum--;

        if (openLoadingNum == 0) {
            viewLoading.setVisibility(GONE);
            mRefreshAnim.reset();
            viewLoading.clearAnimation();
        }

    }

    public synchronized void startLoading() {

        centerLinear.setVisibility(VISIBLE);
        viewLoading.setVisibility(VISIBLE);

        openLoadingNum++;

        if (openLoadingNum == 1) {
            mRefreshAnim.reset();
            viewLoading.clearAnimation();
            viewLoading.startAnimation(mRefreshAnim);
        }
    }

    public void visibleLoading() {
        viewLoading.setVisibility(VISIBLE);
    }

    public void setTitle(String title) {
        barLin.setVisibility(VISIBLE);
        centerLinear.setVisibility(VISIBLE);
        titleTextView.setVisibility(VISIBLE);
        titleTextView.setText(title);
    }

    public void setBack(@DrawableRes int id, final BackListener backListener) {
        barLin.setVisibility(VISIBLE);
        leftLinear.setVisibility(VISIBLE);
        leftImageView.setVisibility(VISIBLE);
        leftImageView.setImageResource(id);
        leftLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                backListener.back();
            }
        });
    }

    public void setBack(String text, final BackListener backListener) {
        barLin.setVisibility(VISIBLE);
        leftLinear.setVisibility(VISIBLE);
        leftTextView.setVisibility(VISIBLE);
        leftTextView.setText(text);
        leftLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                backListener.back();
            }
        });
    }

    public void setBack(@DrawableRes int id, String text, final BackListener backListener) {
        barLin.setVisibility(VISIBLE);
        leftLinear.setVisibility(VISIBLE);
        leftImageView.setVisibility(VISIBLE);
        leftTextView.setVisibility(VISIBLE);
        leftImageView.setImageResource(id);
        leftTextView.setText(text);
        leftLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                backListener.back();
            }
        });
    }

    public void setMore(@DrawableRes int id, final MoreListener moreListener) {
        barLin.setVisibility(VISIBLE);
        rightLinear.setVisibility(VISIBLE);
        rightImageView.setVisibility(VISIBLE);
        rightImageView.setImageResource(id);
        rightLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener.more();
            }
        });
    }

    public void setMore(String text, final MoreListener moreListener) {
        barLin.setVisibility(VISIBLE);
        rightLinear.setVisibility(VISIBLE);
        rightImageView.setVisibility(GONE);
        rightTextView.setVisibility(VISIBLE);
        rightTextView.setText(text);
        rightLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener.more();
            }
        });
    }

    public void setMore(@DrawableRes int id, String text, final MoreListener moreListener) {
        barLin.setVisibility(VISIBLE);
        rightLinear.setVisibility(VISIBLE);
        rightImageView.setVisibility(VISIBLE);
        rightTextView.setVisibility(VISIBLE);
        rightImageView.setImageResource(id);
        rightTextView.setText(text);
        rightLinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener.more();
            }
        });
    }

    public void setMore(@DrawableRes int id, @DrawableRes int id2, final MoreListener moreListener, final MoreListener moreListener2) {
        barLin.setVisibility(VISIBLE);
        rightLinear.setVisibility(VISIBLE);
        rightImageView.setVisibility(VISIBLE);
        rightImageView2.setVisibility(VISIBLE);
        rightTextView.setVisibility(VISIBLE);
        rightTextView.setVisibility(VISIBLE);
        rightImageView.setImageResource(id);
        rightImageView2.setImageResource(id2);

        rightImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener.more();
            }
        });
        rightImageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moreListener2.more();
            }
        });
    }

    public void hideLine() {
        statusBottomLineShow.setVisibility(GONE);
    }

    public interface BackListener {
        void back();
    }

    public interface MoreListener {
        void more();
    }
}
