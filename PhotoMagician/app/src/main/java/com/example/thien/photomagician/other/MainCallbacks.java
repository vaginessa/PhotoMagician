package com.example.thien.photomagician.other;

/**
 * Created by Quoc on 12/2/2016.
 */

public interface MainCallbacks {
public void onMsgFromFragToMain (String sender, int id);
public void onMsgFromFragToMainSeekBar (String sender, int id, float contrast, float brightness);
        }
