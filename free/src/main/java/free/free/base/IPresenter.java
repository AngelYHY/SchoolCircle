package free.free.base;

import android.support.annotation.NonNull;

/**
 * Author:  ljo_h
 * Date:    2016/8/9
 * Description:
 */
public interface IPresenter<T> {
    int RESULT_NOTHING = 1;
    int RESULT_DATA = 2;
    int RESULT_PICTURE = 3;
    int RESULT_DATA_QQ = 4;
    int RESULT_DATA_EVERNOTE = 5;
    int RESULT_DATA_USER = 6;

    int REQUEST_NOTHING = 1;

    /**
     * 注入View，使之能够与View相互响应
     *
     * @param iView
     */
    void attachView(@NonNull T iView);

    /**
     * 释放资源
     */
    void detachView();
}
