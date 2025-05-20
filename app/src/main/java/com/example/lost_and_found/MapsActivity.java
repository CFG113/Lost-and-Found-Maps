package com.example.lost_and_found;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Your layout must have <fragment android:id="@+id/map" />

        dbHelper = new DbHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsActivity", "Map fragment not found!");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMarkersFromDb();
    }

    private void showMarkersFromDb() {
        Cursor cursor = dbHelper.fetchAll();
        boolean hasMovedCamera = false;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                if (lat != 0.0 || lng != 0.0) {
                    LatLng location = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(status)
                            .snippet(desc));

                    if (!hasMovedCamera) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                        hasMovedCamera = true;
                    }

                    Log.d("MapsActivity", "Marker: " + status + " @ " + lat + ", " + lng);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Fallback to Melbourne
        if (!hasMovedCamera) {
            LatLng melbourne = new LatLng(-37.8136, 144.9631);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 10));
        }
    }
}
