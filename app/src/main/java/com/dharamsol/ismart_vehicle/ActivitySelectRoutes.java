package com.dharamsol.ismart_vehicle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActivitySelectRoutes extends ActionBarActivity {
    Button find;
    Spinner source;
    Spinner destination;

    private double Latitude = 0;
    private double Longitude = 0;
    GPSTracker myTracker;
    // Spinner Drop down elements
    List<String> dropdown_source = new ArrayList<String>();
    List<String> dropdown_destination = new ArrayList<String>();
    List<String> dropdown_sourceID = new ArrayList<String>();
    List<String> dropdown_destinationID = new ArrayList<String>();
    String LINK_URL = "http://smarttrack.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_routes);

        find = (Button)findViewById(R.id.btn_find);
        source = (Spinner)findViewById(R.id.spinner_source);
        destination = (Spinner)findViewById(R.id.spinner_destination);

        myTracker = new GPSTracker(ActivitySelectRoutes.this);

        if(myTracker.canGetLocation()){
            Latitude = myTracker.getLatitude();
            Longitude = myTracker.getLongitude();
            Toast.makeText(getApplicationContext(), "Lat:"+Latitude+", Lon:"+Longitude, Toast.LENGTH_SHORT).show();
        }
        else{
            myTracker.showSettingsAlert();
        }

//        Latitude = myTracker.getLatitude();
//        Longitude = myTracker.getLongitude();

        //"search_vehicles_by_Location.php?latitude="+Latitude+"&longitude="+Longitude
        new AllStopsLoader().execute(LINK_URL +"query_users/qnear_stops?lat="+myTracker.getLatitude()+"&long="+myTracker.getLongitude());
    //    new BackgroundLoader2().execute(LINK_URL + "stops.json");

        myTracker.stopUsingGPS();

        find.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d("source", dropdown_source.get(source.getSelectedItemPosition()));
                Log.d("destination", dropdown_destination.get(destination.getSelectedItemPosition()));

                Intent intent = new Intent(ActivitySelectRoutes.this,ActivityAllVehiclesActiveBySelectedRoutes.class);
                intent.putExtra("sourceID", dropdown_sourceID.get(source.getSelectedItemPosition()));
                intent.putExtra("destinationID", dropdown_destinationID.get(destination.getSelectedItemPosition()));
                startActivity(intent);
            }

        });
    }

    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ActivitySelectRoutes.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
             //   new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
            //    registerErrorMsg.setText("Error in Network Connection");
            }
        }
    }

    private class AllStopsLoader extends AsyncTask<String,Void,String> {
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
                    dropdown_source.add(json.getString("stop_name"));
                    dropdown_sourceID.add(json.getString("stop_id"));
                    Log.e("i=", json.toString());

                //    Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivitySelectRoutes.this, android.R.layout.simple_spinner_dropdown_item, dropdown_source);
                // Drop down layout style - list view with radio button
                //    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                source.setAdapter(dataAdapter);

                for (int j = jArray.length()-1; j >=0; j--) {
                    JSONObject json = null;

                    json = jArray.getJSONObject(j);
                    // Integer.decode(json.getString("vehicle_id")),
                    dropdown_destination.add(json.getString("stop_name"));
                    dropdown_destinationID.add(json.getString("stop_id"));

                    Log.e("j=", json.toString());

                    //    Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(ActivitySelectRoutes.this, android.R.layout.simple_spinner_dropdown_item, dropdown_destination);

                // Drop down layout style - list view with radio button
                //    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                destination.setAdapter(dataAdapter2);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
