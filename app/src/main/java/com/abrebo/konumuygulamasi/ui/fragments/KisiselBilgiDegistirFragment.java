package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentKisiselBilgiDegistirBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class KisiselBilgiDegistirFragment extends Fragment {
    private FragmentKisiselBilgiDegistirBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentKisiselBilgiDegistirBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        binding.textProfilEmail.setEnabled(false);
        getData();
        binding.buttonProfilGuncelle.setOnClickListener(view -> {
            guncelle(view);
        });

        binding.textViewSifreDegis.setOnClickListener(view -> {
            sifreDegis(view);
        });

        return binding.getRoot();
    }
    private void sifreDegis(View view) {
        Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_sifreDegisFragment);
    }
    private Boolean bosMu(String ad,String email){
        Boolean kontrol;
        if(ad.equals("")&&email.equals("")){
            kontrol=true;
        }else{
            kontrol=false;
        }
        return kontrol;
    }
    private void guncelle(View view) {
        String yeniAd = binding.textProfilAd.getText().toString();
        String email = auth.getCurrentUser().getEmail();
        if(!bosMu(yeniAd,email)){

            bilgileriKaydet(yeniAd,email);

        }
        else{
            Snackbar.make(view,"Tüm alanları doldurun.",Snackbar.LENGTH_LONG).show();
        }
    }

    private void bilgileriKaydet(String yeniAd,String email) {
        Map<String, Object> guncellenmisBilgiler = new HashMap<>();
        guncellenmisBilgiler.put("adSoyad", yeniAd);


        try {
            // Belgeyi bul ve güncelle
            firestore.collection("kullanicilar").whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Kullanıcının belgesini bulduğunuzda
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Belgeyi güncelle
                            firestore.collection("kullanicilar").document(documentSnapshot.getId())
                                    .update(guncellenmisBilgiler)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Bilgiler güncellendi", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Bilgiler güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Belge bulunamadığında veya bir hata oluştuğunda
                        Toast.makeText(getContext(), "Belge bulunamadı veya bir hata oluştu", Toast.LENGTH_SHORT).show();
                    });
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }
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
                                binding.textProfilAd.setText(ad);
                                binding.textProfilEmail.setText(email);

                            }
                        }
                    }
                });
    }
}