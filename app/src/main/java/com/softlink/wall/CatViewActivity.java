package com.softlink.wall;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.softlink.wall.Model_Holder.Link;
import com.softlink.wall.Model_Holder.LinkViewHolder;

import java.util.HashMap;

public class CatViewActivity extends AppCompatActivity {

    private DatabaseReference DataRaf, FavRaf, LikeRaf;
    private RecyclerView recyclerView;

    FirebaseRecyclerOptions<Link> options;
    FirebaseRecyclerAdapter<Link, LinkViewHolder> adapter;
    String key;


    FirebaseAuth mAuth;
    String currentUser;
    Boolean likechecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_view);
        recyclerView = findViewById(R.id.cat_full_recycler_view);
        recyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        key = getIntent().getExtras().get("name").toString();
        DataRaf = FirebaseDatabase.getInstance().getReference().child("catagory");
        FavRaf = FirebaseDatabase.getInstance().getReference().child("favorite");
        LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");

        options = new FirebaseRecyclerOptions.Builder<Link>()
                .setQuery(DataRaf.child(key), Link.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Link, LinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LinkViewHolder holder, int position, @NonNull final Link model)
            {
                final String k = getRef(position).getKey();

                Glide.with(getApplicationContext()).load(model.getLink()).placeholder(R.drawable.ic_sync_black_24dp).into(holder.imageView);
                holder.getAllLikes(k);
                holder.LikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likechecker = true;
                        LikeRaf.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likechecker.equals(true))
                                {
                                    if (dataSnapshot.child(k).hasChild(currentUser))
                                    {
                                        LikeRaf.child(k).child(currentUser).removeValue();
                                        likechecker = false;
                                        FavRaf.child(currentUser).child(k).removeValue();
                                    }
                                    else
                                    {
                                        LikeRaf.child(k).child(currentUser).setValue(true);
                                        likechecker = false;
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("key", k);
                                        map.put("link", model.getLink());
                                        FavRaf.child(currentUser).child(k).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        Intent intent = new Intent(CatViewActivity.this, FullscreenActivity2.class);
                        intent.putExtra("link", model.getLink());
                        intent.putExtra("key2",k);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.imageview, viewGroup, false);

                return new LinkViewHolder(view);

            }
        };

        GridLayoutManager LayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(LayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
