package zou.dahua.branch.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;
import zou.dahua.branch.bean.EveryDayBean;

/**
 * 全局接口
 * Created by Deep on 2018/4/20 0020.
 */

public interface GlobalNetInterface {

    /**
     * 每日一句
     */
    @GET("http://open.iciba.com/dsapi/")
    Observable<EveryDayBean>
    getEveryDay();
}
