package com.abrebo.konumuygulamasi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abrebo.konumuygulamasi.data.models.Etkinlik;
import com.abrebo.konumuygulamasi.data.models.Mesaj;
import com.abrebo.konumuygulamasi.databinding.FragmentMesajBinding;
import com.abrebo.konumuygulamasi.ui.adapters.MesajAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MesajFragment extends Fragment {
    private FragmentMesajBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String gonderen_ad,gonderen_email;
    ArrayList<Mesaj> mesajArrayList;
    MesajAdapter mAdapter;
    Etkinlik etkinlik;
    boolean isAtBottom=false;
    String mesajId,alici_etkinlik,alici_email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        mesajArrayList=new ArrayList<>();
        MesajFragmentArgs bundle=MesajFragmentArgs.fromBundle(getArguments());
        etkinlik=bundle.getEtkinlik();
        gonderen_email=auth.getCurrentUser().getEmail();


        if (etkinlik!=null){
            binding.materialToolbarMesaj.setTitle(etkinlik.getTur()+" - "+etkinlik.getAd());
            getData(etkinlik);
            initRecyclerView();
            alici_etkinlik= etkinlik.getDocID();
            alici_email=etkinlik.getEmail();
        }


        getKullaniciAd(firestore, auth, new AdCallback() {
            @Override
            public void onAdReceived(String adValue) {
                gonderen_ad=adValue;
            }
        });

        binding.buttonMesajGonder.setOnClickListener(view -> {
            mesajGonder();
        });







        return binding.getRoot();
    }

    private void mesajGonder() {
        HashMap<String,Object> gonder=new HashMap<>();
        String mesaj=binding.mesajText.getText().toString();

        gonder.put("alici_etkinlik_id",alici_etkinlik);
        gonder.put("alici_email",alici_email);
        gonder.put("gonderen_ad",gonderen_ad);
        gonder.put("gonderen_email",gonderen_email);
        gonder.put("mesaj",mesaj);
        gonder.put("tarih", FieldValue.serverTimestamp());

        Calendar calendar = Calendar.getInstance();
        int saat = calendar.get(Calendar.HOUR_OF_DAY);
        int dakika = calendar.get(Calendar.MINUTE);
        String zaman = String.format(Locale.getDefault(), "%02d:%02d", saat, dakika);
        gonder.put("saat", zaman);
        firestore.collection("mesaj").add(gonder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                binding.mesajText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.mesajText.setText("");
            }
        });
    }

    private void getData(Etkinlik etkinlik) {
        firestore.collection("mesaj")
                .orderBy("tarih", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.i("Mesaj",error.getMessage());
                        }else {
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getContext(), "Mesaj Yok", Toast.LENGTH_SHORT).show();
                                } else {
                                    mesajArrayList.clear();
                                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                        Map<String, Object> data = documentSnapshot.getData();
                                        String alici_etkinlik_id = (String) data.get("alici_etkinlik_id");
                                        if (etkinlik.getDocID().equals(alici_etkinlik_id))
                                        {
                                            String alici_etkinlik_id2 = (String) data.get("alici_etkinlik_id");
                                            String alici_email = (String) data.get("alici_email");
                                            String gonderen_ad = (String) data.get("gonderen_ad");
                                            String gonderen_email = (String) data.get("gonderen_email");
                                            String mesaj = (String) data.get("mesaj");
                                            String saat = (String) data.get("saat");
                                            mesajId = documentSnapshot.getId();

                                            Mesaj mesaj1=new Mesaj(alici_etkinlik_id2,alici_email,gonderen_ad,gonderen_email,mesaj,saat,mesajId);
                                            mesajArrayList.add(mesaj1);
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    binding.rvMesajKutusu.scrollToPosition(mesajArrayList.size() - 1);

                                }
                            }
                        }
                    }
                });
    }

    private void getKullaniciAd(FirebaseFirestore firestore, FirebaseAuth auth, MesajFragment.AdCallback callback) {
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Hata işleme
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                String ad = document.getString("adSoyad");
                                callback.onAdReceived(ad);
                            }
                        }
                    }
                });
    }
    private void initRecyclerView() {
        mAdapter = new MesajAdapter(getContext(),mesajArrayList,auth.getCurrentUser().getEmail());
        binding.rvMesajKutusu.setAdapter(mAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (isAtBottom) {
                    binding.rvMesajKutusu.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        };
        binding.rvMesajKutusu.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    isAtBottom = true;
                } else {
                    isAtBottom = false;
                }
            }
        });
        mAdapter.registerAdapterDataObserver(observer);
        binding.rvMesajKutusu.setLayoutManager(linearLayoutManager);

    }
    interface AdCallback {
        void onAdReceived(String adValue);
    }
}