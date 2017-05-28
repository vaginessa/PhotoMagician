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
import android.widget.TextView;
import android.widget.Toast;

import com.example.thien.photomagician.R;
import com.example.thien.photomagician.activity.NavigationActivity;
import com.example.thien.photomagician.other.Filter;
import com.example.thien.photomagician.other.FragmentCallbacks;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class FilterFragment extends Fragment implements FragmentCallbacks{
    NavigationActivity navigationActivity;
    ViewGroup scrollViewgroup;
    Bitmap originalImage = null;
    Mat ImageMat;
    String[] items = {"None filter",
            "Gray",
            "B&W",
            "Invert",
            "Luminance",
            "Night Vision",
            "BGR",
            "Protanopia",
            "Tint",
            "Tritanopia",
            "Pink",
            "Pink Light",
            "Protanomaly",
            "Tritanomaly",
            "XYZ",
            "Undertone",
            "Warm",
            "Cool",
            "Polaroid",
            "Sepia",
            "Vintage",
            "Brownie",
            "Kodochrome",
            "Technicolor",
            "Temperature",
            "Cartoon"};

    public FilterFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
// this layout goes inside the HorizontalScrollView
        scrollViewgroup = (ViewGroup) rootView.findViewById(R.id.viewgroup);
        //navigationAcivity.filterImage = navigationAcivity.currentImage.copy(Bitmap.Config.ARGB_8888, true);
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
//put data [icon, caption] in each frame
            progressBar.setVisibility(View.VISIBLE);
            if (ImageMat != null) {
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        originalImage = Filter.grayscale(ImageMat);
                        break;
                    case 2:
                        originalImage = Filter.Luminance(ImageMat);
                        break;
                    case 3:
                        originalImage = Filter.doInvert(ImageMat);
                        break;
                    case 4:
                        originalImage = Filter.Erode(ImageMat);
                        break;
                    case 5:
                        originalImage = Filter.Dilate(ImageMat);
                        break;
                    case 6:
                        originalImage = Filter.bgr(ImageMat);
                        break;
                    case 7:
                        originalImage = Filter.protanopia(ImageMat);
                        break;
                    case 8:
                        originalImage = Filter.tintImage(ImageMat, 50);
                        break;
                    case 9:
                        originalImage = Filter.tritanopia(ImageMat);
                        break;
                    case 10:
                        originalImage = Filter.tintImage(ImageMat, -50);
                        break;
                    case 11:
                        originalImage = Filter.tint(ImageMat);
                        break;
                    case 12:
                        originalImage = Filter.protanomaly(ImageMat);
                        break;
                    case 13:
                        originalImage = Filter.tritanomaly(ImageMat);
                        break;
                    case 14:
                        originalImage = Filter.XYZ(ImageMat);
                        break;
                    case 15:
                        originalImage = Filter.undertone(ImageMat);
                        break;
                    case 16:
                        originalImage = Filter.warm(ImageMat);
                        break;
                    case 17:
                        originalImage = Filter.cool(ImageMat);
                        break;
                    case 18:
                        originalImage = Filter.Polaroid(ImageMat);
                        break;
                    case 19:
                        originalImage = Filter.Sepia(ImageMat);
                        break;
                    case 20:
                        originalImage = Filter.Vintage(ImageMat);
                        break;
                    case 21:
                        originalImage = Filter.Brownie(ImageMat);
                        break;
                    case 22:
                        originalImage = Filter.Kodachrome(ImageMat);
                        break;
                    case 23:
                        originalImage = Filter.Technicolor(ImageMat);
                        break;
                    case 24:
                        originalImage = Filter.temperature(ImageMat);
                        break;
                    case 25:
                        originalImage = Filter.Cartoon(ImageMat);
                        break;
                    default:
                        break;
                }
            }
            icon.setImageBitmap(originalImage);
            caption.setText(items[i]);
            caption.setTextColor(Color.parseColor("#E0F2F1"));
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
                    navigationActivity.onMsgFromFragToMain("FILTER-FRAG", singleFrame.getId());
                }
            });// listener
            progressBar.setVisibility(View.INVISIBLE);
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