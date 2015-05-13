package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nine on 5/10/2015.
 */
public class ActivityAllVehiclesActive extends ActionBarActivity {
    Button vFind;
    Spinner vehicle;

    // Spinner Drop down elements
    List<String> active_vehicles = new ArrayList<String>();
    List<String> active_IDS = new ArrayList<String>();
    String vehicleSelected = "";
    String LINK_URL1 = "http://smarttrack.herokuapp.com/vehicles/search_by_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_active_vehicles);

        vFind = (Button)findViewById(R.id.btn_startTrack);
        vehicle = (Spinner)findViewById(R.id.select_active_vehicle);

        Intent intent = getIntent();
        vehicleSelected = intent.getStringExtra("vehicle_name");

        try {
            String query1 = URLEncoder.encode(vehicleSelected, "utf-8");
            new ActiveVehiclesAsyncLoader().execute(LINK_URL1 + "?busName="+query1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        vFind.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(!active_vehicles.isEmpty()){
                    Log.d("vehicle_name", active_vehicles.get(vehicle.getSelectedItemPosition()));
                    Toast.makeText(getApplicationContext(), "vehicleName: " + active_IDS.get(vehicle.getSelectedItemPosition()) + "vehicleID: " + active_IDS.get(vehicle.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ActivityAllVehiclesActive.this,ActivityGoogleMaps.class);
                    intent.putExtra("vehicle_id", active_IDS.get(vehicle.getSelectedItemPosition()));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry! Right now, No Active Vehicle Found..", Toast.LENGTH_SHORT).show();
                    try {
                        String query1 = URLEncoder.encode(vehicleSelected, "utf-8");
                        new ActiveVehiclesAsyncLoader().execute(LINK_URL1 + "?busName="+query1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private class ActiveVehiclesAsyncLoader extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(params[0]);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("jj=", data.toString());
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("First TRY", "Error in http connection:" + e.toString());
            }

            return data;

        }

        @Override
        protected void onPostExecute(String data) {
            //    super.onPostExecute(data);
            //    Log.e("i=", data.toString());
            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);
                int count=0;
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = null;
                    count++;
                    json = jArray.getJSONObject(i);
                    // Integer.decode(json.getString("vehicle_id")),
                //    active_vehicles.add(json.getString("name"));
                    active_vehicles.add(json.getString("name") + " , " + json.getString("id") );
                    active_IDS.add(json.getString("id"));

                    Log.e("i=", json.toString());
                }
                if(count == 0){
                    Toast.makeText(getApplicationContext(), "Sorry! No Active Vehicle Found.", Toast.LENGTH_SHORT).show();
                }
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityAllVehiclesActive.this, android.R.layout.simple_spinner_dropdown_item, active_vehicles);

                // Drop down layout style - list view with radio button
                //    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                vehicle.setAdapter(dataAdapter);

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

