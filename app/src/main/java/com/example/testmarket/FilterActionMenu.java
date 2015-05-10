package com.example.testmarket;

import com.example.testmarket.R;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterActionMenu {

	private LayoutInflater inflater;
	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWN = 1;
	private int viewState = STATE_HIDDEN;
	private int anchorId;
	private ViewGroup inflatedView, viewGroup;
	private Context context;
	TextView textView;
	private View clickView;
	private RadioGroup radiogroup;

	private int screenHeight, screenWidth;

	/**
	 * Instantiates a new balloon menu.
	 * 
	 * @param viewGroup
	 *            the view group
	 * @param clickView
	 *            the click view
	 * @param anchorId
	 *            the anchor id
	 */
	public FilterActionMenu(ViewGroup viewGroup, View clickView, int anchorId,
			int screenWidth, int screenHeight) {
		// Get context

		context = viewGroup.getContext();

		// get inflate service and inflate popup menu layout
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatedView = (ViewGroup) inflater.inflate(R.layout.filter_menu,
				viewGroup, false);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inflatedView
				.getLayoutParams();
		params.setMargins(0, (screenHeight / 12) + 132, 0, 0);
		params.width = (int) (screenWidth * 3) / 4;
		inflatedView.setLayoutParams(params);

		this.screenHeight = screenWidth;
		this.screenWidth = screenHeight;

		// Assign values for class variables
		this.clickView = clickView;
		this.viewGroup = viewGroup;
		this.anchorId = anchorId;

		// Hide popup menu on first display of activity
		hide();

		// set click listener for the view to toggle hide/show on click
		setClickListener();
	}

	/**
	 * Toggle Hide/Show of the popup menu
	 */
	public void toggleMenu() {
		if (viewState == STATE_SHOWN) {
			hide();
		} else if (viewState == STATE_HIDDEN) {
			show();
		}
	}

	/**
	 * Show Menu
	 */
	private void show() {

		// setMenuWidth();

		// position menu
		// update view state to show
		viewState = STATE_SHOWN;

		viewGroup.addView(inflatedView);

		// start animation to the menu to shwo it
		Animation alphaIn = new AlphaAnimation(0.00f, 1.00f);
		alphaIn.setDuration(300);
		inflatedView.startAnimation(alphaIn);
		inflatedView.setVisibility(View.VISIBLE);

	}

	/**
	 * Hide Menu
	 */
	public void hide() {
		viewState = STATE_HIDDEN;
		Animation alphaOut = new AlphaAnimation(1.00f, 0.00f);
		alphaOut.setDuration(300);

		inflatedView.startAnimation(alphaOut);
		inflatedView.setVisibility(View.GONE);
		viewGroup.removeView(inflatedView);
	}

	/**
	 * Attach click listener to the imagebutton
	 */
	private void setClickListener() {
		clickView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// toggle menu display hide/show
				toggleMenu();
			}
		});
	}

	public RadioGroup setRadioGroup() {
		RadioGroup radioGroup = (RadioGroup) inflatedView
				.findViewById(R.id.filter_radiogroup);
		return radioGroup;
	}

}
