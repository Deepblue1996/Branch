package zou.dahua.branch.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import zou.dahua.branch.core.CoreApplication;

/**
 * Toast工具类
 * @author Administrator
 */
public class ToastUtil {

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }
    
    private static Toast toast = null;
    public static void show(Context context, View v){
    	if(toast != null){
    		toast.cancel();
    		toast = null;
    	}
    	toast = new Toast(context);
    	toast.setView(v);
    	toast.show();
    }

    public static void show(CharSequence text){
        if (null == toast) {
            toast = Toast.makeText(CoreApplication.getInstance().getApplicationContext() , text, Toast.LENGTH_SHORT);
        }else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}
