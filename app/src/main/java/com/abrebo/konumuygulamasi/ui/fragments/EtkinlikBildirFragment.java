package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentEtkinlikBildirBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EtkinlikBildirFragment extends Fragment {
    private FragmentEtkinlikBildirBinding binding;
    private FirebaseFirestore firestore;
    String email,docID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentEtkinlikBildirBinding.inflate(inflater, container, false);
        EtkinlikBildirFragmentArgs bundle=EtkinlikBildirFragmentArgs.fromBundle(getArguments());
        email=bundle.getEmail();
        docID=bundle.getDocID();
        firestore=FirebaseFirestore.getInstance();


        binding.buttonBildiriGonder.setOnClickListener(view -> {
            if (binding.bildiriBaslik.getText().toString().isEmpty()
                    ||binding.bildiriIcerik.getText().toString().isEmpty())
            {
                Snackbar.make(view,"Tüm alanlar dolu olmalıdır.",Snackbar.LENGTH_SHORT).show();
            }else{
                firestoreBildiriGonder(view,email,docID,firestore);
            }
        });




        return binding.getRoot();
    }

    private void firestoreBildiriGonder(View view, String email, String docID, FirebaseFirestore firestore) {
        CollectionReference bildirilerCollectionRef = firestore.collection("bildiriler");
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Bildiri başarıyla eklendi", Toast.LENGTH_SHORT).show();
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