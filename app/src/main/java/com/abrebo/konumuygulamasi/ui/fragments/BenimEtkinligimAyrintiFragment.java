package com.abrebo.konumuygulamasi.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.MainActivity;
import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.data.models.ImageUtil;
import com.abrebo.konumuygulamasi.databinding.FragmentBenimEtkinligimAyrintiBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class BenimEtkinligimAyrintiFragment extends Fragment {
    private FragmentBenimEtkinligimAyrintiBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Etkinlik etkinlik;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String tarih="",saat="";
    Uri imageData=null,imageData2=null,imageData3=null,imageData4=null;
    Bitmap img;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ActivityResultLauncher<String> permissionLauncher2;
    ActivityResultLauncher<Intent> activityResultLauncher3;
    ActivityResultLauncher<String> permissionLauncher3;
    ActivityResultLauncher<Intent> activityResultLauncher4;
    ActivityResultLauncher<String> permissionLauncher4;
    private ArrayList<Uri>imageDatalist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBenimEtkinligimAyrintiBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        imageDatalist=new ArrayList<>();
        BenimEtkinligimAyrintiFragmentArgs bundle=BenimEtkinligimAyrintiFragmentArgs.fromBundle(getArguments());
        etkinlik=bundle.getEtkinlik();
        binding.editTextEtkinlikAdBenim.setText(etkinlik.getAd());
        binding.autoCompleteTextViewBenim.setText(etkinlik.getTur());
        binding.editTextEtkinlikAdresBenim.setText(etkinlik.getKonum());
        binding.editTextEtkinlikAciklamaBenim.setText(etkinlik.getAciklama());
        binding.textViewTarihBenim.setText(etkinlik.getTarih());
        binding.textViewSaatBenim.setText(etkinlik.getSaat());
        turBaslat();

        //izin işlemleri
        registerLauncher();
        registerLauncher2();
        registerLauncher3();
        registerLauncher4();
        tarih=binding.textViewTarihBenim.getText().toString();
        saat=binding.textViewSaatBenim.getText().toString();
        Picasso.get().load(etkinlik.getFoto()).into(binding.imageViewBenim);
        if (!etkinlik.getFoto2().equals("null")){
            Picasso.get().load(etkinlik.getFoto2()).into(binding.addImage2Benim);
        }
        if (!etkinlik.getFoto3().equals("null")){
            Picasso.get().load(etkinlik.getFoto3()).into(binding.addImage3Benim);
        }
        if (!etkinlik.getFoto4().equals("null")){
            Picasso.get().load(etkinlik.getFoto4()).into(binding.addImage4Benim);
        }
        binding.imageViewBenim.setOnClickListener(view -> {
                fotografTiklandi1(view);
        });
        binding.addImage2Benim.setOnClickListener(view -> {
            if (!binding.addImage2Benim.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi2(view);
            }
        });
        binding.addImage3Benim.setOnClickListener(view -> {
            if (!binding.addImage3Benim.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi3(view);
            }
        });
        binding.addImage4Benim.setOnClickListener(view -> {
            if (!binding.addImage4Benim.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi4(view);
            }
        });
        //tarih butonu tıklandığında
        binding.buttonTarihBenim.setOnClickListener(view -> {
            MaterialDatePicker datePicker= MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Tarih Seç")
                    .build();
            datePicker.show(getChildFragmentManager(),"Tarih");

            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tarih=dateFormat.format(selection);
                    binding.buttonTarihBenim.setText(tarih);
                }
            });
        });


        binding.buttonSaatBenim.setOnClickListener(view -> {
            MaterialTimePicker timePicker=new MaterialTimePicker.Builder().setTitleText("Saat Seç")
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();
            timePicker.show(getChildFragmentManager(),"Saat");

            timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String formattedMinute = String.format("%02d", timePicker.getMinute());
                    binding.textViewSaatBenim.setText(timePicker.getHour() + ":" + formattedMinute);
                    saat = timePicker.getHour() + ":" + formattedMinute;
                }
            });
        });


        binding.buttonEtkinlikPaylasBenim.setOnClickListener(view -> {
            etlinlikGuncelle(view);
        });
        
        binding.materialToolbar4.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.duzenle_sil) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Etkinliği Sil");
                    alert.setMessage("Etkinliği silmek istediğinizden emin misiniz?");
                    alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // etkinlik bildirisi satfası açılacak

                            gitEtkinlikSil(auth.getCurrentUser().getEmail(),etkinlik.getDocID(),getView());
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

        return binding.getRoot();
    }

    private void gitEtkinlikSil(String email, String docID, View view) {
        // Firestore'dan belirli bir etkinliği silmek için gereken kodu buraya yazın
        firestore.collection("etkinlikler").document(docID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Etkinlik başarıyla silindi", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_benimEtkinligimAyrintiFragment_to_benimEtkinliklerimFragment);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Etkinlik silinirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void etlinlikGuncelle(View view) {
        if (binding.editTextEtkinlikAdBenim.getText().toString().isEmpty()
                || binding.autoCompleteTextViewBenim.getText().toString().isEmpty()
                || binding.editTextEtkinlikAdresBenim.getText().toString().isEmpty()
                || binding.editTextEtkinlikAciklamaBenim.getText().toString().isEmpty()
                || tarih.isEmpty() || saat.isEmpty()
        ) {
            Toast.makeText(getContext(), "Tüm Alanlar Doldurulmalıdır!", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBarEtkinlikEkleBenim.setVisibility(View.VISIBLE);
        binding.buttonEtkinlikPaylasBenim.setEnabled(false);
        imageDatalist.add(imageData);
        imageDatalist.add(imageData2);
        imageDatalist.add(imageData3);
        imageDatalist.add(imageData4);
        // Resim URL'lerini saklayacak bir dizi oluştur
        String[] fotoURLs = new String[4];

        for (int i = 0; i < 4; i++) {
            final int index = i;

            if (imageDatalist.get(i) != null) {
                UUID uuid = UUID.randomUUID();
                String path = "images/" + uuid + "_" + (i + 1) + ".jpg";

                // Resim yükleme işleminin başarı dinleyicisi
                storageReference.child(path).putFile(imageDatalist.get(i)).addOnSuccessListener(taskSnapshot -> {
                    // Resim yükleme işlemi başarılı olduğunda URL alma işlemini gerçekleştir
                    storageReference.child(path).getDownloadUrl().addOnSuccessListener(uri -> {
                        fotoURLs[index] = uri.toString();

                        // Tüm resimlerin yüklenmesini beklerip, URL'leri aldıktan sonra veritabanına kaydet
                        if (allURLsReceived(fotoURLs)) {
                            savedF(view, fotoURLs[0], fotoURLs[1], fotoURLs[2], fotoURLs[3]);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "URL alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Resim yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // Resim verisi yoksa, URL'yi "null" olarak ayarla
                fotoURLs[i] = "null";

                // Tüm resimlerin yüklenmesini beklerip, URL'leri aldıktan sonra veritabanına kaydet
                if (allURLsReceived(fotoURLs)) {
                    savedF(view, fotoURLs[0], fotoURLs[1], fotoURLs[2], fotoURLs[3]);
                }
            }
        }
    }

    private void savedF(View view, String foto, String foto2, String foto3, String foto4) {
        try {
            // Tüm alanlar doldurulduğundan emin ol
            if (binding.editTextEtkinlikAdBenim.getText().toString().isEmpty()
                    || binding.autoCompleteTextViewBenim.getText().toString().isEmpty()
                    || binding.editTextEtkinlikAdresBenim.getText().toString().isEmpty()
                    || binding.editTextEtkinlikAciklamaBenim.getText().toString().isEmpty()
                    || tarih.isEmpty() || saat.isEmpty()
            ) {
                Toast.makeText(getContext(), "Tüm Alanlar Doldurulmalıdır!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Güncellenecek etkinliğin belirteci
            String etkinlikBelirteci = etkinlik.getDocID(); // Etkinliği belirleyen bir ID kullanılmalıdır.

            // Yeni verileri bir haritada saklayalım
            HashMap<String, Object> updatedData = new HashMap<>();
            updatedData.put("ad", binding.editTextEtkinlikAdBenim.getText().toString().substring(0, 1).toUpperCase() + binding.editTextEtkinlikAdBenim.getText().toString().substring(1));
            updatedData.put("tur", binding.autoCompleteTextViewBenim.getText().toString());
            updatedData.put("konum", binding.editTextEtkinlikAdresBenim.getText().toString());
            updatedData.put("aciklama", binding.editTextEtkinlikAciklamaBenim.getText().toString());
            updatedData.put("tarih", tarih);
            updatedData.put("saat", saat);
            // Tarih ve saat bilgisini Firestore'a kaydetmek için birleştir
            String dateTimeString = tarih + " " + saat.replace(" ", ""); // Boşluğu kaldır
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date date = dateTimeFormat.parse(dateTimeString);
                Timestamp timestamp = new Timestamp(date);
                // Firestore'a timestamp olarak kaydet
                updatedData.put("tarih2", timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!foto.equals("null")) {
                updatedData.put("foto", foto);
            }
            if (!foto2.equals("null")) {
                updatedData.put("foto2", foto2);
            }
            if (!foto3.equals("null")) {
                updatedData.put("foto3", foto3);
            }
            if (!foto4.equals("null")) {
                updatedData.put("foto4", foto4);
            }

            // Firestore koleksiyonunda güncelleme işlemi
            firestore.collection("etkinlikler").document(etkinlikBelirteci)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Etkinlik Güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Etkinlik Güncellenirken Bir Hata Oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        binding.progressBarEtkinlikEkleBenim.setVisibility(View.INVISIBLE);
                        binding.buttonEtkinlikPaylasBenim.setEnabled(true);
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarEtkinlikEkleBenim.setVisibility(View.INVISIBLE);
            binding.buttonEtkinlikPaylasBenim.setEnabled(true);
        }
    }


    private boolean allURLsReceived(String[] urls) {
        for (String url : urls) {
            if (url == null) {
                return false;
            }
        }
        return true;
    }
    private void turBaslat() {
        ArrayList<String> turler= new ArrayList<>();
        turler.add("Konser");
        turler.add("Toplantı");
        turler.add("Seminer");
        turler.add("Yemek");
        turler.add("Spor");
        turler.add("Oyun");
        turler.add("Parti");
        turler.add("Diğer");
        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,turler);
        binding.autoCompleteTextViewBenim.setAdapter(arrayAdapter);
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
    public void fotografTiklandi2(View view) {
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
                            permissionLauncher2.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher2.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher2.launch(intentToGallery);
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
                    permissionLauncher2.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher2.launch(intentToGallery);
            }
        }
    }

    public void fotografTiklandi4(View view) {
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
                            permissionLauncher4.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher4.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher4.launch(intentToGallery);
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
                    permissionLauncher4.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher4.launch(intentToGallery);
            }
        }
    }
    public void fotografTiklandi3(View view) {
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
                            permissionLauncher3.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher3.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher3.launch(intentToGallery);
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
                    permissionLauncher3.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher3.launch(intentToGallery);
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
                            binding.imageViewBenim.setImageBitmap(img);
                            binding.addImage2Benim.setImageResource(R.drawable.add_photo);
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

    private void registerLauncher2() {
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData2 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData2);
                            imageData2=null;
                            // Bitmap'i kullanabilirsiniz
                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            imageData2 = intentFromResult.getData();
                            binding.addImage2Benim.setImageBitmap(img);
                            binding.addImage3Benim.setImageResource(R.drawable.add_photo);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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

    private void registerLauncher3() {
        activityResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData3 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData3);
                            imageData3=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            imageData3 = intentFromResult.getData();
                            binding.addImage3Benim.setImageBitmap(img);
                            binding.addImage4Benim.setImageResource(R.drawable.add_photo);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher3 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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

    private void registerLauncher4() {
        activityResultLauncher4 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData4 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData4);
                            imageData4=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            imageData4 = intentFromResult.getData();
                            binding.addImage4Benim.setImageBitmap(img);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher4 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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