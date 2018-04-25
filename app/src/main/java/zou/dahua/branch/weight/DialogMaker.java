package zou.dahua.branch.weight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import zou.dahua.branch.R;
import zou.dahua.branch.util.DialogUtil;
import zou.dahua.branch.util.JStringKit;

/**
 * 对app的所有对话框进行管理
 *
 * @author jishengjie
 */
@SuppressLint("InflateParams")
public class DialogMaker {

    public interface DialogCallBack {
        /**
         * 对话框按钮点击回调
         *
         * @param position 点击Button的索引.
         * @param tag
         */
        public void onButtonClicked(Dialog dialog, int position, Object tag);

        /**
         * 当对话框消失的时候回调
         *
         * @param tag
         */
        public void onCancelDialog(Dialog dialog, Object tag);
    }

    /**
     * 显示统一风格的等待对话框。没有标题
     *
     * @param msg             对话框内容
     * @param callBack        对话框回调
     * @param isCanCancelabel 是否可以取消
     * @param tag
     */
    public static Dialog showCommenWaitDialog(Context context, String msg, final DialogCallBack callBack, boolean isCanCancelabel, final Object tag) {
//		View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_wait_common, null);
        View contentView = LayoutInflater.from(context).inflate(R.layout.load_loading_layout, null);
        final Dialog dialog = DialogUtil.dialog(context, contentView, R.style.dialog, 0.0f, 0.8f);
        dialog.setOwnerActivity((Activity) context);

        //ImageView imageView= (ImageView) contentView.findViewById(R.id.loading_img);
        //AnimationDrawable animationDrawable= (AnimationDrawable) imageView.getBackground();
        //animationDrawable.start();
        if (JStringKit.isNotBlank(msg)) {
            TextView contentTv = (TextView) contentView.findViewById(R.id.dialog_content_tv);
            contentTv.setText(msg);
        }
        if (callBack != null) {
            dialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {

                    callBack.onCancelDialog((Dialog) dialog, tag);
                }
            });
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(isCanCancelabel);
        dialog.setContentView(contentView);
        dialog.show();
        return dialog;

    }
}
