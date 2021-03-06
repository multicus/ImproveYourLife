package com.multicus.stoprelapsing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.multicus.stoprelapsing.Model.Interactors.CardInteractor;
import com.multicus.stoprelapsing.Model.Interactors.HelpedCardInteractor;
import com.multicus.stoprelapsing.Model.Interactors.HomeInteractor;
import com.multicus.stoprelapsing.Model.Repository;
import com.multicus.stoprelapsing.Presenter.MainPresenter;
import com.multicus.stoprelapsing.Utilities.ImageLoaderTask;
import com.multicus.stoprelapsing.View.MainView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView {

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize Presenter
        presenter = new MainPresenter(this);

        // initialize our Repository + Interactors
        long startTime = System.nanoTime(); // for debugging
        Repository.init(MainActivity.this);
        HomeInteractor.init(MainActivity.this);
        HelpedCardInteractor.init();                    // HelpedCards MUST be initialized before CardInteractor
        CardInteractor.init(MainActivity.this);
        Log.d("Repository init()", "Initiation of Model data took: " + ((System.nanoTime() - startTime) / 1000000) + "ms");

        // set a selected background on app startup
        presenter.setBackground();

        // set the default home screen fragment
        setShowingFragment(new HomeFragment());

        // setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // setup drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);           // automatically select Home menu-item
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below is for "settings" menu
        /*
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // below is for "settings" menu
        /*
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        presenter.onNavigationItemSelected(id); // send to presenter

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setBackground(int imageId) {
        ImageLoaderTask.Dimens screenDim = ImageLoaderTask.getScreenDimensions(this);
        Picasso.get()
                .load(imageId)
                .resize(screenDim.width, screenDim.height)
                .centerCrop()
                .into((ImageView)findViewById(R.id.homeImageView));
    }

    @Override
    public Drawable getBackground() {
        ImageView homeBackground = findViewById(R.id.homeImageView);

        return homeBackground.getBackground();
    }

    @Override
    public void setShowingFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.homeFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public Fragment getShowingFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.homeFrameLayout);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }
}
