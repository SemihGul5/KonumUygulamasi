package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentAyarlarBinding;
import com.abrebo.konumuygulamasi.ui.adapters.AyarlarAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AyarlarFragment extends Fragment {
    private FragmentAyarlarBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ArrayList<String> ayarlarList;
    private AyarlarAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAyarlarBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        ayarlarList=new ArrayList<>();
        ayarlarList.add("Profilim");
        ayarlarList.add("Kişilikler Ne Anlama Geliyor?");
        ayarlarList.add("Uygulamayı Paylaş");
        ayarlarList.add("Bize Ulaşın");
        ayarlarList.add("Çıkış Yap");

        adapter = new AyarlarAdapter(getContext(), ayarlarList,firestore,auth);
        binding.ayarlarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ayarlarRecyclerView.setAdapter(adapter);


        return binding.getRoot();
    }
}