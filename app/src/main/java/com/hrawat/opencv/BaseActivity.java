package com.hrawat.opencv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private Toast toast;
    private ProgressDialog progress;
    private boolean isAlive;
    private boolean isActive;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV is loaded");
        }
    }

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG)
            Log.e(TAG, "onCreate of: " + this.getClass().getSimpleName());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isAlive = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded Successfully");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV not Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, baseLoaderCallback);
        }
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isAlive() {
        return isAlive;
    }

    public boolean isActive() {
        return isActive;
    }

    //Start Activity And finish Previous one
    public void startActivityWithFinish(Class c) {
        startActivityWithFinish(c, null);
    }

    public void startActivityWithFinish(Class c, Bundle bundle) {
        Intent intent = new Intent(this, c);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void startActivityClearTop(Class c) {
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void startActivityClearTop(Class c, Bundle bundle) {
        Intent intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    protected void startActivity(Class c, Bundle bundle) {
        Intent intent = new Intent(this, c);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showToast(final String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    protected void cancelToast() {
        if (toast != null)
            toast.cancel();
    }

    /*  protected void replaceFragment(int container, BaseFragment fragment) {
          replaceFragment(container, fragment, false);
      }

      protected void replaceFragment(int container, BaseFragment fragment, boolean addToBackStack) {
          FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
          fragmentTransaction.replace(container, fragment);
          if (addToBackStack)
              fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
          fragmentTransaction.commit();
      }
  */
    public void showProgress() {
        hideProgress();
        progress = ProgressDialog.show(new ContextThemeWrapper(this,
                android.R.style.Theme_Holo_Light), "", "", true, false);
        progress.setContentView(R.layout.progressbar);
    }

    public void hideProgress() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    protected void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
