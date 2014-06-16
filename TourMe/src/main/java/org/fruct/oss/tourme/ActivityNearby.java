package org.fruct.oss.tourme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ActivityNearby extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentNearby fragment = new FragmentNearby(true);
        //fragment.prepareForSeparateActivity();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
