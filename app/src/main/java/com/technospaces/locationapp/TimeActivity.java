package com.technospaces.locationapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class TimeActivity extends ActionBarActivity {
    ImageButton plusBtn;
    private LinearLayout timeListLayout;
    DBClass newDB;
//    private List<HashMap<String, String>> selectAllTime;
    private List<TimeRange> timeRangeList;

//    ArrayAdapter<String> myArrayAdapter;
    ArrayAdapter<TimeRange> myArrayAdapter;
    List<TimeRange> all_times_list;

    private TextView no_mute_time_tv;
    ListView allTimeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        newDB = new DBClass(this);
        newDB.getWritableDatabase();

//        selectAllTime = newDB.selectAllTime();
        timeRangeList = newDB.selectAllTimeRanges();

        plusBtn = (ImageButton) findViewById(R.id.plusBtn);
        timeListLayout = (LinearLayout) findViewById(R.id.timeListLayout);

        all_times_list = new ArrayList<TimeRange>();
        allTimeListView = new ListView(this);

        int[] colors = {0xFF96AFAF, 0xFF96AFAF, 0xFF96AFAF}; // red for the example
        allTimeListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));

        no_mute_time_tv = (TextView)findViewById(R.id.no_mute_time_tv);
        allTimeListView.setDividerHeight(1);

        setPlusBtnClickListener();
        populateTimeListLayout();
        setMyTimeLongClickListener();

        allTimeListView.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if(allTimeListView.getChildCount() == 0){
                            no_mute_time_tv.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );


    }
    private void populateTimeListLayout(){

        if(timeRangeList != null){
            for(int i = 0; i < timeRangeList.size(); i++){
                TimeRange timeRange = timeRangeList.get(i);
                String result = "";
                result = "Id: "+timeRange.get_id()+"\n"+timeRange.getName()+ "\nTime Start: "+ timeRange.getTimeStart() +"\nTime End: "+ timeRange.getTimeEnd();
                all_times_list.add(timeRange);
            }
        }

        myArrayAdapter = new ArrayAdapter<TimeRange>(this, R.layout.times_list,R.id.timeName,
                all_times_list);
        allTimeListView.setAdapter(myArrayAdapter);
        timeListLayout.addView(allTimeListView);

    }

    public void setMyTimeLongClickListener(){
        allTimeListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        CharSequence actions[] = new CharSequence[]{"Delete"};
                        final int pos = position;
                        final TimeRange timeRange = myArrayAdapter.getItem(position);

//                        final HashMap map = selectAllTime.get(position);
//                        final TimeRange timeRange = timeRangeList.get(position);

//                        String _id = map.get("_id").toString();
                        String _id = Integer.toString(timeRange.get_id());
                        String alarmIds = timeRange.getAlarmIds();
                        final String[] splitAlarmIds = alarmIds.split(",");
                        final long _id1 = Long.parseLong(_id);



                        AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
                        builder.setTitle("Choose an Action");
                        builder.setItems(actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on colors[which]
                                if (true/*newDB.deleteUserPlace(_id)*/) {
                                    try {
                                        all_times_list.remove(pos);
                                        myArrayAdapter.notifyDataSetChanged();
                                        newDB.deleteTime(timeRange);

                                        SilentModeOnBroadCastReceiver.cancelAlarmOn(TimeActivity.this, Integer.parseInt(splitAlarmIds[0]));
                                        SilentModeOffBroadCastReceiver.cancelAlarmOff(TimeActivity.this, Integer.parseInt(splitAlarmIds[1]));

                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), "Not Deleted", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                }
        );
    }
    public void setPlusBtnClickListener(){
        plusBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(TimeActivity.this, AddTimeActivity.class);
                        startActivity(i);
                    }
                }
        );
    }
}
