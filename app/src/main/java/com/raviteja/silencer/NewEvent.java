package com.raviteja.silencer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class NewEvent extends ActionBarActivity {

    EditText etFromDate,etToDate,etFromTime,etToTime,etDescription;
    TextView[] repeatDays;
    private final int HIGHLIGHT_COLOR = Color.parseColor("#ff181818");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6D4939")));
        this.repeatDays= new TextView[7];
        repeatDays[0] = (TextView) findViewById(R.id.textView);
        repeatDays[1] = (TextView) findViewById(R.id.textView2);
        repeatDays[2] = (TextView) findViewById(R.id.textView3);
        repeatDays[3] = (TextView) findViewById(R.id.textView4);
        repeatDays[4] = (TextView) findViewById(R.id.textView5);
        repeatDays[5] = (TextView) findViewById(R.id.textView6);
        repeatDays[6] = (TextView) findViewById(R.id.textView7);

        etDescription = (EditText) findViewById(R.id.editText);
        etFromDate = (EditText) findViewById(R.id.editText2);
        etToDate = (EditText) findViewById(R.id.editText4);
        etFromTime = (EditText) findViewById(R.id.editText3);
        etToTime = (EditText) findViewById(R.id.editText5);

        CheckBox cbRepeat = (CheckBox) findViewById(R.id.radioButton);
        cbRepeat.setChecked(true);
        cbRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout dayView = (LinearLayout) findViewById(R.id.linearLayout);
                if(isChecked) {
                    dayView.setVisibility(LinearLayout.VISIBLE);
                }
                else{
                    dayView.setVisibility(LinearLayout.GONE);
                }
            }
        });

        setBoldFont(etDescription);
        setMediumFont(etFromDate);
        setMediumFont(etFromTime);
        setMediumFont(etToDate);
        setMediumFont(etToTime);
        setMediumFont(cbRepeat);

        for(TextView tv : repeatDays){
            setBoldFont(tv);
        }

        if(this.getIntent().hasExtra("ID")) {
            final long ID = getIntent().getLongExtra("d",-1);
            if(ID != -1) {
                setFields(ID);
            }
        }
    }

    public void setFields(long ID)
    {
        SilenceEvent event = (new MasterDB(NewEvent.this)).getSilenceEventById(ID);
        if(event!=null) {
            Calendar from = event.getSilenceFrom();
            Calendar to = event.getSilenceTo();
            String description = event.getDescription();

            // TODO:set the fields here, for opening activity via intent
            etFromDate.setText(this.getDateString(from));
            etToDate.setText(this.getDateString(to));
            etFromTime.setText(this.getTimeString(from));
            etToTime.setText(this.getTimeString(to));

            Button b = (Button) findViewById(R.id.button);
            b.setText("SAVE CHANGES");
        }
        else {
            Log.d("Silencer-NewEvent", "ERROR: Invalid ID found in intent, Cannot edit event with ID:"+ID);
        }
    }

    public void setMediumFont(View view)
    {
        TextView tv = (TextView) view;
        Typeface typeface_medium;
        typeface_medium = Typeface.createFromAsset(NewEvent.this.getAssets(), "fonts/KlinicSlabMedium.otf");
        tv.setTypeface(typeface_medium);
    }

    public void setBoldFont(View view)
    {
        TextView tv = (TextView) view;
        Typeface typeface_bold;
        typeface_bold = Typeface.createFromAsset(NewEvent.this.getAssets(), "fonts/KlinicSlabBold.otf");
        tv.setTypeface(typeface_bold);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    public void handleDayClick(View view)
    {
        TextView target = (TextView) view;

        if(((ColorDrawable)view.getBackground()).getColor() == HIGHLIGHT_COLOR) {
            target.setBackgroundColor(Color.TRANSPARENT);
            target.setTextColor(HIGHLIGHT_COLOR);
        }
        else{
            target.setBackgroundColor(HIGHLIGHT_COLOR);
            target.setTextColor(Color.WHITE);
        }

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

    public void goBack()
    {
        Intent intent = new Intent(this,MainActivity.class);
        this.startActivity(intent);
    }

    public void onSaveClick(View view)
    {
        String fromDate,toDate,fromTime,toTime;

        fromDate = etFromDate.getText().toString();
        toDate = etToDate.getText().toString();
        fromTime = etFromTime.getText().toString();
        toTime = etToTime.getText().toString();

        if(fromDate.isEmpty()){
            etFromDate.setBackgroundColor(Color.RED);
        }
        else if(toDate.isEmpty()){
            etToTime.setBackgroundColor(Color.RED);
        }
        else if(fromTime.isEmpty()){
            etFromTime.setBackgroundColor(Color.RED);
        }
        else if(toTime.isEmpty()){
            etToTime.setBackgroundColor(Color.RED);
        }
        else {

            Calendar from, to;
            from = Calendar.getInstance();
            to = Calendar.getInstance();
            from = getCalendar(fromDate, fromTime);
            to = getCalendar(toDate,toTime);
            MasterDB db = new MasterDB(NewEvent.this);
            db.insert(from,to,etDescription.getText().toString());
            goBack();
        }
    }

    private Calendar getCalendar(String date,String time)
    {
        Calendar c = Calendar.getInstance();
        String dateParts[],timeParts[];
        dateParts = date.split("-");
        timeParts = time.split(":");
        if(dateParts.length != 3 && timeParts.length != 2) {
            return c;
        }
        else{
            int day,month,year,hour,minute;
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            year = Integer.parseInt(dateParts[2]);
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
            c.set(year,month,day,hour,minute,0);
            return c;
        }
    }

    private String getDateString(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR);
    }

    private String getTimeString(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
    }

    public void onTimeFieldClick(View view)
    {
        view.setBackgroundColor(Color.TRANSPARENT);
        try{
            final EditText target = (EditText) view;
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final int min = Calendar.getInstance().get(Calendar.MINUTE);
            int sec = Calendar.getInstance().get(Calendar.SECOND);
            TimePickerDialog timePicker = new TimePickerDialog(NewEvent.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    target.setText(hourOfDay+":"+minute);
                }
            },hour,min,false);
            timePicker.setTitle(target.equals(etFromTime) ? "Start time" : "End time");
            timePicker.show();
        }
        catch(Exception e)
        {
            displayToast(e.getMessage());
        }
    }

    public void onDateFieldClick(View view)
    {
        view.setBackgroundColor(Color.TRANSPARENT);
        try {
            final EditText target = (EditText) view;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(NewEvent.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    target.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                    if(view.equals(etFromDate)){
                        etToTime.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                    }
                }
            }, year, month, day);
            datePicker.show();
        }
        catch(Exception e)
        {
            displayToast(e.getMessage());
        }
    }

    public void displayToast(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
