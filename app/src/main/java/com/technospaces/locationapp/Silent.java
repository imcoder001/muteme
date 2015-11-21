package com.technospaces.locationapp;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

/**
 * Created by Coder on 8/29/2015.
 */
public class Silent {
    Context mContext;
    private  AudioManager mAudioManager;
    public Silent(Context mContext){
        this.mContext = mContext;
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }



    public  boolean checkIfPhoneIsSilent() {
        int ringermode = mAudioManager.getRingerMode();
        if (ringermode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        }
        else
        {
            return false;
        }

    }

    public  void activateAudioMode(int mode){
        mAudioManager.setRingerMode(mode);

    }
}
