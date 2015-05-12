package com.dharamsol.ismart_vehicle;

/**
 * Created by Nine on 5/8/2015.
 */

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


public class ActivityRegister extends ActionBarActivity {

    Button login,reg,forgot;
    EditText pass,name,email,phone,cnic;
    boolean found = false;
    static int id=0;
    String vehicle_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

      //  Intent intent = getIntent();
     //   vehicle_id = intent.getStringExtra("vehicle_id");

        login = (Button)findViewById(R.id.r_login);
        reg   = (Button)findViewById(R.id.r_register);

        name  = (EditText) findViewById(R.id.name);
        email  = (EditText) findViewById(R.id.email);
        pass  = (EditText) findViewById(R.id.password);
        phone  = (EditText) findViewById(R.id.phone);
        cnic  = (EditText) findViewById(R.id.cnic);

        name.setHint("Name");
        email.setHint("abc@gmail.com");
        pass.setHint("*****");
        phone.setHint("+923123841914");
        cnic.setHint("4420557191853");

        reg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                new JSONparser().execute("http://tiodd.com/smartvehicletracking/register_driver.php?email="+email.getText().toString()+"&password="+pass.getText().toString()+"&user_name="+name.getText().toString()+"&mobile="+phone.getText().toString()+"&cnic="+cnic.getText().toString());
            }

        });

        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(ActivityRegister.this,ActivityLogin.class);
            //    intent.putExtra("ID", "1");
                startActivity(intent);
            }
        });
    }

    private class JSONparser extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
        //    Toast.makeText(getApplicationContext(), "doInBackground.", Toast.LENGTH_SHORT).show();
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
            //super.onPostExecute(data);
        //    Toast.makeText(getApplicationContext(), "onPostExecute.", Toast.LENGTH_SHORT).show();
            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);
            //    for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = null;

                json = jArray.getJSONObject(0);

                Log.e("Response: ","success: "+json.getString("success")+",message: "+json.getString("message") );

                s = json.getString("success");
                Toast.makeText(getApplicationContext(), "success: "+json.getString("success")+",message: "+json.getString("message"), Toast.LENGTH_SHORT).show();
                if (s.equals("1")) {
                    found = true;
                    Toast.makeText(getApplicationContext(), "Registered Successfully..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityRegister.this,ActivityRegisterVehicle.class);
                    intent.putExtra("driver_id", json.getString("driver_id"));
                //    intent.putExtra("vehicle_id", json.getString("vehicle_id"));
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                }
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

