package threads.advanced.simpleThreads;

public class RaceDemo {

  private final static int NUMRUNNERS = 2;

  public static void main(String[] args) {
    SelfishRunner[] runners = new SelfishRunner[NUMRUNNERS];

    for (int i = 0; i < NUMRUNNERS; i++) {
      runners[i] = new SelfishRunner(i);
      runners[i].setPriority(2);
    }
    for (int i = 0; i < NUMRUNNERS; i++)
      runners[i].start();
  }
}

class SelfishRunner extends Thread {

  private int tick = 1;

  private int num;

  public SelfishRunner(int num) {
    this.num = num;
  }

  public void run() {
    while (tick < 400000) {
      tick++;
      if ((tick % 50000) == 0)
        System.out.println("Thread #" + num + ", tick = " + tick);
    }
  }
}