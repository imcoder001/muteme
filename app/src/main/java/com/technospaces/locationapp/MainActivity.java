package com.technospaces.locationapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;

import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;


import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.internal.MarkerOptionsParcelable;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    private LocationManager locationManager;
    private String provider;
    private GoogleMap mMap;
//    Polyline line;
//    PolylineOptions polyOptions;
    List<LatLng> myList;

    private boolean mPhoneIsSilent;
    Map<String,LatLng> markerInfoList;
    Marker mMarker;

    int initialPosition;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private DBClass newDB;
    private List<Place> selectAllUserPlaces;
    Button myLocationBtn;
    Dialog myDialog;
    Marker m;
    MarkerOptions markerOptions;

    RadioGroup switch_mapView;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        switch_mapView = (RadioGroup) findViewById(R.id.switch_mapView);

        startMapService();
        newDB = new DBClass(this);
        newDB.getWritableDatabase();
        myLocationBtn  = new Button(this);




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation != null)
        {
            markerOptions = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Lattitude = " + mLastLocation.getLatitude() + " Longitude " + mLastLocation.getLongitude());
        }
        else{
            markerOptions = new MarkerOptions().position(new LatLng(0, 0)).title("Lattitude = " + 0 + " Longitude " + 0);
        }

        markerInfoList = new HashMap<String, LatLng>();





        myList = new ArrayList<LatLng>();
//        polyOptions = new PolylineOptions()
//                .width(5)
//                .color(Color.BLUE)
//                .geodesic(true);



        setUpMapIfNeeded();

        setUpMap();


        /*
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon));
        m = mMap.addMarker(markerOptions);
        */
        mMap.setMyLocationEnabled(true);

//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            MarkerOptions marker = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Lattitude = " + mLastLocation.getLatitude() + " Longitude " + mLastLocation.getLongitude());
//            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon));
//            m = mMap.addMarker(marker);
//        }


    }



    public void putPreviousMarkers(){
        selectAllUserPlaces = newDB.selectAllUserPlaces();
        if(selectAllUserPlaces != null){
            mMap.clear();
            for(int i = 0; i < selectAllUserPlaces.size(); i++){
                Place place = selectAllUserPlaces.get(i);
                String latitude = Double.toString(place.getLatitude());
                String longitude = Double.toString(place.getLongitude());
                String name = place.getName();

                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title(name)).getId();
            }
        }
        else{
            mMap.clear();
        }
    }

    private void startMapService() {
        Intent i = new Intent(this, MapService.class);
        startService(i);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
//                line = mMap.addPolyline(polyOptions);




                switch_mapView.setOnCheckedChangeListener(
                        new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.mapViewBtn:
                                        mMap.setMapType(MAP_TYPE_NORMAL);
                                        break;
                                    case R.id.satelliteViewBtn:
                                        mMap.setMapType(MAP_TYPE_HYBRID);
                                        break;
                                }
                            }
                        }
                );
                mMap.setOnMapLongClickListener(
                        new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(LatLng point) {

                                callSaveLocationForm(point);


                            }
                        }
                );
            }
        }
    }

    private void callSaveLocationForm(LatLng point)
    {
        final LatLng p = point;
        myDialog = new Dialog(this, R.style.CustomDialog);
        myDialog.setTitle("Save Your Location");
//        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.save_loaction_form);
        myDialog.setCancelable(true);
        Button saveBtn = (Button) myDialog.findViewById(R.id.saveBtn);
//
        final EditText nameField = (EditText) myDialog.findViewById(R.id.nameField);
        myDialog.show();

        final InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        saveBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(nameField.getText().toString().trim().length() > 0){
                    long rowId = newDB.insertRecord(nameField.getText().toString(), p.latitude, p.longitude);
                    if(rowId != -1){

                        // get the marker Id as String
                        String id = mMap.addMarker(new MarkerOptions().position(new LatLng(p.latitude, p.longitude)).title(nameField.getText().toString().trim())).getId();
                        //add the marker ID to Map this way you are not holding on to  GoogleMap object
                        markerInfoList.put(id, p);
                        Toast.makeText(MainActivity.this, "Location Saved Successfully", Toast.LENGTH_LONG).show();
                        myDialog.hide();
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Location not saved due to an error", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Name Cannot  be empty", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
    private void setUpMap() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String latitude1 = getIntent().getExtras().getString("latitude");
            String longitude1 = getIntent().getExtras().getString("longitude");

            if(!latitude1.equalsIgnoreCase("") || !longitude1.equalsIgnoreCase("")){

//                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude1), Double.parseDouble(longitude1))).title("Lattitude = " + latitude1 + " Longitude " + longitude1);
//                mMap.addMarker(marker);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(latitude1), Double.parseDouble(longitude1)), 23));


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(latitude1), Double.parseDouble(longitude1)))     // Sets the center of the map to location user
                        .zoom(20)                   // Sets the zoom
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

    }
    private void setUpMap(double latitude, double longitude) {

//        polyOptions.add(new LatLng(latitude, longitude));

        LatLng l1 = new LatLng(latitude, longitude);
        myList.add(l1);
//        line.setPoints(myList);
//        animateMarker(m, l1, false);


//        animateMarker(mMarker,  l1, true);





        initialPosition++;
    }



    public static void printMap(Map mp) {

        try {
            Iterator it = mp.entrySet().iterator();
            if(it != null) {
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        putPreviousMarkers();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {

        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());
        setUpMap(lat, lng);

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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    /*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    */

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                marker.setTitle("Latitude: " + toPosition.latitude + "Longitude: " + toPosition.longitude);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;
        //noinspection SimplifiableIfStatement
        switch (id){

            case R.id.my_saved_places:
                i = new Intent(this, MyPlacesActivity.class);
                startActivity(i);
                break;
            case R.id.time_range_link:
                i = new Intent(this, TimeActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void zoomToCurrentLocation(){
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 23));
//        if(initialPosition == 10){


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(20)                   // Sets the zoom
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }
}
