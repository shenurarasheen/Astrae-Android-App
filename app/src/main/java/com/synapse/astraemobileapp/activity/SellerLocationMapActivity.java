package com.synapse.astraemobileapp.activity;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivitySellerLocationBinding;

public class SellerLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivitySellerLocationBinding binding;

    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySellerLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;
        this.latitude = bundle.getDouble("latitude", 0.0);
        this.longitude = bundle.getDouble("longitude", 0.0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.astrae_map);
        mMap.setMapStyle(style);

        LatLng sellerLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sellerLocation).title("Seller location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sellerLocation, 15f));
    }
}