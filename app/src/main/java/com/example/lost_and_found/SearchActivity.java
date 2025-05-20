package com.example.lost_and_found;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Create Places client
        PlacesClient placesClient = Places.createClient(this);

        // Use your actual fragment ID here
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
            ));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    if (place.getLatLng() != null) {
                        Intent data = new Intent();
                        data.putExtra("lat", place.getLatLng().latitude);
                        data.putExtra("lng", place.getLatLng().longitude);
                        data.putExtra("chosen_address", place.getAddress());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(SearchActivity.this, "Error: " + status, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Fragment not found: check R.id.search_fragment", Toast.LENGTH_SHORT).show();
        }
    }
}
