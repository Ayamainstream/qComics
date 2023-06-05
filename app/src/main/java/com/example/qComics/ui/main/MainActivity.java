package com.example.qComics.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.base.BaseActivity;
import com.example.qComics.ui.main.comics.ComicsFragment;
import com.example.qComics.ui.main.home.HomeFragment;
import com.example.qComics.ui.main.user.UserFragment;
import com.example.q_comics.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainContract.View, BottomNavigationView.OnNavigationItemSelectedListener{


    @BindView(R.id.nav_host_fragment_activity_base)
    FrameLayout flMainContainer;
    BottomNavigationView navView;
    MainContract.Presenter presenter;

    public MainActivity() {
        super(R.id.nav_host_fragment_activity_base);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainPresenter(this);
        presenter.attachView(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        navView.setSelectedItemId(R.id.navigation_home);
    }

    public void showBottomNavigationView() {
        navView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigationView() {
        navView.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                addFragment(new HomeFragment(), true);
                return true;

            case R.id.navigation_comics:
                addFragment(new ComicsFragment(), true);
                return true;

            case R.id.navigation_user:
                addFragment(new UserFragment(), true);
                return true;
        }
        return false;
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void logout() {
        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}