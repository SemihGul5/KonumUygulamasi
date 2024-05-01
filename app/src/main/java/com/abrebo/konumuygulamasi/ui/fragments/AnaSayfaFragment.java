package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AnaSayfaFragment extends Fragment {
    private FragmentAnaSayfaBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    ArrayList<Etkinlik> etkinlikList;
    EtkinlikAdapter adapter;
    String kullaniciSehir="";
    Boolean oneri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAnaSayfaBinding.inflate(inflater, container, false);
        binding.materialToolbar.setTitle("Ana Sayfa");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        etkinlikList=new ArrayList<>();
        getKullaniciOneriDurum();
        binding.recyclerViewEtkinliklerAnaSayfa.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter=new EtkinlikAdapter(getContext(),etkinlikList);
        binding.recyclerViewEtkinliklerAnaSayfa.setAdapter(adapter);





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
                                getData(kullaniciSehir,true);
                            } else {
                                getData(kullaniciSehir,false);
                            }
                        }
                    }
                });
    }
    private void getData(@Nullable String sehir,Boolean kisilikVarMi) {
        if (kisilikVarMi){
            getKullaniciKisilik(db, auth, new AnaSayfaFragment.KisilikCallback() {
                @Override
                public void onKisilikReceived(String kisilikValue) {
                        db.collection("etkinlikler")
                                .whereEqualTo("konum",sehir)
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
                                                Etkinlik etkinlik=new Etkinlik(foto,ad,tur,konum,aciklama,enlem,boylam,email,paylasildi_mi,
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
                                .whereEqualTo("konum",sehir)
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
                                                Etkinlik etkinlik=new Etkinlik(foto,ad,tur,konum,aciklama,enlem,boylam,email,paylasildi_mi,
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
}