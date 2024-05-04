package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentKayitOlBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;

public class KayitOlFragment extends Fragment {
    private FragmentKayitOlBinding binding;
    private String adSoyad,email,sifre,sifreTekrar,kullaniciAdi,cinsiyet,dogumTarihi,yas;
    Calendar cal;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentKayitOlBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        cal = Calendar.getInstance();

        binding.girisYapGitTextButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_kayitOlFragment_to_girisYapFragment);
        });




        return binding.getRoot();
    }


}