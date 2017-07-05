package com.samuelberrien.odyspace.utils.main;

import android.app.Activity;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;

/**
 * Created by samuel on 05/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class ViewHelper {

	public static void makeViewTransition(final Activity activity, final View view) {
		final TransitionDrawable transition = (TransitionDrawable) view.getBackground();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				transition.startTransition(120);
			}
		});
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				transition.reverseTransition(120);
			}
		});
	}
}
