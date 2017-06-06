package com.dragoncoil.histogramdataentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView display;
    private String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        display = (TextView) findViewById(R.id.displayText);
        display.setText("");

        Intent i = getIntent();
        String newHost = i.getStringExtra(SettingsActivity.EXTRA_HOST);
        if (newHost != null) {
            host = newHost;
        }

        if (host == null) {
            loadPreferences();
        } else {
            savePreferences();
        }
    }

    private void loadPreferences() {
        SharedPreferences p = this.getPreferences(Context.MODE_PRIVATE);
        host = p.getString(SettingsActivity.EXTRA_HOST, "");
    }

    private void savePreferences() {
        SharedPreferences p = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putString(SettingsActivity.EXTRA_HOST, host);
        e.apply();
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
        if (id == R.id.action_settings) {
            // Settings menu item clicked.
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra(SettingsActivity.EXTRA_HOST, host);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View v) {
        String label = ((Button) v).getText().toString();
        String value = display.getText().toString();

        switch (label) {
            case "<-":
                int len = value.length();
                if (len > 0) {
                    value = value.substring(0, value.length() - 1);
                }
                break;
            case ".":
                int idx = value.indexOf('.');
                if (idx < 0) {
                    // Decimal point not in value yet.
                    value += label;
                }
                break;
            default:
                value += label;
                break;
        }

        display.setText(value);
    }

    public void onSubmitClick(View v) {
        String value = display.getText().toString();
        Log.i(TAG, "Submit " + value + " to host, " + host);
        display.setText("");
    }
}
