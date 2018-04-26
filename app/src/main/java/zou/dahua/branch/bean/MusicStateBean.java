package zou.dahua.branch.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 音乐控件状态
 * Created by Deep on 2018/4/21 0021.
 */

public class MusicStateBean implements Serializable {

    // 当前进度
    private int progress = 0;
    // 转盘进度
    private float imgProgress = 0;
    // 是否播放
    private boolean isPlaying = false;
    // 播放曲单
    private List<MediaEntity> mediaEntityList = new ArrayList<>();
    // 定位
    private int location = -1;

    public float getImgProgress() {
        return imgProgress;
    }

    public void setImgProgress(float imgProgress) {
        this.imgProgress = imgProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public List<MediaEntity> getMediaEntityList() {
        return mediaEntityList;
    }

    public void setMediaEntityList(List<MediaEntity> mediaEntityList) {
        this.mediaEntityList.clear();
        this.mediaEntityList.addAll(mediaEntityList);
        this.location = 0;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getSongName() {
        if(mediaEntityList.size() > 0) {
            return mediaEntityList.get(location).title;
        } else {
            return "播放列表为空";
        }
    }

    public String getSongerName() {
        if(mediaEntityList.size() > 0) {
            return mediaEntityList.get(location).artist;
        } else {
            return "";
        }
    }

    public MediaEntity getSong() {
        if(mediaEntityList.size() > 0) {
            return mediaEntityList.get(location);
        } else {
            return null;
        }
    }
}
