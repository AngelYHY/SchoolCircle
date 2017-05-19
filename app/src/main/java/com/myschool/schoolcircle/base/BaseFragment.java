package com.myschool.schoolcircle.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.myschool.schoolcircle.injector.component.DaggerFragmentComponent;
import com.myschool.schoolcircle.injector.component.FragmentComponent;
import com.myschool.schoolcircle.injector.module.FragmentModule;
import com.myschool.schoolcircle.main.R;
import com.orhanobut.logger.Logger;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import free.free.base.BaseAppCompatFragment;
import free.free.base.IPresenter;
import free.free.base.IView;
import free.free.util.ToastUtils;
import free.free.util.netstatu.NetUtils;
import free.free.widget.EmptyView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jact on 2016/2/16.
 */
public abstract class BaseFragment extends BaseAppCompatFragment implements IView {
    protected MaterialDialog dialog;
    protected FragmentComponent mFragmentComponent;
    protected EmptyView mEmptyView;
    public int page = 1;
    protected final static int PAGE_SIZE = 5;

    protected IPresenter mIPresenter;


    protected BaseActivity activity;
    protected AppApplication application;

    @Override
    protected void initInjector() {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .applicationComponent(((AppApplication) getActivity().getApplication()).getApplicationComponent())
                .build();
        init();
    }

    private void init() {
        activity = (BaseActivity) getActivity();
        application = (AppApplication) activity.getApplication();
    }

    protected void intentToActivity(Context activity, Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(activity,targetActivity);
        startActivity(intent);
    }

    //强制显示软键盘
    public void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


//    @Override
//    protected View getLoadingTargetView() {
//        return null;
//    }

//    protected int getScreenHeight() {
//        return getActivity().findViewById(android.R.id.content).getHeight();
//    }

    /*@Override
    public <T> Observable bindToLifecycle(Observable<T> observable) {
        return observable.compose(this.<T>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }*/


    @Override
    public <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    /**
     * startActivity
     *
     * @param clazz
     */
    public void readyGo(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    public void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
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
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
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
        Intent intent = new Intent(getActivity(), clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return null;
    }

//    @Override
//    protected boolean isBindEventBus() {
//        return false;
//    }

    @Override
    protected void onNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    public void showException(Throwable ex, int flag) {
        //toggleShowError(true, msg, null);
        if (flag == 1) {
            showFail(1);
        } else if (flag == 2) {
            showFail(2);
            return;
        }
        if (ex instanceof Exception) {
            //无网络
            if (ex instanceof UnknownHostException) {
                if (mEmptyView != null) {
                    mEmptyView.showError("网络异常");
                } else {
                    showMsg("网络异常");
                }
            } else if (ex instanceof SocketTimeoutException) {
                showMsg(mContext.getString(R.string.common_error_service) + "具体原因：连接超时");
            } else {
                showMsg(mContext.getString(R.string.common_error_service) + "具体原因：" + ex.getMessage());
            }
            Logger.e(ex.getMessage());
        }
    }

    @Override
    public void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.showEmpty();
        }
    }

    @Override
    public void showEmpty(String msg) {
        if (mEmptyView != null) {
            mEmptyView.showEmpty();
        }
    }


    @Override
    public void showLoading() {
        if (mEmptyView != null) {
            mEmptyView.showLoading();
        }
    }

    @Override
    public void hide() {
        if (mEmptyView != null) {
                mEmptyView.hide();
        }
    }

    @Override
    public void showMsg(String msg) {
//        ToastUtils.LongToast(getActivity().getApplicationContext(), msg);
        ToastUtils.shortToast(getActivity().getApplicationContext(), msg);
    }

    @Override
    public void showDialog() {
        showDialog(getString(R.string.loading_default_text));
    }

    @Override
    public void showDialog(String content) {
        if (dialog == null) {
            dialog = new MaterialDialog.Builder(getActivity())
                    .progress(true, 0)
                    .content(content)
                    .canceledOnTouchOutside(false)
                    .build();
            dialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showFail(int flag) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIPresenter != null) {
            mIPresenter.detachView();
        }
        /*RefWatcher refWatcher = ((AppApplication)getActivity().getApplication()).getRefWatcher(getActivity());
        refWatcher.watch(this);*/
    }

}
