package free.free.base;

import android.support.annotation.NonNull;

/**
 * Created by codeest on 2016/8/2.
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */
public class BasePresenter<T extends IView> implements IPresenter<T> {

    protected T mView;

    @Override
    public void attachView(@NonNull T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

}
