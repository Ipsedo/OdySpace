package com.samuelberrien.odyspace.core.threads;

import com.samuelberrien.odyspace.core.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class CancelableThread extends Thread {

	long timeToWait;

	private boolean isCanceled;
	protected Level level;

	CancelableThread(String threadName, Level level) {
		super(threadName);
		this.isCanceled = false;
		this.level = level;
		timeToWait = 10L;
	}

	public void cancel() {
		this.isCanceled = true;
	}

	public boolean isCanceled() {
		return this.isCanceled;
	}

	public abstract void afterInit();

	public abstract void work();

	private void waitRequiredTime(long t1) {
		try {
			long toWait = timeToWait - t1 >= 0 ? timeToWait - t1 : 0;
			Thread.sleep(toWait);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}

	public void run() {
		while (!this.isCanceled && !this.level.isInit()) {
			try {
				Thread.sleep(this.timeToWait);
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
