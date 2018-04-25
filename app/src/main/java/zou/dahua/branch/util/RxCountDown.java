package zou.dahua.branch.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * 倒计时(RxJava)
 * Created by Deep on 2017/11/4 0004.
 */

public class RxCountDown {
    public static Observable<Integer> countdown(int time) {
        if (time < 0) time = 0;

        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long aLong) throws Exception {
                        return countTime - aLong.intValue();
                    }
                })
                .take(countTime + 1);

    }
}
