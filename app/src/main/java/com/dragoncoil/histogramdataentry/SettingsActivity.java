package com.dragoncoil.histogramdataentry;
/*
Copyright 2017, Andrew Lin
All rights reserved.

This software is licensed under the BSD 3-Clause License.
See LICENSE.txt at the root of the project or
https://opensource.org/licenses/BSD-3-Clause
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {
    public final static String EXTRA_HOST = "com.dragoncoil.histogramdataentry.HOST";

    private EditText hostEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hostEdit = (EditText) findViewById(R.id.hostEditText);

        // Restore settings.
        Intent i = getIntent();
        String newHost = i.getStringExtra(EXTRA_HOST);
        if (newHost != null) {
            hostEdit.setText(newHost);
        }
    }

    public void onOkClick(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(EXTRA_HOST, hostEdit.getText().toString());
        startActivity(i);
    }
}
