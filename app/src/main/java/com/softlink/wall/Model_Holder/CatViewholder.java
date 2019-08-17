package com.softlink.wall.Model_Holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softlink.wall.R;

public class CatViewholder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView textView;
    public CatViewholder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.cat_image_view);
        textView = itemView.findViewById(R.id.cat_name_view);
    }
}
