package edu.skku.finalproject.studyhelper.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.fragment.AttendanceFragment;
import edu.skku.finalproject.studyhelper.fragment.MemberLocationFragment;
import edu.skku.finalproject.studyhelper.fragment.StudyListFragment;
import edu.skku.finalproject.studyhelper.fragment.StudyLocationFragment;
import edu.skku.finalproject.studyhelper.fragment.StudyMemberFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                StudyListFragment studyListFragment = new StudyListFragment();
                return studyListFragment;
            case 1:
                StudyMemberFragment studyMemberFragment = new StudyMemberFragment();
                return studyMemberFragment;
            case 2:
                StudyLocationFragment studyLocationFragment= new StudyLocationFragment();
                return studyLocationFragment;
            case 3:
                AttendanceFragment attendanceFragment = new AttendanceFragment();
                return attendanceFragment;
            case 4:
                MemberLocationFragment memberLocationFragment = new MemberLocationFragment();
                return memberLocationFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);


    }
}
