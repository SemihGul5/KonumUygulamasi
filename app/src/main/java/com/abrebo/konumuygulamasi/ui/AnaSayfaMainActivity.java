package com.abrebo.konumuygulamasi.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.ActivityAnaSayfaMainBinding;

public class AnaSayfaMainActivity extends AppCompatActivity {
    private ActivityAnaSayfaMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnaSayfaMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}