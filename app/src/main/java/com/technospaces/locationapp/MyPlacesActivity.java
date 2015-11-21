package com.technospaces.locationapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyPlacesActivity extends ActionBarActivity {
    ListView allPlacesList;
    LinearLayout my_places_container;
    private DBClass newDB;
    private List<Place> selectAllUserPlaces;
    ArrayAdapter<Place> myArrayAdapter;
    private TextView no_location_message;
//    List<Place> all_places_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        newDB = new DBClass(this);
        newDB.getWritableDatabase();

        selectAllUserPlaces = newDB.selectAllUserPlaces();
        no_location_message = (TextView)findViewById(R.id.no_location_message);



        my_places_container = (LinearLayout) findViewById(R.id.my_places_container);
        allPlacesList = new ListView(this);
        int[] colors = {0xFF96AFAF, 0xFF96AFAF, 0xFF96AFAF}; // red for the example
        allPlacesList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        allPlacesList.setDividerHeight(1);
        allPlacesList.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if(allPlacesList.getChildCount()==0){
                            no_location_message.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );



//        all_locations_list = new ArrayList<String>();
//        all_places_list = new ArrayList<Place>();

        if(selectAllUserPlaces != null){
            myArrayAdapter = new ArrayAdapter<Place>(this, R.layout.places_list, R.id.itemName,
                    selectAllUserPlaces);
            allPlacesList.setAdapter(myArrayAdapter);
            my_places_container.addView(allPlacesList);
        }
        else{
            appendTextView();
        }





        setMyPlacesClickListener();
        setMyPlacesLongClickListener();



    }
    public void appendTextView(){
        TextView textView = new TextView(this);
        textView.setText("No Location Added... To start adding Locations Long Press on the Map Screen");
//            my_places_container.addView(textView);
        textView.setTextColor(Color.WHITE);
        my_places_container.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));
    }
    public void setMyPlacesClickListener(){
        allPlacesList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        parent.getSelectedItemId();
                        Place place = selectAllUserPlaces.get(position);



                        Intent i = new Intent(MyPlacesActivity.this, MainActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("latitude", Double.toString(place.getLatitude()));
                        mBundle.putString("longitude", Double.toString(place.getLongitude()));
                        i.putExtras(mBundle);
                        startActivity(i);
                    }
                }
        );
    }
    public void setMyPlacesLongClickListener(){
        allPlacesList.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        CharSequence actions[] = new CharSequence[] {"Delete"};
                        final int pos = position;

                        Place place = selectAllUserPlaces.get(position);
                        String _id = Integer.toString(place.get_id());
                        final long _id1 = Long.parseLong(_id);


                        AlertDialog.Builder builder = new AlertDialog.Builder(MyPlacesActivity.this);
                        builder.setTitle("Choose an Action");
                        builder.setItems(actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on colors[which]
                                if(true/*newDB.deleteUserPlace(_id)*/){
                                    try{
                                        selectAllUserPlaces.remove(pos);
                                        myArrayAdapter.notifyDataSetChanged();
                                        newDB.deleteUserPlace(_id1);
                                        Toast.makeText(getApplicationContext(),"Location Deleted", Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Not Deleted", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                }
        );
    }

}
