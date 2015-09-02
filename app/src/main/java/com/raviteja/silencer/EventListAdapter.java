package com.raviteja.silencer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by CYBERZEUS on 02-09-2015.
 */
public class EventListAdapter extends CursorAdapter
{

    Context context;
    ArrayList<SilenceEvent> events;

    public EventListAdapter(Context ctx,Cursor cursor)
    {
        super(ctx,cursor,true);
        context = ctx;
        this.events = new ArrayList<SilenceEvent>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newView = inflater.inflate(R.layout.event_list_item,parent,false); // returns a new view
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvDes,tvDate,tvTime;
        tvDes = (TextView)view.findViewById(R.id.tvDescription);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvTime = (TextView) view.findViewById(R.id.tvTimeRange);

        SilenceEvent event = new SilenceEvent();
        event.setDescription(cursor.getString(cursor.getColumnIndex(MasterDB.col5)));
        event.setDate(cursor.getString(cursor.getColumnIndex(MasterDB.col2)));
        event.setFromTime(cursor.getString(cursor.getColumnIndex(MasterDB.col3)));
        event.setToTime(cursor.getString(cursor.getColumnIndex(MasterDB.col4)));
        this.events.add(event);

        tvDes.setText(event.getDescription());
        tvDate.setText(event.getDate());
        tvTime.setText(event.getFromTime() + " to "+event.getToTime());
    }
}
