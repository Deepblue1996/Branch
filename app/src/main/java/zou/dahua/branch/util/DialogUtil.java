package zou.dahua.branch.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import zou.dahua.branch.R;

public class DialogUtil {

	public static Dialog dialog(Activity context, View view){
		final Dialog dialog=new Dialog(context, R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setContentView(view);
		//解决dialog宽度显示与view效果不一致的问题
		Window window=dialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		lp.width=(int) (context.getWindowManager().getDefaultDisplay().getWidth()*0.9);
		window.setGravity(Gravity.CENTER);
		window.setAttributes(lp);
		
		return dialog;
	}
	public static Dialog dialog(Activity context, View view, float scale){
		final Dialog dialog=new Dialog(context, R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setContentView(view);
		//解决dialog宽度显示与view效果不一致的问题
		Window window=dialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		lp.width=(int) (context.getWindowManager().getDefaultDisplay().getWidth()*scale);
		window.setGravity(Gravity.CENTER);
		window.setAttributes(lp);

		return dialog;
	}
	public static Dialog dialog(Context context, View view, int style){
		final Dialog dialog=new Dialog(context,style>0?style:R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setContentView(view);
		//解决dialog宽度显示与view效果不一致的问题
		Window window=dialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		lp.width=(int) (context.getResources().getDisplayMetrics().widthPixels*0.9);
		window.setGravity(Gravity.CENTER);
		window.setAttributes(lp);

		return dialog;
	}
	public static Dialog dialog(Context context, View view, int style, float scale, float alpha){
		final Dialog dialog=new Dialog(context,style>0?style:R.style.dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setContentView(view);
		//解决dialog宽度显示与view效果不一致的问题
		Window window=dialog.getWindow();
		WindowManager.LayoutParams lp=window.getAttributes();
		if(scale>0)
			lp.width=(int) (context.getResources().getDisplayMetrics().widthPixels*scale);
		if(alpha>=0)
			lp.alpha=alpha;
		window.setGravity(Gravity.CENTER);
		window.setAttributes(lp);

		return dialog;
	}
}
