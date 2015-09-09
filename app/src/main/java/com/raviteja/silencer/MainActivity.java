package com.raviteja.silencer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    private ListView silenceList;
    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MasterDB db = new MasterDB(this);
        silenceList = (ListView)findViewById(R.id.listView);
        adapter = new EventListAdapter(this,db.getAllEvents());
        silenceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6D4939")));
        silenceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                CharSequence[] options = {"Edit","Delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent i = new Intent(MainActivity.this, NewEvent.class);
                                i.putExtra("ID", adapter.getId(pos));
                                MainActivity.this.startActivity(i);
                                break;
                            case 1:
                                db.delete(adapter.getId(pos));
                                adapter.clear();
                                adapter.changeCursor(db.getAllEvents());
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }
    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent i = new Intent(MainActivity.this,NewEvent.class);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
