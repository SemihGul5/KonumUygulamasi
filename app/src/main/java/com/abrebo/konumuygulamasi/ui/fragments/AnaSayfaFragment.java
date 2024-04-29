package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentAnaSayfaBinding;

public class AnaSayfaFragment extends Fragment {
    private FragmentAnaSayfaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAnaSayfaBinding.inflate(inflater, container, false);





        return binding.getRoot();
    }
}