package com.abrebo.konumuygulamasi.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentMesajBinding;

public class MesajFragment extends Fragment {
    private FragmentMesajBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}