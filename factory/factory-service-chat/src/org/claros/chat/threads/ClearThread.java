package org.claros.chat.threads;

import org.claros.chat.controllers.QueueController;

//import org.claros.chat.controllers.QueueController;

public class ClearThread extends Thread {
	public void run() {
		try {
			Thread.sleep(1000 * 60 * 2);
			QueueController.fullClear();
		} catch (InterruptedException e1) {
			// do nothing sier
		}

		while (true) {
			try {
				QueueController.fullClear();
				Thread.sleep(1000 * 60 * 60);
			} catch (Throwable e) {
				// do nothing sier
			}
			
		}
	}
	
}
