package com.technospaces.locationapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Coder on 8/29/2015.
 */
public class SilentModeOnBroadCastReceiver extends BroadcastReceiver
{
    Calendar date1;
    Calendar date2;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Log.i("SilentModeOn", "Yes");
        Silent silent = new Silent(context);
//        boolean checkPhoneSilent = silent.checkIfPhoneIsSilent();
//        if(!checkPhoneSilent){
        PlaySound ps = new PlaySound();
        ps.playSoundThread();
//            silent.activateAudioMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(context, "Silent Activated !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
//        }

        wl.release();
    }

    public void startAlarmAt(Context context, ArrayList<TimeRange> list) throws ParseException {
        TimeZone timeZone = TimeZone.getDefault();
        final Calendar now = Calendar.getInstance();
        for (int j = 0; j < list.size(); j++ ){
            String timeStart = list.get(j).getTimeStart();
            String alarmIds = list.get(j).getAlarmIds();


            String[] splitAlarmIds = alarmIds.split(",");

//            final int _id = list.get(j).get_id();
            String[] split = timeStart.split(":");

            date1 = Calendar.getInstance(timeZone);
            date2 = Calendar.getInstance(timeZone);

            date1.set(now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    00);

            if(date1.before(GregorianCalendar.getInstance())) {
                date1.add(Calendar.DAY_OF_MONTH, 1);
            }

            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, SilentModeOnBroadCastReceiver.class);
//            final int _id = (int) System.currentTimeMillis();
            PendingIntent pi = PendingIntent.getBroadcast(context, Integer.parseInt(splitAlarmIds[0]), i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, date1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }

    }

    public static void cancelAlarmOn(Context context, int _id){
        Intent i = new Intent(context, SilentModeOnBroadCastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, _id, i, 0);
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy-hh-mm-ss", cal).toString();

        return date;
    }
    public void setAlarm(Context context)
    {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 21);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.i("Calendarhaha", calendar.toString());

        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SilentModeOnBroadCastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, SilentModeOnBroadCastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}