package edu.skku.finalproject.studyhelper.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.Collections;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.adapter.TabPagerAdapter;
import edu.skku.finalproject.studyhelper.dto.Member;
import edu.skku.finalproject.studyhelper.dto.Study;
import edu.skku.finalproject.studyhelper.service.MyService;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String MYNAME = "";
    double lng, lat;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static LatLng centerLatLng;
    public static String centerLatLngStr;
    public static ArrayList<Study> studies = new ArrayList<Study>();
    public static ArrayList<Member> members = new ArrayList<Member>();
    public static Member me;
    public static Study thisWeekStudy;
    public static String sName;
    public static TabPagerAdapter pagerAdapter;
    public static ProgressDialog progressDialog;
    public String myName = "";
    /* location */
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) { // location이 변했을 때
            lat=location.getLatitude();
            lng =location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this,"액티비티 재실행",Toast.LENGTH_LONG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sName = getIntent().getStringExtra("studyName");
        myName = getIntent().getStringExtra("myName");
        Log.d("intent:",sName);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("다음\n스터디"));
        tabLayout.addTab(tabLayout.newTab().setText("스터디원"));
        tabLayout.addTab(tabLayout.newTab().setText("스터디\n추가"));
        tabLayout.addTab(tabLayout.newTab().setText("출결 확인"));
        tabLayout.addTab(tabLayout.newTab().setText("스터디원\n위치"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setCustomFont();
        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating edu.skku.finalproject.studyhelper.adapter.TabPagerAdapter adapter
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(sName);
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GenericTypeIndicator<ArrayList<Study>> sT = new GenericTypeIndicator<ArrayList<Study>>() {};
                if (dataSnapshot.child("/studies").getValue(sT) != null){
                    studies = dataSnapshot.child("/studies").getValue(sT);
                    Collections.sort(studies, Study.StuDateComparator);
                    for(Study s : studies){
                        if(s.getStatus().equals("완료")){
                            continue;
                        }
                        else {
                            thisWeekStudy = s;
                            break;
                        }
                    }
                }

                GenericTypeIndicator<ArrayList<Member>> mT = new GenericTypeIndicator<ArrayList<Member>>() {};
                members = dataSnapshot.child("/members").getValue(mT);
                Collections.sort(members, Member.StuNameComparator);
                for (Member m : members){
                    if (m.getName().equals(myName)){
                        me = m;
                    }
                }

                centerLatLng = getCenterLatLng();
                centerLatLngStr = centerLatLng.latitude + "," + centerLatLng.longitude;
                Log.d("kwang","data refreshed.");
                if(members.size() > 0){
//                    pagerAdapter.notifyDataSetChanged();
                    Log.d("kwang","data refreshed." + members.size());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    /* location */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean canUseLocation=false;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
            } else {
                canUseLocation=true;
            }
        } else {
            canUseLocation=true;
        }

        if(canUseLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null){
                lat = lastLocation.getLatitude();
                lng = lastLocation.getLongitude();
            }else{
                lat = 37.5609775;
                lng = 126.9664153;
            }
        }

        if (!isServiceRunning())startService(new Intent(this,MyService.class));
        if (thisWeekStudy != null){
            Toast.makeText(this,"이번주 스터디:" + thisWeekStudy.getLocation().getName(),Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
//        stopService(new Intent(this,MyService.class));
        super.onDestroy();
    }
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
        }
    }
    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    static public void updateDB(String sName, ArrayList studies, ArrayList members){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(sName);
        myRef.child("members").setValue(members);
        myRef.child("studies").setValue(studies);
    }

    public LatLng getCenterLatLng(){
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (int i = 0; i < members.size(); i++) {
            String[] s = members.get(i).getStartLocation().getLatlng().split(",");
            builder.include( new LatLng(Double.valueOf(s[0]),Double.valueOf(s[1])));
        }
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }

    public void setCustomFont(){
        ViewGroup vg=(ViewGroup) tabLayout.getChildAt(0);
        int tabsCount=vg.getChildCount();

        for(int j=0; j<tabsCount; j++){
            ViewGroup vgTab=(ViewGroup) vg.getChildAt(j);
            int tabChildsCount=vgTab.getChildCount();

            for(int i=0; i<tabChildsCount; i++){
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView){
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "THEjunggt110.otf"));
                }
            }
        }
    }



}