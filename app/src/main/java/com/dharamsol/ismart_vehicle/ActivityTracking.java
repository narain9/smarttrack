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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ActivityTracking extends ActionBarActivity{

    Button start,stop ;
//    RadioGroup timeGroup;
//    RadioButton timeButton;
    String USER_ID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_stop);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);

    //    track.setBackgroundColor();
    //    stop.setBackgroundColor(0x000);

        Intent intent = getIntent();
        USER_ID = intent.getStringExtra("user_id");

        start.setClickable(false);
        start.setText("STARTED");

        stop.setClickable(true);
        Intent serviceIntent = new Intent(ActivityTracking.this,myTrackingService.class);
        serviceIntent.putExtra("user_id", USER_ID);
        serviceIntent.putExtra("minTime" , "15000");
        startService(serviceIntent);

        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stop.setClickable(false);
                stop.setText("STOPPED");
             //   start.setClickable(true);
                stopService(new Intent(ActivityTracking.this, myTrackingService.class));
                new JSONParser().execute("http://smarttrack.herokuapp.com/share_users/remove/"+USER_ID);
            }
        });
    //    USER_ID = "19";
//        start.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                start.setClickable(false);
//                stop.setClickable(true);
//                timeGroup = (RadioGroup) findViewById(R.id.radioGroup);
//                int selectedId = timeGroup.getCheckedRadioButtonId();
//                timeButton = (RadioButton) findViewById(selectedId);
//
//
//                RadioButton temp1 = (RadioButton) findViewById(R.id.radioButton);
//                RadioButton temp2 = (RadioButton) findViewById(R.id.radioButton2);
//                RadioButton temp3 = (RadioButton) findViewById(R.id.radioButton3);
//
//                if (timeButton.getText() == temp1.getText())
//                {
//                    Intent serviceIntent = new Intent(ActivityTracking.this,myTrackingService.class);
//                    serviceIntent.putExtra("user_id", USER_ID);
//                    serviceIntent.putExtra("minTime" , "15000");
//                    startService(serviceIntent);
//                }
//
//                else if (timeButton.getText() == temp2.getText())
//                {
//                    Intent serviceIntent = new Intent(ActivityTracking.this,myTrackingService.class);
//                    serviceIntent.putExtra("user_id", USER_ID);
//                    serviceIntent.putExtra("minTime" , "30000");
//                    startService(serviceIntent);
//
//                }
//
//                else if (timeButton.getText() == temp3.getText())
//                {
//                    Intent serviceIntent = new Intent(ActivityTracking.this,myTrackingService.class);
//                    serviceIntent.putExtra("user_id", USER_ID);
//                    serviceIntent.putExtra("minTime" , "60000");
//                    startService(serviceIntent);
//
//                }
//            }
//        });
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
                Toast.makeText(getApplicationContext(), "i=" + json.toString(), Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(), "success:" + json.getInt("success"), Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_tracking, menu);
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
