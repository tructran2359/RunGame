package com.tructran2359.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.tructran2359.app.R;
import com.tructran2359.app.helper.LogHelper;
import com.tructran2359.app.helper.MyHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    //widgets
    private ImageView mIVContent, mIVRequiredArea;
    private TextView mTVScore, mTVHighestScore, mTVFinalScore, mTVTitle;
    private Button mBtnStart, mBtnShareOnFacebook;
    private FrameLayout mFLSurface;
    private RelativeLayout mRLGroupControl, mRLRoot;
    private LoginButton mLoginButton;

    //game data
    private float mAngle = 0;
    private float mAngleIncrement = DEFAULT_ANGLE_INCREMENT;
    private int mTotalScore = 0;
    private int mScoreIncrement = DEFAULT_SCORE_INCREMENT;
    private int mHighestScore = 0;
    private int mAreaMin, mAreaMax;
    private Random mRandom = new Random();


    //game constants
    private static final float DEFAULT_ANGLE_INCREMENT = 3;
    private static final int DEFAULT_SCORE_INCREMENT = 100;
    private static final int REQUIRED_AREA_MIN = 270;
    private static final int REQUIRED_AREA_MAX = 359;
    private static final float FRAME_TIME = (float) 1000 / 60;
    private static final int CIRCLE_DEGREE = 360;
    private static final int NUMBER_OF_ACCEPTED_AREA = 7;

    private static final int ACCEPTED_AREA_30 = 0;
    private static final int ACCEPTED_AREA_45 = 1;
    private static final int ACCEPTED_AREA_60 = 2;
    private static final int ACCEPTED_AREA_90 = 3;
    private static final int ACCEPTED_AREA_120 = 4;
    private static final int ACCEPTED_AREA_150 = 5;
    private static final int ACCEPTED_AREA_180 = 6;

    private static final List<Integer> LIST_ACCEPTED_AREA_DRAWABLE_ID = Arrays.asList(
            R.drawable.accept_30,
            R.drawable.accept_45,
            R.drawable.accept_60,
            R.drawable.accept_90,
            R.drawable.accept_120,
            R.drawable.accept_150,
            R.drawable.accept_180);

    private static final List<Integer> LIST_ACCEPTED_AREA_DEGREE = Arrays.asList(
            30,
            45,
            60,
            90,
            120,
            150,
            180);
    private MyHandler mHandler;
    public static final int HANDLER_START = 2;
    public static final int HANDLER_STOP = 3;

    //facebook
    private UiLifecycleHelper mUIHelper;
    private Session.StatusCallback mStatusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler();

        mUIHelper = new UiLifecycleHelper(this, null);
        mUIHelper.onCreate(savedInstanceState);

        initWidgets();

        Session session = Session.getActiveSession();
        if (session == null) {
            session = new Session(this);
            Session.setActiveSession(session);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogHelper.i("actResult", "requestCode: " + requestCode + " resultCode: " + resultCode);
        Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(this, requestCode, resultCode, data);
        }
        mUIHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle bundle) {
                MyHelper.showToast(MainActivity.this, getString(R.string.done));
            }

            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception e, Bundle bundle) {
                MyHelper.showToast(MainActivity.this, getString(R.string.error));
                LogHelper.i("facebook", "onActivityResult " + e.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUIHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUIHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUIHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUIHelper.onSaveInstanceState(outState);
    }


    //================== my methods ================================

    private int mCount = 0;

    public void initWidgets() {
        mRLRoot = (RelativeLayout) findViewById(R.id.act_main_rl_root);
        mTVScore = (TextView) findViewById(R.id.act_main_tv_score);
        mTVFinalScore = (TextView) findViewById(R.id.act_main_tv_final_score);
        mTVHighestScore = (TextView) findViewById(R.id.act_main_tv_highest_score);
        mTVTitle = (TextView) findViewById(R.id.act_main_tv_title);

        mIVContent = (ImageView) findViewById(R.id.act_main_iv_content);
        mIVRequiredArea = (ImageView) findViewById(R.id.act_main_iv_required_area);

        MyHelper.ScreenSize screenSize = MyHelper.getScreenSize(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIVContent.getLayoutParams();
        params.width = screenSize.width - 100;
        params.height = params.width;

        mIVContent.setLayoutParams(params);
        mIVContent.setScaleType(ImageView.ScaleType.FIT_XY);

        mIVRequiredArea.setLayoutParams(params);
        mIVRequiredArea.setScaleType(ImageView.ScaleType.FIT_XY);

        mRLGroupControl = (RelativeLayout) findViewById(R.id.act_main_group_control);

        mBtnStart = (Button) findViewById(R.id.act_main_btn_start_stop);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });


        mBtnShareOnFacebook = (Button) findViewById(R.id.act_main_btn_share_on_facebook);
        mBtnShareOnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShareFacebook();
            }
        });

        mFLSurface = (FrameLayout) findViewById(R.id.act_main_fl_surface_view);
        mFLSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    checkEvent();
                    return true;
                }
                return false;
            }
        });
        mFLSurface.setEnabled(false);

        mRLGroupControl.setVisibility(View.VISIBLE);
        mFLSurface.setVisibility(View.GONE);
        mBtnShareOnFacebook.setVisibility(View.GONE);
        mTVFinalScore.setVisibility(View.GONE);
        mTVHighestScore.setVisibility(View.GONE);
        mTVTitle.setVisibility(View.GONE);

        mLoginButton = (LoginButton) findViewById(R.id.act_main_btn_fb_login);
        mLoginButton.setPublishPermissions(Arrays.asList("email", "user_photos", "publish_actions"));
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_START:
                    startAnimation();
                    break;

                case HANDLER_STOP:
                    stopAnimation();
                    break;
            }

        }
    }

    public void startGame() {
        resetData();
        randomAcceptedArea();
        mRLGroupControl.setVisibility(View.GONE);
        mFLSurface.setVisibility(View.VISIBLE);
        mFLSurface.setEnabled(true);

        mHandler.sendEmptyMessage(HANDLER_START);
    }

    public void startAnimation() {
        mIVContent.setRotation(mAngle);
        mAngle += mAngleIncrement;
        mHandler.sendEmptyMessageDelayed(HANDLER_START, (long) FRAME_TIME);
    }

    public void stopAnimation() {
        mHandler.removeMessages(HANDLER_START);
        mFLSurface.setEnabled(false);
        showResult();
    }

    public void resetData() {
        mIVContent.setRotation(0);
        mTotalScore = 0;
        mAngle = 0;
        mAngleIncrement = DEFAULT_ANGLE_INCREMENT;
        mScoreIncrement = DEFAULT_SCORE_INCREMENT;
        mTVScore.setText(getString(R.string.total_score) + ": " + mTotalScore);
    }

    public void showResult() {
        mRLGroupControl.setVisibility(View.VISIBLE);
        mFLSurface.setVisibility(View.VISIBLE);
        mFLSurface.setEnabled(false);
        mBtnShareOnFacebook.setVisibility(View.VISIBLE);
        mTVTitle.setVisibility(View.VISIBLE);
        mTVHighestScore.setVisibility(View.VISIBLE);
        mTVFinalScore.setVisibility(View.VISIBLE);

        mIVRequiredArea.setRotation(mAreaMin);
        mIVContent.setRotation(mAngle);

        if (mTotalScore >= mHighestScore) {
            mHighestScore = mTotalScore;
            mTVTitle.setText(getString(R.string.new_record));
        } else {
            mTVTitle.setText(getString(R.string.game_over));
        }
        mTVHighestScore.setText(getString(R.string.highest_score) + ": " + mHighestScore);
        mTVFinalScore.setText(getString(R.string.your_score) + ": " + mTotalScore);


    }

    public void checkEvent() {
        int angle = (int) mAngle % 360;
        if ((mAreaMin < mAreaMax && (angle >= mAreaMin && angle <= mAreaMax))
                || ((mAreaMin > mAreaMax)) && (angle >= mAreaMin || angle <= mAreaMax)) {
            if (mCount >= 4) {
                mAngleIncrement += 1;
                mScoreIncrement += 100;
                mCount = 0;
            } else {
                mCount++;
            }
            mTotalScore += mScoreIncrement;
            mTVScore.setText(getString(R.string.total_score) + ": " + mTotalScore);
            randomAcceptedArea();
        } else {
            LogHelper.i("StopGame", "angle: " + angle + " min: " + mAreaMin + " max: " + mAreaMax);
            mHandler.sendEmptyMessage(HANDLER_STOP);
        }
    }

    public void randomAcceptedArea() {
        int randomAngle = mRandom.nextInt(CIRCLE_DEGREE);
        int randomAcceptedArea = mRandom.nextInt(NUMBER_OF_ACCEPTED_AREA);

        mIVRequiredArea.setImageResource(LIST_ACCEPTED_AREA_DRAWABLE_ID.get(randomAcceptedArea));
        int acceptedAreaDegree = LIST_ACCEPTED_AREA_DEGREE.get(randomAcceptedArea);


        mAreaMin = (randomAngle + 0) % 360;
        mAreaMax = (randomAngle + acceptedAreaDegree) % 360;
        mIVRequiredArea.setRotation((float) randomAngle);
    }

    public void doShareFacebook() {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            shareScreenShot();
        } else {
            LogHelper.i("facebook", "login");
            session.openForPublish(
                    new Session.OpenRequest(this)
                            .setCallback(mStatusCallback)
                            .setPermissions(Arrays.asList("email", "publish_actions", "publish_stream")));
        }
    }

    public void shareScreenShot() {
        LogHelper.i("facebook", "share");
        mRLRoot.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(mRLRoot.getDrawingCache());
        mRLRoot.setDrawingCacheEnabled(false);
        ArrayList<Bitmap> listBmp = new ArrayList<>();
        listBmp.add(bmp);

        Session session = Session.getActiveSession();
        List<String> permistions = session.getPermissions();
        for (String permistion : permistions) {
            LogHelper.i("facebook", permistion);
        }
        LogHelper.i("facebook", "share screenshot");
        if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.PHOTOS)) {
            FacebookDialog fbDialog = new FacebookDialog.PhotoShareDialogBuilder(this).addPhotos(listBmp).build();
            mUIHelper.trackPendingDialogCall(fbDialog.present());
        } else {
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), bmp, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    MyHelper.showToast(MainActivity.this, response.getError() == null ? "no error" : response.getError().toString());
                    LogHelper.i("facebook", response.getError() == null ? "no error" : response.getError().toString());
                }
            });
            Bundle bundle = request.getParameters();
            bundle.putString("message", getString(R.string.facebook_message));
            request.setParameters(bundle);
            request.executeAsync();
        }
    }
}
