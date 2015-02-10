package com.tructran2359.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tructran2359.app.R;
import com.tructran2359.app.helper.MyHelper;

public class MainActivity extends ActionBarActivity {


    private ImageView mIVContent;
    private Button mBtnStart;
    private RelativeLayout mRLSurface;
    private boolean mIsRunning = false;
    private float mAngle = 0;
    private float mAngleIncreasement = DEFAULT_ANGLE_INCREASEMENT;
    private MyHandler mHandler;

    private static final float FRAME_TIME = (float) 1000 / 100;
    private static final float DEFAULT_ANGLE_INCREASEMENT = 1;

    public static final int HANDLER_START = 1;
    public static final int HANDLER_MOVE = 2;
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

    public void initWidgets() {
        mIVContent = (ImageView) findViewById(R.id.act_main_iv_content);
        MyHelper.ScreenSize screenSize = MyHelper.getScreenSize(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIVContent.getLayoutParams();
        params.width = screenSize.width * 2 / 3;
        params.height = params.width;
        mIVContent.setLayoutParams(params);
        mIVContent.setScaleType(ImageView.ScaleType.FIT_XY);

        mBtnStart = (Button) findViewById(R.id.act_main_btn_start_stop);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) {
                    mIsRunning = false;
                    mBtnStart.setText("Start");
                    mHandler.sendEmptyMessageDelayed(HANDLER_STOP, (long) FRAME_TIME);
                } else {
                    mIsRunning = true;
                    mBtnStart.setText("Stop");
                    mHandler.sendEmptyMessageDelayed(HANDLER_MOVE, (long) FRAME_TIME);
                }
            }
        });

        mRLSurface = (RelativeLayout) findViewById(R.id.act_main_rl_surface_view);
        mRLSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAngleIncreasement += 1;
            }
        });

    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MOVE:
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
        mAngle += mAngleIncreasement;
        mHandler.sendEmptyMessageDelayed(HANDLER_MOVE, (long) FRAME_TIME);
    }

    public void stopAnimation() {
        mHandler.removeMessages(HANDLER_MOVE);
        mAngleIncreasement = DEFAULT_ANGLE_INCREASEMENT;
    }
}
