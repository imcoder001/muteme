package com.technospaces.locationapp;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MapService extends Service implements GoogleApiClient.ConnectionCallbacks,
                                            GoogleApiClient.OnConnectionFailedListener,
                                            LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AudioManager mAudioManager;
    List<String> all_locations_list;
    private DBClass newDB;
    private List<Place> selectAllUserPlaces;
    int previousMode = -1;


//    int _id2 = (int) System.currentTimeMillis();
    SilentModeOnBroadCastReceiver silentModeOnBroadCastReceiver = new SilentModeOnBroadCastReceiver();
    SilentModeOffBroadCastReceiver silentModeOffBroadCastReceiver = new SilentModeOffBroadCastReceiver();

    public MapService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        newDB = new DBClass(this);
        newDB.getWritableDatabase();



        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                Calendar c = Calendar.getInstance();
                int currentHour = c.get(Calendar.HOUR_OF_DAY);
                int minHour = 1;
                int maxHour = 5;


//                if(currentHour > minHour && currentHour  < maxHour){
//                    activateAudioMode(AudioManager.RINGER_MODE_SILENT);
//                }
//                else{
//                    activateAudioMode(AudioManager.RINGER_MODE_NORMAL);
//                }
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        ArrayList<TimeRange> arraylist_start = (ArrayList<TimeRange>) intent.getSerializableExtra("arraylist_start");

        if(arraylist_start != null){
            try {

                silentModeOnBroadCastReceiver.startAlarmAt(this, arraylist_start);
                silentModeOffBroadCastReceiver.startAlarmOff(this, arraylist_start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGoogleApiClient.connect();
//        silentModeOnBroadCastReceiver.setAlarm(this);
//        silentModeOffBroadCastReceiver.setAlarm(this);
        try{

            ArrayList<TimeRange> arraylist_start = (ArrayList<TimeRange>) intent.getSerializableExtra("arraylist_start");

            if(arraylist_start != null){
                try {
                    silentModeOnBroadCastReceiver.startAlarmAt(this, arraylist_start);
                    silentModeOffBroadCastReceiver.startAlarmOff(this, arraylist_start);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (NullPointerException ne){

        }
        catch (Exception e){

        }
//        Bundle bundle = intent.getExtras();
//        if(bundle != null){
//            ArrayList<Integer> list = bundle.getIntegerArrayList("0");
//    //        ArrayList<Integer> list = new ArrayList<Integer>();
//    //        list.add(36);
//    //        list.add(37);
//    //        list.add(38);
//
//
//        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {



        boolean distanceLessThan30 = false;
        selectAllUserPlaces = newDB.selectAllUserPlaces();
        if(selectAllUserPlaces != null){
            for(int i = 0; i < selectAllUserPlaces.size(); i++){
                Place place = selectAllUserPlaces.get(i);
                String result = "";
                String latitudeStr = Double.toString(place.getLatitude());
                String longitudeStr = Double.toString(place.getLongitude());;


                double currentLat = location.getLatitude();
                double currentLng = location.getLongitude();

                float distance = distanceBetween(Double.parseDouble(latitudeStr), Double.parseDouble(longitudeStr), currentLat, currentLng);
                if(distance < 30){
//                    PlaySound ps = new PlaySound();
//                    ps.playSoundThread();
                    distanceLessThan30 = true;
                    break;
                }
                else{
                    distanceLessThan30 = false;
                }

            }
        }

        if(distanceLessThan30){
            //Silent  mode
            if (!checkIfPhoneIsSilent()) {
                previousMode = mAudioManager.getRingerMode();
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(this, "Silent Mode Activated: ", Toast.LENGTH_LONG).show();
            }
        }
        else{
            if(previousMode != -1){
                mAudioManager.setRingerMode(previousMode);
                Toast.makeText(this, "Normal Mode Activated: ", Toast.LENGTH_LONG).show();
                previousMode = -1;
            }
        }





    }

    public void activateAudioMode(int mode){
        if(!checkIfPhoneIsSilent()){
            mAudioManager.setRingerMode(mode);
            Toast.makeText(this, "Silent Mode Activated: ", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public float distanceBetween(double latitude1, double longitude1, double latitude2, double longitude2){
        //Distance
        Location currentLocation = new Location("");
        currentLocation.setLatitude(latitude1);
        currentLocation.setLongitude(longitude1);

        Location targetLocation = new Location("");
        targetLocation.setLatitude(latitude2);
        targetLocation.setLongitude(longitude2);


        return currentLocation.distanceTo(targetLocation);
    }

    private boolean checkIfPhoneIsSilent() {
        int ringermode = mAudioManager.getRingerMode();
        if (ringermode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        }
        else
        {
            return false;
        }

    }
}
