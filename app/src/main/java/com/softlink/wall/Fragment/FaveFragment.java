package com.softlink.wall.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softlink.wall.CatViewActivity;
import com.softlink.wall.FullscreenActivity2;
import com.softlink.wall.Model_Holder.Fav;
import com.softlink.wall.Model_Holder.ImageViewHolder;
import com.softlink.wall.R;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaveFragment extends Fragment {

    private RecyclerView allFav;

    FirebaseRecyclerAdapter<Fav, ImageViewHolder> adapter;
    FirebaseRecyclerOptions<Fav> options;

    DatabaseReference FavRaf, LikeRaf;
    FirebaseAuth mAuth;
    String currentUser;
    Boolean likechecker = false;
    public FaveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // ((AppCompatActivity) getContext()).getSupportActionBar().hide();
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_fave, container, false);
        allFav = root.findViewById(R.id.all_fav);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser().getUid();

        FavRaf = FirebaseDatabase.getInstance().getReference().child("favorite");
        LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");

        options = new FirebaseRecyclerOptions.Builder<Fav>()
                .setQuery(FavRaf.child(currentUser), Fav.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Fav, ImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, final int i, @NonNull final Fav fav) {
                final String key = getRef(i).getKey();

                imageViewHolder.getAllLikes(key);
                Glide.with(getContext()).load(fav.getLink()).placeholder(R.drawable.ic_sync_black_24dp).into(imageViewHolder.imageView);

                imageViewHolder.LikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likechecker = true;
                        LikeRaf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likechecker.equals(true))
                                {
                                    if (dataSnapshot.child(key).hasChild(currentUser))
                                    {
                                        LikeRaf.child(key).child(currentUser).removeValue();
                                        likechecker = false;
                                        FavRaf.child(currentUser).child(key).removeValue();
                                    }
                                    else
                                    {
                                        LikeRaf.child(key).child(currentUser).setValue(true);
                                        likechecker = false;
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("key", key);
                                        map.put("link", fav.getLink());
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
                        intent.putExtra("link", fav.getLink());
                        intent.putExtra("key2", key);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.imageview, parent, false);

                return new ImageViewHolder(view);
            }
        };
        GridLayoutManager LayoutManager = new GridLayoutManager(getContext(), 2);
        allFav.setLayoutManager(LayoutManager);
        adapter.startListening();
        allFav.setAdapter(adapter);
        return root;
    }

}
