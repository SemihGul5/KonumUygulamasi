package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.FragmentEtkinlikAyrintiBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EtkinlikAyrintiFragment extends Fragment {
    private FragmentEtkinlikAyrintiBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String email;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentEtkinlikAyrintiBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
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
            EtkinlikAyrintiFragmentDirections.ActionEtkinlikAyrintiFragmentToMesajFragment gecis=
                    EtkinlikAyrintiFragmentDirections.actionEtkinlikAyrintiFragmentToMesajFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        });

        //menü elemanlarına tıklanıldığında
        binding.materialToolbarEtkinlikAyrinti.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.etkinlikBildir) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Bildir");
                    alert.setMessage("Bildirmek istediğinizden emin misiniz? Gereksiz bildirimler tekrarlanırsa cezalandırılacaktır.");
                    alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // etkinlik bildirisi satfası açılacak

                            gitEtkinlikBildir(auth.getCurrentUser().getEmail(),etkinlik.getDocID(),getView());
                        }
                    });
                    alert.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alert.show();
                }
                return false;
            }
        });

        // favorilere ekleye tıklandığında
        binding.imageViewFavorilereEkle.setOnClickListener(view -> {
            if (binding.imageViewFavorilereEkle.getResources().equals(R.drawable.baseline_bookmark_add_24)){
                // favorilerinde değil, ekle
                etkinligiFavorilereEkle(etkinlik.getDocID(),email,firestore);
            }else{
                //favorilerde, sil
                etkinligiFavorilerdenSil(etkinlik.getDocID(),firestore);
            }
        });



        return binding.getRoot();
    }

    private void etkinligiFavorilerdenSil(String docID, FirebaseFirestore firestore) {
        firestore.collection("favoriler")
                .document(docID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Etkinlik favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                        binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_add_24);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Etkinlik favorilerden kaldırılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void etkinligiFavorilereEkle(String docID, String email, FirebaseFirestore firestore) {
        CollectionReference bildirilerCollectionRef = firestore.collection("favoriler");
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                        binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_added_24);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Favorilere eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void gitEtkinlikBildir(String email, String docID, View view) {
        EtkinlikAyrintiFragmentDirections.ActionEtkinlikAyrintiFragmentToEtkinlikBildirFragment gecis=
                EtkinlikAyrintiFragmentDirections.actionEtkinlikAyrintiFragmentToEtkinlikBildirFragment(email,docID);
        Navigation.findNavController(view).navigate(gecis);

    }

    private void gitHaritaFragment(String enlem, String boylam, View view) {
        EtkinlikAyrintiFragmentDirections.ActionEtkinlikAyrintiFragmentToMapsFragmentEtkinlikAyrinti gecis=
                EtkinlikAyrintiFragmentDirections.actionEtkinlikAyrintiFragmentToMapsFragmentEtkinlikAyrinti(enlem,boylam);
        Navigation.findNavController(view).navigate(gecis);
    }
}