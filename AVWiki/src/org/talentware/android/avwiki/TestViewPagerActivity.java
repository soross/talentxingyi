package org.talentware.android.avwiki;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import org.talentware.android.avwiki.view.ViewPagerFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-23
 * Time: ����5:08
 * To change this template use File | Settings | File Templates.
 */
public class TestViewPagerActivity extends FragmentActivity {


    /** ҳ��list **/
    List<Fragment> fragmentList = new ArrayList<Fragment>();
    /** ҳ��title list **/
    List<String>   titleList    = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager vp = (ViewPager)findViewById(R.id.viewPager);
        fragmentList.add(new ViewPagerFragment("ҳ��1"));
        fragmentList.add(new ViewPagerFragment("ҳ��2"));
        fragmentList.add(new ViewPagerFragment("ҳ��3"));
        titleList.add("title 1 ");
        titleList.add("title 2 ");
        titleList.add("title 3 ");
        vp.setAdapter(new myPagerAdapter(getSupportFragmentManager(), fragmentList, titleList));
    }

    /**
     * ����������
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

        /**
         * �õ�ÿ��ҳ��
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
        }

        /**
         * ÿ��ҳ���title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return (titleList.size() > position) ? titleList.get(position) : "";
        }

        /**
         * ҳ����ܸ���
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }

}