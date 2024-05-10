package com.abrebo.konumuygulamasi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EtkinlikAyrintiFragment extends Fragment {
    private FragmentEtkinlikAyrintiBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String email;
    Boolean favoriMi;
    private String etkinlikPaylasanIsim;
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
        binding.etkinlikAyrintiKonum.setText(etkinlik.getKonum());
        getEtkinlikSahibiAd(etkinlik.getEmail());
        getFavoriMi(auth.getCurrentUser().getEmail(),etkinlik.getDocID());


        
        
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

        binding.imageViewFavorilereEkle.setOnClickListener(view -> {
            if (favoriMi){
                etkinligiFavorilerdenSil(etkinlik.getDocID(), firestore);
            }else{
                etkinligiFavorilereEkle(etkinlik.getDocID(), email, firestore);
            }
        });

        binding.textViewPaylasanKisi.setOnClickListener(view -> {
            EtkinlikAyrintiFragmentDirections.ActionEtkinlikAyrintiFragmentToBaskasininProfiliFragment gecis=
                    EtkinlikAyrintiFragmentDirections.actionEtkinlikAyrintiFragmentToBaskasininProfiliFragment(etkinlik.getEmail());
            Navigation.findNavController(view).navigate(gecis);
        });




        return binding.getRoot();
    }

    private void getEtkinlikSahibiAd(String email) {
        firestore.collection("kullanicilar")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                if (data != null && !data.isEmpty()) {
                                    etkinlikPaylasanIsim = (String) data.get("adSoyad");
                                    binding.textViewPaylasanKisi.setText(etkinlikPaylasanIsim);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
        FloatingActionButton fab=requireActivity().findViewById(R.id.fab);
        bottomAppBar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    public void getFavoriMi(String email, String docID) {
        // Kullanıcının emailine göre Firestore'da favoriler koleksiyonunda arama yap
        firestore.collection("favoriler")
                .whereEqualTo("email", email)
                .whereEqualTo("docID", docID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Favoriler koleksiyonunda kayıt bulundu
                        if (!task.getResult().isEmpty()) {
                            // Favoride var
                            favoriMi=true;
                            binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_added_24_beyaz);
                        } else {
                            // Favoride yok, ekle
                            favoriMi=false;;
                            binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_add_24_beyaz);
                        }
                    } else {
                        // Firestore sorgusu başarısız oldu
                        Toast.makeText(requireContext(), "Favorileri kontrol ederken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void etkinligiFavorilerdenSil(String docID, FirebaseFirestore firestore) {
        firestore.collection("favoriler")
                .whereEqualTo("docID", docID)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Favoriler koleksiyonunda belirli bir etkinliğin kaydını bulduk
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        // Belirli bir kaydı sil
                        firestore.collection("favoriler")
                                .document(snapshot.getId()) // Silinecek belgenin kimliğini al
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                                    binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_add_24_beyaz);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Favorileri kontrol ederken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        binding.imageViewFavorilereEkle.setImageResource(R.drawable.baseline_bookmark_added_24_beyaz);
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