package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentFavorilerimBinding;
import com.abrebo.konumuygulamasi.ui.adapters.EtkinlikAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FavorilerimFragment extends Fragment {
    private FragmentFavorilerimBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String email;
    private ArrayList<String> docIDListesi;
    private ArrayList<Etkinlik> etkinlikList;
    private EtkinlikAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentFavorilerimBinding.inflate(inflater, container, false);
        binding.materialToolbarFavorilerim.setTitle("Favorilerim");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        docIDListesi = new ArrayList<>();
        etkinlikList=new ArrayList<>();
        getData(email,firestore);
        binding.rvFavorilerim.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter=new EtkinlikAdapter(getContext(),etkinlikList);
        binding.rvFavorilerim.setAdapter(adapter);




        return binding.getRoot();
    }

    private void getData(String email, FirebaseFirestore firestore) {
        // Favoriler koleksiyonundan belirli bir kullanıcının favori etkinliklerini getir
        firestore.collection("favoriler")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Etkinlik DocID'lerini al ve ArrayList'e ekle
                            String etkinlikDocID = document.getString("docID");
                            docIDListesi.add(etkinlikDocID);
                        }

                        // Elde edilen DocID listesiyle başka bir işlem yapmak için bir fonksiyonu çağırın
                        aramaYap(docIDListesi);
                    } else {
                        // Veri alınamadığında yapılacak işlemler
                        Toast.makeText(getContext(), "Favori etkinlikler alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void aramaYap(ArrayList<String> docIDListesi) {
        for (String docID : docIDListesi) {
            firestore.collection("etkinlikler")
                    .document(docID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = document.getData();
                                if (data != null) {
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
                                    String email = (String) data.get("email");
                                    String paylasildi_mi = (String) data.get("paylasildi_mi");
                                    String tarih = (String) data.get("tarih");
                                    String saat = (String) data.get("saat");
                                    String docID2 = document.getId();

                                    // Yeni etkinlik nesnesi oluştur ve listeye ekle
                                    Etkinlik etkinlik = new Etkinlik(foto,foto2,foto3,foto4, ad, tur, konum, aciklama, enlem, boylam, email, paylasildi_mi,
                                            tarih, saat, docID2);
                                    etkinlikList.add(etkinlik);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                // Belge bulunamadığında yapılacak işlemler
                                Toast.makeText(getContext(), "Etkinlik bulunamadı", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Arama yapılırken hata oluştuğunda yapılacak işlemler
                            Toast.makeText(getContext(), "Etkinlik aranırken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



}