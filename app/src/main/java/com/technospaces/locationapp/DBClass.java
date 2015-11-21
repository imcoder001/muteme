package com.technospaces.locationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Coder on 8/6/2015.
 */

public class DBClass extends SQLiteOpenHelper {
    public DBClass(Context context) {
        super(context, "MyLocationDB", null, 12);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("On Create", "Called On Create");
        String sql = "CREATE TABLE LocationTbl(" +
                "_id INTEGER PRIMARY KEY," +
                "Name VARCHAR(50)," +
                "Latitude VARCHAR(50)," +
                "Longitude VARCHAR(50)" +
                ")";
        String sql1 = "CREATE TABLE TimeTbl(" +
                "_id INTEGER PRIMARY KEY," +
                "Name VARCHAR(50)," +
                "timeStart TEXT," +
                "timeEnd TEXT," +
                "repeatWeekly INTEGER(1)," +
                "alarmIds TEXT" +
                ")";
        String sql2 = "CREATE TABLE RepeatDayTbl(" +
                "_id INTEGER PRIMARY KEY," +
                "repeatDay VARCHAR(50)," +
                "timeId INTEGER," +
                "FOREIGN KEY(timeId) REFERENCES TimeTbl(_id)" +
                ")";
        try {
            db.execSQL(sql);
            db.execSQL(sql1);
            db.execSQL(sql2);
        }
        catch (Exception e)
        {
            Log.i("Exception", e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("On Upgrade","Called On Upgrade");
        String sql = "DROP TABLE IF EXISTS LocationTbl";
        String sql1 = "DROP TABLE IF EXISTS TimeTbl";
        String sql2 = "DROP TABLE IF EXISTS RepeatDayTbl";
        try {
            db.execSQL(sql);
            db.execSQL(sql1);
            db.execSQL(sql2);
            onCreate(db);

        }
        catch (Exception e)
        {
            Log.i("Exception", e.getMessage());
        }
    }
    public long insertRecord(String  name, double latitude, double longitude) {
        try
        {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Name", name);
            values.put("Latitude", Double.toString(latitude));
            values.put("Longitude", Double.toString(longitude));
            long rowId = database.insert("LocationTbl", null, values);
            database.close();
            return rowId;

        }
        catch (Exception e){
            return -1;
        }
    }

    public long insertRecordTimeTbl(String  name, String timeStart, String timeEnd, int repeatWeekly){
        try
        {

            int _id1 = (int) System.currentTimeMillis();

            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Name", name);
            values.put("timeStart", timeStart);
            values.put("timeEnd", timeEnd);
            values.put("repeatWeekly", repeatWeekly);
            String alarmIds = _id1+","+(_id1+1);
            values.put("alarmIds", alarmIds);
            long rowId = database.insert("TimeTbl", null, values);
            database.close();
            return rowId;

        }
        catch (Exception e){
            return -1;
        }
    }
//    public List<HashMap<String, String>> selectAllUserPlaces(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM LocationTbl", null);
//
//        List<HashMap<String, String>> allPlaces = new ArrayList<HashMap<String, String>>();
//
//
//        if(c.moveToFirst()){
//            int count = 0;
//            do{
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("_id", c.getString(0));
//                map.put("name", c.getString(1));
//                map.put("latitude", c.getString(2));
//                map.put("longitude", c.getString(3));
//                allPlaces.add(count++, map);
//
//            }while(c.moveToNext());
//            return allPlaces;
//        }
//        c.close();
//        db.close();
//        return null;
//    }

    public List<Place> selectAllUserPlaces(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM LocationTbl", null);

        List<Place> allPlaces = new ArrayList<Place>();


        if(c.moveToFirst()){
            int count = 0;
            do{
                Place place = new Place();
                place.set_id(Integer.parseInt(c.getString(0)));
                place.setName(c.getString(1));
                place.setLatitude(Double.parseDouble(c.getString(2)));
                place.setLongitude(Double.parseDouble(c.getString(3)));
                allPlaces.add(count++, place);

            }while(c.moveToNext());
            return allPlaces;
        }
        c.close();
        db.close();
        return null;
    }



    public List<TimeRange> selectAllTimeRanges(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM TimeTbl", null);

        List<TimeRange> allTime = new ArrayList<TimeRange>();


        if(c.moveToFirst()){
            int count = 0;
            do{
                TimeRange timeRange = new TimeRange();
                timeRange.set_id(Integer.parseInt(c.getString(0)));
                timeRange.setName(c.getString(1));
                timeRange.setTimeStart(c.getString(2));
                timeRange.setTimeEnd(c.getString(3));
                timeRange.setAlarmIds(c.getString(5));
                allTime.add(count++, timeRange);

            }while(c.moveToNext());
            return allTime;
        }
        c.close();
        db.close();
        return null;
    }


    /*
    public List<HashMap<String, String>> selectAllTime(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM TimeTbl", null);

        List<HashMap<String, String>> allTime = new ArrayList<HashMap<String, String>>();


        if(c.moveToFirst()){
            int count = 0;
            do{
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", c.getString(0));
                map.put("Name", c.getString(1));
                map.put("timeStart", c.getString(2));
                map.put("timeEnd", c.getString(3));
                map.put("repeatWeekly", c.getString(4));
                allTime.add(count++, map);

            }while(c.moveToNext());
            return allTime;
        }
        c.close();
        db.close();
        return null;
    }
    */


    public boolean deleteUserPlace(long _id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM LocationTbl WHERE _id = "+ _id;
        try {
            db.execSQL(sql);
            return true;

        }
        catch (Exception e)
        {
            Log.i("Exception", e.getMessage());
            return false;
        }
    }
    public boolean deleteTime(long _id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM TimeTbl WHERE _id = "+ _id;
        try {
            db.execSQL(sql);
            return true;

        }
        catch (Exception e)
        {
            Log.i("Exception", e.getMessage());
            return false;
        }
    }
    public boolean deleteTime(TimeRange timeRange){
        SQLiteDatabase db = this.getReadableDatabase();
        int _id = timeRange.get_id();
        String sql = "DELETE FROM TimeTbl WHERE _id = "+ _id;
        try {
            db.execSQL(sql);
            return true;

        }
        catch (Exception e)
        {
            Log.i("Exception", e.getMessage());
            return false;
        }
    }
}
