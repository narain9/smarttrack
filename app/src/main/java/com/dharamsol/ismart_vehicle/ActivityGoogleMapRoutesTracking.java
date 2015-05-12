package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ActivityGoogleMapRoutesTracking extends FragmentActivity {
    Button reserveSeat;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double Latitude = 0;
    private double Longitude = 0;
    GPSTracker myTracker;
    Handler mHandler = new Handler();
    boolean StartBtn = false;
    private int interval = 15;
    private int counter = 0;
    PolylineOptions rectOptions = new PolylineOptions();
    private String busID = "2";
    private String src = "";
    private String dest = "";
    List<String> all_vehiclesName = new ArrayList<String>();
    List<String> all_vehiclesID = new ArrayList<String>();
    String LINK_URL = "http://smarttrack.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_selected_routes);

        Intent intent = getIntent();

        src = intent.getStringExtra("source");
        dest = intent.getStringExtra("destination");

        reserveSeat = (Button)findViewById(R.id.btn_reserve2);
        try {
            String query1 = URLEncoder.encode(src, "utf-8");
            String query2 = URLEncoder.encode(dest, "utf-8");
            new BackgroundLoader1().execute(LINK_URL +"stops/near_vehicles?srcStop="+ query1+"&destStop="+query2);
        //    busID = all_vehiclesID.get(all_vehiclesName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        myTracker = new GPSTracker(ActivityGoogleMapRoutesTracking.this);

        if(myTracker.canGetLocation()){
            Latitude = myTracker.getLatitude();
            Longitude = myTracker.getLongitude();
//            Latitude = 24.88478;
//            Longitude= 67.17538;
            Toast.makeText(getApplicationContext(), "Current Lat:" + Latitude + ", Lon:" + Longitude, Toast.LENGTH_SHORT).show();
        }else{
            myTracker.showSettingsAlert();
        }

        setUpMapIfNeeded();
        reserveSeat.setText("Start");

        reserveSeat.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                StartBtn = !StartBtn;
                if(StartBtn){
                    reserveSeat.setText("Stop");
                    StartHandlerThread();
                }
                else{
                    reserveSeat.setText("Start");
                    StopHandlerThread();
                }
            }

        });
    }

    private void StopHandlerThread(){
        Toast.makeText(getBaseContext(), "Updates Stopped!", Toast.LENGTH_LONG).show();
        mMap.clear();
        myTracker.stopUsingGPS();
    }
    private void StartHandlerThread(){
        Toast.makeText(getBaseContext(), "Updates Started!", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (StartBtn) {
                    try {
                        Thread.sleep(interval*1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                post("");
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    public void Moving(double lat, double lon,String time, String bName) {
        Log.d("moving", "moving ." + counter);
        Toast.makeText(getApplicationContext(), "Remaining Time:"+time+" , BusName: "+bName, Toast.LENGTH_SHORT).show();
        final CameraPosition EE_Dept =
                new CameraPosition.Builder().target(new LatLng(lat, lon))
                        .zoom(10) //mMap.getMaxZoomLevel()-1.5f
                        .bearing(0)
                        .tilt(25)
                        .build();
        rectOptions.add(new LatLng(lat,lon));
        Polyline polyline = mMap.addPolyline(rectOptions);

    /*    Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(Latitude, Longitude), new LatLng(lat, lon))
                .width(25)
                .color(Color.BLUE));
    */
//        Latitude = lat;
//        Longitude = lon;

        changeCamera(CameraUpdateFactory.newCameraPosition(EE_Dept), lat, lon,time,bName, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });
    }
    private void changeCamera(CameraUpdate update,double lat, double lon,String time,String bName, GoogleMap.CancelableCallback callback) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .snippet("Lat: " + lat + ",Lng: " + lon)
                        .flat(true)
                        .title("Bus:"+bName+" , Remaining Time: "+time).visible(true)
        ).showInfoWindow();

        int duration = 3000;
        // The duration must be strictly positive so we make it at least 1.
        mMap.animateCamera(update, Math.max(duration, 1), callback);

        // Zoom in the Google Map
        //   mMap.animateCamera(CameraUpdateFactory.zoomTo( mMap.getMaxZoomLevel()-3 ));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat2, lon2),  mMap.getMaxZoomLevel()-3 ));

    }
    private void moveMarker(double lat, double lon) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .snippet("Lat: " + lat + ",Lng: " + lon)
                        .flat(true)
                        .title("Here!").visible(true)
        ).showInfoWindow();
    }
    private class BackgroundLoader1 extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String data = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("First TRY", "Error in http connection:" + e.toString());
            }

            return data;

        }

        @Override
        protected void onPostExecute(String data) {
            //    super.onPostExecute(data);

            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = null;

                    json = jArray.getJSONObject(i);
                    // Integer.decode(json.getString("vehicle_id")),
                    all_vehiclesName.add(json.getString("name"));
                    all_vehiclesID.add(json.getString("id"));
                    Log.e("i=", json.toString());

                    //    Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"JSON Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            //    nDialog.dismiss();
        }
    }
    private class JSONParser extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("First TRY", "Error in http connection:" + e.toString());
            }

            return (data);

        }

        @Override
        protected void onPostExecute(String data) {
            //    super.onPostExecute(data);

            try {
                String dataJSON = data;
                JSONArray jArray = new JSONArray(dataJSON);

                JSONObject json = null;
                json = jArray.getJSONObject(0);

                Log.e("i=", json.toString());
                Toast.makeText(getApplicationContext(), "success: "+json.getString("success"), Toast.LENGTH_SHORT).show();
                if(json.getInt("success") == 1){
                    double lat = Double.parseDouble(json.getString("lat"));
                    double lon = Double.parseDouble(json.getString("long"));
                    Moving(lat,lon,json.getString("time_remaining"),json.getString("bus_name"));
                }
                else{ // success=0
                    Toast.makeText(getApplicationContext(), "success: "+json.getString("success"), Toast.LENGTH_SHORT).show();
                    StartBtn = !StartBtn;
                    if(!StartBtn){
                        StopHandlerThread();
                        reserveSeat.setText("Start");
                    }
                }
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


    private void post(String str) {
        Latitude = myTracker.getLatitude();
        Longitude = myTracker.getLongitude();

        String url = "http://smarttrack.herokuapp.com/share_users/real_coords?busId="+busID+"&lat="+Latitude+"&long="+Longitude;

        Log.d("url=", url);

        new JSONParser().execute(url);
        counter++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // set map type
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                // Enable MyLocation Layer of Google Map
                //    mMap.setMyLocationEnabled(true);

                mMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
                //    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng currentPosition = new LatLng(Latitude,Longitude);

        rectOptions.add(currentPosition);

        mMap.addMarker(new MarkerOptions()
                        .position(currentPosition)
                        .snippet("Lat: " + Latitude + ",Lng: " + Longitude)
                        .flat(true)
                        .title("I am Here!").visible(true)
        ).showInfoWindow();
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel() - 3));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude),  mMap.getMaxZoomLevel()-3 ));

    }
}
