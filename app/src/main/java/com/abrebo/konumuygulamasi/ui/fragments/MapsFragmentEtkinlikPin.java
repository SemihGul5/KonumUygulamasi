package com.abrebo.konumuygulamasi.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentMapsEtkinlikPinBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentEtkinlikPin extends Fragment {
    private FragmentMapsEtkinlikPinBinding binding;
    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<Etkinlik>etkinliks;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            getLastKnownLocation();
            showCurrentLocation();
            getAllEtkinlikLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentMapsEtkinlikPinBinding.inflate(inflater, container, false);
        etkinliks=new ArrayList<>();




        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapEtkinlikPin);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        firestore = FirebaseFirestore.getInstance();
    }
    private void getLastKnownLocation() {
        // Son bilinen konumu almak için işlem yap
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa işlem yapma
            return;
        }

        // Son bilinen konumu al
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Son bilinen konumu al ve haritayı bu konumla başlat
                            LatLng lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 15));
                        }
                    }
                });
    }
    private void showCurrentLocation() {
        // Kullanıcının mevcut konumunu al
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa işlem yapma
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    private void getAllEtkinlikLocation() {
        firestore.collection("etkinlikler")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            
                            String foto = document.getString("foto");
                            String foto2 = document.getString("foto2");
                            String foto3 = document.getString("foto3");
                            String foto4 = document.getString("foto4");
                            String ad = document.getString("ad");
                            String tur = document.getString("tur");
                            String konum = document.getString("konum");
                            String aciklama = document.getString("aciklama");
                            String enlem = document.getString("enlem");
                            String boylam = document.getString("boylam");
                            String email = document.getString("email");
                            String paylasildi_mi = document.getString("paylasildi_mi");
                            String tarih = document.getString("tarih");
                            String saat = document.getString("saat");
                            String docid=document.getId();
                            
                            Etkinlik etkinlik=new Etkinlik(foto,foto2,foto3,foto4,ad,tur,konum,aciklama,enlem,
                                    boylam,email,paylasildi_mi,tarih,saat,docid);
                            etkinliks.add(etkinlik);

                            // konumunu al
                            double latitude = Double.parseDouble(document.getString("enlem"));
                            double longitude = Double.parseDouble(document.getString("boylam"));

                            // Marker oluştur ve haritaya ekle
                            LatLng animalLocation = new LatLng(latitude, longitude);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(animalLocation));

                            // Marker'a tıklanınca hangi hayvana tıklanıldığını belirlemek için dinleyici ekle
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    // Marker'a tıklandığında ilgili hayvanın bilgilerini göster
                                    /*Hayvan clickedHayvan = getHayvanFromMarker(marker);
                                    if (clickedHayvan != null) {
                                        // İlgili hayvanın bilgilerini gösteren bottom sheet dialog oluştur
                                        showAnimalDetailsDialog(clickedHayvan);
                                    }*/
                                    Toast.makeText(getContext(), "Tıklandı", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });
                        }
                    }
                });
    }
}