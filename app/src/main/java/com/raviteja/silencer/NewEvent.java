package com.raviteja.silencer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;


public class NewEvent extends ActionBarActivity {

    EditText etDate,etFromTime,etToTime,etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        etDate = (EditText) findViewById(R.id.editText);
        etFromTime = (EditText) findViewById(R.id.editText2);
        etToTime = (EditText) findViewById(R.id.editText4);
        etDescription = (EditText) findViewById(R.id.editText3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
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

    public void goBack()
    {
        Intent intent = new Intent(this,MainActivity.class);
        this.startActivity(intent);
    }

    public void onSaveClick(View view)
    {
        String date = etDate.getText().toString();
        String fromDate = etFromTime.getText().toString();
        String toDate = etToTime.getText().toString();
        String description = etDescription.getText().toString();
        MasterDB db = new MasterDB(NewEvent.this);

        if(date.length() == 0)
            etDate.setBackgroundColor(Color.RED);

        else if(fromDate.length() == 0)
            etFromTime.setBackgroundColor(Color.RED);

        else if(toDate.length() == 0)
            etToTime.setBackgroundColor(Color.RED);

        else if(description.trim().length() == 0)
            etDescription.setBackgroundColor(Color.RED);

        else {
            db.insert(date, fromDate, toDate, description.trim());
            displayToast("Event added.");
            goBack();
        }
    }

    public void onTimeFieldClick(View view)
    {
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
            timePicker.show();
        }
        catch(Exception e)
        {
            displayToast(e.getMessage());
        }
    }

    public void onDateFieldClick(View view)
    {
        try {
            final EditText target = (EditText) view;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(NewEvent.this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    target.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
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
