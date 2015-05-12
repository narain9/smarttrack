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

public class ActivityGoogleMaps extends FragmentActivity {
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
    private String vehicleName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        Intent intent = getIntent();

        busID = intent.getStringExtra("vehicle_id");
        reserveSeat = (Button)findViewById(R.id.btn_reserve);

        myTracker = new GPSTracker(ActivityGoogleMaps.this);

        if(myTracker.canGetLocation()){
            Latitude = myTracker.getLatitude();
            Longitude = myTracker.getLongitude();
//            Latitude = 24.88478;
//            Longitude= 67.17538;
            Toast.makeText(getApplicationContext(), "Current Lat:"+Latitude+", Lon:"+Longitude, Toast.LENGTH_SHORT).show();
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

    public void animateMarker2(final MarkerOptions marker, final LatLng toPosition)
    {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 2500;

        final Interpolator interpolator = new BounceInterpolator();
        marker.visible(true);

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);

                marker.anchor(0.5f, 1.0f + 6 * t);
                marker.position(toPosition);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
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
        Log.d("moving","moving ."+counter);
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
     * If it isn't installed {@link SupportMapFragment} (and
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
                mMap.setMyLocationEnabled(true);

                mMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
        LatLng currentPosition = new LatLng(myTracker.getLatitude(),myTracker.getLongitude());
        rectOptions.add(currentPosition);
        mMap.addMarker(new MarkerOptions()
                        .position(currentPosition)
                        .snippet("Lat: " + myTracker.getLatitude() + ",Lng: " + myTracker.getLongitude())
                        .flat(true)
                        .title("You are Here!").visible(true)
        ).showInfoWindow();
        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getMaxZoomLevel() - 3));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition,  mMap.getMaxZoomLevel()-3 ));

    }
}
