package com.example.thien.photomagician.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.thien.photomagician.R;
import com.example.thien.photomagician.fragment.AdjustmentFragment;
import com.example.thien.photomagician.fragment.FilterFragment;
import com.example.thien.photomagician.fragment.StickerFragment;
import com.example.thien.photomagician.other.Filter;
import com.example.thien.photomagician.other.MainCallbacks;
import com.example.thien.photomagician.other.StickerView;
import com.example.thien.photomagician.other.TouchImageView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainCallbacks{

    FragmentTransaction ft;
    AdjustmentFragment fragment_adjustment;
    FilterFragment fragment_filter;
    StickerFragment fragment_sticker;

    Context context;
    private TouchImageView imgView;
    private TouchImageView imgView2;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private ProgressBar bar;
    public Bitmap originalImage;
    public float contrast = 1;
    public float brightness = 0;
    public Mat ImageMat = null;
    public Mat ImageMatAdjust = null;
    public Mat tmp = null;
    public boolean adjusted = false;
    public boolean filtered = false;
    public boolean paused = false;
    Filter filter = new Filter();
    boolean isCompared = false;
    boolean isSaved = true;
    AtomicBoolean isRunning = new AtomicBoolean(false);
    List<StickerView> mStickers = new ArrayList<>();
    int mStatusBarHeight;
    int mToolBarHeight;
    int itemChoose=0;
    NavigationView navigationView;
    ShareDialog shareDialog;

    Integer[] thumbnails = {R.drawable.ic_sticker_batman1,
            R.drawable.ic_sticker_batman2,
            R.drawable.ic_sticker_baymax,
            R.drawable.ic_sticker_bear,
            R.drawable.ic_sticker_bearmoney,
            R.drawable.ic_sticker_doraemon,
            R.drawable.ic_sticker_england,
            R.drawable.ic_sticker_flash,
            R.drawable.ic_sticker_gate,
            R.drawable.ic_sticker_greenlantern,
            R.drawable.ic_sticker_husky1,
            R.drawable.ic_sticker_husky2,
            R.drawable.ic_sticker_husky3,
            R.drawable.ic_sticker_japan,
            R.drawable.ic_sticker_lixi,
            R.drawable.ic_sticker_pho,
            R.drawable.ic_sticker_pikachu,
            R.drawable.ic_sticker_shit,
            R.drawable.ic_sticker_shoe,
            R.drawable.ic_sticker_sumo,
            R.drawable.ic_sticker_superman,
            R.drawable.ic_sticker_sushi,
            R.drawable.ic_sticker_trump,
            R.drawable.ic_sticker_tuietiep,
            R.drawable.ic_sticker_wonderwoman
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // change UI
            if (msg.what == 1) {
                ImageMat = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8U);
                tmp = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8U);
                Utils.bitmapToMat(originalImage, ImageMat);
                imgView.setImageBitmap(originalImage);
                imgView2.setImageBitmap(originalImage);
                fragment_adjustment = new AdjustmentFragment();
                fragment_adjustment.onMsgFromMainToFragment("MAIN", ImageMat);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_holder, fragment_adjustment);
                ft.commit();
                navigationView.getMenu().getItem(itemChoose).setChecked(true);
            }
            if (msg.what == 2) {
                imgView.setImageBitmap(originalImage);
            }
            if (msg.what == 3) {
                String saved = (String) msg.obj;
                if (saved != null)
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                mStickers.clear();
                originalImage = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                Utils.bitmapToMat(originalImage, ImageMat);
                contrast = 1;
                brightness = 0;
                adjusted = false;
                filtered = false;
            }
            if (msg.what == 4) {
                imgView.setImageBitmap(originalImage);
            }
            bar.setVisibility(View.INVISIBLE);
        }
    };
    private String[] activityTitles = {"Adjustment", "Filter", "Sticker", "Crop", "Compare"};

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        shareDialog=new ShareDialog(this);

        mStatusBarHeight = getStatusBarHeight();
        mToolBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

        imgView = (TouchImageView) findViewById(R.id.imgLarge);
        imgView2 = (TouchImageView) findViewById(R.id.imgLarge2);

        imgView.setVisibility(View.VISIBLE);
        imgView2.setVisibility(View.INVISIBLE);

        bar = (ProgressBar) findViewById(R.id.progressBar);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setToolbarTitle(itemChoose);
        imgView.setZoom(0.9999f);
        imgView.setMaxZoom(5f);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.example.thien.photomagician",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!paused)
            bar.setVisibility(View.VISIBLE);
        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    if (isRunning.get()) {
                        if (!paused) {
                            String ps = getIntent().getStringExtra("image");
                            originalImage = BitmapFactory.decodeFile(ps);
                            int orientation = getExifOrientation(ps);
                            //rotate bitmap
                            Matrix matrix = new Matrix();
                            matrix.postRotate(orientation);
                            //create new rotated bitmap
                            originalImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, true);
                            handler.sendMessage(handler.obtainMessage(1));
                        }
                    }
                } catch (Throwable t) {
                    // just end the background thread
                    isRunning.set(false);
                }
            }
        });
        isRunning.set(true);
        background.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning.set(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(isSaved==false)
            new AlertDialog.Builder(this)
                    .setTitle("Save?")
                    .setMessage("Do you want to save your beautiful image?")
                    .setIcon(R.mipmap.ic_logo)
// set three option buttons
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
// actions serving "YES" button go here
                                    saveImage();
                                    finish();
                                }
                            })// setPositiveButton
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
// actions serving "NO" button go here
                            finish();
                        }
                    })// setNegativeButton
                    .create()
                    .show();
        else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //Internal OpenCV library not found. Using OpenCV Manager for initialization
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            //OpenCV library found inside package. Using it!
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        //Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    private void showMyAlertDialog(NavigationActivity navigationActivity) {
        new AlertDialog.Builder(navigationActivity)
                .setTitle("Save?")
                .setMessage("Do you want to save your beautiful image?")
                .setIcon(R.mipmap.ic_logo)
// set three option buttons
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
// actions serving "YES" button go here
                                saveImage();
                                finish();
                            }
                        })// setPositiveButton
                .setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
// actions serving "CANCEL" button go here
                            }// OnClick
                        })// setNeutralButton
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
// actions serving "NO" button go here
                        finish();
                    }
                })// setNegativeButton
                .create()
                .show();
    }// showMyAlertDialog

    public void saveImage() {
        imgView.setVisibility(View.VISIBLE);
        imgView2.setVisibility(View.INVISIBLE);
        isCompared = false;

        bar.setVisibility(View.VISIBLE);
        if (!mStickers.isEmpty())
            saveEffectBitmap();
        Thread background = new Thread(new Runnable() {
            public void run() {
                try {
                    if (isRunning.get()) {
                        Bitmap b = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                        String timeStamp = now();
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        String saved = insertImage(getApplicationContext().getContentResolver(), b, imageFileName);
                        handler.sendMessage(handler.obtainMessage(3, saved));
                    }
                } catch (Throwable t) {
                    // just end the background thread
                    isRunning.set(false);
                }
            }
        });
        isRunning.set(true);
        background.start();
        isSaved=true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            if(isSaved==false){
                showMyAlertDialog(this);
            }else{
                finish();
            }
            return true;
        } else if (id == R.id.action_save) {
            saveImage();
            return true;
        } else if (id == R.id.action_rotate) {
            imgView.setVisibility(View.VISIBLE);
            imgView2.setVisibility(View.INVISIBLE);
            originalImage = rotateImage(originalImage, 90);
            imgView.setImageBitmap(originalImage);
            isCompared = false;
            isSaved=false;

        } else if (id == R.id.action_compare) {
            imgView.resetZoom();
            imgView2.resetZoom();
            if (!isCompared) {
                imgView.setVisibility(View.INVISIBLE);
                imgView2.setVisibility(View.VISIBLE);
                isCompared = true;
            } else {
                imgView.setVisibility(View.VISIBLE);
                imgView2.setVisibility(View.INVISIBLE);
                isCompared = false;
            }
        }
        return false;
    }

    public static Bitmap rotateImage(Bitmap sourceImage, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    @Override
    public void onMsgFromFragToMain(String sender, int id) {
// show message arriving to MainActivity
        if (sender.equals("ADJ-FRAG")) {
            try {
                //forward blue-data to redFragment using its callback method
                if (id != 0)
                    filtered = true;
                bar.setVisibility(View.VISIBLE);
                switch (id) {
                    case 0:
                        //originalImage = null;
                        //originalImage = Filter.changeBitmapContrastBrightness(ImageMat, 1, 0);
                        Utils.matToBitmap(ImageMat, originalImage);
                        imgView.setImageBitmap(originalImage);
                        contrast = 1;
                        brightness = 0;
                        filtered = false;
                        adjusted = false;
                        isSaved = true;
                        bar.setVisibility(View.GONE);
                        break;
                    case 3:
                        Thread background0 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Sharpen(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background0.start();
                        break;
                    case 4:
                        Thread background1 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Blur(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background1.start();
                        break;
                }
                if (id != 0)
                    isSaved=false;
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
        //Layout muon chuyen la layout nao ?

        if (sender.equals("FILTER-FRAG")) {
            try {
                //forward blue-data to redFragment using its callback method
                //filterOnlyImage = null;
                filtered = true;
                bar.setVisibility(View.VISIBLE);
                switch (id) {
                        case 0:
                            Thread background0 = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Utils.matToBitmap(ImageMat, originalImage);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = Filter.changeBitmapContrastBrightness(ImageMat, contrast, brightness);
                                        }
                                        filtered = false;
                                        handler.sendMessage(handler.obtainMessage(2));
                                    } catch (Throwable t) {
                                        // just end the background thread
                                        isRunning.set(false);
                                    }
                                }
                            });
                            isRunning.set(true);
                            background0.start();
                            break;
                        case 1:
                            Thread background = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        if (isRunning.get()) {
                                            originalImage = Filter.grayscale(ImageMat);
                                            Utils.bitmapToMat(originalImage, tmp);
                                            if (adjusted && (contrast != 1 || brightness != 0)) {
                                                //originalImage = filter.grayscale(ImageMatAdjust);
                                                originalImage = null;
                                                originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                            }
                                            //originalImage = filter.grayscale(ImageMat);

                                            handler.sendMessage(handler.obtainMessage(2));
                                        }
                                    } catch (Throwable t) {
                                        // just end the background thread
                                        isRunning.set(false);
                                    }
                                }
                            });
                            isRunning.set(true);
                            background.start();
                            break;
                        case 2:
                            Thread background1 = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        if (isRunning.get()) {
                                            originalImage = Filter.Luminance(ImageMat);
                                            Utils.bitmapToMat(originalImage, tmp);
                                            if (adjusted && (contrast != 1 || brightness != 0)) {
                                                originalImage = null;
                                                originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                            }

                                            handler.sendMessage(handler.obtainMessage(2));
                                        }
                                    } catch (Throwable t) {
                                        // just end the background thread
                                        isRunning.set(false);
                                    }
                                }
                            });
                            isRunning.set(true);
                            background1.start();
                            break;
                        case 3:
                            Thread background2 = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        if (isRunning.get()) {
                                            originalImage = Filter.doInvert(ImageMat);
                                            Utils.bitmapToMat(originalImage, tmp);
                                            if (adjusted && (contrast != 1 || brightness != 0)) {
                                                originalImage = null;
                                                originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                            }

                                            handler.sendMessage(handler.obtainMessage(2));
                                        }
                                    } catch (Throwable t) {
                                        // just end the background thread
                                        isRunning.set(false);
                                    }
                                }
                            });
                            isRunning.set(true);
                            background2.start();
                            break;
                    case 4:
                        Thread background3 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Erode(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background3.start();
                        break;
                    case 5:
                        Thread background4 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Dilate(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background4.start();
                        break;
                    case 6:
                        Thread background5 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.bgr(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background5.start();
                        break;
                    case 7:
                        Thread background6 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.protanopia(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background6.start();
                        break;
                    case 8:
                        Thread background7 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.tintImage(ImageMat, 50);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background7.start();
                        break;
                    case 9:
                        Thread background8 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.tritanopia(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background8.start();
                        break;
                    case 10:
                        Thread background9 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.tintImage(ImageMat, -50);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background9.start();
                        break;
                    case 11:
                        Thread background10 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.tint(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background10.start();
                        break;
                    case 12:
                        Thread background11 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.protanomaly(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background11.start();
                        break;
                    case 13:
                        Thread background12 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.tritanomaly(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background12.start();
                        break;
                    case 14:
                        Thread background13 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.XYZ(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background13.start();
                        break;
                    case 15:
                        Thread background14 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.undertone(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background14.start();
                        break;
                    case 16:
                        Thread background15 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.warm(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background15.start();
                        break;
                    case 17:
                        Thread background16 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.cool(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background16.start();
                        break;
                    case 18:
                        Thread background17 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Polaroid(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background17.start();
                        break;
                    case 19:
                        Thread background18 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Sepia(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background18.start();
                        break;
                    case 20:
                        Thread background19 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Vintage(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background19.start();
                        break;
                    case 21:
                        Thread background20 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Brownie(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background20.start();
                        break;
                    case 22:
                        Thread background21 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Kodachrome(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background21.start();
                        break;
                    case 23:
                        Thread background22 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Technicolor(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background22.start();
                        break;
                    case 24:
                        Thread background23 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.temperature(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background23.start();
                        break;
                    case 25:
                        Thread background24 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    if (isRunning.get()) {
                                        originalImage = Filter.Cartoon(ImageMat);
                                        Utils.bitmapToMat(originalImage, tmp);
                                        if (adjusted && (contrast != 1 || brightness != 0)) {
                                            originalImage = null;
                                            originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                        }
                                        handler.sendMessage(handler.obtainMessage(2));
                                    }
                                } catch (Throwable t) {
                                    // just end the background thread
                                    isRunning.set(false);
                                }
                            }
                        });
                        isRunning.set(true);
                        background24.start();
                        break;
                    default:
                        break;
                }
                isSaved=false;
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
        if (sender.equals("STICKER-FRAG")) {
            try {
                addStickerItem(id);
                isSaved=false;
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
    }

    @Override
    public void onMsgFromFragToMainSeekBar(String sender, int id, float alpha, float beta) {
        if (sender.equals("ADJ-FRAG")) {
            try {
                if (id == 1 || id == 2) { // brightness
                    contrast = alpha;
                    brightness = beta;
                    adjusted = true;
                    Thread background1 = new Thread(new Runnable() {
                        public void run() {
                            try {
                                if (isRunning.get()) {
                                    if (filtered) {
                                        originalImage = Filter.changeBitmapContrastBrightness(tmp, contrast, brightness);
                                    } else {
                                        originalImage = Filter.changeBitmapContrastBrightness(ImageMat, contrast, brightness);
                                    }
                                    handler.sendMessage(handler.obtainMessage(4));
                                }
                            } catch (Throwable t) {
                                // just end the background thread
                                isRunning.set(false);
                            }
                        }
                    });
                    isRunning.set(true);
                    background1.start();
                }
                isSaved=false;
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
    }

    private void setToolbarTitle(int navItemIndex) {
        //getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_adjustment) {
            imgView.setMaxZoom(5f);
            itemChoose=0;
            setToolbarTitle(itemChoose);
            fragment_adjustment = new AdjustmentFragment();
            fragment_adjustment.onMsgFromMainToFragment("MAIN", ImageMat);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_holder, fragment_adjustment);
            ft.commit();
        } else if (id == R.id.nav_filter) {
            imgView.setMaxZoom(5f);
            itemChoose=1;
            setToolbarTitle(itemChoose);
            fragment_filter = new FilterFragment();
            fragment_filter.onMsgFromMainToFragment("MAIN", ImageMat);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_holder, fragment_filter);
            ft.commit();

        } else if (id == R.id.nav_sticker) {
            imgView.setZoom(0.9999f);
            imgView.setMaxZoom(1f);
            itemChoose=2;
            setToolbarTitle(itemChoose);
            fragment_sticker = new StickerFragment();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_holder, fragment_sticker);
            ft.commit();
        } else if (id == R.id.nav_share) {
            navigationView.getMenu().getItem(4).setChecked(false);
            navigationView.getMenu().getItem(itemChoose).setChecked(true);
            saveImage();
            sharePhotoToFacebook();
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(NavigationActivity.this, AboutUsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sharePhotoToFacebook(){
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(originalImage)
                .setCaption("#PhotoMagician")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        shareDialog.show(content);
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognise a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    public static String insertImage(ContentResolver cr, Bitmap source, String title) {
        String stringUrl;
        File storedImagePath = generateImagePath(title, "jpg");
        if (!compressAndSaveImage(storedImagePath, source)) {
            return null;
        }
        Uri url = addImageToGallery(cr, "jpg", storedImagePath, title);
        stringUrl = url.toString();
        return stringUrl;
    }

    private static File getImagesDirectory() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "/PhotoMagician/");//Environment.getExternalStorageDirectory()
        if (!file.mkdirs() && !file.isDirectory()) {
            Log.e("mkdir", "Directory not created");
        }
        return file;
    }

    public static File generateImagePath(String title, String imgType) {
        return new File(getImagesDirectory(), title + "." + imgType);
    }

    public static boolean compressAndSaveImage(File file, Bitmap bitmap) {
        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                Log.w("image manager", "Compression success");
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Uri addImageToGallery(ContentResolver cr, String imgType, File filepath, String title) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imgType);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, filepath.toString());

        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    private void addStickerItem(int resId) {
        resetStickersFocus();
        StickerView stickerView = new StickerView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgLarge);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.imgLarge);
        ((ViewGroup) imgView.getParent()).addView(stickerView, params);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), thumbnails[resId]);
        stickerView.setWaterMark(bitmap);
        mStickers.add(stickerView);
        stickerView.setOnStickerDeleteListener(new StickerView.OnStickerDeleteListener() {
            @Override
            public void onDelete(StickerView stickerView) {
                if (mStickers.contains(stickerView))
                    mStickers.remove(stickerView);
            }
        });
    }

    private void resetStickersFocus() {
        for (StickerView stickerView : mStickers) {
            stickerView.setFocusable(false);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            //calculate action point Y apart from Container layout origin
            int y = (int) ev.getY() - mStatusBarHeight - mToolBarHeight;
            for (StickerView stickerView : mStickers) {
                // dispatch focus to the sticker based on Coordinate
                boolean isContains = stickerView.getContentRect().contains(x, y);
                if (isContains) {
                    resetStickersFocus();
                    stickerView.setFocusable(true);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void saveEffectBitmap() {
        int[] pos = getBitmapPositionInsideImageView(imgView);
        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache();
        Bitmap bmBg = imgView.getDrawingCache();//get background bitmap
        bmBg = Bitmap.createBitmap(bmBg, 0, 0, bmBg.getWidth(), bmBg.getHeight());//create bitmap with size
        imgView.destroyDrawingCache();
        Canvas canvas = new Canvas(bmBg);//create canvas with background bitmap size
        canvas.drawBitmap(bmBg, 0, 0, null);

        //draw stickers on canvas
        for (StickerView stickerView : mStickers) {
            Bitmap bmSticker = stickerView.getBitmap();
            canvas.drawBitmap(bmSticker, 0, 0, null);
            stickerView.setVisibility(View.GONE);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        // lay bitmap dung size
        imgView.setImageBitmap(bmBg);
        imgView.setDrawingCacheEnabled(true);
        imgView.buildDrawingCache();
        Bitmap bmBg1 = imgView.getDrawingCache();//get background bitmap
        bmBg1 = Bitmap.createBitmap(bmBg1, pos[0], pos[1], pos[2], pos[3]);//create bitmap with size
        imgView.setImageBitmap(bmBg1);
        //----
        imgView.destroyDrawingCache();
    }


    public static int[] getBitmapPositionInsideImageView(TouchImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }
}


