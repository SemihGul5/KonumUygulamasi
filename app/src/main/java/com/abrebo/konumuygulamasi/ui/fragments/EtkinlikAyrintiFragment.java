package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentEtkinlikAyrintiBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EtkinlikAyrintiFragment extends Fragment {
    private FragmentEtkinlikAyrintiBinding binding;
    FirebaseFirestore firestore;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentEtkinlikAyrintiBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        EtkinlikAyrintiFragmentArgs bundle=EtkinlikAyrintiFragmentArgs.fromBundle(getArguments());
        Etkinlik etkinlik=bundle.getEtkinlik();
        binding.materialToolbarEtkinlikAyrinti.setTitle(etkinlik.getAd());
        Picasso.get().load(etkinlik.getFoto()).into(binding.imageViewEtkinlikAyrintiFoto);
        binding.etkinlikAyrintiAD.setText(etkinlik.getAd());
        binding.etkinlikAyrintiTarihSaat.setText(etkinlik.getTarih()+" "+etkinlik.getSaat());
        binding.etkinlikAyrintiTur.setText(etkinlik.getTur());
        binding.etkinlikAyrintiAciklama.setText(etkinlik.getAciklama());
        
        
        // haritada göstere tıklandığında;
        binding.etkinlikAyrintiHaritadaGoster.setOnClickListener(view -> {
            gitHaritaFragment(etkinlik.getEnlem(),etkinlik.getBoylam(),view);
        });
        
        //sohbet odasına katıla tıklandığında
        binding.buttonMesaj.setOnClickListener(view -> {
            
        });




        return binding.getRoot();
    }

    private void gitHaritaFragment(String enlem, String boylam, View view) {
        EtkinlikAyrintiFragmentDirections.ActionEtkinlikAyrintiFragmentToMapsFragmentEtkinlikAyrinti gecis=
                EtkinlikAyrintiFragmentDirections.actionEtkinlikAyrintiFragmentToMapsFragmentEtkinlikAyrinti(enlem,boylam);
        Navigation.findNavController(view).navigate(gecis);
    }
}