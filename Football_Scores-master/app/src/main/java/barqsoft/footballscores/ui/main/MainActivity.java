package barqsoft.footballscores.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import barqsoft.footballscores.R;
import barqsoft.footballscores.infrastructure.utils.Constants;
import barqsoft.footballscores.infrastructure.utils.Utilities;
import barqsoft.footballscores.ui.about.AboutActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends ActionBarActivity implements MainScreenFragment.Contract {
    private static String EXTRA_SELECTED_MATCH_ID = MainActivity.class.getSimpleName() + ":extra_match_id";
    private static String EXTRA_CURRENT_FRAGMENT = MainActivity.class.getSimpleName() + ":extra_current_fragment";
    @Bind(R.id.pager)
    ViewPager mPager;
    private long mMatchId;
    private int mCurrentFragment = 2;
    private MyPageAdapter mMyPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt(EXTRA_CURRENT_FRAGMENT);
            mMatchId = savedInstanceState.getLong(EXTRA_SELECTED_MATCH_ID);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setPagerAndTabs();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_FRAGMENT, mPager.getCurrentItem());
        outState.putLong(EXTRA_SELECTED_MATCH_ID, mMatchId);

    }

    @Override
    public Long getSelectedMatchId() {
        return mMatchId;
    }

    @Override
    public void setSelectedMatchId(Long id) {
        mMatchId = id;
    }

    private void setPagerAndTabs() {
        final String [] titles = Utilities.getDateTitlesForViewPager(this);
        mMyPageAdapter = new MyPageAdapter(getSupportFragmentManager(), titles);
        mPager.setAdapter(mMyPageAdapter);
        mPager.setCurrentItem(mCurrentFragment);
    }

    private class MyPageAdapter extends FragmentStatePagerAdapter {

        private final String[] mTitles;

        public MyPageAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int i) {
            final Bundle bundle = new Bundle();
            bundle.putString(MainScreenFragment.ARG_DATE, getDateForFragment(i));
            return Fragment.instantiate(MainActivity.this, MainScreenFragment.TAG , bundle);
        }

        @Override
        public int getCount() {
            return Constants.NUM_PAGES;
        }

        private String getDateForFragment(final int position){

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            switch (position){
                case 0 :
                    calendar.add(Calendar.DATE, -2);
                    break;
                case 1 :
                    calendar.add(Calendar.DATE, -1);
                    break;
                case 2 :
                    calendar.add(Calendar.DATE, 0);
                    break;
                case 3 :
                    calendar.add(Calendar.DATE, 1);
                    break;
                case 4 :
                    calendar.add(Calendar.DATE, 2);
                    break;
                default:
                    break;

            }

            return simpleDateFormat.format(calendar.getTime());
        }

    }

}
