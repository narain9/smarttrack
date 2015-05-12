package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nine on 5/8/2015.
 */

public class ActivityRegisterVehicle extends ActionBarActivity {

    Button login,reg;
    EditText v_plate,v_name,v_type;
    boolean found = false;
    static int id=0;
    String driver_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);

        Intent intent = getIntent();
        driver_id = intent.getStringExtra("driver_id");

        login = (Button)findViewById(R.id.v_login);
        reg   = (Button)findViewById(R.id.v_register);

        v_name  = (EditText) findViewById(R.id.v_name);
        v_plate  = (EditText) findViewById(R.id.v_plate);
        v_type  = (EditText) findViewById(R.id.v_type);

        v_name.setHint("VehicleName");
        v_plate.setHint("PlatNumber");
        v_type.setHint("Type");

        reg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                new JSONparser().execute("http://tiodd.com/smartvehicletracking/register_vehicle2.php?vehicle_name="+v_name.getText().toString()+"&vehicle_type="+v_type.getText().toString()+"&plat_num="+v_plate.getText().toString()+"&driver_id="+driver_id.toString());
            }

        });

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(ActivityRegisterVehicle.this,ActivityLogin.class);
        //        intent.putExtra("driver_id", driver_id);
                startActivity(intent);
            }
        });
    }

    private class JSONparser extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                HttpResponse response = httpclient.execute(httppost);
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

                s = json.getString("success");

                Log.e("Response: ","success: "+json.getString("success")+",message: "+json.getString("message") );

                Toast.makeText(getApplicationContext(), "success: "+json.getString("success")+",message: "+json.getString("message"), Toast.LENGTH_SHORT).show();

                if (s.equals("1")) {
                    found = true;
                    Toast.makeText(getApplicationContext(), "Registered Successfully..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityRegisterVehicle.this,ActivityTracking.class);
                //    intent.putExtra("driver_id", json.getString("driver_id"));
                    intent.putExtra("vehicle_id", json.getString("vehicle_id"));
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                }
           //     }
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

