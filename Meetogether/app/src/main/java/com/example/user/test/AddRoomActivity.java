package com.example.user.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import static com.example.user.test.MainActivity.userName;

public class AddRoomActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;

    EditText roomTitle;
    EditText roomPeopleNum;
    //EditText roomGender;
    EditText roomEnglishLv;
    EditText roomMainPlace;
    EditText roomCost;
    Button addRoomBtn, addRoute;


    RadioGroup roomGender;
    RadioButton radio_m, radio_wm;

    String TAG = getClass().getSimpleName();
    //ImageView ivUser;
    ImageView roomImage;
    Bitmap bitmap;
    String stUid;
    String stEmail;
    private StorageReference mStorageRef;

    List<String> listview_items;
    ArrayAdapter<String> listview_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roomImage = findViewById(R.id.room_image);
        TextView addPic = findViewById(R.id.add_pic);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SharedPreferences sharedPreferences = this.getSharedPreferences("email", Context.MODE_PRIVATE);
        stUid = sharedPreferences.getString("uid", "");
        stEmail = sharedPreferences.getString("email", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        roomTitle = findViewById(R.id.room_title);
        roomPeopleNum = findViewById(R.id.room_people_num);
        roomEnglishLv = findViewById(R.id.room_english_lv);
        roomMainPlace = findViewById(R.id.room_main_place);
        roomCost = findViewById(R.id.room_cost);

        roomGender = findViewById(R.id.room_gender);
        radio_m = findViewById(R.id.radio_m);
        radio_wm = findViewById(R.id.radio_wm);

        addRoomBtn = findViewById(R.id.add_room_btn);
        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //원래코드
                byte[] data;
                try{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();

                    //myRef.child("users").child(stUid).removeValue();
                    uploadImage();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (NullPointerException e){
                    //data = new byte[]{0x00};
                    Toast.makeText(AddRoomActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                }

                /*//테스트용: 이미지 없이 올라가짐
                myRef.child("users").child(stUid).removeValue();
                uploadImage();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();*/
            }
        });
        myRef.child("users").child(stUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                String stPhoto = dataSnapshot.child("photo").getValue(String.class);

                if (TextUtils.isEmpty(stPhoto)) {
                } else {
                    Picasso.get().load(stPhoto).fit().centerInside().into(roomImage, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImage() {

        StorageReference mountainsRef = mStorageRef.child("users").child(stUid + ".jpg");
        byte[] data;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
        }
        catch (NullPointerException e){
            data = new byte[]{0x00};
        }
/*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
*/
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String photoUri = String.valueOf(downloadUrl);
                Log.d("url", photoUri);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");


                String name = null;
                name = user.getDisplayName();
                String email = null;
                email= user.getEmail();

                String newRoomTitle = roomTitle.getText().toString();
                String peopleNum = roomPeopleNum.getText().toString();
                //String peopleNum = "4";
                //String gender = roomGender.getText().toString();
                String gender="NO SELECT";
                String engLv = roomEnglishLv.getText().toString();
                String mainPlace = roomMainPlace.getText().toString();
                String cost = roomCost.getText().toString();

                if(radio_m.isChecked()){
                    gender="MALE";
                }else if(radio_wm.isChecked()){
                    gender="FEMALE";
                }
                //dbRef.child("room").push().setValue(newRoomTitle);

                Hashtable<String, String> profile = new Hashtable<>();

                if (email != null) {
                    profile.put("email", userName);
                    profile.put("photo", photoUri);
                    profile.put("room", newRoomTitle);
                    profile.put("key", stUid);
                    profile.put("peopleNum", peopleNum);
                    profile.put("gender", gender);
                    profile.put("engLv", engLv);
                    profile.put("mainPlace", mainPlace);
                    profile.put("cost", cost);

                    myRef.child(stUid).setValue(profile);

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue().toString();
                            Log.d("Profile", s);
                            if (dataSnapshot != null) {
                                Toast.makeText(getApplicationContext(), "사진 업로드가 잘 됐습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    profile.put("email", email);
                    profile.put("photo", photoUri);
                    profile.put("room", newRoomTitle);
                    profile.put("key", stUid);
                    profile.put("peopleNum", peopleNum);
                    profile.put("gender", gender);
                    profile.put("engLv", engLv);
                    profile.put("mainPlace", mainPlace);
                    profile.put("cost", cost);


                    myRef.child(stUid).setValue(profile);

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue().toString();
                            Log.d("Profile", s);
                            if (dataSnapshot != null) {
                                Toast.makeText(getApplicationContext(), "게시글 업로드 완료", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            Uri image = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                roomImage.setImageBitmap(bitmap);
                //uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(requestCode == RESULT_CANCELED){
                finish();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
        }
    }
}

/*
package com.example.user.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import static com.example.user.test.MainActivity.userName;

public class AddRoomActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;

    EditText roomTitle;
    EditText roomPeopleNum;
    //EditText roomGender;
    EditText roomEnglishLv;
    EditText roomMainPlace;
    EditText roomCost;
    Button addRoomBtn, addRoute;


    RadioGroup roomGender;
    RadioButton radio_m, radio_wm;

    String TAG = getClass().getSimpleName();
    //ImageView ivUser;
    ImageView roomImage;
    Bitmap bitmap;
    public static String stUid;
    String stEmail;
    private StorageReference mStorageRef;
    int count;

    List<String> listview_items;
    ArrayAdapter<String> listview_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roomImage = findViewById(R.id.room_image);
        TextView addPic = findViewById(R.id.add_pic);
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SharedPreferences sharedPreferences = this.getSharedPreferences("email", Context.MODE_PRIVATE);
        stUid = sharedPreferences.getString("uid", "");
        stEmail = sharedPreferences.getString("email", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        roomTitle = findViewById(R.id.room_title);
        //roomPeopleNum = findViewById(R.id.room_people_num);
        roomEnglishLv = findViewById(R.id.room_english_lv);
        roomMainPlace = findViewById(R.id.room_main_place);
        roomCost = findViewById(R.id.room_cost);

        roomGender = findViewById(R.id.room_gender);
        radio_m = findViewById(R.id.radio_m);
        radio_wm = findViewById(R.id.radio_wm);

        addRoomBtn = findViewById(R.id.add_room_btn);
        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    Toast.makeText(AddRoomActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    myRef.child("users").child(stUid).removeValue();
                    uploadImage();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
*/
/*
        addRoute = findViewById(R.id.add_route_btn);
        addRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTmapActivity.class);
                startActivity(intent);
            }
        });
*//*

        myRef.child("users").child(stUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                String stPhoto = dataSnapshot.child("photo").getValue(String.class);

                if (TextUtils.isEmpty(stPhoto)) {
                    // pbLogin.setVisibility(View.GONE);

                } else {

                    // pbLogin.setVisibility(View.VISIBLE);
                    Picasso.get().load(stPhoto).fit().centerInside().into(roomImage, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                            Log.d(TAG, "SUCCESS");
                            // pbLogin.setVisibility(View.GONE);
                        }
                    });
                }
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
                }
                return;
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                return;
            }
        }

        //스피너

        */
/*
         * 스피너에서 선택한 항목들을 보여줄 리스트뷰
         *//*

        ListView listview = (ListView)findViewById(R.id.listView);

        //데이터를 저장하게 되는 리스트
        listview_items = new ArrayList<>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        listview_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listview_items);

        //리스트뷰의 어댑터 지정
        listview.setAdapter(listview_adapter);


        */
/*
         * 스피너 관련
         *//*

        Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();

                if ( str != "")
                    listview_adapter.add( str );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //데이터를 저장하게 되는 리스트
        List<String> spinner_items = new ArrayList<>();

        //스피너와 리스트를 연결하기 위해 사용되는 어댑터
        ArrayAdapter<String> spinner_adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinner_items);

        for(int i=1; i<11; i++) {
            spinner_items.add(String.valueOf(i));
        }

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너의 어댑터 지정
        spinner.setAdapter(spinner_adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImage() {

        StorageReference mountainsRef = mStorageRef.child("users").child(stUid + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String photoUri = String.valueOf(downloadUrl);
                Log.d("url", photoUri);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                String email = null;
                email= user.getEmail();

                String newRoomTitle = roomTitle.getText().toString();
                //String peopleNum = roomPeopleNum.getText().toString();
                String peopleNum = "4";
                //String gender = roomGender.getText().toString();
                String gender="NO SELECT";
                String engLv = roomEnglishLv.getText().toString();
                String mainPlace = roomMainPlace.getText().toString();
                String cost = roomCost.getText().toString();

                if(radio_m.isChecked()){
                    gender="MALE";
                }else if(radio_wm.isChecked()){
                    gender="FEMALE";
                }
                //dbRef.child("room").push().setValue(newRoomTitle);

                Hashtable<String, String> profile = new Hashtable<>();

                if (email != null) {
                    profile.put("email", userName);
                    profile.put("photo", photoUri);
                    profile.put("room", newRoomTitle);
                    profile.put("key", stUid);
                    profile.put("peopleNum", peopleNum);
                    profile.put("gender", gender);
                    profile.put("engLv", engLv);
                    profile.put("mainPlace", mainPlace);
                    profile.put("cost", cost);

                    myRef.child(stUid).setValue(profile);

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue().toString();
                            Log.d("Profile", s);
                            if (dataSnapshot != null) {
                                Toast.makeText(getApplicationContext(), "사진 업로드가 잘 됐습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    profile.put("email", email);
                    profile.put("photo", photoUri);
                    profile.put("room", newRoomTitle);
                    profile.put("key", stUid);
                    profile.put("peopleNum", peopleNum);
                    profile.put("gender", gender);
                    profile.put("engLv", engLv);
                    profile.put("mainPlace", mainPlace);
                    profile.put("cost", cost);


                    myRef.child(stUid).setValue(profile);

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.getValue().toString();
                            Log.d("Profile", s);
                            if (dataSnapshot != null) {
                                Toast.makeText(getApplicationContext(), "사진 업로드가 잘 됐습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //사진없으면에러
        Uri image = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
            roomImage.setImageBitmap(bitmap);
            //uploadImage();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(requestCode == RESULT_CANCELED){
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        //Intent intent = getIntent();
        */
/*if(count > 0) {
            byte[] arr = getIntent().getByteArrayExtra("image");
            Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            ImageView BigImage = findViewById(R.id.imageView2);
            BigImage.setImageBitmap(image);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), AddRoomActivity.class);
            startActivity(intent);
        }*//*

    }
}
*/
