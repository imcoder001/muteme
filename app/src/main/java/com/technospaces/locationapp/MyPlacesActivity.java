package com.technospaces.locationapp;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    RelativeLayout my_places_container;
    private DBClass newDB;
    private List<HashMap<String, String>> selectAllUserPlaces;
    ArrayAdapter<String> myArrayAdapter;
    List<String> all_locations_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        newDB = new DBClass(this);
        newDB.getWritableDatabase();

        selectAllUserPlaces = newDB.selectAllUserPlaces();



        my_places_container = (RelativeLayout) findViewById(R.id.my_places_container);
        allPlacesList = new ListView(this);



        all_locations_list = new ArrayList<String>();

        if(selectAllUserPlaces != null){
            for(int i = 0; i < selectAllUserPlaces.size(); i++){
                HashMap<String, String> map = selectAllUserPlaces.get(i);
                String result = "";
                result = map.get("name").toString();
//                for (HashMap.Entry<String, String> entry : map.entrySet())
//                {
//                    result += entry.getKey() + "/" + entry.getValue()+ "\n";
//                }

                all_locations_list.add(result);


            }
        }
        else{
            TextView textView = new TextView(this);
            textView.setText("No Location Added... To start adding Locations Long Press on the Map Screen");
            my_places_container.addView(textView);
        }





        myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                all_locations_list);
        allPlacesList.setAdapter(myArrayAdapter);
        my_places_container.addView(allPlacesList);
        setMyPlacesClickListener();
        setMyPlacesLongClickListener();



    }
    public void setMyPlacesClickListener(){
        allPlacesList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        parent.getSelectedItemId();
                        HashMap map = selectAllUserPlaces.get(position);



                        Intent i = new Intent(MyPlacesActivity.this, MainActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("latitude", map.get("latitude").toString());
                        mBundle.putString("longitude", map.get("longitude").toString());
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

                        HashMap map = selectAllUserPlaces.get(position);
                        String _id = map.get("_id").toString();
                        final long _id1 = Long.parseLong(_id);


                        AlertDialog.Builder builder = new AlertDialog.Builder(MyPlacesActivity.this);
                        builder.setTitle("Choose an Action");
                        builder.setItems(actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on colors[which]
                                if(true/*newDB.deleteUserPlace(_id)*/){
                                    try{
                                        all_locations_list.remove(pos);
                                        myArrayAdapter.notifyDataSetChanged();
                                        newDB.deleteUserPlace(_id1);
                                        Toast.makeText(getApplicationContext(),"Deleted", Toast.LENGTH_LONG).show();
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

    @Override
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

        switch (id){
            case R.id.my_saved_places:
                Intent i = new Intent(this, MyPlacesActivity.class);
                startActivity(i);
                break;
            /*case R.id.my_current_location:
                zoomToCurrentLocation();
                break;
                */
        }

        return super.onOptionsItemSelected(item);
    }
}
