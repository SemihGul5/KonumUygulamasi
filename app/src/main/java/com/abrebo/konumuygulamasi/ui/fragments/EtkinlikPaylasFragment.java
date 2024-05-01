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
    Uri imageData=null;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Bitmap img;
    String latitude,longitude,sehir,ilce;
    boolean secildiMi=false;
    String tarih="",saat="";
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
        binding.imageView.setOnClickListener(view -> {
            fotografTiklandi(view);
        });
        // başlatılmalar
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        Bundle bundle = getArguments();
        if (bundle != null) {
            latitude = bundle.getString("la");
            longitude = bundle.getString("lo");
            sehir = bundle.getString("sehir");
            ilce = bundle.getString("ilce");
            if (latitude==null){
                binding.imageViewCheckPass.setImageResource(R.drawable.close);
                secildiMi=false;
            }else{
                binding.imageViewCheckPass.setImageResource(R.drawable.check);
                secildiMi=true;
            }
            // Veri alındı, burada işlemleri yapabilirsiniz
        }
        if (secildiMi){
            binding.imageViewKonum.setEnabled(false);
        }

        //kaydet butonu tıklanması
        binding.buttonEtkinlikPaylas.setOnClickListener(view -> {
            etlinlikPaylas(view);
        });
        // konum eklemeye tıklanması
        binding.imageViewKonum.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_etkinlikPaylasFragment_to_mapsFragmentEtkinlikPaylas);
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
                || binding.imageViewCheckPass.getResources().equals(R.drawable.close)
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
                    postData.put("foto", foto);
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
        binding.imageViewCheckPass.setImageResource(R.drawable.close);
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
    public void fotografTiklandi(View view) {
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
                        binding.imageView.setImageURI(imageData);

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