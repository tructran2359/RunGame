package com.tructran2359.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tructran2359.app.R;
import com.tructran2359.app.helper.LogHelper;
import com.tructran2359.app.helper.MyHelper;

public class MainActivity extends ActionBarActivity {


    private ImageView mIVContent, mIVRequiredArea;
    private TextView mTVScore;
    private Button mBtnStart;
    private FrameLayout mFLSurface;
    private boolean mIsRunning = false;
    private float mAngle = 0;
    private float mAngleIncrement = DEFAULT_ANGLE_INCREMENT;
    private int mScoreIncrement = DEFAULT_SCORE_INCREMENT;
    private MyHandler mHandler;
    private int mTotalScore = 0;

    private static final int REQUIRED_AREA_MIN = 270;
    private static final int REQUIRED_AREA_MAX = 359;

    private static final float FRAME_TIME = (float) 1000 / 100;
    private static final float DEFAULT_ANGLE_INCREMENT = 3;
    private static final int DEFAULT_SCORE_INCREMENT = 100;

    public static final int HANDLER_START = 2;
    public static final int HANDLER_STOP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler();

        initWidgets();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //================== my methods ================================
    
    private int mCount = 0;

    public void initWidgets() {
        mTVScore = (TextView) findViewById(R.id.act_main_tv_score);
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

        mBtnStart = (Button) findViewById(R.id.act_main_btn_start_stop);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIVContent.setRotation(0);
                mAngleIncrement = DEFAULT_ANGLE_INCREMENT;
                mHandler.sendEmptyMessage(HANDLER_START);
            }
        });

        mFLSurface = (FrameLayout) findViewById(R.id.act_main_fl_surface_view);
        mFLSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int angle = (int) mAngle % 360;
                if (angle >= REQUIRED_AREA_MIN && angle <= REQUIRED_AREA_MAX) {
                    if (mCount >= 4) {
                        mAngleIncrement += 1;
                        mScoreIncrement += 100;
                        mCount = 0;
                    } else {
                        mCount ++;
                    }
                    mTotalScore += mScoreIncrement;
                    mTVScore.setText("Total score: " + mTotalScore);
                } else {
                    LogHelper.i("StopGame", "angle: " + angle);
                    mHandler.sendEmptyMessage(HANDLER_STOP);
                }
            }
        });

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

    public void startAnimation() {
        mIVContent.setRotation(mAngle);
        mAngle += mAngleIncrement;
        mHandler.sendEmptyMessageDelayed(HANDLER_START, (long) FRAME_TIME);
    }

    public void stopAnimation() {
        mHandler.removeMessages(HANDLER_START);
        mAngleIncrement = DEFAULT_ANGLE_INCREMENT;
        mScoreIncrement = DEFAULT_SCORE_INCREMENT;
    }
}
