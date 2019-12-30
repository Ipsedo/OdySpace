package com.samuelberrien.odyspace.ui.infos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 12/10/17.
 */

public abstract class ItemInfosView extends LinearLayout implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Dialog dialog;

	protected SharedPreferences savedShop;
	protected SharedPreferences savedShip;

	protected String itemName;

	private final Item3DView item3DView;
	protected TextView infos;


	//private Activity parentsActivity;
	private LayoutInflater layoutInflater;

	private LinearLayout selectItemLayout;
	protected TextView titleItemChooser;
	protected RadioGroup radioGroup;

	public ItemInfosView(Activity mActivity) {
		super(mActivity);
		setOrientation(LinearLayout.HORIZONTAL);

		layoutInflater = mActivity.getLayoutInflater();

		savedShop = mActivity.getApplicationContext()
				.getSharedPreferences(getContext().getString(R.string.shop_preferences),
						Context.MODE_PRIVATE);
		savedShip = mActivity.getApplicationContext()
				.getSharedPreferences(getContext().getString(R.string.ship_info_preferences),
						Context.MODE_PRIVATE);

		savedShip.registerOnSharedPreferenceChangeListener(this);
		savedShop.registerOnSharedPreferenceChangeListener(this);

		LinearLayout.LayoutParams layoutParams = getLayoutParams(getContext());


		itemName = loadName();
		item3DView = make3DView(getContext(), itemName);
		makeText();

		addView(item3DView, layoutParams);
		addView(infos, layoutParams);

		setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawer_button));

		dialog = new Dialog(getContext(), R.style.AppTheme);
		makeItemChooser();
		dialog.getWindow()
				.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(selectItemLayout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		setOnClickListener((view) -> {
			Point screenSize = getScreenSize();
			dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);
			selectItemLayout.requestLayout();
			dialog.show();
		});
	}

	protected abstract Item3DView make3DView(Context context, String itemName);

	protected void reinit() {
		itemName = loadName();
		setText();
		item3DView.changeObj(itemName);
	}

	protected abstract String loadName();

	protected abstract void setText();

	private void makeText() {
		infos = new TextView(getContext());
		infos.setGravity(Gravity.CENTER);
		setText();
	}

	protected abstract void fillItemChooser();

	private void makeItemChooser() {
		selectItemLayout = (LinearLayout) layoutInflater
				.inflate(R.layout.select_item_layout,
						findViewById(R.id.select_item_layout));
		radioGroup = selectItemLayout.findViewById(R.id.select_item_radio_group);
		titleItemChooser = selectItemLayout.findViewById(R.id.select_item_text);
		fillItemChooser();
	}

	private void reloadItemChooser() {
		radioGroup.removeAllViews();
		fillItemChooser();
	}


	public void dismissDialog() {
		dialog.dismiss();
	}

	private Point getScreenSize() {
		Display display = getDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	private LinearLayout.LayoutParams getLayoutParams(Context context) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);
		return layoutParams;
	}

	public void setGLViewOnTop(boolean onTop) {
		item3DView.setZOrderOnTop(onTop);
	}

}
