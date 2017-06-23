package com.hrawat.opencv;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ReadPalmActivity extends AppCompatActivity implements View.OnClickListener,
        CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = ReadPalmActivity.class.getSimpleName();

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV is loaded");
        }
    }

    private Mat mRgba;
    private JavaCameraView javaCameraView;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
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
        setContentView(R.layout.activity_read_palm);
        initViews();
    }

    private void initViews() {
//        ImageView imageFrame = (ImageView) findViewById(R.id.iv_frame);
        Button btnCapture = (Button) findViewById(R.id.btn_view_read);
        btnCapture.setOnClickListener(this);
        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_read:
                captureImage();
                break;
            default:
                break;
        }
    }

    private void captureImage() {
        // custom dialog
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle("Image");
        Bitmap bm = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mRgba, bm);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        // find the imageview and draw it!
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageBitmap(bm);
        Button buttonHistogram = (Button) dialog.findViewById(R.id.dialogButtonHistogram);
        buttonHistogram.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(width, height, CvType.CV_8UC4);
//        mGrey = new Mat(width, height, CvType.CV_8UC1);
//        mCanny = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // first i load the input matrix, then i made a matrix pattern with the new size,
        // then i flip it (so the image flip by 90 degrees) and
        // finally i return to the original matrix size.
        mRgba = inputFrame.gray();
//        mGrey = inputFrame.gray();
//        mGrey = mGrey.t();
//        Core.flip(mGrey, mGrey, -1);
        //grey
//        Imgproc.cvtColor(mRgbaT, mGrey, Imgproc.COLOR_RGB2GRAY);
        //canny
//        Imgproc.Canny(mGrey, mGrey, 50, 80);
//        Mat mRgbaT = mGrey.t();/
//        Core.flip(mRgbaT.t(), mRgbaT, 1);
//        Imgproc.resize(mRgbaT, mRgbaT, mGrey.size());
        return mRgba;
    }
}



