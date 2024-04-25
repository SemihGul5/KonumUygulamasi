package com.abrebo.konumuygulamasi.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentProfilimBinding;

public class ProfilimFragment extends Fragment {
    private FragmentProfilimBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfilimBinding.inflate(inflater, container, false);




        return binding.getRoot();
    }
}