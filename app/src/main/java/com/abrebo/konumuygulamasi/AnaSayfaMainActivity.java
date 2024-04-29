package com.abrebo.konumuygulamasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.ActivityAnaSayfaMainBinding;

public class AnaSayfaMainActivity extends AppCompatActivity {
    private ActivityAnaSayfaMainBinding binding;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnaSayfaMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager =getSupportFragmentManager();


        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        //NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.getNavController());
    }
}