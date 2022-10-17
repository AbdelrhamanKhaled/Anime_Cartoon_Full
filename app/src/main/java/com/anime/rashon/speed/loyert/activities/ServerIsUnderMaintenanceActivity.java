package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.Utilities;
import com.anime.rashon.speed.loyert.app.Config;

public class ServerIsUnderMaintenanceActivity extends AppCompatActivity {
    Button close ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        View decor_View = getWindow().getDecorView();
        Utilities.hideNavBar(decor_View);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_server_is_under_maintance);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}