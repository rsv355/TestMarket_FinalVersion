package com.example.testmarket;

import com.example.testmarket.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class QuickActionMenu {

	private LayoutInflater inflater;
	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWN = 1;
	private int viewState = STATE_HIDDEN;
	private int anchorId;
	private ViewGroup inflatedView, viewGroup;
	private Context context;
	private View clickView;
	private int POPUP_MENU_HALF_WIDTH = (220 / 2);
	private int MENU_LEFT;
	private Integer menuHeight, menuWidth;
	private QuickActionMenu[] VIEWS_TO_HIDE;
	protected int selectedItem = 1;
	protected ListAdapter adapter;

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
	public QuickActionMenu(ViewGroup viewGroup, View clickView, int anchorId) {
		// Get context
		context = viewGroup.getContext();

		// get inflate service and inflate popup menu layout
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatedView = (ViewGroup) inflater.inflate(
				R.layout.quickaction_menu_wrapper, viewGroup, false);

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
		int clickViewleft = clickView.getLeft();

		if (clickViewleft > POPUP_MENU_HALF_WIDTH) {
			alignMenu(clickViewleft - POPUP_MENU_HALF_WIDTH, -21, 0, 0);

		} else {
			alignMenu(5, 0, 0, 0);
		}

		// position arrow; arrow position is calculated based on the menu itself
		// and not the screen
		int arraowLeft = clickViewleft - this.MENU_LEFT;
		setArowAlign(arraowLeft, 0, 0, 0);

		// update view state to show
		viewState = STATE_SHOWN;

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
	 * Align menu based on user preference
	 */
	private void alignMenu(int left, int top, int right, int bottom) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inflatedView
				.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.BELOW, anchorId);
		params.setMargins(left, top, right, bottom);

		// if width and height are set then add them the view, otherwise use
		// defaults
		if (menuWidth != null)
			params.width = menuWidth;
		if (menuHeight != null)
			params.height = menuHeight;

		viewGroup.addView(inflatedView, params);

		// Add left margin to global varaible to calcualte arrow position later
		this.MENU_LEFT = left;
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

	public void setMenuWidth(int width) {
		menuWidth = convertPXtoDP(width);
		POPUP_MENU_HALF_WIDTH = (menuWidth / 2);
	}

	public void setMenuHeight(int height) {
		menuHeight = convertPXtoDP(height);
	}

	public void setSelectedItem(int position) {
		this.selectedItem = position;
	}

	public void setArowAlign(int left, int top, int right, int bottom) {
		ImageView popupArrow = (ImageView) inflatedView
				.findViewById(R.id.popup_arrow);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) popupArrow
				.getLayoutParams();
		lp.setMargins(left, convertPXtoDP(top), right, bottom);
		popupArrow.setLayoutParams(lp);
		popupArrow.getBackground().setAlpha(180);
	}

	public ListView setList(String[] items, String tag) {
		ListView list = (ListView) inflatedView.findViewById(R.id.popup_list);
		adapter = new ListAdapter(context,
				R.layout.quickaction_menu_single_item,
				R.id.quickaction_single_item, items);
		list.setTag(tag);
		list.setAdapter(adapter);
		return list;
	}

	public ListAdapter getAdapter() {
		return adapter;

	}

	


	private int convertPXtoDP(float dpValue) {
		// The gesture threshold expressed in dp
		// final float GESTURE_THRESHOLD_DP = 16.0f;

		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		int mGestureThreshold = (int) (dpValue * scale + 0.5f);
		return mGestureThreshold;
	}

	public class ListAdapter extends ArrayAdapter<String> {

		private static final int TYPE_COUNT = 2;
		private static final int TYPE_ITEM_NORMAL = 0;
		private static final int TYPE_ITEM_SELECTED = 1;

		public ListAdapter(Context context, int layout, int resource,
				String[] items) {
			super(context, layout, resource, items);
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}

		@Override
		public int getItemViewType(int position) {

			// String item = getItem(position);

			return (position == selectedItem) ? TYPE_ITEM_SELECTED
					: TYPE_ITEM_NORMAL;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View singleItem = super.getView(position, convertView, parent);

			TextView selectedItem = (TextView) singleItem
					.findViewById(R.id.quickaction_selected_item);
			switch (getItemViewType(position)) {
			case TYPE_ITEM_SELECTED:
				selectedItem.setVisibility(View.VISIBLE);
				break;
			case TYPE_ITEM_NORMAL:
				break;
			}

			return singleItem;

		}
	}
}
