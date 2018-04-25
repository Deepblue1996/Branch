package zou.dahua.branch.event;

/**
 * Created by Deep on 2018/4/21 0021.
 */

public class ShowAnimViewEvent {
    private boolean show;

    public ShowAnimViewEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
