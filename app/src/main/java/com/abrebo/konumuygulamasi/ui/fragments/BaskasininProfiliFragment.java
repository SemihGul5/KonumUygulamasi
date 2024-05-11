package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.data.models.Kullanici;
import com.abrebo.konumuygulamasi.databinding.FragmentBaskasininProfiliBinding;
import com.abrebo.konumuygulamasi.ui.adapters.EtkinlikAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class BaskasininProfiliFragment extends Fragment {
    private FragmentBaskasininProfiliBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String email;
    private String kullanici_isim;
    private Kullanici kullanici;
    private EtkinlikAdapter adapter;
    private ArrayList<Etkinlik> etkinlikList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBaskasininProfiliBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        etkinlikList=new ArrayList<>();
        BaskasininProfiliFragmentArgs bundle=BaskasininProfiliFragmentArgs.fromBundle(getArguments());
        email=bundle.getEmail();
        getEtkinlikSahibiAd(email);
        getEtkinlikSayisi(email);

        getEtkinlikler(email,firestore);

        binding.rvBaskasininProfili.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter=new EtkinlikAdapter(getContext(),etkinlikList, EtkinlikAdapter.SayfaTuru.BASKASININ_PROFILI);
        binding.rvBaskasininProfili.setAdapter(adapter);


        return binding.getRoot();
    }

    private void getEtkinlikler(String email, FirebaseFirestore firestore) {
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

    private void getEtkinlikSayisi(String email) {
        firestore.collection("etkinlikler")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int etkinlikSayisi = queryDocumentSnapshots.size();
                    binding.textViewPaylasilanEtkinlikSayisi.setText(String.valueOf(etkinlikSayisi));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Etkinlik sayısı alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void getEtkinlikSahibiAd(String email) {
        firestore.collection("kullanicilar")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                if (data != null && !data.isEmpty()) {
                                    String adSoyad = (String) data.get("adSoyad");
                                    String cinsiyet = (String) data.get("cinsiyet");
                                    String dogumTarihi = (String) data.get("dogumTarihi");
                                    String email1 = (String) data.get("email");
                                    String kisilik = (String) data.get("kisilik");
                                    String kisilik_durum = (String) data.get("kisilik_durum");
                                    kullanici_isim = (String) data.get("kullaniciAdi");
                                    String foto = (String) data.get("foto");
                                    kullanici=new Kullanici(adSoyad,kullanici_isim,email1,dogumTarihi,cinsiyet,kisilik,kisilik_durum,foto);
                                    binding.materialToolbar3.setTitle(kullanici.getKullaniciAdi());
                                    binding.textViewProfiliAd.setText(kullanici.getAdSoyad());
                                    Picasso.get().load(kullanici.getFoto())
                                            .into(binding.imageView5);
                                    String tanim="";
                                    if (kisilik.equals("null")){
                                        tanim="Henüz kişilik testi yapılmamış";
                                    }else{
                                        tanim="Kişilik: "+kisilik;
                                    }
                                    binding.textViewKisilikBaskasininProfili.setText(tanim);

                                }
                            }
                        }
                    }
                });
    }

}