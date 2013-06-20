package org.fruct.oss.tourme;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PrepareActivity extends FragmentActivity {

	static View.OnClickListener nextButtonListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prepare);
		getActionBar().hide();
		
		if (findViewById(R.id.fragment_container_prepare) != null) {
			PrepareOneFragment f = new PrepareOneFragment();
			getFragmentManager().beginTransaction().add(R.id.fragment_container_prepare, f).commit();
		}
		
		// OnClickListener for 'Next' button in each fragment
		nextButtonListener = new View.OnClickListener() {
						
			@Override
			public void onClick(View v) {
				Fragment f = null;
				switch(v.getId()) {
				case(R.id.prepare_1_next):
					f = new PrepareOneFragment();
					break;
				default:
					break;
				}
				
				if (f != null)
					getFragmentManager().beginTransaction().add(R.id.fragment_container_prepare, f).commit();
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.prepare, menu);
		return true;
	}
	
	
	/**
	 * First screen of Welcome\prepare
	 * @author alexander
	 *
	 */
	public static class PrepareOneFragment extends Fragment {
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View view = inflater.inflate(R.layout.fragment_prepare_1, container, false);
			return view;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			
			// Set value of network availability
			Boolean hasNetwork = ConstantsAndTools.isOnline(getActivity());
			TextView networkState = (TextView) view.findViewById(R.id.prepare_1_network);
			Button btnNext = (Button) view.findViewById(R.id.prepare_1_next);
			
			if (hasNetwork) {
				networkState.setText(
						getResources().getString(R.string.prepare_1_network) + " " +
						getResources().getString(R.string.available));
				
				btnNext.setEnabled(true);
				btnNext.setOnClickListener(nextButtonListener);
			} else {				
				// TODO: test
				networkState.setText(
						getResources().getString(R.string.prepare_1_network) + " " +
						getResources().getString(R.string.unavailable) + "\n" +
						getResources().getString(R.string.no_network));				
				
				btnNext.setEnabled(false);
			}			
			
		}
	}

}
