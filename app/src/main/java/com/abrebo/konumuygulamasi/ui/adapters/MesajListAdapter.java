package com.abrebo.konumuygulamasi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.MesajListesiBinding;
import com.abrebo.konumuygulamasi.ui.fragments.MesajListesiFragmentDirections;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MesajListAdapter extends RecyclerView.Adapter<MesajListAdapter.MesajListesiHolder> {
    private Context context;
    private List<Etkinlik> etkinlikList;
    private FirebaseFirestore firestore;

    public MesajListAdapter(Context context, List<Etkinlik> etkinlikList, FirebaseFirestore firestore) {
        this.context = context;
        this.etkinlikList = etkinlikList;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public MesajListesiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MesajListesiBinding binding=MesajListesiBinding.inflate(LayoutInflater.from(context),parent,false);
        return new MesajListesiHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajListesiHolder holder, int position) {
        firestore=FirebaseFirestore.getInstance();
        Etkinlik etkinlik=etkinlikList.get(position);
        holder.binding.textViewAliciAdSoyad.setText(etkinlik.getAd());

        holder.binding.cardMesajlarLsitesi.setOnClickListener(view -> {
            //o konuşmayı aç
            MesajListesiFragmentDirections.ActionMesajListesiFragmentToMesajFragment gecis=
                    MesajListesiFragmentDirections.actionMesajListesiFragmentToMesajFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        });
    }

    @Override
    public int getItemCount() {
        return etkinlikList.size();
    }

    public class MesajListesiHolder extends RecyclerView.ViewHolder{
        private MesajListesiBinding binding;

        public MesajListesiHolder(MesajListesiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
