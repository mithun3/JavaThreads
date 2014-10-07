package threads.advanced.template;

public class InnerSelfRunMain extends Object {
  private Thread internalThread;

  private volatile boolean noStopRequested;

  public InnerSelfRunMain() {
    // add your code here ...
    System.out.println("initializing...");

    noStopRequested = true;

    Runnable r = new Runnable() {
      public void run() {
        try {
          runWork();
        } catch (Exception x) {
          // in case ANY exception slips through
          x.printStackTrace();
        }
      }
    };

    internalThread = new Thread(r);
    internalThread.start();
  }

  private void runWork() {
    while (noStopRequested) {
      System.out.println("in runWork() - still going...");

      try {
        Thread.sleep(700);
      } catch (InterruptedException x) {
        Thread.currentThread().interrupt(); // re-assert interrupt
      }
    }
  }

  public void stopRequest() {
    noStopRequested = false;
    internalThread.interrupt();
  }

  public boolean isAlive() {
    return internalThread.isAlive();
  }

  public static void main(String[] args) {
    InnerSelfRunMain sr = new InnerSelfRunMain();

    try {
      Thread.sleep(3000);
    } catch (InterruptedException x) {
    }
    sr.stopRequest();
  }
}