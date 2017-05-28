package com.example.thien.photomagician.fragment;

/**
 * Created by thien on 01/12/2016.
 */

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thien.photomagician.R;
import com.example.thien.photomagician.activity.NavigationActivity;

public class StickerFragment extends Fragment{

    ViewGroup scrollViewgroup;
    NavigationActivity navigationActivity;

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

    public StickerFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_sticker, container, false);
// this layout goes inside the HorizontalScrollView
        scrollViewgroup = (ViewGroup) rootView.findViewById(R.id.viewgroup);
// populate the ScrollView
        for (int i = 0; i < thumbnails.length; i++) {
//create single frames [icon & caption] using XML inflater
            final View singleFrame = getActivity().getLayoutInflater().inflate(
                    R.layout.fram_icon_caption, null);
//frame: 0, frame: 1, frame: 2, ... and so on
            singleFrame.setId(i);
//internal plumbing to reach elements inside single frame
            TextView caption = (TextView) singleFrame.findViewById(R.id.txtCaption);
            ImageView icon = (ImageView) singleFrame.findViewById(R.id.imgIcon);
//put data [icon, caption] in each frame
            icon.setImageResource(thumbnails[i]);
            //caption.setText(items[i]);
            //caption.setTextColor(Color.parseColor("#E0F2F1"));
//add frame to the scrollView
            scrollViewgroup.addView(singleFrame);
            scrollViewgroup.setBackgroundColor(Color.parseColor("#E0F2F1"));
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < thumbnails.length; j++) {
                        scrollViewgroup.getChildAt(j).setBackgroundColor(Color.parseColor("#E0F2F1"));
                    }
                    singleFrame.setBackgroundColor(Color.parseColor("#00796B"));
                    navigationActivity.onMsgFromFragToMain("STICKER-FRAG", singleFrame.getId());
                }
            });// listener
        }// for – populating ScrollView
        return rootView;
    }
}