package com.tructran2359.app.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tructran2359.app.R;
import com.tructran2359.app.helper.LogHelper;

public class TestActivity extends ActionBarActivity {

    private View mVCenter, mVCircle, mVRoot;
    private float mCenterWidth = 0, mCenterHeight = 0, mRadius = 0;
    private Button mBtnStart;
    private float mAngle = 0;
    private static final float FRAME_TIME = (float)1000/100;
    //    private MyHandler mHandler = new MyHandler();
    private boolean mIsRunning = false;
    private Worker mWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            calculatePosition();
        }
    }

    //my methods===================================================
    public void initWidgets() {
        mVCenter = findViewById(R.id.act_test_v_center);
        mVCircle = findViewById(R.id.act_test_v_circle);
        mVRoot = findViewById(R.id.act_main_root);
        mBtnStart = (Button) findViewById(R.id.act_test_btn_start);
        mWorker = new Worker();
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRunning) {
                    mIsRunning = false;
                    mWorker.stop();
                    mBtnStart.setText("Start");
                } else {
                    mIsRunning = true;
                    mWorker.start();
                    mBtnStart.setText("Stop");
                }
            }
        });
    }

    public void calculatePosition() {
        int viewW = mVCenter.getWidth();
        int viewH = mVCenter.getHeight();

        if (mCenterHeight == 0 || mCenterWidth == 0 || mRadius == 0) {
            int width = mVRoot.getWidth();
            int height = mVRoot.getHeight();

            mCenterWidth =      ( (float)width / 2);
            mCenterHeight =     ( (float)height / 2);
            mRadius = mCenterWidth - 100;
        }

        mVCenter.setX(mCenterWidth - ( (float)viewW / 2 ));
        mVCenter.setY(mCenterHeight - ( (float)viewH / 2 ));

        mVCircle.setX(mCenterWidth - (float)viewW / 2 - mRadius);
        mVCircle.setY(mCenterHeight - (float)viewH / 2 );

        LogHelper.i("position", "center " + mVCenter.getX() + " : " + mVCenter.getY());
        LogHelper.i("position", "circle " + mVCircle.getX() + " : " + mVCircle.getY());
//        mHandler.sendEmptyMessageDelayed(0, (long)FRAME_TIME);
    }

//    public class MyHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            doAnimation();
//            mHandler.sendEmptyMessageDelayed(0, (long)FRAME_TIME);
//        }
//    }

    private Thread mMyThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                doAnimation();
                try {
                    Thread.sleep((long)FRAME_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogHelper.i("thread", e.toString());
                }
            }
        }
    });

    public class Worker implements Runnable {
        private Thread backgroundThread;

        public void start() {
            if (backgroundThread == null) {
                backgroundThread = new Thread(this);
            }
            backgroundThread.start();
        }

        public void stop() {
            if (backgroundThread != null) {
                backgroundThread.interrupt();
                backgroundThread = null;
            }
        }

        @Override
        public void run() {
            while (!backgroundThread.isInterrupted()) {
                while (true) {
                    doAnimation();
                    try {
                        Thread.sleep((long)FRAME_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        LogHelper.i("thread", e.toString());
                        return;
                    }
                }
            }
        }
    }

    public void doAnimation() {
        mAngle += 0.025;

        float w = (float)mVCircle.getWidth() / 2;
        float h = (float)mVCircle.getHeight() / 2;

        float x = mRadius * (float)Math.sin((double)mAngle) - w + mCenterWidth;
        float y = mRadius * (float)Math.cos((double)mAngle) - h + mCenterHeight;

        mVCircle.setX(x);
        mVCircle.setY(y);

    }
}
