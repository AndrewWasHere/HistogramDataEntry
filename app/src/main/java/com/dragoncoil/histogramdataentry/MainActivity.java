package com.dragoncoil.histogramdataentry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

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
                    if (value.length() == 0) {
                        // Prepend a zero if decimal point is first character.
                        value = "0";
                    }
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

        if (!value.equals("") && !value.equals("0.") ) {
            Log.v(TAG, "Submitting " + value);
            toggleInterface(false);
            String sending = "Submitting...";
            display.setText(sending);
            sendToHost(value);
        }
    }

    private void sendToHost(String data) {
        class SendToHostTask extends AsyncTask<String, Integer, Boolean> {
            private final String TAG = SendToHostTask.class.getSimpleName();

            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    URL url = new URL("http://" + host + "/data");
                    Log.v(TAG, url.toString());
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("POST");
                    c.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(c.getOutputStream());
                    wr.writeBytes("value=" + strings[0]);
                    wr.flush();
                    wr.close();

                    int responseCode = c.getResponseCode();
                    Log.v(TAG, "HTTP response code = " + responseCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                display.setText("");
                toggleInterface(true);
            }
        }

        new SendToHostTask().execute(data);
    }

    private void toggleInterface(boolean enabled) {
        for (
            int id : Arrays.asList(
                R.id.zeroButton,
                R.id.oneButton,
                R.id.twoButton,
                R.id.threeButton,
                R.id.fourButton,
                R.id.fiveButton,
                R.id.sixButton,
                R.id.sevenButton,
                R.id.eightButton,
                R.id.nineButton,
                R.id.decimalButton,
                R.id.backspaceButton,
                R.id.submitButton
            )
        ) {
            Button b = (Button) findViewById(id);
            b.setEnabled(enabled);
        }
    }
}
