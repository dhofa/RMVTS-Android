package id.ac.pens.student.it.ahmadmundhofa.rmvts;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlidingTabMapsAdapter extends FragmentPagerAdapter {

    public SlidingTabMapsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                LogMapsFragment fragment = new LogMapsFragment();
                return fragment;
            case 1:
                RealtimeMapsFragment fragment1= new RealtimeMapsFragment();
                return fragment1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Log Maps";
            case 1:
                return "Realtime Maps";
        }
        return null;
    }
}
