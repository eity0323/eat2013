package com.gae.eat2013;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SearchActivity extends Activity {
private View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = getLayoutInflater().inflate(R.layout.searchactivity, null);
		this.setContentView(view);
	}

}
