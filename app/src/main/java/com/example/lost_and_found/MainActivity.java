package com.example.lost_and_found;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCompose).setOnClickListener(v ->
                startActivity(new Intent(this, CreateAdActivity.class)));

        findViewById(R.id.btnFeed).setOnClickListener(v ->
                startActivity(new Intent(this, ShowAdvertItemsActivity.class)));

        findViewById(R.id.btnShowMap).setOnClickListener(v ->
                startActivity(new Intent(this, MapsActivity.class)));
    }
}