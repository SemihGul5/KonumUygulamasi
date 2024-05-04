package com.abrebo.konumuygulamasi.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.ImageUtil;
import com.abrebo.konumuygulamasi.databinding.FragmentEtkinlikPaylasBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


public class EtkinlikPaylasFragment extends Fragment {
    private FragmentEtkinlikPaylasBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ActivityResultLauncher<String> permissionLauncher2;
    ActivityResultLauncher<Intent> activityResultLauncher3;
    ActivityResultLauncher<String> permissionLauncher3;
    ActivityResultLauncher<Intent> activityResultLauncher4;
    ActivityResultLauncher<String> permissionLauncher4;
    Uri imageData=null,imageData2=null,imageData3=null,imageData4=null;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Bitmap img;
    String latitude,longitude,sehir,ilce;
    private String tarih="",saat="";
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentEtkinlikPaylasBinding.inflate(inflater, container, false);
        binding.toolbarEtkinlikEkle.setTitle("Etkinlik Paylaş");
        //etkinlik türünün başlatılması
        turBaslat();
        //izin işlemleri
        registerLauncher();
        registerLauncher2();
        registerLauncher3();
        registerLauncher4();
        binding.imageView.setOnClickListener(view -> {
            fotografTiklandi1(view);
        });
        binding.addImage2.setOnClickListener(view -> {
            if (!binding.addImage2.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi2(view);
            }
        });
        binding.addImage3.setOnClickListener(view -> {
            if (!binding.addImage3.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi3(view);
            }
        });
        binding.addImage4.setOnClickListener(view -> {
            if (!binding.addImage4.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi4(view);
            }
        });
        // başlatılmalar
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        EtkinlikPaylasFragmentArgs bundle=EtkinlikPaylasFragmentArgs.fromBundle(getArguments());
        latitude=bundle.getLatitude();
        longitude=bundle.getLongitude();
        sehir=bundle.getSehir();
        ilce=bundle.getIlce();

        binding.editTextEtkinlikAdres.setText(sehir+" / "+ilce);

        //kaydet butonu tıklanması
        binding.buttonEtkinlikPaylas.setOnClickListener(view -> {
            etlinlikPaylas(view);
        });


        //tarih butonu tıklanması
        binding.buttonTarih.setOnClickListener(view -> {
            MaterialDatePicker datePicker= MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Tarih Seç")
                    .build();
            datePicker.show(getChildFragmentManager(),"Tarih");

            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tarih=dateFormat.format(selection);
                    binding.textViewTarih.setText(tarih);
                }
            });
        });

        //saat butonuna tıklanması
        binding.buttonSaat.setOnClickListener(view -> {
            MaterialTimePicker timePicker=new MaterialTimePicker.Builder().setTitleText("Saat Seç")
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();
            timePicker.show(getChildFragmentManager(),"Saat");

            timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.textViewSaat.setText(timePicker.getHour()+" : "+timePicker.getMinute());
                    saat=timePicker.getHour()+" : "+timePicker.getMinute();
                }
            });
        });

        return binding.getRoot();
    }

    private void etlinlikPaylas(View view) {
        if (    imageData == null
                || binding.editTextEtkinlikAd.getText().toString().isEmpty()
                || binding.autoCompleteTextView.getText().toString().isEmpty()
                || binding.editTextEtkinlikAdres.getText().toString().isEmpty()
                || binding.editTextEtkinlikAciklama.getText().toString().isEmpty()
                || tarih.isEmpty() ||saat.isEmpty()
                ) {
            Toast.makeText(getContext(), "Tüm Alanlar Doldurulmalıdır!", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBarEtkinlikEkle.setVisibility(View.VISIBLE);
        binding.buttonEtkinlikPaylas.setEnabled(false);
        try {
            //unique bir id oluşturur
            UUID uuid = UUID.randomUUID();
            String path = "images/" + uuid + ".jpg";
            storageReference.child(path).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                //görseli storage'a kaydettik, onSuccess de veritabanına kaydetme işlemleri, ilgili kullanıcının
                //direkt o kaydedilen resmi eşleştiriyoruz
                StorageReference reference = firebaseStorage.getReference(path);
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    //veritabanına koyma işlemleri
                    FirebaseUser user = auth.getCurrentUser();
                    //kaydedilecek veriler;
                    String email = user.getEmail();
                    String foto = uri.toString();
                    String ad = binding.editTextEtkinlikAd.getText().toString();
                    ad=ad.substring(0, 1).toUpperCase() + ad.substring(1);
                    String tur = binding.autoCompleteTextView.getText().toString();
                    String konum = binding.editTextEtkinlikAdres.getText().toString();
                    String aciklama= binding.editTextEtkinlikAciklama.getText().toString();

                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("email", email);
                    postData.put("ad", ad);
                    postData.put("foto", imageData);
                    if (imageData2 != null){
                        postData.put("foto2", imageData2);
                    }else{
                        postData.put("foto2", "null");
                    }
                    if (imageData3 != null){
                        postData.put("foto3", imageData3);
                    }else{
                        postData.put("foto3", "null");
                    }
                    if (imageData4 != null){
                        postData.put("foto4", imageData4);
                    }else{
                        postData.put("foto4", "null");
                    }

                    postData.put("tur", tur);
                    postData.put("konum", konum);
                    postData.put("aciklama", aciklama);
                    postData.put("paylasildi_mi","true");
                    postData.put("enlem",latitude);
                    postData.put("boylam",longitude);
                    postData.put("tarih",tarih);
                    postData.put("saat",saat);

                    //firebase koleksiyonuna yükleme işlemi ve sonucunun ne olduğunu değerlendirme
                    firebaseFirestore.collection("etkinlikler")
                            .add(postData)
                            .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Paylaşıldı", Toast.LENGTH_SHORT).show();
                        temizle();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnCompleteListener(task -> {
                        binding.progressBarEtkinlikEkle.setVisibility(View.INVISIBLE);
                        binding.buttonEtkinlikPaylas.setEnabled(true);
                    });
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBarEtkinlikEkle.setVisibility(View.INVISIBLE);
                binding.buttonEtkinlikPaylas.setEnabled(true);

            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarEtkinlikEkle.setVisibility(View.INVISIBLE);
            binding.buttonEtkinlikPaylas.setEnabled(true);

        }
    }

    private void turBaslat() {
        ArrayList<String> turler= new ArrayList<>();
        turler.add("Konser");
        turler.add("Toplantı");
        turler.add("Seminer");
        turler.add("Yemek");
        turler.add("Spor");
        turler.add("Parti");
        turler.add("Diğer");
        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,turler);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);
    }
    private void temizle(){
        binding.autoCompleteTextView.clearListSelection();
        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24);
        binding.editTextEtkinlikAd.setText("");
        binding.editTextEtkinlikAdres.setText("");
        binding.editTextEtkinlikAciklama.setText("");
        binding.autoCompleteTextView.clearListSelection();
        binding.textViewSaat.setText("");
        binding.textViewTarih.setText("");
        tarih="";
        saat="";
    }


    //izin işlemleri













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
                            binding.imageView.setImageBitmap(img);
                            binding.addImage2.setImageResource(R.drawable.add_photo);
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
                            binding.addImage2.setImageBitmap(img);
                            binding.addImage3.setImageResource(R.drawable.add_photo);
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
                            binding.addImage3.setImageBitmap(img);
                            binding.addImage4.setImageResource(R.drawable.add_photo);
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
                            binding.addImage4.setImageBitmap(img);
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