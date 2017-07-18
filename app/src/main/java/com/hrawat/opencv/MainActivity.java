package com.hrawat.opencv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hrawat.opencv.utils.BitmapHelper;
import com.hrawat.opencv.utils.HistogramHelper;
import com.hrawat.opencv.utils.ImageLoaderUtils;
import com.hrawat.opencv.utils.ImagePickerUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.threshold;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 1;
    private static final int RESULT_LOAD_IMG = 21;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageLoaderUtils.initImageLoader(this);
        initNavigationDrawer();
        initViews();
    }

    private void initViews() {
        Button btnReadPalm = (Button) findViewById(R.id.btn_read_palm);
        btnReadPalm.setOnClickListener(this);
        Button btnReadFromStorage = (Button) findViewById(R.id.btn_read_from_existing);
        btnReadFromStorage.setOnClickListener(this);
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.logout:
                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        // Setting Dialog Title
//                        alertDialog.setTitle("Exit");
                        // Setting Dialog Message
                        alertDialog.setMessage("Do you want to Exit?");
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email);
        tv_email.setText("itsme@mail.com");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                        R.string.drawer_open, R.string.drawer_close) {
                    @Override
                    public void onDrawerClosed(View v) {
                        super.onDrawerClosed(v);
                    }

                    @Override
                    public void onDrawerOpened(View v) {
                        super.onDrawerOpened(v);
                    }
                };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read_palm:
                Intent intent = new Intent(MainActivity.this, ReadPalmActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_read_from_existing:
                ImagePickerUtil.add(getSupportFragmentManager(), new ImagePickerUtil.OnImagePickerListener() {
                    @Override
                    public void success(String name, String path) {
                        path.replace("file:///", "");
                        showAvatar(path);
                    }

                    @Override
                    public void fail(String message) {
                        showToast(message);
                    }
                });
                break;
        }
    }

    private void showAvatar(String filePath) {
        if (filePath != null) {
            // custom dialog
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.image_dialog);
            dialog.setTitle("Image");
            // find the imageview and draw it!
            final ImageView image = (ImageView) dialog.findViewById(R.id.image);
            final Bitmap bitmap = BitmapFactory.decodeFile(filePath.replace("file:////", ""));
            image.setImageBitmap(bitmap);
//           mat = applyDesaturation(mat);
//           Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2GRAY);
            /**Laplacian uses the gradient of images, it calls internally the Sobel operator
             * to perform its computation. */
//            Imgproc.Laplacian(mat, mat, 3);
            Button buttonSrc = (Button) dialog.findViewById(R.id.dialogButtonSource);
            // if button is clicked, close the custom dialog
            buttonSrc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image.setImageBitmap(bitmap);
                }
            });
            Button buttonT1 = (Button) dialog.findViewById(R.id.dialogButtonT1);
            // if button is clicked, close the custom dialog
            buttonT1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            // findDesaturation(bitmap, image);
                            Mat src = new Mat();
                            Utils.bitmapToMat(bitmap, src);
                            removeNoise(src);
//          Imgproc.medianBlur(src,src,10);
                            applyThreshold(src);
                            floodFill(src);
                            Imgproc.Canny(src, src, 50, 70);
//            applyHoughLines(floodMat);
//            locateSpecialPoints(floodMat);
//            drawImageOutline(bitmap, image);
                            final Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(src, bmp);
                            image.setImageBitmap(bmp);
                        }
                    }, 1000);
                }
            });
            Button buttonT2 = (Button) dialog.findViewById(R.id.dialogButtonT2);
            // if button is clicked, close the custom dialog
            buttonT2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mat src = new Mat();
                    Utils.bitmapToMat(bitmap, src);
                    Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
                    Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
                    final Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(src, bmp);
                    image.setImageBitmap(bmp);
                }
            });
            Button buttonLines = (Button) dialog.findViewById(R.id.dialogButtonLines);
            // if button is clicked, close the custom dialog
            buttonLines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mat src = new Mat();
                    Utils.bitmapToMat(bitmap, src);
                    Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
                    Mat croppedMat = new Mat(src, new Rect(150, 500, 400, 300));
//                  Mat houghMat= applyHoughLines(croppedMat);
                    Imgproc.medianBlur(croppedMat, croppedMat, 5);
//                    Imgproc.Canny(croppedMat, croppedMat, 20, 60);
                    final Bitmap bmp = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(croppedMat, bmp);
                    image.setImageBitmap(bmp);
                }
            });

            Button buttonBoundary= (Button) dialog.findViewById(R.id.dialogButtonBoundary);
            // if button is clicked, close the custom dialog
            buttonBoundary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawBoundary(image,bitmap);

                }
            });

            Button buttonHistogram = (Button) dialog.findViewById(R.id.dialogButtonHistogram);
            // if button is clicked, close the custom dialog
            buttonHistogram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawHistogram(image, bitmap);
                }
            });
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
    }

    /**
     * HoughLines function takes a single channel binary image, processed through
     * the Canny edge detection filter.
     * HoughLines finds lines in a binary image using the standard Hough transform.
     */
    private Mat applyHoughLines(Mat floodMat) {
        int threshold = 10;
        int minLineSize = 5;
        int lineGap = 10;
        Mat lines = new Mat();
        Imgproc.HoughLinesP(floodMat, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(floodMat, start, end, new Scalar(255, 0, 0), 3);
        }
        return floodMat;
    }

    private void removeNoise(Mat src) {
        //gray
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
        // performed adaptive threshold using Gaussian filter:
        Imgproc.adaptiveThreshold(src, src, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        Imgproc.morphologyEx(src, src, 1,
                Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4)));
        // highlight
        Imgproc.dilate(src, src, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1)));
    }

    private void locateSpecialPoints(Mat floodMat) {
        floodMat.channels();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // find contours:
        Imgproc.findContours(floodMat, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point());
        Point high = new Point();
        Point med = new Point();
        Point low = new Point();
        for (MatOfPoint mop : contours) {
            for (Point p : mop.toList()) {
                if (p.y >= high.y)
                    high = new Point(p.x, p.y);
                else {
                    if (p.y >= med.y)
                        med = new Point(p.x, p.y);
                    else if (p.y >= low.y)
                        low = new Point(p.x, p.y);
                }
                Log.d("point", p.x + " ," + p.y);
            }
        }
        Log.d("Highest point ", high.x + "," + high.y);
        Log.d("Medium point ", med.x + "," + med.y);
        Log.d("Lowest point ", low.x + "," + low.y);
    }

    /**
     * Thresholding is a method of image segmentation, in general it is used to create binary images.
     * Thresholding is of two types namely,
     * simple thresholding and
     * adaptive thresholding.
     */
    private Mat applyThreshold(Mat src) {
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        Imgproc.threshold(src, src, 50, 100, Imgproc.THRESH_BINARY);
        return src;
    }

    private static Mat applyDilade(Mat img) {
        // Creating an empty matrix to store the result
        Mat dst = new Mat();
        // Preparing the kernel matrix object
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size((2 * 2) + 1, (2 * 2) + 1));
        // Applying dilate on the Image
        Imgproc.dilate(img, dst, kernel);
        return dst;
    }

    /**
     * floodFill fill a connected component starting from
     * the seed point with the specified color.
     */
    private void floodFill(Mat src) {
        Mat floodfilled = Mat.zeros(src.rows() + 2, src.cols() + 2, CvType.CV_8U);
        Imgproc.floodFill(src, floodfilled, new Point(300, 350), new Scalar(255), new Rect(),
                new Scalar(0), new Scalar(0), 4 + (255 << 8) + Imgproc.FLOODFILL_MASK_ONLY);
//        Core.subtract(floodfilled, Scalar.all(0), floodfilled);
//        Rect roi = new Rect(1, 1, img.cols() - 2, img.rows() - 2);
//        Mat temp = new Mat();
//        floodfilled.submat(roi).copyTo(temp);
//        img = temp;
        //Core.bitwise_not(img, img);
    }

    private void findDesaturation(Bitmap bitmap, ImageView imageView) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        threshold(gray, gray, 70, 100, ADAPTIVE_THRESH_MEAN_C);
//      Imgproc.adaptiveThreshold(gray, gray, 100, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 40);
        //Fills a connected component with the given color.
        Imgproc.floodFill(gray, new Mat(gray.height() + 2, gray.width() + 2, CvType.CV_8UC1),
                new Point(0, 0), new Scalar(0, 255, 0));
        Bitmap bmp = Bitmap.createBitmap(gray.cols(), gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray, bmp);
        imageView.setImageBitmap(bmp);
    }

    private void drawImageOutline(Bitmap bitmap, ImageView image) {
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(gray, gray, 50, 70);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // find contours:
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Mat drawing = Mat.zeros(gray.size(), CV_8UC3);
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            Scalar color = new Scalar(0, 255, 0);
////            Imgproc.drawContours(gray, contours, contourIdx, new Scalar(0, 0, 255), 1);
//            if (contourIdx < 50)
//                Imgproc.drawContours(drawing, contours, contourIdx, new Scalar(255, 0, 0), 2, 8, hierarchy, 0, new Point(100,100));
//            else
//                Imgproc.drawContours(drawing, contours, contourIdx, color, 2, 8, hierarchy, 0, new Point());
//        }
//        }
        Bitmap bmp = Bitmap.createBitmap(gray.cols(), gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray, bmp);
        image.setImageBitmap(bmp);
    }

    private void drawHistogram(ImageView image, Bitmap bitmap) {
        try {
            Mat rgba = new Mat();
            Utils.bitmapToMat(bitmap, rgba);
            Size rgbaSize = rgba.size();
            int histSize = 256;
            MatOfInt histogramSize = new MatOfInt(histSize);
            int histogramHeight = (int) rgbaSize.height;
            int binWidth = 5;
            MatOfFloat histogramRange = new MatOfFloat(0f, 256f);
            Scalar[] colorsRgb = new Scalar[]{new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255)};
            MatOfInt[] channels = new MatOfInt[]{new MatOfInt(0), new MatOfInt(1), new MatOfInt(2)};
            Mat[] histograms = new Mat[]{new Mat(), new Mat(), new Mat()};
            Mat histMatBitmap = new Mat(rgbaSize, rgba.type());
            for (int i = 0; i < channels.length; i++) {
                Imgproc.calcHist(Collections.singletonList(rgba), channels[i], new Mat(), histograms[i], histogramSize, histogramRange);
                Core.normalize(histograms[i], histograms[i], histogramHeight, 0, Core.NORM_INF);
                for (int j = 0; j < histSize; j++) {
                    Point p1 = new Point(binWidth * (j - 1), histogramHeight - Math.round(histograms[i].get(j - 1, 0)[0]));
                    Point p2 = new Point(binWidth * j, histogramHeight - Math.round(histograms[i].get(j, 0)[0]));
                    Imgproc.line(histMatBitmap, p1, p2, colorsRgb[i], 2, 8, 0);
                }
            }
            for (int i = 0; i < histograms.length; i++) {
                calculationsOnHistogram(histograms[i]);
            }
            // Don't do that at home or work it's for visualization purpose.
//            BitmapHelper.showBitmap(this, bitmap, imageView);
            Bitmap histBitmap = Bitmap.createBitmap(histMatBitmap.cols(), histMatBitmap.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(histMatBitmap, histBitmap);
//            BitmapHelper.showBitmap(this, histBitmap, histogramView);
            BitmapHelper.showBitmap(this, histBitmap, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawBoundary(ImageView image, Bitmap bitmap) {
        try {
            Mat mat = new Mat();
            Mat colorMat=new Mat();
            Utils.bitmapToMat(bitmap, mat);
            mat.copyTo(colorMat);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0);

            /** if background is dark use THRESH_BINARY and if light THRESH_BINARY_INV */
      //      Imgproc.threshold(mat, mat, 45, 255, Imgproc.THRESH_BINARY_INV);
            Imgproc.threshold(mat, mat, 45, 255, Imgproc.THRESH_BINARY);
            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*5 + 1, 2*5+1));

            Imgproc.erode(mat, mat, element);
            Imgproc.dilate(mat, mat,element);

            List<MatOfPoint> mcontours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(mat, mcontours, hierarchy, Imgproc.RETR_EXTERNAL,
                    Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
            Imgproc.drawContours(colorMat, mcontours, -1, new Scalar(0, 255, 255), 2);
                Bitmap histBitmap = Bitmap.createBitmap(colorMat.cols(), colorMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(colorMat, histBitmap);
                BitmapHelper.showBitmap(this, histBitmap, image);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    private void calculationsOnHistogram(Mat histogram) {
        SparseArray<ArrayList<Float>> compartments = HistogramHelper.createCompartments(histogram);
        float sumAll = HistogramHelper.sumCompartmentsValues(compartments);
        float averageAll = HistogramHelper.averageValueOfCompartments(compartments);
        Log.i(TAG, "Sum: " + Core.sumElems(histogram));
        Log.i(TAG, "Sum of all compartments " + String.valueOf(sumAll));
        Log.i(TAG, "Average value of all compartments " + String.valueOf(averageAll));
        Log.i(TAG, " ");
        for (int i = 0; i < compartments.size(); i++) {
            float sumLast = HistogramHelper.sumCompartmentValues(i, compartments);
            float averageLast = HistogramHelper.averageValueOfCompartment(i, compartments);
            float averagePercentageLastCompartment = HistogramHelper.averagePercentageOfCompartment(i, compartments);
            float percentageLastCompartment = HistogramHelper.percentageOfCompartment(i, compartments);
            Log.i(TAG, "Sum of " + (i + 1) + " compartment " + String.valueOf(sumLast));
            Log.i(TAG, "Average value of the " + (i + 1) + " compartment " + String.valueOf(averageLast));
            Log.i(TAG, "Average percentage of the " + (i + 1) + " compartment " + String.valueOf(averagePercentageLastCompartment));
            Log.i(TAG, "Percentage of the " + (i + 1) + " compartment " + String.valueOf(percentageLastCompartment));
            Log.i(TAG, " ");
        }
        Log.i(TAG, " ");
    }

    private Mat copyToMat(Bitmap bitmap) {
        //copy a portion of mat into other mat
        //here  Mat small is added to the Mat gray at 40,400
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        Rect roi = new Rect(40, 400, 550, 680);
        Mat small = new Mat(gray, roi);
        Imgproc.Canny(small, small, 50, 200);
        small.copyTo(new Mat(gray, roi));
        return gray;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            saveAndShowPictureDialog(photo);
        } else if (resultCode == Activity.RESULT_OK && requestCode == RESULT_LOAD_IMG) {
            Uri selectedImageUri = data.getData();
            String pathString = getRealPathFromURI(this, selectedImageUri);
            String path = "file:///" + pathString;
            showImageDialog(path);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showImageDialog(String pictureFile) {
        // custom dialog
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle("Image");
        // find the imageview and draw it!
        Bitmap bmImg;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmImg = BitmapFactory.decodeFile(pictureFile, options);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageBitmap(bmImg);
//        Bitmap bmImg = BitmapFactory.decodeFile(pictureFile);
//        image.setImageBitmap(bmImg);
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

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveAndShowPictureDialog(Bitmap photo) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        showDialog(pictureFile);
    }

    private void showDialog(File pictureFile) {
        // custom dialog
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle("Image");
        // find the imageview and draw it!
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        Bitmap thumbnail;
        try {
            thumbnail = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), Uri.fromFile(pictureFile));
//            image.setImageBitmap(thumbnail);
//            Mat tmp = new Mat(thumbnail.getWidth(), thumbnail.getHeight(), CvType.CV_8UC1);
//            Utils.bitmapToMat(thumbnail, tmp);
//            //converting img to grey
////            Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
//            Bitmap image1 = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
////            tmp = getContoursFromBitmap(thumbnail);
            // create a blank temp bitmap:
//            applyMorphologicalClosing(image, thumbnail);
//            applyDiladeAndErode(image, thumbnail);
//            applyROI(image, thumbnail);
//            showGreyImage(image, thumbnail);
            showNormalImage(image, thumbnail);

       /*     Utils.matToBitmap(getContoursFromBitmap(thumbnail), image1);
//            Imgproc.threshold(tmp, tmp, 60, 100, Imgproc.THRESH_BINARY);
//            Utils.matToBitmap(tmp, image1);
//            Imgproc.threshold(tmp, tmp, 1, 255, Imgproc.THRESH_OTSU);
            //using floodfill and watershed to remove noise
//            Mat mask = new Mat(tmp.rows() + 2, tmp.cols() + 2, CvType.CV_8UC1);
//            Imgproc.floodFill(tmp, mask, new Point(tmp.cols() - 10, 10), new Scalar(255.0, 255.0, 255));
            Utils.matToBitmap(tmp, image1);
            image.setImageBitmap(image1);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
//        image.setImageBitmap(myBitmap);
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

    private void applyROI(ImageView image, Mat mat) {
        Rect roi = new Rect(40, 100, 100, 120);
        Mat cropped = new Mat(mat, roi);
//        Imgproc.medianBlur(cropped,cropped,10);
        Bitmap tempBmp1 = Bitmap.createBitmap(100, 120, Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(cropped, cropped, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(cropped, cropped, 50, 100);
        Utils.matToBitmap(cropped, tempBmp1);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(cropped, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint maxContour = null;
        Point v = null;
        for (int idx = 0; idx < contours.size(); idx++) {
            MatOfPoint contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            double maxContourArea = 0;
            if (contourarea > maxContourArea) {
                maxContour = contour;
                maxContourArea = contourarea;
                int maxAreaIdx = idx;
            }
        }
        Point[] points_contour = maxContour.toArray();
        int nbPoints = points_contour.length;
        for (int i = 0; i < nbPoints; i++) {
            v = points_contour[i];
        }
        Imgproc.drawMarker(mat, v, new Scalar(255));
        image.setImageBitmap(tempBmp1);
    }

    private void applyDiladeAndErode(ImageView image, Bitmap thumbnail) {
        Mat mInput = new Mat();
        Utils.bitmapToMat(thumbnail, mInput);
        Imgproc.erode(mInput, mInput,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.dilate(mInput, mInput,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Bitmap tempBmp1 = Bitmap.createBitmap(thumbnail.getWidth(), thumbnail.getHeight(),
                thumbnail.getConfig());
        Utils.matToBitmap(mInput, tempBmp1);
//        applyROI(image, tempBmp1);
//        showGreyImage(image, tempBmp1);
    }

    private void applyMorphologicalClosing(ImageView imageView, Bitmap thumbnail) {
        Mat mat = new Mat();
        Mat image = new Mat();
        Mat closedImage = new Mat();
        Utils.bitmapToMat(thumbnail, mat);
        Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11, 11));
        Utils.bitmapToMat(thumbnail, image);
        Imgproc.morphologyEx(image, closedImage, Imgproc.MORPH_CLOSE, kernel);
        Core.divide(image, closedImage, image);
        Core.normalize(image, image, 0, 255, Core.NORM_MINMAX);
        Bitmap tempBmp1 = Bitmap.createBitmap(thumbnail.getWidth(), thumbnail.getHeight(),
                thumbnail.getConfig());
        Utils.matToBitmap(image, tempBmp1);
        imageView.setImageBitmap(tempBmp1);
    }

    private void showNormalImage(ImageView image, Bitmap thumbnail) {
        Mat src = new Mat();
        Utils.bitmapToMat(thumbnail, src);
        //converts mat color
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR);
//    finding edges in an image using the [canny86] algorithm.
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.Canny(src, src, 50, 100);
//        Bitmap tempBmp1 = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(src, tempBmp1);
//        image.setImageBitmap(tempBmp1);
        applyROI(image, src);
    }

    private Mat applyDesaturation(Mat imageMat) {
        threshold(imageMat, imageMat, 0, 255, THRESH_BINARY + Imgproc.THRESH_OTSU);
        return imageMat;
    }

    private void showGreyImage(ImageView image, Bitmap thumbnail) {
        Mat src = new Mat();
        Utils.bitmapToMat(thumbnail, src);
        //converts mat color
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
//    finding edges in an image using the [canny86] algorithm.
        Imgproc.Canny(src, src, 50, 100);
        applyROI(image, src);
//        remove noise from bitmap
//        Imgproc.floodFill(src, Mat.zeros(new Size(src.cols() + 2, src.rows() + 2), CV_8U),
//                new Point(50, 50), new Scalar(255.0, 255.0, 255.0));
        //creates a line on Image Mat
//        Imgproc.line(src, new Point(50, 50), new Point(50, 100), new Scalar(0, 255, 0), 1);
    /*    List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        Imgproc.drawContours(dst, contours, -1, new Scalar(255, 255, 255), 3);*/
//        Utils.matToBitmap(src, tempBmp1);
//        image.setImageBitmap(tempBmp1);
    }

    private Mat getContoursFromBitmap(Bitmap thumbnail) {
        Mat src = new Mat();
        Utils.bitmapToMat(thumbnail, src);
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(gray, gray, 50, 200);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // find contours:
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            Imgproc.drawContours(src, contours, contourIdx, new Scalar(0.299, 0.587, 0.144), -1);
            Imgproc.drawContours(src, contours, contourIdx, new Scalar(255, 0, 0), -1);
        }
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 2);
        return src;
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
