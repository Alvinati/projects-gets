package id.gets.bookingservice;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager mViewPager;
        Toolbar mTopToolbar;

        mTopToolbar = (Toolbar) findViewById(R.id.toolbarApp);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        final int[] ICONS = new int[]{
                R.drawable.ic_home_black_24dp,
                R.drawable.ic_assignment_black_24dp,
                R.drawable.ic_account_circle_black_24dp,
        };

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int normalColor = getResources().getColor(R.color.colorPrimary);
        int selectedColor = getResources().getColor(R.color.colorPrimary);
        tabLayout.setTabTextColors(normalColor, selectedColor);

        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    ActivityCompat.finishAffinity(MainActivity.this);
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
