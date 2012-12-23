package org.talentware.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import org.talentware.android.view.FragmentView;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-23
 * Time: 下午2:14
 * To change this template use File | Settings | File Templates.
 */
public class TestActivity extends FragmentActivity {
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<String> titleList    = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_demo);

        ViewPager vp = (ViewPager)findViewById(R.id.viewPager);
        fragmentList.add(new FragmentView("页面1"));
        fragmentList.add(new FragmentView("页面2"));
        fragmentList.add(new FragmentView("页面3"));
        // fragmentList.add(new ViewPagerFragment2());
        // fragmentList.add(new ViewPagerFragment2());
        // fragmentList.add(new ViewPagerFragment2());
        titleList.add("title 1 ");
        titleList.add("title 2 ");
        titleList.add("title 3 ");
        vp.setAdapter(new myPagerAdapter(getSupportFragmentManager(), fragmentList, titleList));
    }

    /**
     * 定义适配器
     *
     * @author gxwu@lewatek.com 2012-11-15
     */
    class myPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;
        private List<String>   titleList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList){
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
        }

//        @Override
        public CharSequence getPageTitle(int position) {
            return (titleList.size() > position) ? titleList.get(position) : "";
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

    }
}