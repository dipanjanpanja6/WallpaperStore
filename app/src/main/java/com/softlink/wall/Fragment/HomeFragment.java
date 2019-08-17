package com.softlink.wall.Fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pd.chocobar.ChocoBar;
import com.softlink.wall.Account.LogInActivity;
import com.softlink.wall.FullscreenActivity2;
import com.softlink.wall.FullscreenActivity3;
import com.softlink.wall.Model_Holder.ImageViewHolder;
import com.softlink.wall.Model_Holder.Images;
import com.softlink.wall.R;

import java.util.HashMap;


public class HomeFragment extends Fragment {
FloatingActionButton floatingActionButton;
    public FirebaseAuth mAuth;


    /////////////////private InterstitialAd interstitial;
    private DatabaseReference LikeRaf, FavRaf;

    FirebaseRecyclerOptions<Images> options;
    FirebaseRecyclerAdapter<Images, ImageViewHolder> adapter, adapter2;

    DatabaseReference Data;
    RecyclerView recyclerView,recyclerViewRecent;


    String currentUser;

    Boolean likechecker = false;
    public HomeFragment() {


        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.fragment_home, container, false);




        mAuth = FirebaseAuth.getInstance();
        Data = FirebaseDatabase.getInstance().getReference().child("wallpaper");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewRecent = view.findViewById(R.id.recyclerViewRecent);

        LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");
        FavRaf = FirebaseDatabase.getInstance().getReference().child("favorite");
        recyclerView.setHasFixedSize(true);
        recyclerViewRecent.setHasFixedSize(true);

        floatingActionButton= view.findViewById(R.id.rate_app);


        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();


        if (User == null)
        {
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
           /** {
                MobileAds.initialize(getContext(), getString(R.string.ADMOB_APP_ID));
                AdRequest adIRequest = new AdRequest.Builder().build();
                interstitial = new InterstitialAd(getContext());
                interstitial.setAdUnitId(getString(R.string.Interstitial));
                interstitial.loadAd(adIRequest);
                interstitial.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        // Call displayInterstitial() function when the Ad loads
                        displayInterstitial();
                    }
                });
            } */

            currentUser = mAuth.getCurrentUser().getUid();

            options = new FirebaseRecyclerOptions.Builder<Images>()
                    .setQuery(Data, Images.class).build();

            adapter2 = new FirebaseRecyclerAdapter<Images, ImageViewHolder>(options) {
                @Override
                public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i, @NonNull final Images images) {
                    Glide.with(getContext()).load(images.getLink()).placeholder(R.drawable.ic_sync_black_24dp).into(imageViewHolder.RecentIamge);



                    final String key = getRef(i).getKey().toString();
                    imageViewHolder.getAllLikes(key);
                    imageViewHolder.LikeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likechecker = true;
                            LikeRaf.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (likechecker.equals(true)) {
                                        if (dataSnapshot.child(key).hasChild(currentUser)) {
                                            LikeRaf.child(key).child(currentUser).removeValue();
                                            likechecker = false;
                                            FavRaf.child(currentUser).child(key).removeValue();
                                        } else {
                                            LikeRaf.child(key).child(currentUser).setValue(true);
                                            likechecker = false;
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("key", key);
                                            map.put("link", images.getLink());
                                            FavRaf.child(currentUser).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(getActivity(), FullscreenActivity2.class);
                           intent.putExtra("link",images.getLink());
                            intent.putExtra("key2",key);
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.recent_image, parent, false);

                    return new ImageViewHolder(view);
                }
            };


            adapter = new FirebaseRecyclerAdapter<Images, ImageViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull final Images model) {
                    final String key = getRef(position).getKey().toString();
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(getContext()).load(model.getLink()).placeholder(R.drawable.ic_sync_black_24dp).into(holder.imageView);




                    holder.getAllLikes(key);
                    holder.LikeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likechecker = true;
                            LikeRaf.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (likechecker.equals(true)) {
                                        if (dataSnapshot.child(key).hasChild(currentUser)) {
                                            LikeRaf.child(key).child(currentUser).removeValue();
                                            likechecker = false;
                                            FavRaf.child(currentUser).child(key).removeValue();
                                        } else {
                                            LikeRaf.child(key).child(currentUser).setValue(true);
                                            likechecker = false;
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("key", key);
                                            map.put("link", model.getLink());
                                            FavRaf.child(currentUser).child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(getActivity(), FullscreenActivity3.class);
                            intent.putExtra("link", model.getLink());
                            intent.putExtra("key", key);
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.imageview, viewGroup, false);


                    return new ImageViewHolder(view);
                }
            };

            if (adapter == null) {

                ChocoBar.builder().setText("Wait...").orange().show();
            }


            GridLayoutManager LayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(LayoutManager);

            LinearLayoutManager LayoutManagerRecent = new LinearLayoutManager(getContext());
            LayoutManagerRecent.setReverseLayout(true);
            LayoutManagerRecent.setOrientation(RecyclerView.HORIZONTAL);
            LayoutManagerRecent.setStackFromEnd(true);
            recyclerViewRecent.setLayoutManager(LayoutManagerRecent);

            adapter.startListening();
            adapter2.startListening();
            recyclerView.setAdapter(adapter);
            recyclerViewRecent.setAdapter(adapter2);


floatingActionButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }

    }
});
        }


        return view;
    }

   /** private void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();

        }
        else {
            Toast.makeText(getContext(), "Network Problem", Toast.LENGTH_SHORT).show();
        }
    }*/

}

