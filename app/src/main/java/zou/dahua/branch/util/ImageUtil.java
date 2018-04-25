package zou.dahua.branch.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import zou.dahua.branch.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 图片加载
 * Created by Deep on 2018/3/23 0023.
 */

public class ImageUtil {

    private final static int defaultImg = R.mipmap.ic_img_load;

    public static void show(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(defaultImg))
                .into(imageView);
    }

    public static void showFit(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(defaultImg))
                .into(imageView);
    }

    public static void show(Context context, int id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(defaultImg))
                .into(imageView);
    }

    public static void show(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(defaultImg))
                .into(imageView);
    }

    public static void showCircle(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(defaultImg)
                        .circleCrop())
                .into(imageView);
    }

    public static void showCircle(Context context, int id, ImageView imageView) {
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(defaultImg)
                        .circleCrop())
                .into(imageView);
    }
}
