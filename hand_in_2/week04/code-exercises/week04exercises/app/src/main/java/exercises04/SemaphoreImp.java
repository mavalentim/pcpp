// For week 4
// raup@itu.dk * 12/09/2023

package exercises04;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SemaphoreImp {
	private final int capacity;
	private int state; // safely-published because it is initialized to zero (see constructor)
	private Lock lock;
	private Condition condition;

	public SemaphoreImp(int c) {
		capacity = c;
		state = 0;
		lock = new ReentrantLock();
		condition = lock.newCondition();
	}

	public void acquire() throws InterruptedException {
		lock.lock();
		try {
			while (state == capacity) { // only when the state is equal to total capacity, this will
				condition.await(); // this releases the lock while it waits for another thread to let go using
									// signalAll
			}
			state++;
		} finally {
			lock.unlock();
		}
	}

	public void release() {
		lock.lock();
		try {
			state--;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public int getState() {
		return this.state;
	}

	public static void main(String[] args) {
		SemaphoreImp s = new SemaphoreImp(2);
		// int sharedState = 10;

		s.release();

		Thread t1 = new Thread(() -> {
			try {
				s.acquire();
				// sharedState++;
				s.release();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		Thread t2 = new Thread(() -> {
			try {
				s.acquire();

				s.release();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		});

	}
}
