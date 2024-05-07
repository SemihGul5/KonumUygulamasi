package com.abrebo.konumuygulamasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.ActivityAnaSayfaMainBinding;
import com.abrebo.konumuygulamasi.ui.fragments.AnaSayfaFragment;
import com.abrebo.konumuygulamasi.ui.fragments.MapsFragmentEtkinlikPaylas;

public class AnaSayfaMainActivity extends AppCompatActivity {
    private ActivityAnaSayfaMainBinding binding;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAnaSayfaMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager =getSupportFragmentManager();
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.getNavController());

        binding.fab.setOnClickListener(view -> {
            FragmentManager fragmentManager1=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager1.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView2,new MapsFragmentEtkinlikPaylas());
            fragmentTransaction.commit();
        });

    }
}