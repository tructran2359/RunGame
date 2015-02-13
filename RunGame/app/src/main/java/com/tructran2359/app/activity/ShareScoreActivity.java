package com.tructran2359.app.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.tructran2359.app.R;
import com.tructran2359.app.helper.LogHelper;
import com.tructran2359.app.helper.MyHelper;

public class ShareScoreActivity extends ActionBarActivity {

    private EditText mETMessage;
    private ImageView mIVScreenShot;
    private Button mBtnOK, mBtnCancel;
    private String mScreenShotPath;
    private Bitmap mBmpScreenShot;

    private MyHelper.ScreenSize mScreenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_score);
        mScreenShotPath = getIntent().getStringExtra(MainActivity.KEY_BITMAP);
        LogHelper.i("savefile", "share score screenshot path:" + mScreenShotPath);
        mBmpScreenShot = BitmapFactory.decodeFile(mScreenShotPath);
        mScreenSize = MyHelper.getScreenSize(this);
        initWidgets();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_score, menu);
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

    //============ my methods ====================
    public void initWidgets() {
        mETMessage = (EditText) findViewById(R.id.act_share_score_et);
        mIVScreenShot = (ImageView) findViewById(R.id.act_share_score_iv);
        mBtnCancel = (Button) findViewById(R.id.act_share_score_btn_cancel);
        mBtnOK = (Button) findViewById(R.id.act_share_score_btn_ok);

        int imgW = mBmpScreenShot.getWidth();
        int imgH = mBmpScreenShot.getHeight();
        float ratio = (float)imgH / (float)imgW;
        int ivW = mScreenSize.width / 2;
        int ivH = (int)(ivW * ratio);

        ViewGroup.LayoutParams params = mIVScreenShot.getLayoutParams();
        params.width = ivW;
        params.height = ivH;
        mIVScreenShot.setLayoutParams(params);
        mIVScreenShot.setImageBitmap(mBmpScreenShot);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScore(mETMessage.getText().toString(), mBmpScreenShot);
            }
        });
    }

    public void showShareDialog(final Bitmap bmp) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_share_score);
        final EditText et = (EditText) dialog.findViewById(R.id.dialog_share_score_et);
        ImageView iv = (ImageView) dialog.findViewById(R.id.dialog_share_score_iv);
        Button btnOK = (Button) dialog.findViewById(R.id.dialog_share_score_btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_share_score_btn_cancel);

        iv.setImageBitmap(bmp);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScore(et.getText().toString(), bmp);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void shareScore(String msg, Bitmap bmp) {

        String message = "";
        if (msg == null || msg.length() == 0) {
            message = getString(R.string.facebook_message);
        } else {
            message = msg;
        }

        message += "\n" + getString(R.string.link_download);

        Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), bmp, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                if (response.getError() == null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                LogHelper.i("facebook", response.getError() == null ? "no error" : response.getError().toString());
            }
        });
        Bundle bundle = request.getParameters();
        bundle.putString("message", message);
        request.setParameters(bundle);
        request.executeAsync();
    }
}
