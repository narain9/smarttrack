package com.dharamsol.ismart_vehicle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ActivityLogin extends ActionBarActivity {

    Button check,reg;
    EditText user,pass;
    boolean found = false;
    static int id=0;
    String u,p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        check = (Button)findViewById(R.id.button);
        reg   = (Button)findViewById(R.id.button2);
        user  = (EditText) findViewById(R.id.editText);
        pass  = (EditText) findViewById(R.id.editText2);

        user.setHint("Email");
        pass.setHint("Password");

        check.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                new JSONparser().execute("http://tiodd.com/smartvehicletracking/login_driver2.php?email="+user.getText().toString()+"&pass="+pass.getText().toString());

            }


        });

        reg.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(ActivityLogin.this,ActivityRegister.class);
              //  intent.putExtra("driver_id", driver_id);
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


            u = user.getText().toString();
            p = pass.getText().toString();

            try {
                String s = "";
                JSONArray jArray = new JSONArray(data);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = null;

                    json = jArray.getJSONObject(i);

                    Toast.makeText(getApplicationContext(), "success: "+json.getString("success")+",message: "+json.getString("message"), Toast.LENGTH_SHORT).show();

                    s = json.getString("email");
                    if (s.equals(u)) {
                        s = json.getString("password_hash");
                        if (p.equals(s)) {
                            found = true;
                            Toast.makeText(getApplicationContext(),"Login Successful.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ActivityLogin.this,ActivityTracking.class);
                        //    intent.putExtra("driver_id", json.getString("driver_id"));
                            intent.putExtra("vehicle_id", json.getString("vehicle_id"));
                            startActivity(intent);
                        }
                        else
                        {
                            found = false;
                            Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
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
