package com.abrebo.konumuygulamasi.ui.fragments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentMapsEtkinlikAyrintiBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragmentEtkinlikAyrinti extends Fragment {
    private FragmentMapsEtkinlikAyrintiBinding binding;
    private String enlem,boylam;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityResultLauncher<String> permissionLauncher;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng location = new LatLng(Double.parseDouble(enlem), Double.parseDouble(boylam));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title("Etkinlik Konumu");
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            // Konum izni kontrolü
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                // Konum al
                getLocation();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentMapsEtkinlikAyrintiBinding.inflate(inflater, container, false);


        // Gelen verileri al
        if (getArguments() != null) {
            enlem = MapsFragmentEtkinlikAyrintiArgs.fromBundle(getArguments()).getEnlem();
            boylam = MapsFragmentEtkinlikAyrintiArgs.fromBundle(getArguments()).getBoylam();
        }

        binding.buttonYolTarifi.setOnClickListener(view -> {
            yolTarifi();
        });





        return binding.getRoot();
    }

    private void yolTarifi() {
        try {
            double hayvanLatitude = Double.parseDouble(enlem);
            double hayvanLongitude = Double.parseDouble(boylam);
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double userLatitude = location.getLatitude();
                double userLongitude = location.getLongitude();
                String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude +
                        "&daddr=" + hayvanLatitude + "," + hayvanLongitude;

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Konum bilgisine erişilemiyor.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }
    }
    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Konum izni verilmiş, işlem yap
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            } else {
                Toast.makeText(requireContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapEtkinlikPin);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        registerLauncher();
    }
    private void getLocation() {
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Konum değiştiğinde çalışacak kodlar
                }
            };
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }
}