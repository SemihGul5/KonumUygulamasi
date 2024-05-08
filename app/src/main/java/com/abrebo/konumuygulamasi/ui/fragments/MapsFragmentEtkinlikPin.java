package com.abrebo.konumuygulamasi.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragmentEtkinlikPin extends Fragment {
    private FragmentMapsEtkinlikPinBinding binding;
    private GoogleMap mMap;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<Etkinlik>etkinliks;
    private Boolean favoriMi;

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
        auth=FirebaseAuth.getInstance();



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
                                    Etkinlik clickedEtkinlik = getEtkinlikFromMarker(marker);
                                    if (clickedEtkinlik != null) {
                                        // İlgili hayvanın bilgilerini gösteren bottom sheet dialog oluştur
                                        showEtkinlikDetailsDialog(clickedEtkinlik);
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                });
    }
    private void showEtkinlikDetailsDialog(Etkinlik etkinlik) {

        // Bottom sheet dialog oluştur
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme);
        // Layout dosyasını yükle
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pin,null);
        bottomSheetDialog.setContentView(view);

        // Özellikleri göster;
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageViewFav = view.findViewById(R.id.imageViewFavorilereEkle_bottom);
        firestore.collection("favoriler")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .whereEqualTo("docID", etkinlik.getDocID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Favoride var
                            favoriMi=true;
                            imageViewFav.setImageResource(R.drawable.baseline_bookmark_added_24);
                        } else {
                            favoriMi=false;
                            imageViewFav.setImageResource(R.drawable.baseline_bookmark_add_24);
                        }
                    } else {
                        // Firestore sorgusu başarısız oldu
                        Toast.makeText(requireContext(), "Favorileri kontrol ederken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageView = view.findViewById(R.id.imageViewEtkinlikAyrintiFoto_bottom);
        Picasso.get().load(etkinlik.getFoto()).resize(150,150)
                .into(imageView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewAd = view.findViewById(R.id.etkinlik_ayrinti_AD_bottom);
        textViewAd.setText(etkinlik.getAd());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewTarihVeSaat = view.findViewById(R.id.etkinlik_ayrinti_tarih_saat_bottom);
        textViewTarihVeSaat.setText(etkinlik.getTarih()+" - "+etkinlik.getSaat());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewKonum = view.findViewById(R.id.etkinlik_ayrinti_konum_bottom);
        textViewKonum.setText(etkinlik.getKonum());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewTur = view.findViewById(R.id.etkinlik_ayrinti_tur_bottom);
        textViewTur.setText(etkinlik.getTur());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewAciklama = view.findViewById(R.id.etkinlik_ayrinti_aciklama_bottom);
        textViewAciklama.setText(etkinlik.getAciklama());

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonMesajGonder = view.findViewById(R.id.buttonMesaj_bottom);
        buttonMesajGonder.setOnClickListener(view1 -> {
            bottomSheetDialog.cancel();
            ilgiliKisiyeMesajListesiAc(etkinlik,view1);
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView buttonFavEkle = view.findViewById(R.id.imageViewFavorilereEkle_bottom);
        buttonFavEkle.setOnClickListener(view1 -> {
            if (favoriMi){
                etkinligiFavorilerdenSil(etkinlik.getDocID(), firestore,imageViewFav);
            }else{
                etkinligiFavorilereEkle(etkinlik.getDocID(), auth.getCurrentUser().getEmail(), firestore,imageViewFav);
            }

        });
        // Dialogu göster
        bottomSheetDialog.show();
    }
    private void etkinligiFavorilerdenSil(String docID, FirebaseFirestore firestore, ImageView imageViewFav) {
        firestore.collection("favoriler")
                .whereEqualTo("docID", docID)
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Favoriler koleksiyonunda belirli bir etkinliğin kaydını bulduk
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        // Belirli bir kaydı sil
                        firestore.collection("favoriler")
                                .document(snapshot.getId()) // Silinecek belgenin kimliğini al
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                                    imageViewFav.setImageResource(R.drawable.baseline_bookmark_add_24);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Favorileri kontrol ederken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void etkinligiFavorilereEkle(String docID, String email, FirebaseFirestore firestore, ImageView imageViewFav) {
        CollectionReference bildirilerCollectionRef = firestore.collection("favoriler");
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                        imageViewFav.setImageResource(R.drawable.baseline_bookmark_added_24);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Favorilere eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void ilgiliKisiyeMesajListesiAc(Etkinlik etkinlik, View view1) {
        try {
            NavController navController = Navigation.findNavController(requireActivity(),R.id.mapEtkinlikPin);

            MapsFragmentEtkinlikPinDirections.ActionMapsFragmentEtkinlikPinToMesajFragment gecis=
                    MapsFragmentEtkinlikPinDirections.actionMapsFragmentEtkinlikPinToMesajFragment(etkinlik);
            navController.navigate(gecis);
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }

    }

    private Etkinlik getEtkinlikFromMarker(Marker marker) {
        LatLng markerPosition = marker.getPosition();
        for (Etkinlik etkinlik : etkinliks) {
            double etkinlikLatitude = Double.parseDouble(etkinlik.getEnlem());
            double etkinlikLongitude = Double.parseDouble(etkinlik.getBoylam());
            LatLng etkinlikLocation = new LatLng(etkinlikLatitude, etkinlikLongitude);
            if (etkinlikLocation.equals(markerPosition)) {
                return etkinlik;
            }
        }
        return null;
    }

}