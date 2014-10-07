package threads.advanced.template;

public class SelfRunThreadTemplate extends Object implements Runnable {
  private Thread internalThread;
  private volatile boolean noStopRequested;

  public SelfRunThreadTemplate() {
    // add your code here ...
    System.out.println("initializing...");

    noStopRequested = true;
    internalThread = new Thread(this);
    internalThread.start();
  }

  public void run() {
    if ( Thread.currentThread() != internalThread ) {
      throw new RuntimeException("only the internal " +
        "thread is allowed to invoke run()");
    }

    while ( noStopRequested ) {
      System.out.println("in run() - still going...");

      try {
        Thread.sleep(700);
      } catch ( InterruptedException x ) {
        Thread.currentThread().interrupt(); 
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
    SelfRunThreadTemplate sr = new SelfRunThreadTemplate();
    try { Thread.sleep(3000); } catch ( InterruptedException x ) { }
    sr.stopRequest();
  }

}