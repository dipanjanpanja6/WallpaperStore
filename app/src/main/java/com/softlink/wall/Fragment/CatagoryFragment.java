package com.softlink.wall.Fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softlink.wall.CatViewActivity;
import com.softlink.wall.Model_Holder.CatViewholder;
import com.softlink.wall.Model_Holder.GatItem;
import com.softlink.wall.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatagoryFragment extends Fragment {

    private DatabaseReference item;
    FirebaseRecyclerOptions<GatItem> options;
    FirebaseRecyclerAdapter<GatItem, CatViewholder> adapter;

    private RecyclerView recyclerView;
    public CatagoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_catagory, container, false);

        item = FirebaseDatabase.getInstance().getReference().child("item");
        recyclerView = view.findViewById(R.id.cat_list);
        recyclerView.setHasFixedSize(true);

        options = new FirebaseRecyclerOptions.Builder<GatItem>()
                .setQuery(item, GatItem.class).build();

        adapter = new FirebaseRecyclerAdapter<GatItem, CatViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CatViewholder holder, int position, @NonNull final GatItem model) {
                Picasso.get().load(model.getLink()).into(holder.imageView);
                holder.textView.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CatViewActivity.class);
                        intent.putExtra("name", model.getName());
                        startActivity(intent);
                    }
                });

            }


            @NonNull
            @Override
            public CatViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.cat_view, viewGroup, false);


                return new CatViewholder(view);
            }
        };
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
        LayoutManager.setReverseLayout(true);
        LayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(LayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        return view;
    }

}
