package com.raviteja.silencer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by raviteja on 06-09-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    private SilenceEvent event;

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
                this.event = db.getSilenceEventById(ID);
                db.close(); // for efficiency
                msg = this.event.getDescription();
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(250);
                silencePhone(ID);
            }
            else {
                Log.d("Silencer-Broadcast","Alarm with Title '"+msg+"' has been canceled the inefficient way :( :( ");
            }
            db.close();
        }
    }
    private void silencePhone(long ID) {
        if(this.event != null) {
            Calendar toDate = this.event.getSilenceTo();
            AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);

            if (System.currentTimeMillis() < toDate.getTimeInMillis()) {
                if (manager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                    manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    displayNotification(ID);
                }
            }
            else {
                if (manager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    dismissNotification(ID);
                }
            }
        }
        else{
            Log.d("Silencer-Volume", "ERROR: Event details null, unable to modify AudioManager!!");
        }
    }

    private void dismissNotification(long ID){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel((int)ID);
    }

    private void displayNotification(long uniqueID)
    {
        String timeString = this.event.getSilenceTo().get(Calendar.HOUR_OF_DAY)+":"+this.event.getSilenceTo().get(Calendar.MINUTE);
        String notifDisplay  = "Phone has been silenced till " + timeString+", Event : "+this.event.getDescription();
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this.context)
                .setTicker(notifDisplay)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Silencer")
                .setContentText(notifDisplay);

        Notification notification = nBuilder.build();
        NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify((int)uniqueID,notification);
    }
}
