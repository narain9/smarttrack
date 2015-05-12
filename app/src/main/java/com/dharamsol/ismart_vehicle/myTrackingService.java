package com.dharamsol.ismart_vehicle;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class myTrackingService extends Service implements LocationListener{
    public static Double a,b;
    public String user_id=null;
    public String minTime = "15000";
    public LocationManager lm ;
    private static String url_add_data = "http://smarttrack.herokuapp.com/share_users/tracking/";
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate()
    {
        super.onCreate();
        a = b = 0.0;
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user_id = intent.getStringExtra("user_id");
    //    user_id = "19";
        minTime = intent.getStringExtra("minTime");
        lm =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,Integer.parseInt(minTime),0,this);
        return START_NOT_STICKY;
    }



    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Booo yah", Toast.LENGTH_SHORT).show();
        a = location.getLatitude();
        b = location.getLongitude();
        if(a!=0 && b!=0){
              new AddRecord().execute(url_add_data,String.valueOf(a),String.valueOf(b),user_id);
              Toast.makeText(this, "Sent to server.(lat:"+a+",lon:"+b+")", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "GPS not enabled..", Toast.LENGTH_SHORT).show();
            showSettingsAlert();
        }
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        lm.removeUpdates(this);
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_SHORT).show();
    }


    class AddRecord extends  AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            String data = "";
            try {
                String url = params[0]+user_id+"?lat="+params[1]+"&long="+params[2];
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(url);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
            //    Toast.makeText(getApplicationContext(), "Boo Yeah.", Toast.LENGTH_SHORT).show();
                Log.d("time",minTime);
                Log.d("lat", params[1]);
                Log.d("long", params[2]);
                Log.d("URl",url);
                Log.d("data",data.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String data) {
            //    super.onPostExecute(data);
            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);
                JSONObject json = null;
                json = jArray.getJSONObject(0);

                Log.e("i=", json.toString());
                Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"JSON Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}


