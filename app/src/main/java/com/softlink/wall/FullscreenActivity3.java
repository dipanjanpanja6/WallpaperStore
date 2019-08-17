package com.softlink.wall;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pd.chocobar.ChocoBar;
import com.softlink.wall.Account.LogInActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class FullscreenActivity3 extends AppCompatActivity {
    private ImageView imageView,LikeView;
    TextView AllLikes;
    private LinearLayout Set,dwnld,fav,shere;
    String link;
    String key;
    int countLikes;
    FirebaseAuth mAuth;
    String currentUser;
    Boolean likechecker = false;
    DatabaseReference LikeRaf,FavRaf;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        link=getIntent().getExtras().get("link").toString();
        key=getIntent().getExtras().get("key").toString();
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();


        if (User == null) {
            Intent intent = new Intent(FullscreenActivity3.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {

            imageView = findViewById(R.id.full_image);
            Picasso.get().load(link).into(imageView);

            //Glide.with(this).load(link).into(imageView);

            LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");
            Set = findViewById(R.id.set_wallpaper);
            dwnld = findViewById(R.id.download_wallpaper);
            fav = findViewById(R.id.favWall);
            shere = findViewById(R.id.share_wallpaper);
            FavRaf = FirebaseDatabase.getInstance().getReference().child("favorite");
            AllLikes=findViewById(R.id.all_like0);
            mAuth = FirebaseAuth.getInstance();
            LikeView = findViewById(R.id.like_view);
            currentUser = mAuth.getCurrentUser().getUid();

            shere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FullscreenActivity3.this, "Getting things ready....", Toast.LENGTH_SHORT).show();

                    shareimage();
                }
            });

            Set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FullscreenActivity3.this, "Getting things ready....", Toast.LENGTH_SHORT).show();
                    setwallpaper();
                }
            });

            dwnld.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            //show popup to grant permission
                            requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                        } else {
                            //permission already granted, save image
                            saveImage();
                        }
                    } else {
                        Toast.makeText(FullscreenActivity3.this, "Getting things ready....", Toast.LENGTH_SHORT).show();
                        //System os is < marshmallow, save image
                        saveImage();
                    }
                }
            });

            getAllLikes(key);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likechecker = true;
                    getlike(key);


                    LikeRaf.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (likechecker.equals(true))
                            {
                                if (dataSnapshot.child(key).hasChild(currentUser)) {
                                    LikeRaf.child(key).child(currentUser).removeValue();
                                    likechecker = false;
                                    FavRaf.child(currentUser).child(key).removeValue();
                                } else
                                 {
                                    LikeRaf.child(key).child(currentUser).setValue(true);
                                    likechecker = false;
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("key", key);
                                    map.put("link", link);
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


        }
    }

    public void getAllLikes(final String k) {
        DatabaseReference LikeRaf = FirebaseDatabase.getInstance().getReference().child("like");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LikeRaf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(k).hasChild(currentUser))
                {
                    countLikes = (int)dataSnapshot.child(k).getChildrenCount();
                    LikeView.setBackgroundResource(R.drawable.ic_favorite);
                    AllLikes.setText(Integer.toString(countLikes));
                }
                else
                {
                    countLikes = (int)dataSnapshot.child(k).getChildrenCount();
                    LikeView.setBackgroundResource(R.drawable.ic_favorite_24dp);
                    AllLikes.setText(Integer.toString(countLikes));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getlike(final String k) {

        LikeRaf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(k).hasChild(currentUser)) {
                    LikeView.setBackgroundResource(R.drawable.ic_favorite);
                    countLikes = (int)dataSnapshot.child(k).getChildrenCount();
                    AllLikes.setText(Integer.toString(countLikes));
                } else {
                    LikeView.setBackgroundResource(R.drawable.ic_favorite_24dp);
                    countLikes = (int)dataSnapshot.child(k).getChildrenCount();
                    AllLikes.setText(Integer.toString(countLikes));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setwallpaper () {


            Picasso.get().load(link).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    WallpaperManager myWallpaperManager;
                    myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        myWallpaperManager.setBitmap(bitmap);
                        ChocoBar.builder().setActivity(FullscreenActivity3.this)
                                .setText("Wallpaper Set")
                                .setDuration(ChocoBar.LENGTH_SHORT)
                                .green()
                                .show();
                    } catch (IOException e) {
                        ChocoBar.builder().setActivity(FullscreenActivity3.this)
                                .setText("Error setting wallpaper")
                                .setDuration(ChocoBar.LENGTH_SHORT)
                                .red()
                                .show();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Download failed")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .red()
                            .show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Downloading...")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .orange()
                            .show();

                }
            });

        }

        private void shareimage () {
            Picasso.get().load(link).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File filepath = Environment.getExternalStorageDirectory();
                    File dir = new File(filepath.getAbsolutePath() + "/Wallpaper Store/");
                    dir.mkdirs();
                    File file = new File(dir, "WallpaperStoreShare" + System.currentTimeMillis() + ".png");
                    try {
                        OutputStream output;
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/png");
                        output = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                        output.flush();
                        output.close();
                        Uri uri = FileProvider.getUriForFile(FullscreenActivity3.this, "com.codepath.fileprovider", file);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Wallpaper Store Image share"));

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Preparing failed")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .red()
                            .show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Prepared for Share")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .orange()
                            .show();

                }
            });

        }


        private void saveImage () {
            Picasso.get().load(link).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(System.currentTimeMillis());
                    File path = Environment.getExternalStorageDirectory();
                    File dir = new File(path + "/Wallpaper Store/");
                    dir.mkdirs();
                    String imageName = timeStamp + ".jpg";
                    File file = new File(dir, imageName);
                    OutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        ChocoBar.builder().setActivity(FullscreenActivity3.this)
                                .setText(imageName + " saved to" + dir)
                                .setDuration(ChocoBar.LENGTH_SHORT)
                                .green()
                                .show();
                    } catch (Exception e) {
                        ChocoBar.builder().setActivity(FullscreenActivity3.this)
                                .setText(e.getMessage())
                                .setDuration(ChocoBar.LENGTH_SHORT).red().show();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Download failed")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .red()
                            .show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    ChocoBar.builder().setActivity(FullscreenActivity3.this)
                            .setText("Downloading")
                            .setDuration(ChocoBar.LENGTH_SHORT)
                            .orange()
                            .show();
                }
            });

        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            switch (requestCode) {
                case WRITE_EXTERNAL_STORAGE_CODE: {
                    //if request code is cancelled the result arrays are empty
                    if (grantResults.length > 0 && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {
                        //permission is granted, save image
                        saveImage();
                    } else {
                        //permission denied
                        ChocoBar.builder().setActivity(FullscreenActivity3.this)
                                .setText("enable permission to save image")
                                .setDuration(ChocoBar.LENGTH_SHORT)
                                .orange()
                                .show();
                    }
                }
            }
        }




}
