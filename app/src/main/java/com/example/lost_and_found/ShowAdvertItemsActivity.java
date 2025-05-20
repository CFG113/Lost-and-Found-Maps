package com.example.lost_and_found;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ShowAdvertItemsActivity extends AppCompatActivity {

    private DbHelper db;
    private final ArrayList<Long> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_show_advert_items);

        db = new DbHelper(this);

        ListView lv = findViewById(R.id.listViewAdverts);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(this, AdvertDetailsActivity.class);
            i.putExtra("ROW_ID", ids.get(position));
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    /* --------------------- helpers --------------------- */

    private void populateList() {
        ListView lv = findViewById(R.id.listViewAdverts);
        ids.clear();
        ArrayList<String> preview = new ArrayList<>();

        Cursor c = db.fetchAll(); // unchanged DbHelper API
        while (c.moveToNext()) {
            ids.add(c.getLong(c.getColumnIndexOrThrow("id"))); // safer: use column name

            // Combine status + author, e.g., "Found - John"
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            String author = c.getString(c.getColumnIndexOrThrow("author"));
            preview.add(status + " - " + author);
        }
        c.close();

        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, preview));
    }
}
