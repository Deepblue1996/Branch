package zou.dahua.branch.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.State;
import com.lzx.musiclibrary.manager.MusicManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import zou.dahua.branch.R;
import zou.dahua.branch.bean.MusicStateBean;
import zou.dahua.branch.core.CoreApplication;
import zou.dahua.branch.event.RefreshServicePlayEvent;
import zou.dahua.branch.event.ServiceLastMusicEvent;
import zou.dahua.branch.event.ServiceNextMusicEvent;
import zou.dahua.branch.event.ServicePlayLocationMusicEvent;
import zou.dahua.branch.event.ServicePlayLocationOneMusicEvent;
import zou.dahua.branch.event.ServicePlayMusicEvent;
import zou.dahua.branch.util.DisplayUtil;
import zou.dahua.branch.view.CoreActivity;

/**
 * 播放器服务
 * Created by Deep on 2018/4/23 0023.
 */

public class ListenMusicService extends Service {

    private static final String TAG = "ListenMusicService";

    private static final String ACTION_SERVICE = "MusicService";
    private static final String ACTION_SERVICE_PLAY = "MusicServicePlay";
    private static final String ACTION_SERVICE_NEXT = "MusicServiceNext";
    private static final String ACTION_SERVICE_START = "MusicServiceStart";

    private MusicStateBean musicStateBean;

    private RemoteViews remoteViews;

    private NotificationManager mNotificationManager;

    private Notification notification;

    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        musicStateBean = CoreApplication.musicBean();

        remoteViews = getRemoteView();

        remoteViews.setOnClickPendingIntent(R.id.playerPlay, getPlay());
        remoteViews.setOnClickPendingIntent(R.id.playerNext, getNext());
        remoteViews.setOnClickPendingIntent(R.id.outLin, getTouch());

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotification();
        }

        notification = getNotification(getBaseContext());

        mNotificationManager.notify(0, notification);

        initBroadcastReceiver();

        MusicManager.get().addPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(SongInfo songInfo) {
                Log.i(TAG, "切换");
            }

            @Override
            public void onPlayerStart() {
            }

            @Override
            public void onPlayerPause() {
            }

            @Override
            public void onPlayCompletion() {
                Log.i(TAG, "完成");
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }

            @Override
            public void onError(String s) {
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            }

            @Override
            public void onAsyncLoading(boolean b) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initNotification() {
        NotificationChannel channel = new NotificationChannel(ACTION_SERVICE,
                ACTION_SERVICE, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.BLUE); //小红点颜色
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        mNotificationManager.createNotificationChannel(channel);
    }

    private void initBroadcastReceiver() {
        // 创建一个IntentFilter对象，将其action指定为
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SERVICE_PLAY);
        intentFilter.addAction(ACTION_SERVICE_NEXT);
        intentFilter.addAction(ACTION_SERVICE_START);
        // 注册广播接收器
        registerReceiver(new ServiceBroadcastReceiver(), intentFilter);
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_SERVICE_PLAY)) {
                EventBus.getDefault().post(new ServicePlayMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            } else if (action.equals(ACTION_SERVICE_NEXT)) {
                EventBus.getDefault().post(new ServiceNextMusicEvent());
                EventBus.getDefault().post(new RefreshServicePlayEvent());
            } else if (action.equals(ACTION_SERVICE_START)) {
                DisplayUtil.collapseStatusBar(getBaseContext());
                Intent intent2 = new Intent(getApplicationContext(), CoreActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
            }
        }
    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand invoke");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务销毁时的回调
     */
    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public Notification getNotification(Context context) {

        return new NotificationCompat.Builder(context, ACTION_SERVICE)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_player_home_play)
                //禁止用户点击删除按钮删除
                .setAutoCancel(false)
                //禁止滑动删除
                .setOngoing(true)
                //取消右上角的时间显示
                .setShowWhen(false)
                //.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(NotificationCompat.PRIORITY_MAX) // 设置该通知优先级
                .setDefaults(NotificationCompat.FLAG_FOREGROUND_SERVICE)
                .setContent(remoteViews)
                .build();
    }

    /**
     * 创建RemoteViews,3.0之后版本使用
     *
     * @return
     */
    public RemoteViews getRemoteView() {
        return new RemoteViews(getPackageName(), R.layout.custom_notification);
    }

    private PendingIntent getPlay() {
        Intent buttonIntent = new Intent(ACTION_SERVICE_PLAY);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        return PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getNext() {
        Intent buttonIntent = new Intent(ACTION_SERVICE_NEXT);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        return PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getTouch() {
        Intent buttonIntent = new Intent(ACTION_SERVICE_START);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        return PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 刷新界面
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshServicePlayEvent event) {
        remoteViews.setTextViewText(R.id.songName, musicStateBean.getSongName());
        remoteViews.setTextViewText(R.id.songNameEr, musicStateBean.getSongerName());

        if (musicStateBean.isPlaying()) {
            remoteViews.setImageViewResource(R.id.playerPlay, R.mipmap.ic_player_home_pause);
        } else {
            remoteViews.setImageViewResource(R.id.playerPlay, R.mipmap.ic_player_home_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.playerPlay, getPlay());
        remoteViews.setOnClickPendingIntent(R.id.playerNext, getNext());
        remoteViews.setOnClickPendingIntent(R.id.outLin, getTouch());

        mNotificationManager.notify(0, notification);
    }

    /**
     * 播放暂停
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServicePlayMusicEvent event) {

        int status = MusicManager.get().getStatus();

        switch (status) {
            case State.STATE_NONE:
            case State.STATE_IDLE:
                // 空闲
                if (musicStateBean.getMediaEntityList().size() > 0) {
                    SongInfo songInfo = new SongInfo();
                    songInfo.setSongId("id" + musicStateBean.getSong().id);
                    songInfo.setSongUrl(musicStateBean.getSong().path);
                    MusicManager.get().playMusicByInfo(songInfo);
                    musicStateBean.setPlaying(true);
                } else {
                    musicStateBean.setPlaying(false);
                }
                break;
            case State.STATE_PLAYING:
                // 播放
                musicStateBean.setPlaying(false);
                MusicManager.get().pauseMusic();
                break;
            case State.STATE_PAUSED:
                // 暂停
                musicStateBean.setPlaying(true);
                MusicManager.get().resumeMusic();
                break;
            case State.STATE_ENDED:
            case State.STATE_ERROR:
                // 结束
                if (musicStateBean.getMediaEntityList().size() > musicStateBean.getLocation() + 1) {
                    musicStateBean.setLocation(musicStateBean.getLocation() + 1);
                    SongInfo songInfo = new SongInfo();
                    songInfo.setSongId("id" + musicStateBean.getSong().id);
                    songInfo.setSongUrl(musicStateBean.getSong().path);
                    MusicManager.get().playMusicByInfo(songInfo);
                    musicStateBean.setPlaying(true);
                } else {
                    musicStateBean.setPlaying(false);
                    if(MusicManager.get().getStatus() == State.STATE_PLAYING) {
                        MusicManager.get().stopMusic();
                    }
                }
                break;
        }
    }

    /**
     * 上一首
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceLastMusicEvent event) {
        if (musicStateBean.getMediaEntityList().size() != 0 && musicStateBean.getLocation() - 1 > 0) {
            Log.i(TAG, "上一首");
            musicStateBean.setLocation(musicStateBean.getLocation() - 1);
            SongInfo songInfo = new SongInfo();
            songInfo.setSongId("id" + musicStateBean.getSong().id);
            songInfo.setSongUrl(musicStateBean.getSong().path);
            MusicManager.get().playMusicByInfo(songInfo);
            musicStateBean.setPlaying(true);
        } else {
            Log.i(TAG, "停止");
            musicStateBean.setPlaying(false);
            if(MusicManager.get().getStatus() == State.STATE_PLAYING) {
                MusicManager.get().stopMusic();
            }
        }
    }

    /**
     * 下一首
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServiceNextMusicEvent event) {
        if (musicStateBean.getMediaEntityList().size() > musicStateBean.getLocation() + 1) {
            Log.i(TAG, "下一首");
            musicStateBean.setLocation(musicStateBean.getLocation() + 1);
            SongInfo songInfo = new SongInfo();
            songInfo.setSongId("id" + musicStateBean.getSong().id);
            songInfo.setSongUrl(musicStateBean.getSong().path);
            MusicManager.get().playMusicByInfo(songInfo);
            musicStateBean.setPlaying(true);
        } else {
            Log.i(TAG, "停止");
            musicStateBean.setPlaying(false);
            if(MusicManager.get().getStatus() == State.STATE_PLAYING) {
                MusicManager.get().stopMusic();
            }
        }
    }

    /**
     * 定位播放
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServicePlayLocationMusicEvent event) {
        SongInfo songInfo = new SongInfo();
        songInfo.setSongId("id" + musicStateBean.getSong().id);
        songInfo.setSongUrl(musicStateBean.getSong().path);
        MusicManager.get().playMusicByInfo(songInfo);
        musicStateBean.setPlaying(true);
    }

    /**
     * 播放指定一首
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ServicePlayLocationOneMusicEvent event) {
        musicStateBean.setPlaying(true);
        SongInfo songInfo = new SongInfo();
        songInfo.setSongId("id" + event.getMediaEntity().id);
        songInfo.setSongUrl(event.getMediaEntity().path);
        MusicManager.get().playMusicByInfo(songInfo);
    }

}
