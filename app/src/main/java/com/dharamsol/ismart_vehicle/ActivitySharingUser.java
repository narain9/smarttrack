package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nine on 5/10/2015.
 */
public class ActivitySharingUser  extends ActionBarActivity {
    Button passenger,driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_user);

        passenger = (Button)findViewById(R.id.btn_passenger);
        driver = (Button)findViewById(R.id.btn_driver);

        passenger.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivitySharingUser.this,ActivityPassengerClick.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });

        driver.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivitySharingUser.this,ActivityGoogleMaps.class);
                intent.putExtra("driver", "1");
                startActivity(intent);
            }

        });
    }
}
