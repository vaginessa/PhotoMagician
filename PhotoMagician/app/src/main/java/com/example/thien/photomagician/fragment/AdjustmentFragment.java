package com.example.thien.photomagician.fragment;

/**
 * Created by thien on 01/12/2016.
 */

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thien.photomagician.R;
import com.example.thien.photomagician.activity.NavigationActivity;
import com.example.thien.photomagician.other.Filter;
import com.example.thien.photomagician.other.FragmentCallbacks;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class AdjustmentFragment extends Fragment implements FragmentCallbacks {
    ViewGroup scrollViewgroup;
    NavigationActivity navigationActivity;
    Bitmap originalImage = null;
    Mat ImageMat;
    public float curBrightness = 255;
    public float curConstract = 15;
    public float new_constrast = 1;
    String[] items = {"None",
            "Brightness",
            "Contrast",
            "Sharpen",
            "Blur"};

    public AdjustmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationActivity = (NavigationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_adjustment, container, false);
// this layout goes inside the HorizontalScrollView
        scrollViewgroup = (ViewGroup) rootView.findViewById(R.id.viewgroup);
        final SeekBar SeekBar_contrast = (SeekBar) rootView.findViewById(R.id.seekbar);
        final SeekBar SeekBar_brightness = (SeekBar) rootView.findViewById(R.id.seekbar1);
        SeekBar_brightness.setVisibility(View.INVISIBLE);
        SeekBar_contrast.setVisibility(View.INVISIBLE);
        SeekBar_contrast.setMax(30);
        SeekBar_brightness.setMax(510);
        SeekBar_brightness.setProgress((int)(curBrightness));
        SeekBar_contrast.setProgress((int)(curConstract));
        //controlTabActivity.adjustmentImage = controlTabActivity.currentImage.copy(Bitmap.Config.ARGB_8888, true);
// populate the ScrollView
        for (int i = 0; i < items.length; i++) {
//create single frames [icon & caption] using XML inflater
            final View singleFrame = getActivity().getLayoutInflater().inflate(
                    R.layout.fram_icon_caption, null);
//frame: 0, frame: 1, frame: 2, ... and so on
            singleFrame.setId(i);
//internal plumbing to reach elements inside single frame
            TextView caption = (TextView) singleFrame.findViewById(R.id.txtCaption);
            ImageView icon = (ImageView) singleFrame.findViewById(R.id.imgIcon);
            ProgressBar progressBar = (ProgressBar) singleFrame.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
//put data [icon, caption] in each frame
            if (ImageMat != null) {
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        originalImage=Filter.changeBitmapContrastBrightness(ImageMat,1,100);
                        break;
                    case 2:
                        originalImage=Filter.changeBitmapContrastBrightness(ImageMat,2,0);
                        break;
                    case 3:
                        originalImage = Filter.Sharpen(ImageMat);
                        break;
                    case 4:
                        originalImage = Filter.Blur(ImageMat);
                        break;
                    default:
                        break;
                }
            }
            icon.setImageBitmap(originalImage);
            progressBar.setVisibility(View.INVISIBLE);
            caption.setText(items[i]);
            caption.setTextColor(Color.parseColor("#E0F2F1"));
            //caption.setTextColor(Color.rgb(209, 196, 233));
//add frame to the scrollView
            scrollViewgroup.addView(singleFrame);
            scrollViewgroup.setBackgroundColor(Color.parseColor("#009688"));
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < items.length; j++) {
                        scrollViewgroup.getChildAt(j).setBackgroundColor(Color.parseColor("#009688"));
                    }
                    singleFrame.setBackgroundColor(Color.parseColor("#00796B"));
                    if ((int)singleFrame.getId() == 1)// brightness
                    {
                        SeekBar_contrast.setVisibility(View.INVISIBLE);
                        SeekBar_brightness.setVisibility(View.VISIBLE);
                        SeekBar_brightness.setProgress((int)(curBrightness));
                        SeekBar_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                        {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                            {
                                navigationActivity.onMsgFromFragToMainSeekBar("ADJ-FRAG", 1, new_constrast, (float)(progress-255));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar)
                            {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar)
                            {
                                curBrightness = (float)seekBar.getProgress();
                            }
                        });
                    }
                    if ((int)singleFrame.getId() == 2) // contrast
                    {
                        SeekBar_brightness.setVisibility(View.INVISIBLE);
                        SeekBar_contrast.setVisibility(View.VISIBLE);
                        SeekBar_contrast.setProgress((int)(curConstract));
                        SeekBar_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                        {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                            {
                                if (progress == 15)
                                    new_constrast = 1;
                                else {
                                    if (progress < 15)
                                        new_constrast = ((float)(progress) / 10.0f);
                                    else
                                        new_constrast = (((float)progress + 10.0f) / 10.0f);
                                }
                                navigationActivity.onMsgFromFragToMainSeekBar("ADJ-FRAG", 2, new_constrast,(float)(SeekBar_brightness.getProgress()-255));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar)
                            {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar)
                            {
                                curConstract = (float)seekBar.getProgress();
                            }
                        });
                    }
                    if ((int)singleFrame.getId() != 1 && (int)singleFrame.getId() != 2)
                    {
                        if ((int)singleFrame.getId() == 0)
                        {
                            curBrightness = 255;
                            curConstract = 15;
                            SeekBar_brightness.setProgress((int)(curBrightness));
                            SeekBar_contrast.setProgress((int)(curConstract));
                        }
                        SeekBar_brightness.setVisibility(View.INVISIBLE);
                        SeekBar_contrast.setVisibility(View.INVISIBLE);

                        navigationActivity.onMsgFromFragToMain("ADJ-FRAG", singleFrame.getId());
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"Click",Toast.LENGTH_SHORT).show();
                }
            });// listener
        }// for – populating ScrollView
        return rootView;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue, Mat Image)
    {
        if (strValue.equals("MAIN")) {
            try {
                originalImage = Bitmap.createBitmap(Image.cols(), Image.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(Image, originalImage);
                originalImage  = Bitmap.createScaledBitmap(originalImage, 120, 120, false);
                ImageMat = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8U);
                Utils.bitmapToMat(originalImage, ImageMat);
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
    }
}
