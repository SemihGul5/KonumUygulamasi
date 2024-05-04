package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

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

        getData();
        return binding.getRoot();
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
                                String alici_email = document.getString("alici_email");

                                if (gonderenEmail.equals(currentUserEmail)|| alici_email.equals(currentUserEmail)) {
                                    uniqueEtkinlikID.add(gonderenEmail.equals(currentUserEmail) ? alici_email : gonderenEmail);
                                }
                            }

                            for (String docID : uniqueEtkinlikID) {
                                firestore.collection("etkinlikler")
                                        .whereEqualTo("docID", docID)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                // Firestore'dan dönen belgeleri işle
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
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
                                                    String docID = (String) data.get("docID");

                                                    Etkinlik etkinlik=new Etkinlik(foto,foto2,foto3,foto4,ad,tur,konum,aciklama,enlem,boylam,email,paylasildi_mi,
                                                            tarih,saat,docID);
                                                    etkinlikLists.add(etkinlik);
                                                }
                                                adapter.notifyDataSetChanged();
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