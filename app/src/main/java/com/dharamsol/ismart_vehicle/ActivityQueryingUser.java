package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nine on 5/10/2015.
 */
public class ActivityQueryingUser extends ActionBarActivity {

    Button btn_bus,btn_routes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_querying_user);

        btn_bus = (Button)findViewById(R.id.btn_bus);
        btn_routes = (Button)findViewById(R.id.btn_routes);

        btn_bus.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivityQueryingUser.this,ActivitySelectVehicle.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });

        btn_routes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivityQueryingUser.this,ActivitySelectRoutes.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });
    }

}
