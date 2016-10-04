package io.apptik.rhub.exampleapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
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

import io.apptik.rhub.exampleapp.components.ActionHandler;
import io.apptik.rhub.exampleapp.components.Worker1;
import io.apptik.rhub.exampleapp.components.Worker2;

import static io.apptik.rhub.exampleapp.Shield.Action.AccOff;
import static io.apptik.rhub.exampleapp.Shield.Action.AccOn;
import static io.apptik.rhub.exampleapp.Shield.Action.LightOff;
import static io.apptik.rhub.exampleapp.Shield.Action.LightOn;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Shield shield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shield = Shield.Inst.get();
        TextView tv1 = (TextView) findViewById(R.id.txt1);
        shield.sensorData().subscribe(new Worker1());
        shield.sensorData().subscribe(new Worker2(tv1));
        shield.actionEvents().subscribe(new ActionHandler());

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
            //shield.addSensor(Sensors.startAcc(getApplicationContext()));
        } else if (id == R.id.nav_gallery) {
            //shield.addSensor(Sensors.startLight(getApplicationContext()));
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

        shield.addActionEvent(RxCompoundButton.checkedChanges(btnLight).map(aBoolean -> {
            if (aBoolean) return new Pair<>(LightOn,btnLight.getContext());
            else return new Pair<>(LightOff,btnLight.getContext());
        }));
        shield.addActionEvent(RxCompoundButton.checkedChanges(btnAcc).map(aBoolean -> {
            if (aBoolean) return new Pair<>(AccOn,btnAcc.getContext());
            else return new Pair<>(AccOff,btnAcc.getContext());
        }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Shield.Inst.getHub().clearUpstream();
    }
}
