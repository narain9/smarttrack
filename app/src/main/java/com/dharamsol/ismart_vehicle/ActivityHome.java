package com.dharamsol.ismart_vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Nine on 5/10/2015.
 */
public class ActivityHome extends ActionBarActivity {
    Button sharing,querying;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharing = (Button)findViewById(R.id.btn_sharingUser);
        querying = (Button)findViewById(R.id.btn_queryingUser);

        sharing.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivityHome.this,ActivitySharingUser.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });

        querying.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ActivityHome.this,ActivityQueryingUser.class);
                //    intent.putExtra("ID", "1");
                startActivity(intent);
            }

        });
    }
}
