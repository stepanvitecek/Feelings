package eu.vstepik.feelings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import eu.vstepik.feelings.R;

public class AboutActivity extends PreferenceActivity {

	 @SuppressWarnings("deprecation")
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.activity_authors);
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	 }
	 
	 @Override
	    public boolean onOptionsItemSelected(MenuItem menuItem)
	    {       
	        startActivity(new Intent(AboutActivity.this,MainActivity.class)); 
	        return true;
	    }
}
