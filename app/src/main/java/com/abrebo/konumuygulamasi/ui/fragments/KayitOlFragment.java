package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.FragmentKayitOlBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;

public class KayitOlFragment extends Fragment {
    private FragmentKayitOlBinding binding;
    private String adSoyad,email,sifre,sifreTekrar,kullaniciAdi,cinsiyet,dogumTarihi,yas;
    Calendar cal;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentKayitOlBinding.inflate(inflater, container, false);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        cal = Calendar.getInstance();

        binding.girisYapGitTextButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_kayitOlFragment_to_girisYapFragment);
        });

        kayitOlButtonTiklandi(getView());


        return binding.getRoot();
    }

    private void kayitOlButtonTiklandi(View view) {

        binding.kayitOlYapButton.setOnClickListener(view1 -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            adSoyad=binding.adVeSoyadText.getText().toString();
            email=binding.EmailText.getText().toString();
            sifre=binding.sifreText.getText().toString();
            sifreTekrar=binding.sifreTekrarText.getText().toString();
            kullaniciAdi=binding.kullaniciAdiText.getText().toString();
            dogumTarihi= binding.yasText.getText().toString();
            int id=binding.radioGroupCinsiyet.getCheckedRadioButtonId();
            if (id==R.id.radioButtonErkek){
                cinsiyet="Erkek";
            }else{
                cinsiyet="Kadın";
            }
            int currentYear = cal.get(Calendar.YEAR);
            int kYas=currentYear-Integer.parseInt(dogumTarihi);
            yas=String.valueOf(kYas);

            if(adSoyad.equals("")||kullaniciAdi.equals("")||dogumTarihi.equals("")
                    ||binding.radioGroupCinsiyet.getCheckedRadioButtonId() == -1
                    ||email.equals("")||sifre.equals("")||sifreTekrar.equals("")){
                Snackbar.make(getView(),"Tüm alanları doldurun",Snackbar.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            } else if (!sifre.equals(sifreTekrar)) {
                Snackbar.make(getView(),"Şifreler aynı olmalıdır",Snackbar.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
            else{
                CollectionReference reference=firestore.collection("kullanicilar");
                Query query=reference.whereEqualTo("kullaniciAdi",kullaniciAdi);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().isEmpty()){
                                kullaniciyiKaydet(adSoyad,email,sifre,view);
                                binding.progressBar.setVisibility(View.GONE);
                            }
                            else{
                                Snackbar.make(view, "Bu kullanıcı adı daha önceden alınmış", Snackbar.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Snackbar.make(view, "Sorgu hatası: " + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);

                        }
                    }
                });
            }
        });
    }

    private void kullaniciyiKaydet(String adSoyad,String email, String sifre, View view) {
        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();//oluşturulan userı aldık, mail göndermek için
                    aktivasyonEmailiGonder(user,view);
                    kullaniciyiFirestoreKaydet(adSoyad,email,sifre,kullaniciAdi,cinsiyet,yas,dogumTarihi);

                }else{
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthUserCollisionException) {
                        // E-posta zaten kayıtlı, kullanıcıyı bilgilendir veya işlem yap
                        Snackbar.make(view,"E posta zaten kayıtlı",Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Diğer hataları işle
                        Snackbar.make(view,exception.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void kullaniciyiFirestoreKaydet(String adSoyad, String email,String sifre,String kAd,String cinsiyet,String yas,String dt) {

        HashMap<String, Object> data=new HashMap<>();

        data.put("adSoyad",adSoyad);
        data.put("email",email);
        data.put("sifre",sifre);
        data.put("kullaniciAdi",kAd);
        data.put("cinsiyet",cinsiyet);
        data.put("yas",yas);
        data.put("dogumTarihi",dt);
        data.put("kisilik","null");
        data.put("kisilik_durum","true");
        data.put("oneri_durum",true);
        firestore.collection("kullanicilar").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //kullanıcı adı kayıt başarılı, mesaj göstermeye gerek yok
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void aktivasyonEmailiGonder(FirebaseUser user, View view) {
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"Email gönderildi, hesabınızı doğrulayın.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}