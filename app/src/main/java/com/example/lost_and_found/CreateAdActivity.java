package com.example.lost_and_found;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CreateAdActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private static final int PLACE_SEARCH_REQUEST_CODE = 2001;

    private RadioGroup rgStatus;
    private EditText etName, etPhone, etDesc, etDate;
    private TextView tvLocation;
    private Button btnSave, btnGetCurrentLocation;

    private DbHelper db;
    private FusedLocationProviderClient fusedClient;

    private double selectedLat = 0.0;
    private double selectedLng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);

        rgStatus = findViewById(R.id.statusRadioGroup);
        etName   = findViewById(R.id.inputFullName);
        etPhone  = findViewById(R.id.inputPhoneNumber);
        etDesc   = findViewById(R.id.inputItemDescription);
        etDate   = findViewById(R.id.inputEventDate);
        tvLocation = findViewById(R.id.tvLocation);

        btnSave = findViewById(R.id.btnSubmitPost);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        db = new DbHelper(this);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        tvLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE);
        });

        btnGetCurrentLocation.setOnClickListener(v -> getLocationPermission());
        btnSave.setOnClickListener(v -> persistForm());
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                selectedLat = location.getLatitude();
                selectedLng = location.getLongitude();
                String locationText = "Current Location: " + selectedLat + ", " + selectedLng;
                tvLocation.setText(locationText);

                Toast.makeText(this, "📍 Marker set at your location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void persistForm() {
        String status = ((RadioButton) findViewById(rgStatus.getCheckedRadioButtonId()))
                .getText().toString();
        String name  = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String desc  = etDesc.getText().toString().trim();
        String date  = etDate.getText().toString().trim();
        String place = tvLocation.getText().toString().trim();

        if (anyEmpty(name, phone, desc, date, place)) {
            Toast.makeText(this, "Please fill every field!", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = db.addPost(new DbHelper.Post(status, name, phone, desc, date, place, selectedLat, selectedLng));
        Toast.makeText(this, "✔️ Advert saved (#" + id + ")", Toast.LENGTH_SHORT).show();

        clearForm();
        finish();  // Optional: remove if you want to stay on the page
    }

    private boolean anyEmpty(String... vals) {
        for (String s : vals) if (TextUtils.isEmpty(s)) return true;
        return false;
    }

    private void clearForm() {
        rgStatus.check(R.id.radioLostItem);
        etName.setText("");
        etPhone.setText("");
        etDesc.setText("");
        etDate.setText("");
        tvLocation.setText("");
        selectedLat = 0.0;
        selectedLng = 0.0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedLat = data.getDoubleExtra("lat", 0.0);
            selectedLng = data.getDoubleExtra("lng", 0.0);
            String address = data.getStringExtra("chosen_address");
            tvLocation.setText(address);
        }
    }
}
