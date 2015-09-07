package com.raviteja.silencer;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.CursorAdapter;

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
            String sql = "CREATE TABLE IF NOT EXISTS "+table_name+" ( "+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ COL_FROM +" INTEGER,"+ COL_TO +" INTEGER,"+ COL_DESCRIPTION +" TEXT)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public MasterDB(Context context)
    {
        helper = new Helper(context,db_name,null,version);
        database = helper.getWritableDatabase();
        this.context = context;
    }

    public void insert(Calendar fromTime,Calendar toTime,String description) {
        ContentValues values = new ContentValues();
        values.put(COL_FROM, fromTime.getTimeInMillis());
        values.put(COL_TO, toTime.getTimeInMillis());
        values.put(COL_DESCRIPTION, description);
        database.insert(table_name, null, values);

        // setting alarm via AlarmManager
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("description",description);


        String fromString,toString;
        fromString = SilenceEvent.format(fromTime.get(Calendar.HOUR_OF_DAY))+":"+SilenceEvent.format(fromTime.get(Calendar.MINUTE));
        toString = SilenceEvent.format(toTime.get(Calendar.HOUR_OF_DAY))+":"+SilenceEvent.format(toTime.get(Calendar.MINUTE));
        intent.putExtra("startTime",fromString);
        intent.putExtra("endTime",toString);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,12345,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,fromTime.getTimeInMillis(),pendingIntent);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context,12346,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,toTime.getTimeInMillis(),pendingIntent);

        Log.d("Silencer-Alarm", "From: " + fromTime.getTimeInMillis());
        Log.d("Silencer-Alarm","To: "+toTime.getTimeInMillis());
    }

    public int getLastID(){
        String SQL = "SELECT MAX("+COL_ID+") AS "+COL_ID+" FROM "+table_name;
        Cursor result = database.rawQuery(SQL,null);
        try{
            if(result.getCount()>0) {
                result.moveToFirst();
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

    public boolean delete(int id) {
        return database.delete(table_name, COL_ID +"="+id,null) > 0;
    }

    public Cursor getAllEvents(){
        return database.rawQuery("SELECT * FROM "+table_name,null);
    }

    public void close(){
        database.close();
    }
}
