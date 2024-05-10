package com.abrebo.konumuygulamasi.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.ImageUtil;
import com.abrebo.konumuygulamasi.databinding.FragmentKisiselBilgiDegistirBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KisiselBilgiDegistirFragment extends Fragment {
    private FragmentKisiselBilgiDegistirBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData=null;
    Bitmap img;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentKisiselBilgiDegistirBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        binding.textProfilEmail.setEnabled(false);
        registerLauncher();
        getData();
        binding.buttonProfilGuncelle.setOnClickListener(view -> {
            guncelle(view);
        });

        binding.textViewSifreDegis.setOnClickListener(view -> {
            sifreDegis(view);
        });

        binding.imageViewProfilResimBilgilerDegistir.setOnClickListener(view -> {
            fotografTiklandi1(view);
        });




        return binding.getRoot();
    }
    private void sifreDegis(View view) {
        Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_sifreDegisFragment);
    }
    private Boolean bosMu(String ad){
        Boolean kontrol;
        if(ad.equals("")){
            kontrol=true;
        }else{
            kontrol=false;
        }
        return kontrol;
    }
    private void guncelle(View view) {
        String yeniAd = binding.textProfilAd.getText().toString();
        String email = auth.getCurrentUser().getEmail();
        if (!bosMu(yeniAd)) {
            if (imageData != null) {
                // Eğer yeni bir fotoğraf seçildiyse, önce fotoğrafı güncelle
                fotoGuncelle(view, yeniAd, email);
            } else {
                // Yeni bir fotoğraf seçilmediyse, sadece kullanıcı adını güncelle
                adGuncelle(view, yeniAd, email);
            }
        } else {
            Snackbar.make(view, "Tüm alanları doldurun.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void adGuncelle(View view, String yeniAd, String email) {
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
        } catch (Exception e) {
            Log.i("Mesaj", e.getMessage());
        }
    }

    private void fotoGuncelle(View view, String yeniAd, String email) {
        // Firebase Storage referansı oluştur
        StorageReference fileRef = storageReference.child("profil_resimleri").child(auth.getCurrentUser().getUid() + ".jpg");

        // Seçilen fotoğrafı Firebase Storage'a yükle
        fileRef.putFile(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    // Fotoğraf yükleme başarılı olduğunda
                    // Fotoğrafın URL'sini al
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Fotoğraf URL'sini Firestore'da güncelle
                        Map<String, Object> guncellenmisBilgiler = new HashMap<>();
                        guncellenmisBilgiler.put("adSoyad", yeniAd);
                        guncellenmisBilgiler.put("foto", uri.toString());

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
                        } catch (Exception e) {
                            Log.i("Mesaj", e.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Fotoğraf yükleme başarısız olduğunda
                    Toast.makeText(getContext(), "Fotoğraf yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                });
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
                                String foto = (String) data.get("foto");
                                binding.textProfilAd.setText(ad);
                                binding.textProfilEmail.setText(email);
                                Picasso.get().load(foto)
                                        .into(binding.imageViewProfilResimBilgilerDegistir);

                            }
                        }
                    }
                });
    }
    public void fotografTiklandi1(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                //izin gerekli
                //kullanıcıya açıklama göstermek zorunda mıyız kontrolü
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)){
                    //açıklama gerekli
                    Snackbar.make(view,"Galeriye gitmek için izin gereklidir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin işlemleri
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //izin gerekli
                //kullanıcıya açıklama göstermek zorunda mıyız kontrolü
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //açıklama gerekli
                    Snackbar.make(view,"Galeriye gitmek için izin gereklidir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin işlemleri
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }
    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData);
                            imageData=null;
                            // Bitmap'i kullanabilirsiniz
                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            imageData = intentFromResult.getData();
                            binding.imageViewProfilResimBilgilerDegistir.setImageBitmap(img);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(getContext(), "İzin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}