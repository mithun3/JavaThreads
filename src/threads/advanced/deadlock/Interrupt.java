package threads.advanced.deadlock;

import java.util.*;

class Blocked extends Thread {
  public Blocked() {
    System.out.println("Starting Blocked");
    start();
  }
  public void run() {
    try {
      synchronized(this) {
		  System.out.println("Helloooooooooooooooooooooo");
        wait(); // Blocks
		  System.out.println("HelloooooooHHHHHooooooooo");
      }
    } catch(InterruptedException e) {
      System.out.println("Interrupted");
    }
    System.out.println("Exiting run()");
  }
}

public class Interrupt {
  static Blocked blocked = new Blocked();
  public static void main(String[] args) {
    new Timer(true).schedule(new TimerTask() {
      public void run() {
        System.out.println("Preparing to interrupt");
        blocked.interrupt();
        blocked = null; // to release it
      }
    }, 2000); // run() after 2000 milliseconds
  }
} ///:~