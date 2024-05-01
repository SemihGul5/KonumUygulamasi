package com.abrebo.konumuygulamasi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.EtkinlikCardBinding;
import com.abrebo.konumuygulamasi.ui.fragments.AnaSayfaFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EtkinlikAdapter extends RecyclerView.Adapter<EtkinlikAdapter.EtkinlikCardHolder> {
    private Context mContext;
    private List<Etkinlik> etkinlikList;

    public EtkinlikAdapter(Context mContext, List<Etkinlik> etkinlikList) {
        this.mContext = mContext;
        this.etkinlikList = etkinlikList;
    }

    @NonNull
    @Override
    public EtkinlikCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EtkinlikCardBinding binding=EtkinlikCardBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new EtkinlikCardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EtkinlikCardHolder holder, int position) {
        //Karta foto, ad ve konum yazılması
        Etkinlik etkinlik=etkinlikList.get(position);

        Picasso.get().load(etkinlik.getFoto()).into(holder.binding.imageViewCardEtkinlikFoto);
        holder.binding.textViewCardEtkinlikAd.setText(etkinlik.getAd());
        holder.binding.textViewEtkinlikKonum.setText(etkinlik.getKonum());
        holder.binding.textViewTarihVeSaat.setText(etkinlik.getTarih()+" "+etkinlik.getSaat());
        holder.binding.textViewEtkinlikTuru.setText("Etkinlik Türü: "+etkinlik.getTur());
        // etkinlik kart tasarımına tıklandığında
        holder.binding.etkinlikCard.setOnClickListener(view -> {
            gitAyrinti(view,etkinlik);
        });
    }

    private void gitAyrinti(View view, Etkinlik etkinlik) {
        AnaSayfaFragmentDirections.ActionAnaSayfaFragmentToEtkinlikAyrintiFragment gecis=
                AnaSayfaFragmentDirections.actionAnaSayfaFragmentToEtkinlikAyrintiFragment(etkinlik);
        Navigation.findNavController(view).navigate(gecis);

    }

    @Override
    public int getItemCount() {
        return etkinlikList.size();
    }

    public class EtkinlikCardHolder extends RecyclerView.ViewHolder{
        private EtkinlikCardBinding binding;

        public EtkinlikCardHolder(EtkinlikCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}