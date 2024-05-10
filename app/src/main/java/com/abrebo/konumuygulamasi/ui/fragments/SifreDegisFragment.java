package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentSifreDegisBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SifreDegisFragment extends Fragment {
    private FragmentSifreDegisBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSifreDegisBinding.inflate(inflater, container, false);

        binding.buttonSifreDegis.setOnClickListener(view -> {
            sifreDegis(view);
        });


        return binding.getRoot();
    }
    private void sifreDegis(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mevcut=binding.textSifreDegisMevcut.getText().toString();
        String yeni=binding.textSifreDegisYeni.getText().toString();
        String yeniTekrar=binding.textSifreDegisYeniTekrar.getText().toString();
        if(mevcut.equals("")||yeni.equals("")||yeniTekrar.equals("")){
            Toast.makeText(getContext(), "Tüm alanları girin", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!yeni.equals(yeniTekrar)){
                Toast.makeText(getContext(), "Şifreler aynı değil!", Toast.LENGTH_SHORT).show();
            }
            else{
                if (user != null) {
                    // Create a credential with the user's current email and password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), mevcut);

                    // Re-authenticate the user with the credential
                    user.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // If re-authentication is successful, update the password
                                    user.updatePassword(yeni)
                                            .addOnCompleteListener(passwordUpdateTask -> {
                                                if (passwordUpdateTask.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // If re-authentication fails, display an error message
                                    Toast.makeText(getContext(), "Authentication failed. Please enter your current password correctly.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }

    }
}