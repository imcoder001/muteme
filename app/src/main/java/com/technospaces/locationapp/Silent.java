package com.technospaces.locationapp;


import android.app.Activity;
import android.media.AudioManager;
import android.widget.Toast;

/**
 * Created by Coder on 8/29/2015.
 */
public class Silent extends Activity {
    private static AudioManager mAudioManager;
    public Silent(){
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
    }
    public static boolean checkIfPhoneIsSilent() {
        int ringermode = mAudioManager.getRingerMode();
        if (ringermode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        }
        else
        {
            return false;
        }

    }

    public static void activateAudioMode(int mode){
        mAudioManager.setRingerMode(mode);

    }
}
