package com.edl.moduleenablemanage;

import com.edl.moduleenablemanage.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class ModuleEnableManager extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ListView) findViewById(R.id.listView1)).setAdapter(ModuleListAdapter.getAdapter(ModuleEnableManager.this));
    }
}
