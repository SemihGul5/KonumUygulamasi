package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentBenimEtkinliklerimBinding;
import com.abrebo.konumuygulamasi.ui.adapters.EtkinlikAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BenimEtkinliklerimFragment extends Fragment {
    private FragmentBenimEtkinliklerimBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String email;
    private ArrayList<Etkinlik> etkinlikList;
    private EtkinlikAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBenimEtkinliklerimBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        etkinlikList=new ArrayList<>();
        getData(email,firestore);

        binding.rvBenimEtkinliklerim.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter=new EtkinlikAdapter(getContext(),etkinlikList, EtkinlikAdapter.SayfaTuru.ETKINLIKLERIM_SAYFASI);
        binding.rvBenimEtkinliklerim.setAdapter(adapter);



        return binding.getRoot();
    }

    private void getData(String email, FirebaseFirestore firestore) {
        firestore.collection("etkinlikler")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            if (data != null&&!data.isEmpty()) {
                                String foto = (String) data.get("foto");
                                String foto2=(String) data.get("foto2");
                                String foto3=(String) data.get("foto3");
                                String foto4=(String) data.get("foto4");
                                String ad = (String) data.get("ad");
                                String tur = (String) data.get("tur");
                                String konum = (String) data.get("konum");
                                String aciklama = (String) data.get("aciklama");
                                String enlem = (String) data.get("enlem");
                                String boylam = (String) data.get("boylam");
                                String email2 = (String) data.get("email");
                                String paylasildi_mi = (String) data.get("paylasildi_mi");
                                String tarih = (String) data.get("tarih");
                                String saat = (String) data.get("saat");
                                String uuid = (String) data.get("uuid");
                                String docID2 = document.getId();

                                // Yeni etkinlik nesnesi oluştur ve listeye ekle
                                Etkinlik etkinlik = new Etkinlik(foto,foto2,foto3,foto4, ad, tur, konum, aciklama, enlem, boylam, email2, paylasildi_mi,
                                        tarih, saat, docID2,uuid);
                                etkinlikList.add(etkinlik);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Etkinlikler alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}