package com.raviteja.silencer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by raviteja on 02-09-2015.
 */
class EventListAdapter extends CursorAdapter
{

    private Context context;
    private ArrayList<SilenceEvent> events;
    private Typeface typeface_medium,typeface_bold;

    public EventListAdapter(Context ctx,Cursor cursor)
    {
        super(ctx,cursor,true);
        context = ctx;
        this.events = new ArrayList<SilenceEvent>();
        typeface_medium = Typeface.createFromAsset(ctx.getAssets(), "fonts/CalendasPlus.otf");
        typeface_bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/Haymaker.ttf");
    }

    public void clear()
    {
        this.events.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.event_list_item,parent,false); // returns a new view
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvDes,tvDate,tvTime;
        tvDes = (TextView)view.findViewById(R.id.tvDescription);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvTime = (TextView) view.findViewById(R.id.tvTimeRange);

        Calendar from = Calendar.getInstance(),to = Calendar.getInstance();
        String description;
        from.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MasterDB.COL_FROM)));
        to.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MasterDB.COL_TO)));
        description = cursor.getString(cursor.getColumnIndex(MasterDB.COL_DESCRIPTION));

        SilenceEvent event = new SilenceEvent();
        event.setSilenceFrom(from);
        event.setSilenceTo(to);
        event.setDescription(description);
        event.setUniqueID(cursor.getInt(cursor.getColumnIndex(MasterDB.COL_ID)));

        this.events.add(event);

        tvDes.setText(event.getDescription());
        tvDate.setText(event.getDateString());
        tvTime.setText(event.getTimeString());

        tvDes.setTypeface(typeface_medium);
        tvDate.setTypeface(typeface_medium);
        tvTime.setTypeface(typeface_bold);
    }

    public long getId(int pos)
    {
        return events.get(pos).getUniqueID();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
