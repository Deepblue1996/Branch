package zou.dahua.branch.weight;

import android.view.animation.LinearInterpolator;

/**
 * 线性
 * Created by Deep on 2018/4/21 0021.
 */

public abstract class MineLinearInterpolator extends LinearInterpolator {

    private float progress;

    @Override
    public float getInterpolation(float input) {
        progress = super.getInterpolation(input);
        onProgress(progress * 360);
        return progress;
    }

    public abstract void onProgress(float nowProgress);
}
