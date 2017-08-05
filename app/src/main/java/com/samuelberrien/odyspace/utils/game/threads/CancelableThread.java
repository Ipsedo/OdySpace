package com.samuelberrien.odyspace.utils.game.threads;

import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class CancelableThread extends Thread {

	private final long TIME_TO_WAIT = 10L;

	private boolean isCanceled;
	protected Level level;

	CancelableThread(String threadName, Level level) {
		super(threadName);
		this.isCanceled = false;
		this.level = level;
	}

	public void cancel() {
		this.isCanceled = true;
	}

	public boolean isCanceled() {
		return this.isCanceled;
	}

	public abstract void afterInit();

	public abstract void work();

	protected void waitRequiredTime(long t1) {
		try {
			Thread.sleep(TIME_TO_WAIT);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public void run() {
		while (!this.isCanceled && !this.level.isInit()) {
			try {
				Thread.sleep(this.TIME_TO_WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.afterInit();
		while (!this.isCanceled) {
			long t1 = System.currentTimeMillis();
			this.work();
			this.waitRequiredTime(System.currentTimeMillis() - t1);
		}
	}
}
