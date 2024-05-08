package com.abrebo.konumuygulamasi.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentAnaSayfaBinding;
import com.abrebo.konumuygulamasi.ui.adapters.EtkinlikAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AnaSayfaFragment extends Fragment{
    private FragmentAnaSayfaBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ArrayList<Etkinlik> etkinlikList;
    private EtkinlikAdapter adapter;
    private String kullaniciSehir="";
    private Boolean oneri;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAnaSayfaBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etkinlikList=new ArrayList<>();
        getKullaniciOneriDurum();


        binding.recyclerViewEtkinliklerAnaSayfa.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter=new EtkinlikAdapter(getContext(),etkinlikList, EtkinlikAdapter.SayfaTuru.ANA_SAYFA);
        binding.recyclerViewEtkinliklerAnaSayfa.setAdapter(adapter);
        geriTusuIslemleri();
        binding.buttonHaritadaGoster.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_mapsFragmentEtkinlikPin);
        });






        return binding.getRoot();
    }



    private void getKullaniciOneriDurum() {
        db.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Boolean oneriDurumu = document.getBoolean("oneri_durum");
                            kullaniciSehir=document.getString("konum");
                            if (oneriDurumu != null && oneriDurumu) {
                                oneri = true;
                            } else {
                                oneri = false;
                            }
                            // Öneri durumu alındıktan sonra gerekli işlemleri yapmak için
                            if (oneri) {
                                getData(true);
                            } else {
                                getData(false);
                            }
                        }
                    }
                });
    }
    private void getData(Boolean kisilikVarMi) {
        if (kisilikVarMi){
            getKullaniciKisilik(db, auth, new AnaSayfaFragment.KisilikCallback() {
                @Override
                public void onKisilikReceived(String kisilikValue) {
                        db.collection("etkinlikler")
                                .whereEqualTo("kisilik",kisilikValue)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        etkinlikList.clear();

                                        if (value != null) {
                                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                Map<String, Object> data = documentSnapshot.getData();
                                                String foto=(String) data.get("foto");
                                                String foto2=(String) data.get("foto2");
                                                String foto3=(String) data.get("foto3");
                                                String foto4=(String) data.get("foto4");
                                                String ad = (String) data.get("ad");
                                                String tur = (String) data.get("tur");
                                                String konum = (String) data.get("konum");
                                                String aciklama = (String) data.get("aciklama");
                                                String enlem = (String) data.get("enlem");
                                                String boylam = (String) data.get("boylam");
                                                String email = (String) data.get("email");
                                                String paylasildi_mi = (String) data.get("paylasildi_mi");
                                                String tarih = (String) data.get("tarih");
                                                String saat = (String) data.get("saat");
                                                String docID=documentSnapshot.getId();
                                                Etkinlik etkinlik=new Etkinlik(foto,foto2,foto3,foto4,ad,tur,konum,aciklama,enlem,boylam,email,paylasildi_mi,
                                                        tarih,saat,docID);
                                                etkinlikList.add(etkinlik);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }

            });
        }else{
            getKullaniciKisilik(db, auth, new AnaSayfaFragment.KisilikCallback() {
                @Override
                public void onKisilikReceived(String kisilikValue) {
                        db.collection("etkinlikler")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        etkinlikList.clear();

                                        if (value != null) {
                                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                Map<String, Object> data = documentSnapshot.getData();
                                                String foto=(String) data.get("foto");
                                                String foto2=(String) data.get("foto2");
                                                String foto3=(String) data.get("foto3");
                                                String foto4=(String) data.get("foto4");
                                                String ad = (String) data.get("ad");
                                                String tur = (String) data.get("tur");
                                                String konum = (String) data.get("konum");
                                                String aciklama = (String) data.get("aciklama");
                                                String enlem = (String) data.get("enlem");
                                                String boylam = (String) data.get("boylam");
                                                String email = (String) data.get("email");
                                                String paylasildi_mi = (String) data.get("paylasildi_mi");
                                                String tarih = (String) data.get("tarih");
                                                String saat = (String) data.get("saat");
                                                String docID=documentSnapshot.getId();
                                                Etkinlik etkinlik=new Etkinlik(foto,foto2,foto3,foto4,ad,tur,konum,aciklama,enlem,boylam,email,paylasildi_mi,
                                                        tarih,saat,docID);
                                                etkinlikList.add(etkinlik);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
            });
        }

    }
    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, AnaSayfaFragment.KisilikCallback callback) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email", currentUserEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                String kisilik = (String) data.get("kisilik");
                                callback.onKisilikReceived(kisilik);
                            }
                        }
                    }
                });
    }
    public interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
    }
    private void geriTusuIslemleri() {
        OnBackPressedCallback backButtonCallback = new OnBackPressedCallback(true) {
            private long backPressedTime = 0;

            @Override
            public void handleOnBackPressed() {
                long currentTime = System.currentTimeMillis();
                if (backPressedTime + 2000 > currentTime) {
                    requireActivity().finishAffinity();
                } else {
                    Toast.makeText(getContext(), "Çıkmak için tekrar basın", Toast.LENGTH_SHORT).show();
                }
                backPressedTime = currentTime;
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backButtonCallback);
    }
}