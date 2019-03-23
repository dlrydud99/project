package edu.skku.finalproject.studyhelper.fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dto.Member;
import edu.skku.finalproject.studyhelper.dto.Study;

//아직 출결 정보 없는 스터디는 출결 빈칸으로

public class AttendanceFragment extends Fragment {
    Context mContext;

    RecyclerView recyclerView;
    RecyclerView.Adapter Adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=getActivity().getApplicationContext();

        View v=inflater.inflate(R.layout.fragment_attendance, container, false);
        recyclerView=(RecyclerView) v.findViewById(R.id.recycler_view_attendance_study);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        Adapter=new MyAdapter(MainActivity.studies, mContext);
        recyclerView.setAdapter(Adapter);

        return v;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.VHolder> {
        private Context mContext;
        private ArrayList mStudies;

        private int lastPosition=-1;

        public MyAdapter (ArrayList studies, Context mContext){
            mStudies=studies;
            this.mContext=mContext;
        }

        @NonNull
        @Override
        public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance, parent, false);
            VHolder holder = new MyAdapter.VHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull VHolder holder, int position) {
            holder.textView1.setText(Integer.toString(position+1)
                    +"번째 스터디\n"
                    +(((Study)mStudies.get(position))).getDate());

            List<Member> singleSectionItems = ((Study) mStudies.get(position)).getMembers();

            Log.i("student", singleSectionItems.toString());

            SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(mContext, singleSectionItems);

            Log.d("student", "student count : " + itemListDataAdapter.getItemCount());

            holder.recycler_view_list.setHasFixedSize(true);
            holder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
            holder.recycler_view_list.setAdapter(itemListDataAdapter);

            setAnimation(holder.textView1,position);
        }

        @Override
        public int getItemCount() {
            return (null!=mStudies?mStudies.size() : 0);
        }

        public class VHolder extends RecyclerView.ViewHolder {

            public TextView textView1;
            public RecyclerView recycler_view_list;

            public VHolder(@NonNull View view) {
                super(view);
                textView1 = (TextView) view.findViewById(R.id.text_att_1);
                recycler_view_list=(RecyclerView) view.findViewById(R.id.recycler_view_attendance_member);
            }
        }

        private void setAnimation(View viewToAnimate, int position){
            if(position>lastPosition){
                lastPosition=position;
            }
        }
    }

    public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleHolder> {

        private List mStudent;
        private Context mContext;

        private int lastPosition=-1;

        public SectionListDataAdapter(Context context, List students){
            this.mStudent=students;
            this.mContext=context;
        }

        @NonNull
        @Override
        public SingleHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.attendance_member, parent, false);
            SingleHolder h = new SingleHolder(v);
            return h;
        }

        @Override
        public void onBindViewHolder(@NonNull SingleHolder holder, int position) {
            Member member=(Member)mStudent.get(position);
            Log.i("student", "onBindViewHolder: "+member.getName()+" "+member.getAtt());
            holder.textView1.setText(member.getName());
            holder.textView2.setText(member.getAtt());

            setAnimation(holder.textView1, position);
        }

        @Override
        public int getItemCount() {
            if(mStudent!=null){
                Log.i("student", "getItemCount: "+mStudent.size());
                return mStudent.size();
            }
            Log.i("student", "getItemCount: "+0);
            return 0;
        }

        public class SingleHolder extends RecyclerView.ViewHolder {
            protected  TextView textView1, textView2;

            public SingleHolder(View view) {
                super(view);

                this.textView1=(TextView) view.findViewById(R.id.text_att_2_1);
                this.textView2=(TextView) view.findViewById(R.id.text_att_2_2);
            }
        }

        private void setAnimation(View viewToAnimate, int position){
            if(position>lastPosition){
                lastPosition=position;
            }
        }
    }

}