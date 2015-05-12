package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nine on 5/10/2015.
 */
public class ActivityVehicleReserved extends ActionBarActivity {
    Button trackVehicle,getAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_reserved);

        trackVehicle = (Button)findViewById(R.id.btn_trackVehicle);
        getAlert = (Button)findViewById(R.id.btn_getAlert);

        trackVehicle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivityVehicleReserved.this,ActivityGoogleMaps.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });

        getAlert.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

            }

        });
    }
}
