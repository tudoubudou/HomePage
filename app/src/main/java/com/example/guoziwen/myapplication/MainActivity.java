package com.example.guoziwen.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoScrollTextView num_h  = (AutoScrollTextView)findViewById(R.id.score_h);
        AutoScrollTextView num_t  = (AutoScrollTextView)findViewById(R.id.score_t);
        AutoScrollTextView num_u  = (AutoScrollTextView)findViewById(R.id.score_u);
        num_h.setMode(AutoScrollTextView.Mode.DOWN);
        num_h.setNumber(0);
        num_h.setTargetNumber(6);
        num_t.setMode(AutoScrollTextView.Mode.DOWN);
        num_t.setNumber(1);
        num_t.setTargetNumber(9);
        num_u.setMode(AutoScrollTextView.Mode.DOWN);
        num_u.setNumber(2);
        num_u.setTargetNumber(8);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
