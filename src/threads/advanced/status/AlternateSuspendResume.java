package threads.advanced.status;

public class AlternateSuspendResume extends Object implements Runnable {

  private volatile int firstVal;

  private volatile int secondVal;

  private volatile boolean suspended;

  public boolean areValuesEqual() {
    return (firstVal == secondVal);
  }

  public void run() {
    try {
      suspended = false;
      firstVal = 0;
      secondVal = 0;
      workMethod();
    } catch (InterruptedException x) {
      System.out.println("interrupted in workMethod()");
    }
  }

  private void workMethod() throws InterruptedException {
    int val = 1;

    while (true) {
      // blocks if suspended is true
      waitWhileSuspended();

      stepOne(val);
      stepTwo(val);
      val++;

      // blocks if suspended is true
      waitWhileSuspended();

      Thread.sleep(200); // pause before looping again
    }
  }

  private void stepOne(int newVal) throws InterruptedException {

    firstVal = newVal;
    // simulate some other lengthy process
    Thread.sleep(300);
  }

  private void stepTwo(int newVal) {
    secondVal = newVal;
  }

  public void suspendRequest() {
    suspended = true;
  }

  public void resumeRequest() {
    suspended = false;
  }

  private void waitWhileSuspended() throws InterruptedException {
    while (suspended) {
      Thread.sleep(200);
    }
  }

  public static void main(String[] args) {
    AlternateSuspendResume asr = new AlternateSuspendResume();

    Thread t = new Thread(asr);
    t.start();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException x) {
    }

    for (int i = 0; i < 10; i++) {
      asr.suspendRequest();

      try {
        Thread.sleep(350);
      } catch (InterruptedException x) {
      }

      System.out.println("dsr.areValuesEqual()=" + asr.areValuesEqual());

      asr.resumeRequest();

      try {
        Thread.sleep((long) (Math.random() * 2000.0));
      } catch (InterruptedException x) {
      }
    }
    System.exit(0);
  }
}