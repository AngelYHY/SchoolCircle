package free.free.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.ProgressView;

import free.free.R;

/**
 * Created by freestar on 2017/1/7 0007.
 */

public class EmptyView extends FrameLayout {

    LinearLayout mHeaderContainer;
    ViewStub mLoadingViewStub;
    ViewStub mEmptyViewStub;
    ViewStub mErrorViewStub;
    private Context mContext;
    private Mode mState;
    private String mEmptySubTitle;

    EmptyViewHolder mEmptyViewHolder;
    ErrorViewHolder mErrorViewHolder;
    LoadViewHolder mLoadViewHolder;
    /**
     * 空标题
     */
    private String mEmptyMsg;
    private View mEmptyFrame;
    private View mLoadingFrame;
    private View mErrorFrame;

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        setEmptySubTitle(context.getString(R.string.common_empty_msg));
    }

    private EmptyView setEmptySubTitle(String msg) {
        mEmptySubTitle = msg;
        return this;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, this, true);
        mHeaderContainer = (LinearLayout) view.findViewById(R.id.header_container);
        mLoadingViewStub = (ViewStub) view.findViewById(R.id.loading_viewStub);
        mEmptyViewStub = (ViewStub) view.findViewById(R.id.empty_viewStub);
        mErrorViewStub = (ViewStub) view.findViewById(R.id.error_viewStub);
        this.mState = Mode.NONE;
    }

    public void showEmpty() {
        if (mEmptyViewStub != null) {
            mEmptyFrame = mEmptyViewStub.inflate();
            mEmptyViewStub = null;
            mEmptyViewHolder = new EmptyViewHolder(mEmptyFrame);
        }
        if (!TextUtils.isEmpty(this.mEmptyMsg)) {
            mEmptyViewHolder.title.setText(mEmptyMsg);
            mEmptyViewHolder.title.setVisibility(VISIBLE);
        }
        mEmptyViewHolder.image.setImageResource(R.drawable.ic_anction_nodata);
        mEmptyFrame.setVisibility(VISIBLE);

    }

    private enum Mode {
        EMPTY, ERROR, LOAD, LOADING, NONE
    }

    private static final class EmptyViewHolder {
        ImageView image;
        TextView subTitle;
        TextView title;

        public EmptyViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.img);
            subTitle = (TextView) v.findViewById(R.id.sub_title);
            title = (TextView) v.findViewById(R.id.title);
        }
    }

    private static final class ErrorViewHolder {
        ImageView mErrorImg;
        TextView mErrorTitle;
        ImageView mErrorRefreshImg;
        TextView mErrorSubMsg;

        public ErrorViewHolder(View v) {
            mErrorImg = (ImageView) v.findViewById(R.id.error_img);
            mErrorTitle = (TextView) v.findViewById(R.id.error_title);
            mErrorRefreshImg = (ImageView) v.findViewById(R.id.error_refresh_img);
            mErrorSubMsg = (TextView) v.findViewById(R.id.error_sub_msg);
        }
    }

    private static final class LoadViewHolder {
        ProgressView mProgressPvCircular;

        public LoadViewHolder(View v) {
            mProgressPvCircular = (ProgressView) v.findViewById(R.id.progress_pv_circular);
        }
    }

    public void showLoading() {
        if (this.mState == Mode.LOADING)
            return;
        if (this.mLoadingViewStub != null) {
            this.mLoadingFrame = this.mLoadingViewStub.inflate();
            this.mLoadViewHolder = new LoadViewHolder(this.mLoadingFrame);
            this.mLoadingViewStub = null;
        }
        if (this.mEmptyFrame != null) {
            this.mEmptyFrame.setVisibility(View.GONE);
            this.mEmptyViewHolder.image.setImageDrawable(null);
        }
        if (this.mErrorFrame != null) {
            this.mErrorFrame.setVisibility(View.GONE);
            this.mErrorViewHolder.mErrorImg.setImageDrawable(null);
        }
        this.mLoadingFrame.setVisibility(View.VISIBLE);
        setVisibility(View.VISIBLE);
        this.mState = Mode.LOADING;

        this.mLoadViewHolder.mProgressPvCircular.start();
    }

    public void hide() {
        if (this.mState == Mode.NONE) {
            return;
        }

        if (this.mEmptyViewHolder != null) {
            this.mEmptyViewHolder.image.setImageDrawable(null);
        }
        if (this.mErrorViewHolder != null) {
            this.mErrorViewHolder.mErrorImg.setImageDrawable(null);
            this.mErrorViewHolder.mErrorRefreshImg.clearAnimation();
        }

        setVisibility(View.GONE);
        this.mState = Mode.NONE;
    }

    public void showError(String paramString) {
        if (this.mState == Mode.ERROR) {
            return;
        }
        if (this.mErrorViewStub != null) {
            this.mErrorFrame = this.mErrorViewStub.inflate();
            this.mErrorViewHolder = new ErrorViewHolder(this.mErrorFrame);
            this.mErrorViewStub = null;
        }
        if (!TextUtils.isEmpty(paramString)) {
            this.mErrorViewHolder.mErrorTitle.setText(paramString);
        }
        this.mErrorViewHolder.mErrorImg.setImageResource(R.drawable.ic_anction_nodata);
        this.mErrorViewHolder.mErrorRefreshImg.clearAnimation();
//        this.mErrorViewHolder.mErrorSubMsg.setOnClickListener(this);
        this.mErrorFrame.setVisibility(View.VISIBLE);
        if (this.mEmptyFrame != null) {
            this.mEmptyFrame.setVisibility(View.GONE);
            this.mEmptyViewHolder.image.setImageDrawable(null);
        }
        if (this.mLoadingFrame != null) {
            this.mLoadingFrame.setVisibility(View.GONE);
        }
        setVisibility(View.VISIBLE);
        this.mState = Mode.ERROR;

        showProgress();
    }

    private void showProgress() {
        if (this.mState == Mode.LOAD)
            return;
        Animation localAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_repeat);
        localAnimation.setInterpolator(new LinearInterpolator());
        this.mErrorViewHolder.mErrorRefreshImg.startAnimation(localAnimation);
        this.mState = Mode.LOAD;
    }

}
