package free.free.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import free.free.R;
import free.free.util.netstatu.NetChangeObserver;
import free.free.util.netstatu.NetStateReceiver;
import free.free.util.netstatu.NetUtils;

/**
 * Created by jact on 2016/2/7.
 */
public abstract class BaseAppCompatActivity extends RxAppCompatActivity {

    protected IPresenter mIPresenter;
    protected Context mContext = null;
    /**
     * network status
     * 网络状态
     */
    protected NetChangeObserver mNetChangeObserver = null;
    /**
     * loading com.android.project.activity.view controller
     * 加载view的控制
     */
//    private VaryViewHelperController mVaryViewHelperController = null;

    /**
     * overridePendingTransition mode
     * Activity 过渡动画类型
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_left);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_right);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.slide_out_in_top, R.anim.slide_out_out_top);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
        super.onCreate(savedInstanceState);

        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        this.mContext = this;

        BaseAppManager.getInstance().addActivity(this);

        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                super.onNetConnected(type);
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                onNetworkDisConnected();
            }
        };

        NetStateReceiver.registerObserver(mNetChangeObserver);
        initInjector();
        initView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
//        if (null != getLoadingTargetView()) {
//            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
//        }
    }

    @Override
    public void finish() {
        super.finish();
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_left);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_right);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.slide_out_in_top, R.anim.slide_out_out_top);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        BaseAppManager.getInstance().removeActivity(this);
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
//        if (isBindEventBus()) {
//            EventBus.getDefault().unregister(this);
//        }
    }

    /**
     * startActivity
     *
     * @param clazz
     */
    public void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    public void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    public void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    public void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * toggle show loading
     *
     * @param toggle
     */
//    protected void toggleShowLoading(boolean toggle, String msg) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//        if (toggle) {
//            mVaryViewHelperController.showLoading(msg);
//        } else {
//            mVaryViewHelperController.restore();
//        }
//    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
//    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//        if (toggle) {
//            mVaryViewHelperController.showEmpty(msg, onClickListener);
//        } else {
//            mVaryViewHelperController.restore();
//        }
//    }

    /**
     * toggle show error
     *
     * @param toggle
     */
//    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//        if (toggle) {
//            mVaryViewHelperController.showError(msg, onClickListener);
//        } else {
//            mVaryViewHelperController.restore();
//        }
//    }

    /**
     * toggle show network error
     *
     * @param toggle
     */
//    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//        if (toggle) {
//            mVaryViewHelperController.showNetworkError(onClickListener);
//        } else {
//            mVaryViewHelperController.restore();
//        }
//    }

    /**
     * init com.android.project.activity.view
     *
     * @return
     */
    protected abstract void initView();

    /**
     * toggle overridePendingTransition
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();

    /**
     * get the overridePendingTransition mode
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();

    /**
     * is bind eventBus
     *
     * @return
     */
//    protected abstract boolean isBindEventBus();

    /**
     * get ContentViewLayoutID
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

    protected abstract void initInjector();

    /**
     * network connected
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);

    /**
     * network disconnected
     */
    protected abstract void onNetworkDisConnected();

    /**
     * get loading target com.android.project.activity.view
     */
//    protected abstract View getLoadingTargetView();

}
