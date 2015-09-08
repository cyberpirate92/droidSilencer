package com.raviteja.silencer;

import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by raviteja on 02-09-2015.
 */
public class MasterDB
{
    static String db_name = "silencer.db";
    static String table_name = "silent_events";
    static String COL_ID = "_id";
    static String COL_FROM = "_from_";
    static String COL_TO = "_to_";
    static String COL_DESCRIPTION = "_description";
    static String COL_ALARM_ID = "_alarmID";
    static int version = 1;

    SQLiteDatabase database;
    Helper helper;
    Context context;

    class Helper extends SQLiteOpenHelper
    {
        public Helper(Context ctx,String name,SQLiteDatabase.CursorFactory factory,int version)
        {
            super(ctx,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "+table_name+" ( "+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ COL_FROM +" INTEGER,"+ COL_TO +" INTEGER,"+ COL_DESCRIPTION +" TEXT, "+COL_ALARM_ID+" INTEGER UNIQUE)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public MasterDB(Context context) {
        helper = new Helper(context, db_name, null, version);
        database = helper.getWritableDatabase();
        this.context = context;
    }

    public void insert(Calendar fromTime,Calendar toTime,String description) {

        int startID,endID = getUniqueRandom();

        ContentValues values = new ContentValues();
        values.put(COL_FROM, fromTime.getTimeInMillis());
        values.put(COL_TO, toTime.getTimeInMillis());
        values.put(COL_DESCRIPTION, description);
        values.put(COL_ALARM_ID, endID);
        long lastID = database.insert(table_name, null, values);

        // self-check...
        if(lastID == (long)getLastID()) {
            Log.d("Silencer-MasterDB","Both Values match, getLastID and insert return value");
        }
        else {
            Log.d("Silencer-MasterDB","Both Values don't match. [getLastID() = "+getLastID()+"],[insert return value = "+lastID+"]");
        }

        // setting alarm via AlarmManager
        startID = getLastID(); // CAUTION:this should not be done before insertion
        Intent startIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        Intent endIntent = new Intent(context.getApplicationContext(),AlarmReceiver.class);

        String fromString,toString;
        fromString = SilenceEvent.format(fromTime.get(Calendar.HOUR_OF_DAY))+":"+SilenceEvent.format(fromTime.get(Calendar.MINUTE));
        toString = SilenceEvent.format(toTime.get(Calendar.HOUR_OF_DAY))+":"+SilenceEvent.format(toTime.get(Calendar.MINUTE));

        startIntent.putExtra("description", description);
        endIntent.putExtra("description", description);
        startIntent.putExtra("id", lastID);
        endIntent.putExtra("id", lastID);
        startIntent.putExtra("startTime", fromString);
        startIntent.putExtra("endTime", toString);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,startID,startIntent,0);
        // setting the start Alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP,fromTime.getTimeInMillis(),pendingIntent);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // setting the end Alarm.
        pendingIntent = PendingIntent.getBroadcast(context,endID,endIntent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,toTime.getTimeInMillis(),pendingIntent);

        Log.d("Silencer-Alarm","Silence has been set for the following range,");
        Log.d("Silencer-Alarm", "From: " + fromTime.getTimeInMillis());
        Log.d("Silencer-Alarm","To: "+toTime.getTimeInMillis());
    }

    public int getLastID(){
        String SQL = "SELECT MAX("+COL_ID+") AS "+COL_ID+" FROM "+table_name;
        Cursor result = database.rawQuery(SQL,null);
        try{
            if(result.getCount()>0) {
                result.moveToFirst();
                Log.d("Silencer-MasterDB","Last ID is "+result.getInt(result.getColumnIndex(COL_ID)));
                return result.getInt(result.getColumnIndex(COL_ID));
            }
            else {
                Log.d("Silencer-MasterDB","Cannot get last id, table seems to be empty.");
                return 0;
            }
        }
        catch(Exception e){
            Log.d("Silencer-MasterDB","ERROR: getLastId() => "+e);
            return 0;
        }
    }

    public int getUniqueRandom() {
        Random random = new Random();
        Cursor cursor;
        while(true) {
            int alarmID = random.nextInt(10000) + 10000;
            cursor = database.rawQuery("SELECT "+COL_ALARM_ID+" FROM "+table_name+" WHERE "+COL_ALARM_ID+" = "+alarmID,null);
            if(cursor.getCount() == 0) {
                return alarmID;
            }
        }
    }

    public boolean idExists(long id)
    {
        Cursor cursor = database.rawQuery("SELECT "+COL_ALARM_ID+" FROM "+table_name+" WHERE "+COL_ID+" = "+id,null);
        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }



    public boolean delete(int id) {
        String sql = "SELECT "+COL_ALARM_ID+" FROM "+table_name+" WHERE "+COL_ID+" = "+id;
        Cursor cursor = database.rawQuery(sql,null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            int startID = id, endID = cursor.getInt(cursor.getColumnIndex(COL_ALARM_ID));
            if (database.delete(table_name, COL_ID + "=" + id, null) > 0) {

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

                // canceling the starting alarm
                Intent startIntent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,startID,startIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);

                // canceling the ending alarm
                Intent endIntent = new Intent(context,AlarmReceiver.class);
                pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),endID,endIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public Cursor getAllEvents(){
        return database.rawQuery("SELECT * FROM "+table_name,null);
    }

    public void close(){
        database.close();
    }
}
