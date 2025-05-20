package com.example.lost_and_found;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lost_found.sqlite";
    private static final int DB_VER  = 2; // incremented version to trigger onUpgrade

    private static final String T_POSTS = "posts";
    private static final String C_ID          = "id";
    private static final String C_STATUS      = "status";      // Lost | Found
    private static final String C_AUTHOR      = "author";
    private static final String C_CONTACT     = "contact";
    private static final String C_DESC        = "description";
    private static final String C_EVENT_DATE  = "event_date";
    private static final String C_PLACE       = "place";
    private static final String C_LAT         = "latitude";
    private static final String C_LNG         = "longitude";

    public DbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String ddl = "CREATE TABLE " + T_POSTS + " ("
                + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_STATUS + " TEXT, "
                + C_AUTHOR + " TEXT, "
                + C_CONTACT + " TEXT, "
                + C_DESC + " TEXT, "
                + C_EVENT_DATE + " TEXT, "
                + C_PLACE + " TEXT, "
                + C_LAT + " REAL, "
                + C_LNG + " REAL)";
        db.execSQL(ddl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_POSTS);
        onCreate(db);
    }

    // Insert new post
    public long addPost(Post p) {
        ContentValues vals = new ContentValues();
        vals.put(C_STATUS , p.status);
        vals.put(C_AUTHOR , p.author);
        vals.put(C_CONTACT, p.contact);
        vals.put(C_DESC   , p.description);
        vals.put(C_EVENT_DATE, p.eventDate);
        vals.put(C_PLACE  , p.place);
        vals.put(C_LAT    , p.latitude);
        vals.put(C_LNG    , p.longitude);
        return getWritableDatabase().insert(T_POSTS, null, vals);
    }

    // Fetch just id and description for basic listing
    public Cursor fetchAll() {
        return getReadableDatabase()
                .rawQuery("SELECT * FROM " + T_POSTS + " ORDER BY " + C_ID + " DESC", null);
    }

    // Fetch full record by ID
    public Post fetch(long id) {
        Cursor c = getReadableDatabase().query(
                T_POSTS,
                null,
                C_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Post p = new Post(
                c.getString(c.getColumnIndexOrThrow(C_STATUS)),
                c.getString(c.getColumnIndexOrThrow(C_AUTHOR)),
                c.getString(c.getColumnIndexOrThrow(C_CONTACT)),
                c.getString(c.getColumnIndexOrThrow(C_DESC)),
                c.getString(c.getColumnIndexOrThrow(C_EVENT_DATE)),
                c.getString(c.getColumnIndexOrThrow(C_PLACE)),
                c.getDouble(c.getColumnIndexOrThrow(C_LAT)),
                c.getDouble(c.getColumnIndexOrThrow(C_LNG))
        );

        p.id = c.getLong(c.getColumnIndexOrThrow(C_ID));
        c.close();
        return p;
    }

    // Delete by ID
    public boolean delete(long id) {
        return getWritableDatabase().delete(T_POSTS, C_ID + "=?", new String[]{String.valueOf(id)}) == 1;
    }

    // Data model
    public static class Post {
        public long id;
        public String status, author, contact, description, eventDate, place;
        public double latitude, longitude;

        public Post(String status, String author, String contact, String desc, String date, String place, double lat, double lng) {
            this.status = status;
            this.author = author;
            this.contact = contact;
            this.description = desc;
            this.eventDate = date;
            this.place = place;
            this.latitude = lat;
            this.longitude = lng;
        }
    }
}
