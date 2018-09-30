package com.example.user.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.example.user.test.MainActivity.userName;

public class RoomActivity extends AppCompatActivity {

    public static String nicName;
    FirebaseDatabase database;
    //room xml 변수
    ImageView mainImage;
    TextView mainText;
    TextView mainUid;
    TextView peopleNum;
    TextView gender;
    TextView englishLv;
    TextView mainPlace;
    TextView cost;
    Button deleteBtn;
    Button updateBun;
    Button joinBtn;
    LinearLayout btn_liner;

    public static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_liner=findViewById(R.id.btn_liner);
        mainImage = findViewById(R.id.main_image);
        mainText = findViewById(R.id.main_room_title);
        mainUid = findViewById(R.id.main_uid);
        peopleNum = findViewById(R.id.people_num);
        gender = findViewById(R.id.gender);
        englishLv = findViewById(R.id.english_lv);
        mainPlace = findViewById(R.id.main_place);
        cost = findViewById(R.id.cost);

        deleteBtn = findViewById(R.id.delete_btn);
        updateBun = findViewById(R.id.update_btn);
        joinBtn = findViewById(R.id.join_btn);

        database = FirebaseDatabase.getInstance();
        final Intent in = getIntent();
        final String userId = in.getStringExtra("roomUid");

        final DatabaseReference myRef = database.getReference("users").child(userId);



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListData data = dataSnapshot.getValue(ListData.class);
                String stPhoto = dataSnapshot.child("photo").getValue(String.class);

                if (data != null) {
                    nicName = data.getEmail();
                    mainUid.setText(data.getEmail());
                    mainText.setText(data.getRoom());
                    peopleNum.setText(data.getPeopleNum());
                    gender.setText(data.getGender());
                    englishLv.setText(data.getEngLv());
                    mainPlace.setText(data.getMainPlace());
                    cost.setText(data.getCost());

                    if (userName.equals(data.getEmail())) {
                        btn_liner.setVisibility(View.VISIBLE);
                    }else {
                        btn_liner.setVisibility(View.INVISIBLE);
                    }

                    Picasso.get().load(stPhoto).fit().centerInside().into(mainImage, new Callback.EmptyCallback() {
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

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), ChatActivity.class);
                in.putExtra("roomUid", userId);
                getApplicationContext().startActivity(in);
                /*Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);*/
            }
        });

        updateBun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = userName;
                String text = mainUid.getText().toString();

                if (name.equals(text)) {
                    myRef.removeValue();
                    Intent intent = new Intent(getApplicationContext(), AddRoomActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(RoomActivity.this, "다른 사람의 게시글입니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = userName;
                String text = mainUid.getText().toString();

                if (name.equals(text)) {
                    myRef.removeValue();
                    Toast.makeText(getApplicationContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(RoomActivity.this, "다른 사람의 게시글입니다.",
                            Toast.LENGTH_SHORT).show();
                }
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
}
