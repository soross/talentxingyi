package talent.xingyi.thread;

public class ControlThread {

	static class JoinThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				System.out.println(getName() + " , i = " + (i++));
			}
		}
	}

	static class YieldThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				System.out.println(getName() + " , i = " + (i++));
				if (i == 20) {
					yield();
				}
			}
		}
	}

	static class DaemonThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 5000; i++) {
				System.out.println(getName() + " , i = " + i);
			}
		}
	}

	public static void main(String[] args) {
		// testJoinThread();

		// testYieldThread();

		testDaemonThread();
	}

	private static void testJoinThread() {
		for (int i = 0; i < 2000; i++) {
			System.out
					.println(Thread.currentThread().getName() + " , i = " + i);
			if (i == 20) {
				JoinThread th1 = new JoinThread();
				th1.start();
				try {
					th1.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void testYieldThread() {
		for (int i = 0; i < 2000; i++) {
			System.out
					.println(Thread.currentThread().getName() + " , i = " + i);
			if (i == 20) {
				JoinThread th1 = new JoinThread();
				th1.start();
			}
		}
	}

	private static void testDaemonThread() {
		for (int i = 0; i < 500; i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			if (i == 20) {
				DaemonThread th1 = new DaemonThread();
				th1.setDaemon(true);
				th1.start();
			}
		}
	}

}
