package edu.skku.finalproject.studyhelper.fragment;

import android.content.Context;//
import android.content.Intent;
import android.net.Uri;//
import android.os.Bundle;//
import android.support.v4.app.Fragment;//
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;//
import android.support.v7.widget.LinearLayoutManager;//
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;//
import android.view.View;//
import android.view.ViewGroup;//
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;//
import android.os.HardwarePropertiesManager;
import android.os.Message;//
import android.support.annotation.NonNull;//
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;//
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;//
import android.widget.LinearLayout;//
import android.widget.TextView;//
import android.widget.Toast;//

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Calendar;

import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dto.MapJsonData;
import edu.skku.finalproject.studyhelper.R;

import edu.skku.finalproject.studyhelper.dto.Member;

import static edu.skku.finalproject.studyhelper.activity.MainActivity.centerLatLng;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.members;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.thisWeekStudy;

public class MemberLocationFragment extends Fragment {
    TextView tvNotice;
    LinearLayout tvNoticeLayout, mapLaoutF;
    Context mContext;
    SupportMapFragment mapF;
    GoogleMap mMap;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member_location, container, false);
        mContext = getActivity().getApplicationContext();
        tvNoticeLayout = view.findViewById(R.id.tv_notice_layout);
        tvNotice = view.findViewById(R.id.tv_notice);
        mapLaoutF = view.findViewById(R.id.mapLayoutF);

        if (thisWeekStudy == null){
            Log.d("멤로 예정스터디없음", "");
            tvNoticeLayout.setVisibility(View.VISIBLE);
            mapLaoutF.setVisibility(View.GONE);
            tvNotice.setText("예정된 스터디가 없습니다.");
        }else {
            String studyTime = thisWeekStudy.getDate();

            try {
                Date studyTimeParsed = sdf.parse(studyTime);
                Date currenTime = new Date(System.currentTimeMillis());
                Log.d("멤로 현재 스터디 시간: ", studyTimeParsed.toString());
                Log.d("멤로 현재 시간: ", currenTime.toString());
                int compareResult = studyTimeParsed.compareTo(currenTime);
                Log.d("멤로 compareResult", Integer.toString(compareResult));
                switch (compareResult) {
                    case -1: // 스터디 시간 지남
                    case 0:
                    case 1: // 아직 스터디 시간 x or 스터디시간
                        long diffTime = (studyTimeParsed.getTime() - currenTime.getTime()) / 1000; // 스터디까지 남은 시간초
                        if (diffTime < 600 || diffTime < 0) { // 10분전
                            Log.d("멤로","10분전");
                            mapLaoutF.setVisibility(View.VISIBLE);
                            tvNoticeLayout.setVisibility(View.GONE);
                            final SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapF);
                            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    mMap = googleMap;

                                    for (int idx = 0; idx < thisWeekStudy.getMembers().size(); idx++) {
                                        mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.valueOf(thisWeekStudy.getMembers().get(idx).getCurrentLocation().getLatlng().split(",")[0]),Double.valueOf(thisWeekStudy.getMembers().get(idx).getCurrentLocation().getLatlng().split(",")[1])))
                                                        .title(thisWeekStudy.getMembers().get(idx).getName())).showInfoWindow();
                                    }
                                    mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(Double.valueOf(thisWeekStudy.getLocation().getLatlng().split(",")[0]),Double.valueOf(thisWeekStudy.getLocation().getLatlng().split(",")[1])))
                                                .title(thisWeekStudy.getLocation().getName())
                                            .snippet("약속장소")
                                    ).showInfoWindow();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(thisWeekStudy.getLocation().getLatlng().split(",")[0]),Double.valueOf(thisWeekStudy.getLocation().getLatlng().split(",")[1])), 15));

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            marker.showInfoWindow();
                                            return false;
                                        }
                                    });
                                }
                            });
                        } else { //10분보다 더 이전
                            tvNoticeLayout.setVisibility(View.VISIBLE);
                            mapLaoutF.setVisibility(View.GONE);
                            tvNotice.setText("10분 전부터 \n 스터디원의 위치를 \n확인할 수 있습니다.");
                        }
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return view;
    }

    public void toggleVisibility(View v){
        if (v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }
}
