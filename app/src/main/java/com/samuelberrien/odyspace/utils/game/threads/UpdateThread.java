package com.samuelberrien.odyspace.utils.game.threads;

import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class UpdateThread extends CancelableThread {

	public UpdateThread(Level level) {
		super("UpdateThread", level);
		this.setPriority(Thread.NORM_PRIORITY);
	}

	@Override
	public void afterInit() {

	}

	@Override
	public void work() {
		super.level.update();
		/*try {
			Thread.sleep(CancelableThread.TIME_TO_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
	}
}
