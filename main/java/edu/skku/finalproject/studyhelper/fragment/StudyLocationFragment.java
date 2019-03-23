package edu.skku.finalproject.studyhelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dialog.StudyAddDialog;
import edu.skku.finalproject.studyhelper.dto.Location;
import edu.skku.finalproject.studyhelper.dto.MapJsonData;
import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.dto.Study;

import static edu.skku.finalproject.studyhelper.activity.MainActivity.me;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.members;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.sName;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.studies;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.centerLatLng;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.centerLatLngStr;


public class StudyLocationFragment extends Fragment {
    Context mContext;
    GoogleMap mMap;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
//    LatLng centerLatLng;
    MapJsonData.ResultClass[] mapResults ;
    String[] placeUrls;
    Button btnToggleMap;
    LinearLayout mapLayout;
    StudyAddDialog dialog;

    int year, month, day, hour, minute;
    String mmonth, mday, mhour, mminute;
    public static View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext=getActivity().getApplicationContext();
        if (view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_study_location, container, false);
        }catch (InflateException e){

        }
        mapLayout = view.findViewById(R.id.map_layout);
        btnToggleMap = view.findViewById(R.id.btn_toggle_map);
        btnToggleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapLayout.getVisibility() == View.GONE){
                    mapLayout.setVisibility(View.VISIBLE);
                }else{
                    mapLayout.setVisibility(View.GONE);
                }

            }
        });
        /* 위치 관련 */
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_2);
        autocompleteFragment.setHint("스터디 장소를 검색하세요.");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
               mapLayout.setVisibility(View.VISIBLE);

                SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng = place.getLatLng();
                            markerOptions.position(latLng).title(place.getName().toString()).snippet("선택하려면 클릭하세요.");
                            mMap.addMarker(markerOptions).showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                final LatLng chooseLatLng = place.getLatLng();
                                dialog=new StudyAddDialog(getActivity());
                                dialog.setMyOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // DB에 저장
                                        year=dialog.getYear();
                                        month=dialog.getMonth();
                                        day=dialog.getDay();
                                        hour=dialog.getHour();
                                        minute=dialog.getMinute();
                                        if(month/10==0&&(month!=10 || month!=20)){
                                            mmonth="0"+Integer.toString(month);
                                        } else mmonth=Integer.toString(month);
                                        if(day/10==0&&(day!=10 || day!=20 || day!=30)){
                                            mday="0"+Integer.toString(day);
                                        } else mday=Integer.toString(day);
                                        if(hour/10==0&&(hour!=10 || hour!=20)){
                                            mhour="0"+Integer.toString(hour);
                                        } else mhour=Integer.toString(hour);
                                        if(minute/10==0&&(minute!=10 || minute!=20 || minute!=30 || minute!=40 || minute!=50)){
                                            mminute="0"+Integer.toString(minute);
                                        } else mminute=Integer.toString(minute);
                                        String date = Integer.toString(year) + "-" + mmonth + "-" + mday + " " +  mhour +":" +mminute;
                                        String l = Double.toString(chooseLatLng.latitude) + "," + Double.toString(chooseLatLng.longitude);
                                        Study study = new Study(date, new Location(place.getName().toString(), l),members,"예정" );
                                        studies.add(study);
                                        Toast.makeText(mContext,Integer.toString(studies.size()) + "번째 스터디\n장소: " + place.getName().toString() + study.getDate(),Toast.LENGTH_LONG  ).show();
                                        Log.i("updatedb", "request");
                                        MainActivity.updateDB(sName,studies,members);
                                        Log.i("updatedb", "success");
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Status status) {}});

        HttpThread httpThread = new HttpThread(1, "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+centerLatLngStr+"&radius=1000&type=cafe&key=AIzaSyC-3hf2hNZk89OQcXeoIbZvUcYXN0Jg5LI&language=ko");
        httpThread.start();
        try {
            httpThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        recyclerView=(RecyclerView) view.findViewById(R.id.recycler_study_location);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Log.d("here: ", Integer.toString(mapResults.length));
        adapter =new MyAdapter(mapResults, mContext);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    public class HttpThread extends Thread {
        int code;
        String urlString;

        public HttpThread(int code, String urlString) {
            this.code = code;
            this.urlString = urlString;
        }

        @Override
        public void run() {
            Message msg = new Message();
            try {
                URL url = new URL(urlString);

                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                http.setConnectTimeout(5 * 1000);
                http.setReadTimeout(5 * 1000);

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        http.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
//                    Log.d("inputline", inputLine);
                    sb.append(inputLine);
                }
                MapJsonData mapJsonData = new Gson().fromJson(sb.toString(), MapJsonData.class);
                mapResults = mapJsonData.results;
                msg.what = code;
                msg.obj = sb.toString();
                handler.sendMessage(msg);

                Log.d("response", sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                msg.what = 0;
                msg.obj = e.getLocalizedMessage();
                handler.sendMessage(msg);
            }

        }
    }//HttpThread

    Handler handler = new android.os.Handler() {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0: {
                    break;
                }
                case 1: {
                    String jsonString = (String) msg.obj;
                    Gson gson = new Gson();
                     //주변 카페 리스트
                    String names = "";
                    placeUrls = new String[mapResults.length]; // 카페 상세 url
                    // todo: 리사이클러뷰로 카페 목록 리스트로 보여주고, 지도보기 누르면 지도로 띄워주고, 자세히 보기를 누르면 url로 place 창 띄워줌. 장소 선택 기능도 만들어야함
//                    while (getChildFragmentManager() == null){
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } 에러 때문에 주석 처리함
                    SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    mMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            MarkerOptions markerOptions = new MarkerOptions();
                            for (int idx = 0; idx < mapResults.length; idx++) {

                                placeUrls[idx] = "https://www.google.com/maps/search/?api=1&query=" + mapResults[idx].name +"&query_place_id=" + mapResults[idx].place_id +"&language=kr";
                                Log.d("here: ", mapResults[idx].name);
                                LatLng latLng = new LatLng(mapResults[idx].getGeometry().getLocation().getLat(), mapResults[idx].getGeometry().getLocation().getLng());
                                markerOptions.position(latLng).title(mapResults[idx].name).snippet("more");
                                mMap.addMarker(markerOptions);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng,15));
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    LatLng latLng = marker.getPosition();
                                    String geo = Double.toString(latLng.latitude) + "," +  Double.toString(latLng.longitude);
                                    String query = marker.getTitle();
                                    Uri gmmIntentUri = Uri.parse("geo:" + geo + "?q=" + query);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                }
                            });
                        }
                    });

                    break;
                }
                case 2: {

                    break;
                }
            }
        }
    };




    class MyAdapter extends RecyclerView.Adapter {
        private Context context;
        private MapJsonData.ResultClass[] resultClasses;

        private int lastPosition=-1;

        public MyAdapter (MapJsonData.ResultClass[] resultClasses, Context mContext){
            this.resultClasses=resultClasses;
            context=mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_location_recycler, parent,false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            StudyLocationFragment.MyAdapter.ViewHolder mViewHolder = (StudyLocationFragment.MyAdapter.ViewHolder) holder;
            final String placeName = resultClasses[position].name;
            mViewHolder.tvCafeName.setText( placeName);
            mViewHolder.tvCafeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapLayout.setVisibility(View.VISIBLE);
                    final LatLng chooseLatLng = new LatLng(resultClasses[position].getGeometry().getLocation().getLat(),resultClasses[position].getGeometry().getLocation().getLng());
                    mMap.addMarker(new MarkerOptions().position(chooseLatLng).title(placeName).snippet("more")).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chooseLatLng,15));
                }
            });


            mViewHolder.btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //todo: 선택시 스터디 장소 업데이트
                    final LatLng chooseLatLng = new LatLng(resultClasses[position].getGeometry().getLocation().getLat(),resultClasses[position].getGeometry().getLocation().getLng());
                    dialog=new StudyAddDialog(getActivity());
                    dialog.setMyOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // DB에 저장
                            year=dialog.getYear();
                            month=dialog.getMonth();
                            day=dialog.getDay();
                            hour=dialog.getHour();
                            minute=dialog.getMinute();
                            if(month/10==0&&(month!=10 || month!=20)){
                                mmonth="0"+Integer.toString(month);
                            } else mmonth=Integer.toString(month);
                            if(day/10==0&&(day!=10 || day!=20 || day!=30)){
                                mday="0"+Integer.toString(day);
                            } else mday=Integer.toString(day);
                            if(hour/10==0&&(hour!=10 || hour!=20)){
                                mhour="0"+Integer.toString(hour);
                            } else mhour=Integer.toString(hour);
                            if(minute/10==0&&(minute!=10 || minute!=20 || minute!=30 || minute!=40 || minute!=50)){
                                mminute="0"+Integer.toString(minute);
                            } else mminute=Integer.toString(minute);
                            String date = Integer.toString(year) + "-" + mmonth + "-" + mday + " " +  mhour +":" +mminute;
                            String l = Double.toString(chooseLatLng.latitude) + "," + Double.toString(chooseLatLng.longitude);
                            Study study = new Study(date, new Location(placeName, l),members,"예정" );
                            studies.add(study);
                            Toast.makeText(context,Integer.toString(studies.size()) + "번째 스터디\n장소: " + placeName,Toast.LENGTH_LONG  ).show();
                            Log.i("updatedb", "request");
                            MainActivity.updateDB(sName,studies,members);
                            Log.i("updatedb", "success");

                        }
                    });
                    dialog.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return mapResults==null?0:mapResults.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCafeName;
            Button btnChoose;

            public ViewHolder(@NonNull View view) {
                super(view);
                tvCafeName = view.findViewById(R.id.tv_cafe_name);
                btnChoose = view.findViewById(R.id.btn_choose);
            }
        }

        private void setAnimation(View viewToAnimate, int position){
            if (position>lastPosition){
                lastPosition=position;
            }
        }

    }


}
