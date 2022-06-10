package dailynews.localandglobalnews.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import dailynews.localandglobalnews.activities.HomeActivity;
import dailynews.localandglobalnews.activities.StartActivity;

public class MyReceiver extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("inside", "OnCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }
    Handler handler = new Handler();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(periodicUpdate);
        return START_STICKY;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    private final Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 1000 - SystemClock.elapsedRealtime() % 1000);
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(HomeActivity.BroadCastStringForAction);
            broadCastIntent.putExtra("online_status", "" + isOnline(MyReceiver.this));
            sendBroadcast(broadCastIntent);
        }
    };
}