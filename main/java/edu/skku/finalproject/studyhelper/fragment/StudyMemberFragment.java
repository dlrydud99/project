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
import android.widget.TextView;

import java.util.ArrayList;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dto.Member;



public class StudyMemberFragment extends Fragment {

    Context mContext;

    RecyclerView recyclerView;
    RecyclerView.Adapter Adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=getActivity().getApplicationContext();

        View v=inflater.inflate(R.layout.fragment_study_member, container, false);
        recyclerView=(RecyclerView) v.findViewById(R.id.recycler_view_member);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        Log.d("member는..",Integer.toString(MainActivity.members.size()));
        Adapter=new StudyMemberFragment.MyAdapter( mContext);

        recyclerView.setAdapter(Adapter);

        return v;
    }

    class MyAdapter extends RecyclerView.Adapter {
        private Context context;
        private int lastPosition = -1;

        public MyAdapter( Context mContext) {
            context = mContext;
        }

        @NonNull
        @Override
        public StudyMemberFragment.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member, parent, false);
            StudyMemberFragment.MyAdapter.ViewHolder holder = new StudyMemberFragment.MyAdapter.ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.d("members: ",MainActivity.members.get(position).getName());
            ((StudyMemberFragment.MyAdapter.ViewHolder) holder).textView1.setText(MainActivity.members.get(position).getName());
            ((StudyMemberFragment.MyAdapter.ViewHolder) holder).textView2.setText("출발 장소\n");
            ((StudyMemberFragment.MyAdapter.ViewHolder) holder).textView3.setText(MainActivity.members.get(position).getStartLocation().getName());

            setAnimation(((StudyMemberFragment.MyAdapter.ViewHolder) holder).textView1, position);
        }

        @Override
        public int getItemCount() {
            return MainActivity.members.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView1;
            public TextView textView2;
            public TextView textView3;

            public ViewHolder(@NonNull View view) {
                super(view);
                textView1 = (TextView) view.findViewById(R.id.text_member_1);
                textView2 = (TextView) view.findViewById(R.id.text_member_2);
                textView3 = (TextView) view.findViewById(R.id.text_member_3);
            }
        }

        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                lastPosition = position;
            }
        }

    }

}