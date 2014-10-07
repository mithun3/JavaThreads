package threads.advanced.simpleThreads;

import java.util.Timer;
import java.util.TimerTask;

class Stopping extends Thread {
  // Must be volatile:
  private volatile boolean stop = false;

  private int counter = 0;

  public void run() {
    while (!stop && counter < 10000) {
      System.out.println(counter++);
    }
    if (stop)
      System.out.println("Detected stop");
  }

  public void requestStop() {
    stop = true;
  }
}

public class CanStop{
  public static void main(String[] args) {
    final Stopping stoppable = new Stopping();
    stoppable.start();
    new Timer(true).schedule(new TimerTask() {
      public void run() {
        System.out.println("Requesting stop");
        stoppable.requestStop();
      }
    }, 500); // run() after 500 milliseconds
  }
} ///:~