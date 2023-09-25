// For week 1
// raup@itu.dk * 2021-08-27
package exercises01;

import java.util.concurrent.locks.ReentrantLock;

public class CounterThreads2Covid {
	ReentrantLock lock = new ReentrantLock();
	long counter = 0;
	final long PEOPLE = 10_000;
	final long MAX_PEOPLE_COVID = 15_000;

	public CounterThreads2Covid() {

		try {
			Turnstile turnstile1 = new Turnstile();
			Turnstile turnstile2 = new Turnstile();

			turnstile1.start();
			turnstile2.start();
			turnstile1.join();
			turnstile2.join();

			System.out.println(counter + " people entered");
		} catch (InterruptedException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new CounterThreads2Covid();
	}

	public class Turnstile extends Thread {
		public void run() {
			for (int i = 0; i < PEOPLE; i++) {
				// ADDED AN IF CONDITION SO THAT COUNTER CAN ONLY BE INCREASED IF IT IS LESS
				// THAN MAX_PEOPLE_COVID BUT IT HAS TO BE ONE TIME LESS THAN THAT SO THAT THE
				// LAST INCREMENT IS DONE
				if (counter < MAX_PEOPLE_COVID - 1) {
					// NOW I WILL WRAP THIS AROUND LOCKS SO ONLY ONE TURNSTILE CAN INCREMENT AT A
					// 14999 PEOPLE AND THE FINAL VALUE IS 15000
					lock.lock();
					counter++;
					lock.unlock();
				}

			}
		}
	}
}
