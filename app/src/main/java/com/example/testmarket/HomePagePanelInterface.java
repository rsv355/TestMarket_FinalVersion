package com.example.testmarket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public interface HomePagePanelInterface {

	public void onCreate(Context context, Activity parentActivity);

	public void hide();
	
	public void show();

	Dialog onCreateDialog(int id);
}
