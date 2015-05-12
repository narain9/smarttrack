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
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nine on 5/10/2015.
 */

public class ActivityPassengerClick extends ActionBarActivity {
    Button submit;
    EditText vehicleID;
    Spinner vehicleName;

    // Spinner Drop down elements
    List<String> dropdown_vehicles = new ArrayList<String>();

    String LINK_URL1 = "http://smarttrack.herokuapp.com/vehicles.json";
    String LINK_URL2 = "http://smarttrack.herokuapp.com/share_users/lets_share";
    boolean found = false;
    static int id=0;
    String u,p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_click);

        submit = (Button)findViewById(R.id.btn_submit);
        vehicleName = (Spinner)findViewById(R.id.spinner_vehicles);
        vehicleID = (EditText)findViewById(R.id.p_vehicleID);

        new BackgroundLoader().execute(LINK_URL1);

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String selected_vehicle = dropdown_vehicles.get(vehicleName.getSelectedItemPosition());
                String selected_vehicle_id = vehicleID.getText().toString();

                Toast.makeText(getApplicationContext(), "vehicleName="+selected_vehicle+",vehicleId="+selected_vehicle_id, Toast.LENGTH_SHORT).show();

                new JSONParser().execute(LINK_URL2+"?vehicleId="+selected_vehicle_id+"&vehicleName="+selected_vehicle);

            }

        });
    }

    private class BackgroundLoader extends AsyncTask<String,Void,String> {

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
                    dropdown_vehicles.add(json.getString("name"));

                    Log.e("i=", json.toString());

                    Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityPassengerClick.this, android.R.layout.simple_spinner_dropdown_item, dropdown_vehicles);

                // Drop down layout style - list view with radio button
            //    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                vehicleName.setAdapter(dataAdapter);

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

            return data;

        }

        @Override
        protected void onPostExecute(String data) {
            //    super.onPostExecute(data);

            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);

            //    for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = null;
                json = jArray.getJSONObject(0);

                Log.e("i=", json.toString());
                Toast.makeText(getApplicationContext(), "i="+json.toString(), Toast.LENGTH_SHORT).show();
                if(json.getInt("success") == 1){
                    Intent intent = new Intent(ActivityPassengerClick.this,ActivityTracking.class);
                    intent.putExtra("user_id", json.getString("id"));
                    startActivity(intent);
                }
             //   Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
            //    }
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
