package com.example.thien.photomagician.other;

import org.opencv.core.Mat;

/**
 * Created by Quoc on 12/2/2016.
 */

public interface FragmentCallbacks {
    public void onMsgFromMainToFragment(String strValue, Mat Image);
}
