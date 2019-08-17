package com.softlink.wall.Model_Holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.softlink.wall.R;

public class LinkViewHolder extends RecyclerView.ViewHolder {

    public ImageView LikeView;
    public TextView AllLikes;
    public LinearLayout LikeLayout;
    int countLikes;
    public ImageView imageView;
    public LinkViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.imageView);
        LikeView = itemView.findViewById(R.id.like_view);
        AllLikes = itemView.findViewById(R.id.all_like);
        LikeLayout = itemView.findViewById(R.id.like_layout);
    }
    public void getAllLikes(final String key) {
        DatabaseReference LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LikeRaf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(key).hasChild(currentUser))
                {
                    countLikes = (int)dataSnapshot.child(key).getChildrenCount();
                    LikeView.setBackgroundResource(R.drawable.ic_favorite);
                    AllLikes.setText(Integer.toString(countLikes));
                }
                else
                {
                    countLikes = (int)dataSnapshot.child(key).getChildrenCount();
                    LikeView.setBackgroundResource(R.drawable.ic_favorite_24dp);
                    AllLikes.setText(Integer.toString(countLikes));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
