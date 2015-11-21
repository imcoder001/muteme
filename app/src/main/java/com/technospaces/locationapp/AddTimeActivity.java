package com.technospaces.locationapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class AddTimeActivity extends ActionBarActivity {

    private static TextView timeViewStart;
    private static TextView timeViewEnd;

    private static Button pickStartingTimeBtn;
    private static Button pickEndingTimeBtn;

    private Calendar date;
    private Calendar currentDate;
    private ArrayList<Integer> alarmTimeVal;
    private Button saveTimeBtn;
    private DBClass newDB;
    private EditText nameTimeField;
    private Button cancelBtn;

    Calendar date1;
    Calendar date2;
    int selectedHour1 = 0;
    int selectedMinute1 = 0;
    int selectedHour2 = 0;
    int selectedMinute2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_time_form);

        newDB = new DBClass(this);
        newDB.getWritableDatabase();
        nameTimeField = (EditText) findViewById(R.id.nameTimeField);

        timeViewStart = (TextView) findViewById(R.id.timeViewStart);
        timeViewEnd = (TextView) findViewById(R.id.timeViewEnd);



        pickStartingTimeBtn = (Button) findViewById(R.id.pickStartingTimeBtn);
        pickEndingTimeBtn = (Button) findViewById(R.id.pickEndingTimeBtn);
        saveTimeBtn = (Button) findViewById(R.id.saveTimeBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        alarmTimeVal = new ArrayList<Integer>();
        date = Calendar.getInstance();
        currentDate = Calendar.getInstance();

        date1 = Calendar.getInstance();
        date2 = Calendar.getInstance();


        pickStartingTimeClickListener();
        pickEndingTimeClickListener();
        saveTimeBtnClickListener();
        setCancelButtonListener();
    }

    private void setCancelButtonListener() {
        cancelBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(AddTimeActivity.this, TimeActivity.class);
                        startActivity(i);
                    }
                }
        );
    }

    private void saveTimeBtnClickListener(){
        saveTimeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

                        String timeStart = dateFormat.format(date1.getTime());
                        String timeEnd = dateFormat.format(date2.getTime());
                        String name = nameTimeField.getText().toString();
                        if(name.equalsIgnoreCase("")){
                            name="Silent";
                        }


                        if(date1.getTimeInMillis() < date2.getTimeInMillis()){
                            long rowId = newDB.insertRecordTimeTbl(name, timeStart, timeEnd, 0);
                            if(rowId != -1){
                                Toast.makeText(AddTimeActivity.this, "Time Saved Successfully", Toast.LENGTH_LONG).show();

                                ArrayList<TimeRange> list = (ArrayList)newDB.selectAllTimeRanges();
                                Intent i = new Intent(AddTimeActivity.this, MapService.class);
                                i.putExtra("arraylist_start", list);
                                startService(i);
                                Intent intent = new Intent(AddTimeActivity.this, TimeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(AddTimeActivity.this, "Starting time must be less than ending time", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
    private void pickStartingTimeClickListener() {
        pickStartingTimeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTimeActivity.this);
                        final TimePicker picker = new TimePicker(AddTimeActivity.this);

                        if (selectedHour1 == 0) {
                            picker.setCurrentHour(currentDate.getTime().getHours());
                        } else {
                            picker.setCurrentHour(selectedHour1);
                        }

                        if (selectedMinute1 == 0) {
                            picker.setCurrentMinute(currentDate.getTime().getMinutes());
                        } else {
                            picker.setCurrentMinute(selectedMinute1);
                        }


//                        picker.setCurrentMinute(57);

                        builder.setTitle("Pick Starting Time");
                        builder.setView(picker);
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Set", null);


                        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alarmTimeVal.add(picker.getCurrentMinute());
                                Intent i = new Intent(AddTimeActivity.this, MapService.class);


                                int currentHour = picker.getCurrentHour();

                                //adjusting hour and minute for starting time
                                selectedHour1 = picker.getCurrentHour();
                                selectedMinute1 = picker.getCurrentMinute();

                                int currentMinute = picker.getCurrentMinute();
                                String timeFormat = "";
                                String hourString = "";
                                String minuteString = "";


                                if (picker.getCurrentHour() > 11) {
                                    if (picker.getCurrentHour() > 12) {
                                        currentHour = picker.getCurrentHour() % 12;
                                    }
                                    timeFormat = "PM";
                                } else {
                                    if (picker.getCurrentHour() == 0) {
                                        currentHour = 12;
                                    }
                                    timeFormat = "AM";
                                }

                                if (currentHour < 10) {
                                    hourString = "0" + currentHour;
                                } else {
                                    hourString = "" + currentHour;
                                }

                                if (currentMinute < 10) {
                                    minuteString = "0" + currentMinute;
                                } else {
                                    minuteString = "" + currentMinute;
                                }

                                String selectedTime = hourString + " : " + minuteString + " " + timeFormat;
                                timeViewStart.setText(selectedTime);

                                date1.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                                date1.set(Calendar.MINUTE, picker.getCurrentMinute());
                                date1.set(Calendar.SECOND, 0);
                                date1.set(Calendar.MILLISECOND, 0);


                                Bundle bundle = new Bundle();
                                bundle.putIntegerArrayList("0", alarmTimeVal);
                                i.putExtras(bundle);
                                startService(i);
                            }
                        });
                        builder.show();
                    }
                }
        );
    }


    private void pickEndingTimeClickListener() {
        pickEndingTimeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTimeActivity.this);
                        final TimePicker picker = new TimePicker(AddTimeActivity.this);


                        if(selectedHour2 == 0){
                            picker.setCurrentHour(currentDate.getTime().getHours());
                        }
                        else{
                            picker.setCurrentHour(selectedHour2);
                        }

                        if(selectedMinute2 == 0){
                            picker.setCurrentMinute(currentDate.getTime().getMinutes());
                        }
                        else{
                            picker.setCurrentMinute(selectedMinute2);
                        }


                        builder.setTitle("Pick Ending Time");
                        builder.setView(picker);
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Set", null);


                        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int currentHour = picker.getCurrentHour();
                                int currentMinute = picker.getCurrentMinute();


                                //adjusting hour and minute for ending time
                                selectedHour2 = picker.getCurrentHour();
                                selectedMinute2 = picker.getCurrentMinute();



                                String timeFormat = "";
                                String hourString = "";
                                String  minuteString = "";
                                if(picker.getCurrentHour() > 11){
                                    if(picker.getCurrentHour() > 12){
                                        currentHour = picker.getCurrentHour() % 12;
                                    }
                                    timeFormat = "PM";
                                }
                                else{
                                    if(picker.getCurrentHour() == 0){
                                        currentHour = 12;
                                    }
                                    timeFormat = "AM";
                                }

                                if(currentHour < 10){
                                    hourString = "0"+currentHour;
                                }
                                else{
                                    hourString = ""+ currentHour;
                                }

                                if(currentMinute < 10){
                                    minuteString = "0"+currentMinute;
                                }
                                else{
                                    minuteString = ""+ currentMinute;
                                }

                                String selectedTime =  hourString + " : " + minuteString +" " + timeFormat;
                                timeViewEnd.setText(selectedTime);

                                date2.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                                date2.set(Calendar.MINUTE, picker.getCurrentMinute());
                                date2.set(Calendar.SECOND, 0);
                                date2.set(Calendar.MILLISECOND, 0);


                            }
                        });
                        builder.show();
                    }
                }
        );
    }


    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, day);
            String selectedDate =  day + "/" + month +  "/" + year;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date inputDate = null;
            try {
                inputDate = dateFormat.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };
/*
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    */

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    /*
    public void selectDaysClickListener(View view) {
        Button mondayBtn = (Button) findViewById(R.id.mondayBtn);
        Button tuesdayBtn = (Button) findViewById(R.id.tuesdayBtn);
        Button wednesdayBtn = (Button) findViewById(R.id.wednesdayBtn);
        Button thursdayBtn = (Button) findViewById(R.id.thursdayBtn);
        Button fridayBtn = (Button) findViewById(R.id.fridayBtn);
        Button saturdayBtn = (Button) findViewById(R.id.saturdayBtn);
        Button sundayBtn = (Button) findViewById(R.id.sundayBtn);

        switch (view.getId()){
            case R.id.mondayBtn:
                mondayBtn.setBackgroundColor(0xFF21BC5B);
//                mondayBtn.setPadding(10, 10, 10, 10);
                break;
            case R.id.tuesdayBtn:
                tuesdayBtn.setBackgroundColor(0xFF21BC5B);
                break;
            case R.id.wednesdayBtn:
                wednesdayBtn.setBackgroundColor(0xFF21BC5B);
                break;
            case R.id.thursdayBtn:
                thursdayBtn.setBackgroundColor(0xFF21BC5B);
                break;
            case R.id.fridayBtn:
                fridayBtn.setBackgroundColor(0xFF21BC5B);
                break;
            case R.id.saturdayBtn:
                saturdayBtn.setBackgroundColor(0xFF21BC5B);
                break;
            case R.id.sundayBtn:
                sundayBtn.setBackgroundColor(0xFF21BC5B);
                break;
        }
    }
    */
    /*public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String SelectedTime =  hourOfDay + " : " + minute ;
            timeViewStart.setText(SelectedTime);
        }
    }
    */


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        public static Date datePicked;


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String selectedDate =  day + "/" + month +  "/" + year;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date inputDate = null;
            try {
                inputDate = dateFormat.parse(selectedDate);
                this.datePicked = inputDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
