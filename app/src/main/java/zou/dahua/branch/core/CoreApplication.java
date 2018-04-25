package zou.dahua.branch.core;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.lzx.musiclibrary.manager.MusicManager;
import com.prohua.dove.Dove;
import com.prohua.dove.base.Nest;

import zou.dahua.branch.bean.MusicStateBean;
import zou.dahua.branch.net.GlobalNetInterface;

/**
 * 主类
 *
 * @author Deep
 * @date 2017/11/10 0010
 */

public class CoreApplication extends Application {

    private static CoreApplication instance;
    // Dove Task.
    private static GlobalNetInterface jobTask;
    // Music List
    private static MusicStateBean musicStateBean;

    public static MusicStateBean musicBean() {
        return musicStateBean;
    }

    public static CoreApplication getInstance() {
        return instance;
    }

    public static GlobalNetInterface jobTask() {
        return jobTask;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // 初始化音乐引擎
        MusicManager.get().setContext(this).init();

        // First make a nest, used to raise dove, let dove send you a letter.
        jobTask = Dove.birth(CoreApplication.getInstance().getApplicationContext(),
                Nest.build().setBaseUrl("http://open.iciba.com/")
                        .setInterfaceClass(GlobalNetInterface.class));

        musicStateBean = new MusicStateBean();
    }

    /**
     * 兼容4.4
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
