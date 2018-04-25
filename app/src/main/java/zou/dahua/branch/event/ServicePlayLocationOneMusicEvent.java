package zou.dahua.branch.event;

import java.util.ArrayList;
import java.util.List;

import zou.dahua.branch.bean.MediaEntity;
import zou.dahua.branch.core.CoreApplication;

/**
 * Created by Deep on 2018/4/24 0024.
 */

public class ServicePlayLocationOneMusicEvent {
    private MediaEntity mediaEntity;

    public ServicePlayLocationOneMusicEvent(MediaEntity mediaEntity) {
        this.mediaEntity = mediaEntity;
        List<MediaEntity> mediaEntityList = new ArrayList<>();
        mediaEntityList.add(mediaEntity);
        CoreApplication.musicBean().setMediaEntityList(mediaEntityList);
    }

    public MediaEntity getMediaEntity() {
        return mediaEntity;
    }

    public void setMediaEntity(MediaEntity mediaEntity) {
        this.mediaEntity = mediaEntity;
    }
}
