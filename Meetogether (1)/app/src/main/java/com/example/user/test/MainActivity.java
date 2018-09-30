package com.example.user.test;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.test.fragments.CustomerFragment;
import com.example.user.test.fragments.HomeFragment;
import com.example.user.test.fragments.InfoFragment;
import com.example.user.test.fragments.LikeRootFragment;
import com.example.user.test.fragments.NoticeFragment;
import com.example.user.test.fragments.QnaFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener{

    public static String userName;
    public static String userEmail;

    FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    //방타이틀
    TextView title;

    Button logOutBtn;
    TextView userId;

    String name;
    String memberName;
    String roomNum;
    boolean check = false;
    public static String member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        // 로그인 작업의 onCreate 메소드에서 FirebaseAuth 개체의 공유 인스턴스를 가져옵니다.
        mAuth = FirebaseAuth.getInstance();

        //로그인정보 가져와 이름/이메일 따오기
        user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference();
        if (user != null) {//현재 로그인중이라면
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    roomNum = dataSnapshot.child("roomNumber").getValue().toString();
                    for (int i = 1; i < Integer.parseInt(roomNum) + 1; i++) {
                        member = dataSnapshot.child("member").child(String.valueOf(i)).child("email").getValue().toString();
                        userName = dataSnapshot.child("member").child(String.valueOf(i)).child("name").getValue().toString();
                        //memberId.add(member);
                        Log.v("알림!!", member);
                        Log.v("알림!!", user.getEmail());
                        if ((user.getEmail()).equals(member)) {
                            userEmail=user.getEmail();
                            check = true;
                            break;
                        }
                        Log.v("알림??", String.valueOf(check));
                        // }
                    }
                    if (check) {
                        Log.v("알림??", String.valueOf(check));
                        View header = ((NavigationView)findViewById(R.id.navView)).getHeaderView(0);
                        ((TextView) header.findViewById(R.id.Name)).setText(userName);
                    }else {
                    }
                    //memberName = dataSnapshot.getValue().toString();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.NavFrag, new HomeFragment());
        ft.commit();

        navigationView.setCheckedItem(R.id.nav_home);

        //방타이틀 가져오기
        /*dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                title.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new HomeFragment());
            ft.commit();
        }else if (id == R.id.nav_root) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new LikeRootFragment());
            ft.commit();
        }
        else if (id == R.id.nav_notice) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new NoticeFragment());
            ft.commit();
        }
        else if (id == R.id.nav_customer) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new CustomerFragment());
            ft.commit();
        }
        else if (id == R.id.nav_info) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new InfoFragment());
            ft.commit();
        }
        else if (id == R.id.nav_qna) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.NavFrag, new QnaFragment());
            ft.commit();
        }
        else if (id == R.id.nav_logout) {
            Log.v("알림", "구글 LOGOUT");

            new AlertDialog.Builder(this, 0)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            signOut();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void signOut() {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mAuth.signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.v("알림", "로그아웃 성공");
                                setResult(1);
                            } else {
                                setResult(0);
                            }
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onConnectionSuspended(int i) {
                Log.v("알림", "Google API Client Connection Suspended");
                setResult(-1);
                finish();
            }
        });
    }

    String Email(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getEmail();
    }
}
/*
package com.example.user.test;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.test.fragments.CustomerFragment;
import com.example.user.test.fragments.InfoFragment;
import com.example.user.test.fragments.LikeRootFragment;
import com.example.user.test.fragments.NoticeFragment;
import com.example.user.test.fragments.QnaFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener{

    public static String userName;
    public static String member;

    FirebaseUser user;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    //방타이틀
    TextView title;

    ViewPager vp;
    TabLayout mTab;
    FloatingActionButton fab;
    private int[] tabIcons = {R.drawable.menu1,R.drawable.menu2,R.drawable.menu3};
    //
    String name;
    String memberName;
    String roomNum;
    boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = findViewById(R.id.viewpager);
        mTab = findViewById(R.id.tabs);
        fab = findViewById(R.id.fab);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);
        mTab.setupWithViewPager(vp);
        setupTabIcons();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        // 로그인 작업의 onCreate 메소드에서 FirebaseAuth 개체의 공유 인스턴스를 가져옵니다.
        mAuth = FirebaseAuth.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                */
/*
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("users");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ListData data = dataSnapshot.getValue(ListData.class);
                        //getEmail = data.getEmail();
                        //name = user.getDisplayName();
                        //Log.v("알림", getEmail);
                        //Log.v("알림", name);


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });

                /*if (name == getEmail) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
                    alt_bld.setMessage("이미 작성된 글이있습니다. 수정하시겠습니까?").setCancelable(false)
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 네 클릭
                                            Intent intent = new Intent(getApplicationContext(), AddRoomActivity.class);
                                            startActivity(intent);
                                        }
                                    }).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 아니오 클릭. dialog 닫기.
                                    dialog.cancel();
                                }
                            });
                }else {//
                Intent intent = new Intent(getApplicationContext(), AddRoomActivity.class);
                startActivity(intent);
                *//*


                Intent intent = new Intent(getApplicationContext(), AddTmapActivity.class);
                startActivity(intent);
                finish();
            }

        });

        //로그인정보 가져와 이름/이메일 따오기
        user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference();
        if (user != null) {//현재 로그인중이라면
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    roomNum = dataSnapshot.child("roomNumber").getValue().toString();
                    for (int i = 1; i < Integer.parseInt(roomNum) + 1; i++) {
                        member = dataSnapshot.child("member").child(String.valueOf(i)).child("email").getValue().toString();
                        userName = dataSnapshot.child("member").child(String.valueOf(i)).child("name").getValue().toString();
                        //memberId.add(member);
                        Log.v("알림!!", member);
                        Log.v("알림!!", user.getEmail());
                        if ((user.getEmail()).equals(member)) {
                            check = true;
                            break;
                        }
                        Log.v("알림??", String.valueOf(check));
                        // }
                    }
                    if (check) {
                        Log.v("알림??", String.valueOf(check));
                        View header = ((NavigationView)findViewById(R.id.navView)).getHeaderView(0);
                        ((TextView) header.findViewById(R.id.Name)).setText(userName);
                    }else {
                    }
                    //memberName = dataSnapshot.getValue().toString();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        //방타이틀 가져오기
        */
/*dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                title.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*//*

    }
    private void setupTabIcons() {
        mTab.getTabAt(0).setIcon(tabIcons[0]);
        mTab.getTabAt(1).setIcon(tabIcons[1]);
        mTab.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        fab.setVisibility(View.INVISIBLE);

        int id = item.getItemId();
        if (id == R.id.nav_root) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_frag, new LikeRootFragment());
            ft.commit();
        }
        else if (id == R.id.nav_notice) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_frag, new NoticeFragment());
            ft.commit();
        }
        else if (id == R.id.nav_customer) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_frag, new CustomerFragment());
            ft.commit();
        }
        else if (id == R.id.nav_info) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_frag, new InfoFragment());
            ft.commit();
        }
        else if (id == R.id.nav_qna) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toolbar_frag, new QnaFragment());
            ft.commit();
        }
        else if (id == R.id.nav_logout) {
            Log.v("알림", "구글 LOGOUT");

            new AlertDialog.Builder(this, 0)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            signOut();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MenuFrag1();
                case 1:
                    return new MenuFrag2();
                case 2:
                    return new MenuFrag3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Menu1";
                case 1:
                    return "Menu2";
                case 2:
                    return "Menu3";
                default:
                    return null;
            }
        }

    }
    public void signOut() {
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mAuth.signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.v("알림", "로그아웃 성공");
                                setResult(1);
                            } else {
                                setResult(0);
                            }
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onConnectionSuspended(int i) {
                Log.v("알림", "Google API Client Connection Suspended");
                setResult(-1);
                finish();
            }
        });
    }
    void reflash(){
        pagerAdapter p = new pagerAdapter(getSupportFragmentManager());
        p.notifyDataSetChanged();
    }
    String Email(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getEmail();
    }
}*/
