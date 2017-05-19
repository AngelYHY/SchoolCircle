package free.free.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import free.free.R;
import free.free.util.netstatu.NetChangeObserver;
import free.free.util.netstatu.NetStateReceiver;
import free.free.util.netstatu.NetUtils;

/**
 * Created by jact on 2016/2/7.
 */
public abstract class BaseAppCompatFragment extends RxFragment {

    protected Context mContext = null;
    protected MaterialDialog dialog = null;
    /**
     * network status
     */
    protected NetChangeObserver mNetChangeObserver = null;
    /**
     * loading com.android.project.activity.view controller
     */
//    private VaryViewHelperController mVaryViewHelperController = null;

    /**
     * overridePendingTransition mode
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in,R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in,R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in,R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in,R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in,R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    break;
            }
        }*/
        View view;
//        if (isBindEventBus()) {
//            EventBus.getDefault().register(this);
//        }
        if (getContentViewLayoutID() != 0) {
            view = setContentView(inflater, container, getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        this.mContext = this.getActivity();

        initLoad();
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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initInjector();
        initView();
    }

    public View setContentView(LayoutInflater inflater, ViewGroup container, int layoutResID) {
        View view = inflater.inflate(layoutResID, container, false);
        ButterKnife.bind(this, view);
//        if (null != getLoadingTargetView()) {
//            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
//        }
        return view;
    }

    private void initLoad() {
        dialog = new MaterialDialog.Builder(mContext)
                .content(mContext.getString(R.string.loading_default_text))
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this.mContext, clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this.mContext, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this.mContext, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this.mContext, clazz);
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
//    protected void toggleShowLoading(boolean toggle, String msg, int layoutID, int resID) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//        /*if (toggle) {
//            mVaryViewHelperController.showLoading(msg, layoutID, resID);
//        } else {
//            mVaryViewHelperController.restore();
//        }*/
//    }

    /**
     * toggle show empty
     *
     * @param toggle
     */
//    protected void toggleShowEmpty(boolean toggle, int resId, String msg, View.OnClickListener onClickListener) {
//        if (null == mVaryViewHelperController) {
//            throw new IllegalArgumentException("You must return a right target com.android.project.activity.view for loading");
//        }
//
//       /* if (toggle) {
//            mVaryViewHelperController.showEmpty(resId, msg, onClickListener);
//        } else {
//            mVaryViewHelperController.restore();
//        }*/
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

    protected abstract void initInjector();

    /**
     * get ContentViewLayoutID
     *
     * @return
     */
    protected abstract int getContentViewLayoutID();

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
