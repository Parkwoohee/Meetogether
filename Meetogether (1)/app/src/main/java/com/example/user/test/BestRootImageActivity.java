package com.example.user.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BestRootImageActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ImageView rootImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_root_image);

        rootImage = findViewById(R.id.br_image);

        database = FirebaseDatabase.getInstance();
        final Intent in = getIntent();
        final String userId = in.getStringExtra("bestroot");

        final DatabaseReference myRef = database.getReference("bestroot").child(userId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListData data = dataSnapshot.getValue(ListData.class);
                String stPhoto = dataSnapshot.child("photo").getValue(String.class);

                if (data != null) {
                    Picasso.get().load(stPhoto).fit().centerInside().into(rootImage, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }
}
