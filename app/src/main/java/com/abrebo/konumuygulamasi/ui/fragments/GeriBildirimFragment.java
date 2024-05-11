package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentGeriBildirimBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GeriBildirimFragment extends Fragment {
    private FragmentGeriBildirimBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGeriBildirimBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        binding.buttonBildiriGonderBizeUlas.setOnClickListener(view -> {
            String baslik=binding.bildiriBaslikBizeUlas.getText().toString();
            String icerik=binding.bildiriIcerikBizeUlas.getText().toString();
            if (baslik.equals("")&&icerik.equals("")){
                Snackbar.make(view,"Başlık ve içerik doldurulmalıdır",Snackbar.LENGTH_SHORT).show();
            }else{
                firestoreKaydet(baslik,icerik,auth.getCurrentUser().getEmail());
            }

        });




        return binding.getRoot();
    }

    private void firestoreKaydet(String baslik, String icerik, String email) {
        CollectionReference bildirilerCollectionRef = firestore.collection("geri_bildirim");
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("baslik",baslik);
        bildiriVerileri.put("icerik",icerik);
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Bildiri başarıyla gönderildi", Toast.LENGTH_SHORT).show();
                        binding.bildiriBaslikBizeUlas.setText("");
                        binding.bildiriIcerikBizeUlas.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Bildiri eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}