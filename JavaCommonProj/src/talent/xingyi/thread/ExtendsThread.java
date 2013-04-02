package talent.xingyi.thread;

public class ExtendsThread extends Thread {

	private int count;

	public void run() {
		for (int i = 0; i < 100; i++) {
			System.out.println(getName() + " " + (count++));
		}
	}

	public static void main(String[] args) {

		System.out.println(Thread.currentThread().getName());

		for (int i = 0; i < 100; i++) {
			if (i == 20) {
				ExtendsThread th1 = new ExtendsThread();
				th1.setName("Thread[0]");
				th1.start();

				ExtendsThread th2 = new ExtendsThread();
				th2.setName("Thread[1]");
				th2.start();
			}
		}

	}

}
