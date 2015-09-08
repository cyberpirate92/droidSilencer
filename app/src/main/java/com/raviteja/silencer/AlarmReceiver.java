package com.raviteja.silencer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by CYBERZEUS on 06-09-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String msg = "It's time";
        if(intent.hasExtra("description"))
        {
            msg = intent.getStringExtra("description");
        }
        if(intent.hasExtra("id")) {
            long ID = intent.getLongExtra("id", -1);
            if(ID == -1){
                Log.d("Silencer-Broadcast","ERROR: ID is -1, no id received in intent, cancelling this intent");
                return;
            }
            MasterDB db = new MasterDB(context);
            if(db.idExists(ID)) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
                if (intent.hasExtra("startTime") && intent.hasExtra("endTime")) {
                    displayNotification(intent.getStringExtra("startTime"), intent.getStringExtra("endTime"));
                }
                silencePhone();
            }
            else {
                Log.d("Silencer-Broadcast","Alarm with Title '"+msg+"' has been canceled the inefficient way :( :( ");
            }
            db.close();
        }
    }
    private void silencePhone()
    {
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d("Silencer-Volume","Phone is currently in silent mode, restoring to normal mode..");
            if(manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                Log.d("Silencer-Volume","Phone is set to normal mode.");
            }
            else{
                Log.d("Silencer-Volume","Unable to set phone in normal mode.");
            }
        }
        else {
            manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Log.d("Silencer-Volume", "Phone is currently in normal mode, restoring to silent mode..");
            if(manager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
                Log.d("Silencer-Volume","Phone is set to silent mode now.");
            }
            else {
                Log.d("Silencer-Volume","Unble to set phone in silent mode.");
            }
        }

    }

    private void displayNotification(String from,String to)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Silencer");
        builder.setContentText("Silencer has silenced your phone until "+to);
        builder.setAutoCancel(false);

        Notification notification = builder.getNotification();
        manager.notify(12,notification);
        //manager.notify(12,builder.build());
    }
}
