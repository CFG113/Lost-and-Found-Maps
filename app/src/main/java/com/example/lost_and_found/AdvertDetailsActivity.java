package com.example.lost_and_found;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdvertDetailsActivity extends AppCompatActivity {

    private long rowId;
    private DbHelper db;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_advert_details);

        rowId = getIntent().getLongExtra("ROW_ID", -1);
        db = new DbHelper(this);

        DbHelper.Post post = db.fetch(rowId);
        if (post == null) {
            finish();
            return;
        }

        // 👇 Change made here: show "Found - John" etc.
        ((TextView) findViewById(R.id.labelAdvertType)).setText(post.status + " - " + post.author);
        ((TextView) findViewById(R.id.labelAdvertDescription)).setText(post.description);
        ((TextView) findViewById(R.id.labelAdvertContactLocation))
                .setText("☎ " + post.contact + "  •  " + post.place + "\n" + post.eventDate);

        Button btnDel = findViewById(R.id.btnDeleteAdvert);
        btnDel.setOnClickListener(v -> {
            if (db.delete(rowId)) {
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Database problem!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
