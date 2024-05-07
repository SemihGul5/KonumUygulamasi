package com.abrebo.konumuygulamasi.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.AnaSayfaMainActivity;
import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentGirisYapBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class GirisYapFragment extends Fragment {
    private FragmentGirisYapBinding binding;
    FirebaseAuth auth;
    String eMail,sifre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGirisYapBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();

        geriTusuAyarlama();

        //kayıt ol sayfasına gidiş
        binding.kayitOlGitTextButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_girisYapFragment_to_kayitOlFragment);
        });

        FirebaseUser user=auth.getCurrentUser();
        if (user!=null&&user.isEmailVerified()){
            //daha önceden oturum açılmış, ana sayfaya git
            Intent intent= new Intent(getContext(), AnaSayfaMainActivity.class);
            startActivity(intent);
        }

        binding.girisYapButton.setOnClickListener(view -> {
            if (binding.userEmailText.equals("")||binding.userPasswordText.equals("")){
                Toast.makeText(getContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            }
            else{
                binding.progressBar.setVisibility(View.VISIBLE);
                eMail=binding.userEmailText.getText().toString();
                sifre=binding.userPasswordText.getText().toString();
                auth.signInWithEmailAndPassword(eMail,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()){
                            //ana sayfaya git
                            Toast.makeText(getContext(), "Giriş Yapılıyor...", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_girisYapFragment_to_anaSayfaMainActivity);

                            binding.progressBar.setVisibility(View.GONE);

                        }
                        else{
                            Snackbar.make(view,"E-mail adresinizi doğrulayın!",Snackbar.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.VISIBLE);

                        Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);

                    }
                });
            }
        });


        binding.textViewSifreyiUnuttum.setOnClickListener(view -> {
            eMail=binding.userEmailText.getText().toString();
            if (eMail.equals("")){
                Snackbar.make(view,"E-mail alanı dolu olmalıdır!",Snackbar.LENGTH_SHORT).show();
            }else{
                auth.sendPasswordResetEmail(eMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(view,"Şifre sıfırlama e-mail'i gönderildi.",Snackbar.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });



        return binding.getRoot();
    }

    private void geriTusuAyarlama() {
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
}