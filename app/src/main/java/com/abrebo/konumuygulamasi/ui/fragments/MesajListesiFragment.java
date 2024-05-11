package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentMesajListesiBinding;
import com.abrebo.konumuygulamasi.ui.adapters.MesajListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MesajListesiFragment extends Fragment {
    private FragmentMesajListesiBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    MesajListAdapter adapter;
    ArrayList<Etkinlik> etkinlikLists;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajListesiBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        etkinlikLists=new ArrayList<>();
        adapter=new MesajListAdapter(getContext(),etkinlikLists,firestore);
        binding.rvMesajListesi.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMesajListesi.setAdapter(adapter);
        geriTusuIslemleri();
        getData();



        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        FloatingActionButton fab=requireActivity().findViewById(R.id.fab);
        bottomAppBar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
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
    private void getData() {
        String currentUserEmail = auth.getCurrentUser().getEmail();

        firestore.collection("mesaj")
                .orderBy("tarih", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Bir şeyler ters gitti.", Toast.LENGTH_SHORT).show();
                            Log.e("Mesaj", error.getMessage());
                            return;
                        }

                        if (value != null) {
                            // Veritabanından gelen mesajları işle
                            Set<String> uniqueEtkinlikID = new HashSet<>();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                String gonderenEmail = document.getString("gonderen_email");
                                String aliciEmail = document.getString("alici_email");
                                String etkinlikID = document.getString("alici_etkinlik_id");

                                if (gonderenEmail.equals(currentUserEmail) || aliciEmail.equals(currentUserEmail)) {
                                    uniqueEtkinlikID.add(etkinlikID);
                                }
                            }

                            for (String etkinlikID : uniqueEtkinlikID) {
                                firestore.collection("etkinlikler")
                                        .document(etkinlikID)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot etkinlikSnapshot) {
                                                if (etkinlikSnapshot.exists()) {
                                                    // Etkinlik varsa işle
                                                    String foto = etkinlikSnapshot.getString("foto");
                                                    String foto2 = etkinlikSnapshot.getString("foto2");
                                                    String foto3 = etkinlikSnapshot.getString("foto3");
                                                    String foto4 = etkinlikSnapshot.getString("foto4");
                                                    String ad = etkinlikSnapshot.getString("ad");
                                                    String tur = etkinlikSnapshot.getString("tur");
                                                    String konum = etkinlikSnapshot.getString("konum");
                                                    String aciklama = etkinlikSnapshot.getString("aciklama");
                                                    String enlem = etkinlikSnapshot.getString("enlem");
                                                    String boylam = etkinlikSnapshot.getString("boylam");
                                                    String email = etkinlikSnapshot.getString("email");
                                                    String paylasildi_mi = etkinlikSnapshot.getString("paylasildi_mi");
                                                    String tarih = etkinlikSnapshot.getString("tarih");
                                                    String saat = etkinlikSnapshot.getString("saat");
                                                    String uuid = etkinlikSnapshot.getString("uuid");
                                                    String docID = etkinlikSnapshot.getId();

                                                    Etkinlik etkinlik = new Etkinlik(foto, foto2, foto3, foto4, ad, tur, konum, aciklama, enlem, boylam, email, paylasildi_mi,
                                                            tarih, saat, docID,uuid);
                                                    etkinlikLists.add(etkinlik);
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    // Etkinlik yoksa işle
                                                    Log.d("Etkinlik", "Belirtilen ID ile eşleşen etkinlik bulunamadı.");
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Hata işleme
                                                Toast.makeText(getContext(), "Bir şeyler ters gitti.", Toast.LENGTH_SHORT).show();
                                                Log.e("Mesaj", e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                });
    }

}