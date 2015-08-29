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
        super(context, "MyLocationDB", null, 5);
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
        try {
            db.execSQL(sql);
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
        try {
            db.execSQL(sql);

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
    public List<HashMap<String, String>> selectAllUserPlaces(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM LocationTbl", null);

        List<HashMap<String, String>> allPlaces = new ArrayList<HashMap<String, String>>();


        if(c.moveToFirst()){
            int count = 0;
            do{
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", c.getString(0));
                map.put("name", c.getString(1));
                map.put("latitude", c.getString(2));
                map.put("longitude", c.getString(3));
                allPlaces.add(count++, map);

            }while(c.moveToNext());
            return allPlaces;
        }
        c.close();
        db.close();
        return null;
    }

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
}
