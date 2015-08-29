package com.technospaces.locationapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTimeActivity extends ActionBarActivity {

    private static TextView timeViewStart;
    private static TextView timeViewEnd;

    private static Button pickStartingTimeBtn;
    private static Button pickEndingTimeBtn;

    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_time_form);


        timeViewStart = (TextView) findViewById(R.id.timeViewStart);
        timeViewEnd = (TextView) findViewById(R.id.timeViewEnd);


        pickStartingTimeBtn = (Button) findViewById(R.id.pickStartingTimeBtn);
        pickEndingTimeBtn = (Button) findViewById(R.id.pickEndingTimeBtn);

        date = Calendar.getInstance();
        pickStartingTimeClickListener();
        pickEndingTimeClickListener();
    }

    private void pickStartingTimeClickListener() {
        pickStartingTimeBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddTimeActivity.this);
                        final TimePicker picker = new TimePicker(AddTimeActivity.this);

                        builder.setTitle("Pick Starting Time");
                        builder.setView(picker);
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Set", null);


                        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String selectedTime = picker.getCurrentHour() + " : " + picker.getCurrentMinute();
                                timeViewStart.setText(selectedTime);
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

                        builder.setTitle("Pick Starting Time");
                        builder.setView(picker);
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Set", null);


                        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String selectedTime =  picker.getCurrentHour() + " : " + picker.getCurrentMinute();
                                timeViewEnd.setText(selectedTime);
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

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static class TimePickerFragment extends DialogFragment
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
