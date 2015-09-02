package com.raviteja.silencer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CYBERZEUS on 02-09-2015.
 */
public class MasterDB
{
    static String db_name = "sevents.db";
    static String table_name = "events";
    static String col1 = "_id";
    static String col2 = "date";
    static String col3 = "from_time";
    static String col4 = "to_time";
    static String col5 = "description";
    static int version = 1;

    SQLiteDatabase database;
    Helper helper;

    class Helper extends SQLiteOpenHelper
    {
        public Helper(Context ctx,String name,SQLiteDatabase.CursorFactory factory,int version)
        {
            super(ctx,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "+table_name+" ( "+col1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+col2+" TEXT,"+col3+" TEXT,"+col4+" TEXT,"+col5+" TEXT )";
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
    }

    public void insert(String date,String fromTime,String toTime,String description) {
        ContentValues values = new ContentValues();
        values.put(col2, date);
        values.put(col3, fromTime);
        values.put(col4, toTime);
        values.put(col5, description);
        database.insert(table_name,null,values);
    }

    public boolean delete(int id) {
        return database.delete(table_name,col1+"="+id,null) > 0;
    }

    public Cursor getAllEvents(){
        return database.rawQuery("SELECT * FROM "+table_name,null);
    }

    public void close(){
        database.close();
    }
}
