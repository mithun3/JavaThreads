package threads.advanced.simpleThreads;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Runnable {
  long n;

  String id;

  private long fib(long n) {
    if (n == 0)
      return 0L;
    if (n == 1)
      return 1L;
    return fib(n - 1) + fib(n - 2);
  }

  public Task(long n, String id) {
    this.n = n;
    this.id = id;
  }

  public void run() {
    Date d = new Date();
    DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
    long startTime = System.currentTimeMillis();
    d.setTime(startTime);
    System.out.println("Starting task " + id + " at " + df.format(d));
    fib(n);
    long endTime = System.currentTimeMillis();
    d.setTime(endTime);
    System.out.println("Ending task " + id + " at " + df.format(d)
        + " after " + (endTime - startTime) + " milliseconds");
  }

  public static void main(String[] args) {
    int nThreads = Integer.parseInt(args[0]);
    long n = Long.parseLong(args[1]);
    Thread t[] = new Thread[nThreads];

    for (int i = 0; i < t.length; i++) {
      t[i] = new Thread(new Task(n, "Task " + i));
      t[i].start();
    }
    for (int i = 0; i < t.length; i++) {
      try {
        t[i].join();
      } catch (InterruptedException ie) {
      }
    }
  }

}