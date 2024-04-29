package com.abrebo.konumuygulamasi.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class MapsFragmentEtkinlikPaylas extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private double latitude, longitude;
    private boolean pinAdded = false;
    String enlem="",boylam="",sehir="",ilce="";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_etkinlik_paylas, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Pin ekle butonuna tıklama işlemi
        Button addButton = view.findViewById(R.id.buttonKonumKaydet);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinAdded) {
                    getCityNameWithLocation(latitude,longitude);
                    MapsFragmentEtkinlikPaylasDirections.ActionMapsFragmentEtkinlikPaylasToEtkinlikPaylasFragment gecis=
                            MapsFragmentEtkinlikPaylasDirections.actionMapsFragmentEtkinlikPaylasToEtkinlikPaylasFragment(enlem,boylam,sehir,ilce);
                    Navigation.findNavController(view).navigate(gecis);
                } else {
                    Toast.makeText(getContext(), "Etkinliğin konumunu uzun basarak kaydetmelisiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!pinAdded) {
                    addPin(latLng); // Kullanıcının uzun basmasıyla pin eklemek için metodu çağır
                } else {
                    googleMap.clear(); // Önceki pini kaldır
                    addPin(latLng); // Yeni pin ekle
                }
            }
        });
    }

    // Pin eklemek için metod
    private void addPin(LatLng latLng) {
        if (markerOptions != null) {
            googleMap.clear(); // Önceki pini kaldır
        }

        // Yeni pini ekle
        markerOptions = new MarkerOptions().position(latLng);
        googleMap.addMarker(markerOptions);

        // Enlem ve boylamı kaydet
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        enlem= String.valueOf(latitude);
        boylam=String.valueOf(longitude);
        // Pin eklenme durumunu güncelle
        pinAdded = true;

        // Toast mesajı göster
        Toast.makeText(getContext(), "Pin eklendi!", Toast.LENGTH_SHORT).show();
    }

    private void getCityNameWithLocation(double lat, double lng) {
        try {

            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {

            } else {
                if (addresses.size() > 0) {
                    Log.i("cityname",addresses.get(0).getAdminArea());
                    sehir=addresses.get(0).getAdminArea();
                    ilce=addresses.get(0).getSubLocality();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }
}
