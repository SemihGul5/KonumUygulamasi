package com.abrebo.konumuygulamasi.ui.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.abrebo.konumuygulamasi.MainActivity;
import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.databinding.AyarlarListesiBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class AyarlarAdapter extends RecyclerView.Adapter<AyarlarAdapter.AyarlarCardTutucu> {

    private Context mContext;
    private List<String> ayarlarList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    public AyarlarAdapter(Context mContext, List<String> ayarlarList,FirebaseFirestore firestore,FirebaseAuth auth) {
        this.mContext = mContext;
        this.ayarlarList = ayarlarList;
        this.firestore=firestore;
        this.auth=auth;
    }

    @NonNull
    @Override
    public AyarlarCardTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AyarlarListesiBinding binding=AyarlarListesiBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new AyarlarCardTutucu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AyarlarCardTutucu holder, int position) {
        String icerik=ayarlarList.get(position);
        holder.binding.textViewAyarIsmi.setText(icerik);


        holder.binding.ayarlarCard.setOnClickListener(view -> {
            listedenElemanTiklanmasi(view,holder,icerik);
        });

        ayarlarIconAyarlama(icerik,holder);



    }

    private void ayarlarIconAyarlama(String icerik, AyarlarCardTutucu holder) {
        if (icerik.equals("Profilim")){
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_person_24);
        } else if (icerik.equals("Kişilik Testi")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_edit_note_60);
        }
        else if (icerik.equals("Kişilikler Ne Anlama Geliyor?")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_question_mark_24);
        }
        else if (icerik.equals("Uygulamayı Paylaş")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_share_24);
        }
        else if (icerik.equals("Bize Ulaşın")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_contact_mail_24);
        }
        else if (icerik.equals("Çıkış Yap")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_logout_24);
        }

    }

    private void listedenElemanTiklanmasi(View view, AyarlarCardTutucu holder, String icerik) {
        String secilen=icerik;
        if(secilen.equals("Profilim")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_kisiselBilgiDegistirFragment);
        } else if (secilen.equals("Kişilikler Ne Anlama Geliyor?")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_kisilikBilgilerFragment);
        } else if (secilen.equals("Uygulamayı Paylaş")) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=Abrebo+Studio");
            startActivity(mContext,Intent.createChooser(sharingIntent, "Paylaş"),null);

        } else if (secilen.equals("Bize Ulaşın")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_geriBildirimFragment);
        } else if (secilen.equals("Çıkış Yap")) {
            cikisYap(view,auth);
        }
        else{
            Toast.makeText(mContext, "Hata", Toast.LENGTH_SHORT).show();
        }
    }

    private void cikisYap(View view, FirebaseAuth auth) {
        try {
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("Çıkıs Yap");
            alert.setMessage("Emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (auth.getCurrentUser()!=null){
                        Toast.makeText(mContext, "Çıkış Yapılıyor.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(mContext, MainActivity.class);
                        startActivity(mContext,intent,null);
                        auth.signOut();
                    }else{
                        Toast.makeText(mContext, "hata.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.show();
        }catch (Exception e){
            Log.i("Çıkış hatası: ",e.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return ayarlarList.size();
    }

    public class AyarlarCardTutucu extends RecyclerView.ViewHolder{
        private AyarlarListesiBinding binding;

        public AyarlarCardTutucu(AyarlarListesiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
