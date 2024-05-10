package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentProfilimBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ProfilimFragment extends Fragment {
    private FragmentProfilimBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfilimBinding.inflate(inflater, container, false);
        geriTusuIslemleri();
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        getData();

        binding.linearLayoutAyarlar.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_ayarlarFragment);
        });
        binding.linearLayoutMesajlar.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_benimEtkinliklerimFragment);
        });
        binding.linearLayoutKisilikTesti.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_bigFiveFragment);
        });
        binding.textViewGitProfilimFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_kisiselBilgiDegistirFragment);
        });
        binding.textViewGitSifreDegistir.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_sifreDegisFragment);
        });
        return binding.getRoot();
    }
    private void geriTusuIslemleri() {
        OnBackPressedCallback backButtonCallback = new OnBackPressedCallback(true) {
            private long backPressedTime = 0;

            @Override
            public void handleOnBackPressed() {
                long currentTime = System.currentTimeMillis();
                if (backPressedTime + 2000 > currentTime) {
                    requireActivity().finishAffinity();
                } else {
                    Toast.makeText(getContext(), "Çıkmak için tekrar basın", Toast.LENGTH_SHORT).show();
                }
                backPressedTime = currentTime;
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backButtonCallback);
    }
    private void getData() {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email",email)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                String ad = (String) data.get("adSoyad");
                                String yas = (String) data.get("yas");
                                String kisilik = (String) data.get("kisilik");

                                String isim=ad;
                                binding.textViewKullaniciIsmi.setText(isim);
                                binding.textViewKullaniciEmail.setText(email);
                                binding.textViewTelBilgi.setText(yas);
                                binding.textViewKisilikBilgi.setText(kisilik);
                            }
                        }
                    }
                });
    }
}