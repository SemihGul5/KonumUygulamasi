package com.abrebo.konumuygulamasi.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.abrebo.konumuygulamasi.R;
import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.databinding.EtkinlikCardBinding;
import com.abrebo.konumuygulamasi.ui.fragments.AnaSayfaFragmentDirections;
import com.abrebo.konumuygulamasi.ui.fragments.BaskasininProfiliFragmentDirections;
import com.abrebo.konumuygulamasi.ui.fragments.BenimEtkinliklerimFragment;
import com.abrebo.konumuygulamasi.ui.fragments.BenimEtkinliklerimFragmentDirections;
import com.abrebo.konumuygulamasi.ui.fragments.FavorilerimFragmentDirections;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class EtkinlikAdapter extends RecyclerView.Adapter<EtkinlikAdapter.EtkinlikCardHolder> {

    public enum SayfaTuru {
        ANA_SAYFA,
        FAVORILER_SAYFASI,
        ETKINLIKLERIM_SAYFASI,
        BASKASININ_PROFILI
    }

    private SayfaTuru sayfaTuru;
    private Context mContext;
    private List<Etkinlik> etkinlikList;

    public EtkinlikAdapter(Context mContext, List<Etkinlik> etkinlikList,SayfaTuru sayfaTuru) {
        this.mContext = mContext;
        this.etkinlikList = etkinlikList;
        this.sayfaTuru=sayfaTuru;
    }

    @NonNull
    @Override
    public EtkinlikCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EtkinlikCardBinding binding=EtkinlikCardBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new EtkinlikCardHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EtkinlikCardHolder holder, int position) {
        //Karta foto, ad ve konum yazılması
        Etkinlik etkinlik=etkinlikList.get(position);

        //Picasso.get().load(etkinlik.getFoto()).into(holder.binding.imageViewCardEtkinlikFoto);
        ArrayList<SlideModel> slideModels=new ArrayList<>();
        String foto1,foto2,foto3,foto4;
        foto1=etkinlik.getFoto();
        foto2=etkinlik.getFoto2();
        foto3=etkinlik.getFoto3();
        foto4=etkinlik.getFoto4();
        SlideModel slideModel1=new SlideModel(foto1, ScaleTypes.CENTER_INSIDE);
        slideModels.add(slideModel1);
        if (!"null".equals(foto2)) {
            SlideModel slideModel2 = new SlideModel(foto2, ScaleTypes.CENTER_INSIDE);
            slideModels.add(slideModel2);
        }

        if (!"null".equals(foto3)) {
            SlideModel slideModel3 = new SlideModel(foto3, ScaleTypes.CENTER_INSIDE);
            slideModels.add(slideModel3);
        }

        if (!"null".equals(foto4)) {
            SlideModel slideModel4 = new SlideModel(foto4, ScaleTypes.CENTER_INSIDE);
            slideModels.add(slideModel4);
        }
        holder.binding.textViewTarihVeSaat.setText(etkinlik.getTarih()+" "+etkinlik.getSaat());
        holder.binding.textViewEtkinlikTuru.setText("Etkinlik Türü: "+etkinlik.getTur());
        holder.binding.imageViewCardEtkinlikFoto.setImageList(slideModels,ScaleTypes.CENTER_INSIDE);
        holder.binding.textViewCardEtkinlikAd.setText(etkinlik.getAd());
        holder.binding.textViewEtkinlikKonum.setText(etkinlik.getKonum());



        // etkinlik kart tasarımına tıklandığında
        holder.binding.etkinlikCard.setOnClickListener(view -> {
            gitAyrinti(view,etkinlik);
        });
        holder.binding.imageViewCardEtkinlikFoto.setOnClickListener(view -> {
            gitAyrinti(view,etkinlik);
        });
    }

    private void gitAyrinti(View view, Etkinlik etkinlik) {
        if (sayfaTuru==SayfaTuru.ANA_SAYFA){
            AnaSayfaFragmentDirections.ActionAnaSayfaFragmentToEtkinlikAyrintiFragment gecis=
                    AnaSayfaFragmentDirections.actionAnaSayfaFragmentToEtkinlikAyrintiFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        }else if (sayfaTuru==SayfaTuru.FAVORILER_SAYFASI){
            FavorilerimFragmentDirections.ActionFavorilerimFragmentToEtkinlikAyrintiFragment gecis=
                    FavorilerimFragmentDirections.actionFavorilerimFragmentToEtkinlikAyrintiFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        }else if (sayfaTuru==SayfaTuru.ETKINLIKLERIM_SAYFASI){
            BenimEtkinliklerimFragmentDirections.ActionBenimEtkinliklerimFragmentToBenimEtkinligimAyrintiFragment gecis=
                    BenimEtkinliklerimFragmentDirections.actionBenimEtkinliklerimFragmentToBenimEtkinligimAyrintiFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        } else if (sayfaTuru==SayfaTuru.BASKASININ_PROFILI) {
            BaskasininProfiliFragmentDirections.ActionBaskasininProfiliFragmentToEtkinlikAyrintiFragment gecis=
                    BaskasininProfiliFragmentDirections.actionBaskasininProfiliFragmentToEtkinlikAyrintiFragment(etkinlik);
            Navigation.findNavController(view).navigate(gecis);
        }

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
