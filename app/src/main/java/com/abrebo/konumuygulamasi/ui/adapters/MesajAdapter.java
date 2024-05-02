package com.abrebo.konumuygulamasi.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Mesaj;
import com.abrebo.konumuygulamasi.databinding.MesajBinding;

import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajHolder> {
    private Context context;
    private List<Mesaj> mesajs;
    private String userEmail;

    public MesajAdapter(Context context, List<Mesaj> mesajs, String userEmail) {
        this.context = context;
        this.mesajs = mesajs;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public MesajHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MesajBinding binding=MesajBinding.inflate(LayoutInflater.from(context),parent,false);
        return new MesajHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajHolder holder, int position) {
        Mesaj mesaj = mesajs.get(position);
        holder.binding.textViewCardMesaj.setText(mesaj.getMesaj());
        holder.binding.textViewMesajSaati.setText(mesaj.getSaat());


        if (mesaj.getGonderen_email().equals(userEmail)) {
            // Mesaj kullanıcı tarafından gönderildiyse, sağa yasla ve arkaplan rengini ayarla
            holder.binding.cardMesaj.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.binding.cardMesaj.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.anaRenk)));
            // Mesajın içeriğini sağa yaslamak için linearLayoutMesaj'ın yerçekimini ayarla
            holder.binding.mesaj.setGravity(Gravity.END);
            holder.binding.linearLayoutMesaj.setGravity(Gravity.END);
        } else {
            // Mesaj kullanıcı tarafından gönderilmediyse, sola yasla
            holder.binding.cardMesaj.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            // Diğer durumlar için gerekli işlemleri buraya ekleyebilirsiniz
            holder.binding.cardMesaj.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kirmizi)));
            // Mesajın içeriğini sağa yaslamak için linearLayoutMesaj'ın yerçekimini ayarla
            holder.binding.mesaj.setGravity(Gravity.START);
            holder.binding.linearLayoutMesaj.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return mesajs.size();
    }

    public class MesajHolder extends RecyclerView.ViewHolder{
        private MesajBinding binding;

        public MesajHolder(MesajBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
