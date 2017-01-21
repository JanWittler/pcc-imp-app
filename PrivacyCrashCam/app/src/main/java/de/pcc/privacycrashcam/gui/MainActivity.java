package de.pcc.privacycrashcam.gui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import de.pcc.privacycrashcam.R;

/**
 * Base class for all activities. Handles navigation through the application's views.
 *
 * @author Giorgio
 */
public abstract class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MAIN_ACT";

    private @Nullable DrawerLayout drawer;

    /**
     * Get the base layout resource for this activity. The layout must contain a toolbar with an id
     * named <b>toolbar</b>.
     * <p>If there is a navigation drawer in the layout it will be displayed. Navigation is handled
     * by the {@link MainActivity MainActivity} class.</p>
     *
     * @return resource id for this activity
     */
    public abstract @LayoutRes int getLayoutRes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        // set toolbar and nav nav_drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            throw new IllegalArgumentException("You passed a layout file without a toolbar, " +
                    "but a toolbar with id \"toolbar\" was expected.");
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    Log.d(TAG, "drawer slide: "+((float)(drawer.getWidth()) * slideOffset)+"    dw: "+ drawer.getWidth());
                    applyDrawerOffsetToUI((float)(drawer.getWidth()) * slideOffset);
                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (drawer == null)
            return false;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // show camera view
        } else if (id == R.id.nav_recorded) {
            // show recorded videos
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called whenever the drawer is opened or closed. Can be used to move certain UI elements
     * accordingly
     * @param offset the offset to apply to other UI components in pixel
     */
    public void applyDrawerOffsetToUI(float offset){
        // intentionally left blank
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
