package edu.skku.finalproject.studyhelper.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.IOException;
import java.util.Locale;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.dialog.StudyCreateDialog;
import edu.skku.finalproject.studyhelper.dialog.StudyEnterDialog;

public class StartActivity extends AppCompatActivity  implements View.OnClickListener{
    Button btnCreateStudy, btnEnterStudy;
    LatLng selectedLocationLatLng;
    String selectedLocationName;
    boolean isPlaceSelected = false;


    /* location */
    LocationManager locationManager;
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) { // location이 변했을 때
            selectedLocationLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
        @Override
        public void onProviderEnabled(String provider){}
        @Override
        public void onProviderDisabled(String provider){}
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        /* 버튼 초기화 */
        btnCreateStudy = findViewById(R.id.btn_create_study);
        btnEnterStudy = findViewById(R.id.btn_enter_study);

        btnCreateStudy.setOnClickListener(this);
        btnEnterStudy.setOnClickListener(this);

        /* 위치 관련 */
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("출발 위치를 입력하세요.");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                selectedLocationName = place.getName().toString();
                selectedLocationLatLng = place.getLatLng();
                isPlaceSelected = true;
            }

            @Override
            public void onError(Status status) {}});

        /* loacation manager 및 권한 획득 */
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
        // 현재 위치를 저장한다.
        if(canUseLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null){
                selectedLocationLatLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            }else{
                selectedLocationLatLng = new LatLng(37.5609775,126.9664153);
            }
        }
        if (selectedLocationLatLng == null){
            selectedLocationLatLng = new LatLng(37.5609775,126.9664153);
        }
        selectedLocationName = getLocationName(selectedLocationLatLng.latitude, selectedLocationLatLng.longitude);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /* 스터디 생성 */
            case R.id.btn_create_study:
                if(!isPlaceSelected){ //출발 위치 선택하지 않았을 경우, 현위치 지정
                    Toast.makeText(this,"출발 장소를 선택하지 않아,\n현재 위치"+ selectedLocationName +"를 출발지로 지정합니다.",Toast.LENGTH_LONG ).show();
                }
                new StudyCreateDialog(this, selectedLocationName, selectedLocationLatLng).show();
                break;
                /* 스터디 입장 */
            case R.id.btn_enter_study:
                if(!isPlaceSelected){
                    Toast.makeText(this,"장소를 선택하지않아, \n현재 위치 "+ selectedLocationName +"를 출발지로 지정합니다.",Toast.LENGTH_LONG ).show();
                }
                new StudyEnterDialog(this, selectedLocationName, selectedLocationLatLng).show();
                break;
        }
    }

    /* 위경도 입력, 현재 위치 이름 반환 */
    public String  getLocationName(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        String result = "";
        try {
            result = geocoder.getFromLocation(selectedLocationLatLng.latitude, selectedLocationLatLng.longitude,1).get(0).getAddressLine(0).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}