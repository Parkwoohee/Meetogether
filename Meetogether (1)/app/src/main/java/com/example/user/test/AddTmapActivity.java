package com.example.user.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class AddTmapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, TMapView.OnClickListenerCallback {

    TMapPoint mapPoint;

    TMapView mapView;
    TMapGpsManager mapGps;

    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    EditText keywordView;
    ListView listView;
    ArrayAdapter<POI> mAdapter;

    TMapMarkerItem item;
    TMapPoint start, end;
    ArrayList<TMapPoint> pass = new ArrayList<>();
    String message;
    RadioGroup typeView;
    RadioButton radio_start, radio_pass1, radio_pass2, radio_pass3, radio_end;
    Button btn_search, btn_route, btn_ok;
    boolean mTrackingMode = true;
    boolean[] settingOn = new boolean[5];

    static LinearLayout linear_screen;

    FloatingActionButton gps_fab;

    private static final String CAPTURE_PATH = "/CAPTURE_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tmap);

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        typeView = findViewById(R.id.group_type);
        radio_start=findViewById(R.id.radio_start);
        radio_pass1=findViewById(R.id.radio_pass1);
        radio_pass2=findViewById(R.id.radio_pass2);
        radio_pass3=findViewById(R.id.radio_pass3);
        radio_end=findViewById(R.id.radio_end);

        keywordView = findViewById(R.id.edit_keyword);

        btn_search= findViewById(R.id.btn_search);
        btn_route = findViewById(R.id.btn_route);
        btn_ok = findViewById(R.id.btn_ok);

        linear_screen = findViewById(R.id.linear);
        gps_fab = findViewById(R.id.gps_fab);

        gps_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackingMode=true;
                mapView.setTrackingMode(mTrackingMode);
            }
        });

        listView = findViewById(R.id.listView);
        mAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mapView = findViewById(R.id.map_view);
        mapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupMap();
                    }
                });
            }

            @Override
            public void SKTMapApikeyFailed(String s) {

            }
        });
        mapView.setSKTMapApiKey("afd72e14-cf7c-4c8b-a550-738c9f76ea3e");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mapGps = new TMapGpsManager(AddTmapActivity.this);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.VISIBLE);

                InputMethodManager im= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(keywordView.getWindowToken(), 0);

                searchPOI();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                POI poi = (POI) listView.getItemAtPosition(position);
                moveMap(poi.item.getPOIPoint().getLatitude(), poi.item.getPOIPoint().getLongitude());
                listView.setVisibility(View.INVISIBLE);
            }
        });

        btn_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start != null && end != null) {
                    if (!pass.isEmpty()) {
                        try {
                            searchRoute(start, end, pass);
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        searchRoute(start, end);
                    }

                    start = end = null;
                    pass = new ArrayList<>();

                    //pass = null;
                    for (int i = 0; i < settingOn.length; i++)
                        settingOn[i] = false;
                } else {
                    Toast.makeText(getApplicationContext(), "출발점과 도착점을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File screenShot = captureView(mapView);
                Log.d("screenShot",screenShot + "");
                if(screenShot!=null){
                    //갤러리에 추가
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                    Log.d("화면캡쳐","성공");
                }
                Toast.makeText(getApplicationContext(), "경로를 지정하였습니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), AddRoomActivity.class);
                startActivity(intent);
                finish();
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

    private void searchRoute(TMapPoint start, TMapPoint end) {
        TMapData data = new TMapData();

        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine tMapPolyLine) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tMapPolyLine.setLineWidth(10);
                        tMapPolyLine.setLineColor(Color.RED);
                        mapView.addTMapPath(tMapPolyLine);

                        mapView.removeMarkerItem("m1");
                        mapView.removeMarkerItem("m2");
                        mapView.removeMarkerItem("m3");
                        mapView.removeMarkerItem("m4");
                        mapView.removeMarkerItem("m5");
                    }
                });

                //mapView.removeAllMarkerItem();
            }
        });
    }

    private void searchRoute(TMapPoint start, TMapPoint end, ArrayList<TMapPoint> pass) throws ParserConfigurationException, SAXException, IOException {
        TMapData data = new TMapData();

        data.findMultiPointPathData(start, end, pass, pass.size(), new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine tMapPolyLine) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tMapPolyLine.setLineWidth(10);
                        tMapPolyLine.setLineColor(Color.RED);
                        mapView.addTMapPath(tMapPolyLine);

                        mapView.removeMarkerItem("m1");
                        mapView.removeMarkerItem("m5");
                    }
                });
                //mapView.removeAllMarkerItem();
            }
        });
    }

    private void searchPOI() {
        TMapData data = new TMapData();
        String keyword = keywordView.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            data.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                @Override
                public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mapView.removeAllMarkerItem();
                            mAdapter.clear();

                            for (TMapPOIItem poi : arrayList) {
                                //listView.setVisibility(View.INVISIBLE);
                                addMarker(poi);
                                mAdapter.add(new POI(poi));
                            }

                            if (arrayList.size() > 0) {
                                TMapPOIItem poi = arrayList.get(0);
                                moveMap(poi.getPOIPoint().getLatitude(), poi.getPOIPoint().getLongitude());
                            }
                        }
                    });
                }
            });
        }
    }

    public void setupMarker() {
        message = null;
        item = new TMapMarkerItem();
    }

    public void addMarker(TMapPOIItem poi) {        // 검색해서 마커 추가
        //setupMarker();

        item.setTMapPoint(poi.getPOIPoint());
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_input_add)).getBitmap();

        if(radio_start.isChecked()){
            icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_map_start)).getBitmap();
            start = poi.getPOIPoint();
            id=1;
        }else if(radio_pass1.isChecked()){
            icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin1)).getBitmap();
            pass.add(poi.getPOIPoint());
            id=2;
        }else if(radio_pass2.isChecked()){
            icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin2)).getBitmap();
            pass.add(poi.getPOIPoint());
            id=3;
        }else if(radio_pass3.isChecked()){
            icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin3)).getBitmap();
            pass.add(poi.getPOIPoint());
            id=4;
        }else if(radio_end.isChecked()){
            icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_map_arrive)).getBitmap();
            end = poi.getPOIPoint();
            id=5;
        }else{
        }

        item.setIcon(icon);
        item.setPosition(0.5f, 1);

        mapView.addMarkerItem("m" + id, item);
    }
    int id = 0;
    boolean isInitialized = false;

    private void setupMap() {
        isInitialized = true;
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setCompassMode(true);

        setGps();
/*
        mapGps.setMinTime(1000);
        mapGps.setMinDistance(5);
        mapGps.setProvider(TMapGpsManager.NETWORK_PROVIDER);

        mapGps.OpenGps();
*/
        mapView.setTrackingMode(mTrackingMode);
        mapView.setSightVisible(true);

        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }

        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                message = null;

                switch (typeView.getCheckedRadioButtonId()){
                    case R.id.radio_start:
                        start = tMapMarkerItem.getTMapPoint();
                        settingOn[0] = true;
                        message = "start";
                        break;
                    case R.id.radio_pass1:
                        pass.add(tMapMarkerItem.getTMapPoint());
                        settingOn[1] = true;
                        message = "pass1";
                        break;
                    case R.id.radio_pass2:
                        pass.add(tMapMarkerItem.getTMapPoint());
                        settingOn[2] = true;
                        message = "pass2";
                        break;
                    case R.id.radio_pass3:
                        pass.add(tMapMarkerItem.getTMapPoint());
                        settingOn[3] = true;
                        message = "pass3";
                        break;
                    case R.id.radio_end:
                        end = tMapMarkerItem.getTMapPoint();
                        settingOn[4] = true;
                        message = "end";
                        break;
                }
                Toast.makeText(getApplicationContext(), message + " setting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    Location cacheLocation = null;

    private void moveMap(double lat, double lng) {
        Log.d("moveMap",lat + "," + lng);

        mapView.setCenterPoint(lng, lat);
        mTrackingMode=false;
        mapView.setTrackingMode(mTrackingMode);
    }

    private void setMyLocation(double lat, double lng) {
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map)).getBitmap();
        mapView.setIcon(icon);
        mapView.setLocationPoint(lng, lat);
        mapView.setIconVisibility(true);
    }
    /*
        LocationListener mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isInitialized) {
                    moveMap(location.getLatitude(), location.getLongitude());
                    setMyLocation(location.getLatitude(), location.getLongitude());
                } else {
                    cacheLocation = location;
                }
            }
    */
    private final LocationListener mListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                mapView.setLocationPoint(longitude, latitude);
                mapView.setCenterPoint(longitude, latitude);
                Log.d("TmapTest",""+longitude+","+latitude);
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onLocationChange(Location location) {
        if (mTrackingMode) {
            Log.d("onLocationChange", mTrackingMode + " ");
            mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        return false;
    }

    @Override
    public boolean onPressEvent(ArrayList markerlist,
                                ArrayList poilist, TMapPoint point, PointF pointf) {
        //mapPoint=point;

        Log.d("onPressEvent",point +"");
        //Log.d("onPressEvent",mapPoint +"");
        listView.setVisibility(View.INVISIBLE);
        addMarker(point);

        return false;
    }

    private void addMarker(TMapPoint point) {

        setupMarker();
        //item.setTMapPoint(point);
        //Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_input_add)).getBitmap();

        if(radio_start.isChecked()) {
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_map_start)).getBitmap();
            start = point;
            id = 1;
            item.setIcon(icon);
            item.setPosition(0.5f, 1);
        }
        else if(radio_pass1.isChecked()) {
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin1)).getBitmap();
            pass.add(point);
            id = 2;
            item.setIcon(icon);
            item.setPosition(0.5f, 0.5f);
        }
        else if(radio_pass2.isChecked()) {
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin2)).getBitmap();
            pass.add(point);
            id = 3;
            item.setIcon(icon);
            item.setPosition(0.5f, 0.5f);
        }
        else if(radio_pass3.isChecked()) {
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.pin3)).getBitmap();
            pass.add(point);
            id = 4;
            item.setIcon(icon);
            item.setPosition(0.5f, 0.5f);
        }
        else if(radio_end.isChecked()) {
            item.setTMapPoint(point);
            Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_map_arrive)).getBitmap();
            end = point;
            id = 5;
            item.setIcon(icon);
            item.setPosition(0.5f, 1);
        }
        else {

        }

        //item.setIcon(icon);
        //item.setPosition(0.5f, 1);

        mapView.addMarkerItem("m" + id, item);
    }


    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            if (ActivityCompat.shouldShowRequestPermissionRationale(AddTmapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) { //거절 시

                Log.v("거절", "위치제공거절");

            } else {
                Log.v("승락 또는 처음", "위치제공승락");

                mapView.setSightVisible(true);
                mapView.setIconVisibility(true);

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                        1000, // 통지사이의 최소 시간간격 (miliSecond)
                        1, // 통지사이의 최소 변경거리 (m)
                        mListener);
            }
        }
    }

    public File captureView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap screenBitmap = view.getDrawingCache();

        String filename = "mapRoute.png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);

        Log.d("file",file + "");
        return file;
    }
}
