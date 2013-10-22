package com.dsusin.android.simplemind;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.Window;

public class SimpleMindActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new SimpleMindFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}
}
