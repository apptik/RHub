package io.apptik.rhub.exampleapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import io.apptik.rhub.exampleapp.workers.ActionHandler;
import io.apptik.rhub.exampleapp.workers.Worker1;
import io.apptik.rhub.exampleapp.workers.Worker2;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataShield dataShield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataShield = DataShield.Inst.get();
        TextView tv1 = (TextView) findViewById(R.id.txt1);
        dataShield.sensorData().subscribe(new Worker1());
        dataShield.sensorData().subscribe(new Worker2(tv1));
        dataShield.actionEvents().subscribe(new ActionHandler(getApplicationContext()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //dataShield.addSensor(Sensors.startAcc(getApplicationContext()));
        } else if (id == R.id.nav_gallery) {
            //dataShield.addSensor(Sensors.startLight(getApplicationContext()));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToggleButton btnLight = (ToggleButton) findViewById(R.id.btnLight);
        ToggleButton btnAcc = (ToggleButton) findViewById(R.id.btnAcc);

        dataShield.addActionEvent(RxCompoundButton.checkedChanges(btnLight).map(aBoolean -> {
            if (aBoolean) return DataShield.Action.LightOn;
            else return DataShield.Action.LightOff;
        }));
        dataShield.addActionEvent(RxCompoundButton.checkedChanges(btnAcc).map(aBoolean -> {
            if (aBoolean) return DataShield.Action.AccOn;
            else return DataShield.Action.AccOff;
        }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataShield.Inst.getHub().clearUpstream();
    }
}