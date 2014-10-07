package threads.advanced.simpleThreads;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Date;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.*;


public class ThreadTester {

  private int[] posArray = new int[] {1, 3, 6, 3, 4, 2, 5};
  private int[] negArray = new int[] {-2, -8, -3, -9, -10};

  public ThreadTester() {
  }

  public void testBubbleSort(PrintStream out) throws IOException {
    Thread t1 = new BubbleSortThread(posArray);
    t1.start();

    out.println("Testing with postive numbers...");
    // Wait for the thread to complete
    try {
      t1.join();
      printArray(posArray, out);
    } catch (InterruptedException ignored) { }

    Thread t2 = new BubbleSortThread(negArray);
    t2.start();

    out.println("Testing with negative numbers...");
    try {
      t2.join();
      printArray(negArray, out);
    } catch (InterruptedException ignored) { }
  }

  private void printArray(int[] a, PrintStream out) throws IOException {
    for (int n : a) {
      out.println(n);
    }
    out.println();
  }

  public void testQueue(PrintStream out) throws IOException {
    BlockingQueue queue = new LinkedBlockingQueue(10);
    Producer p = new Producer(queue, out);
    Consumer c1 = new Consumer("Consumer 1", queue, out);
    Consumer c2 = new Consumer("Consumer 2", queue, out);
    Consumer c3 = new Consumer("Consumer 3", queue, out);
    Consumer c4 = new Consumer("Consumer 4", queue, out);

    p.start(); c1.start(); c2.start(); c3.start(); c4.start();
    try {
      MILLISECONDS.sleep(100);
    } catch (InterruptedException ignored) { }

    // Finish up with these threads
    p.stop();
    c1.stop(); c2.stop(); c3.stop(); c4.stop();
  }

  public void testCallable(PrintStream out) throws IOException {
    ExecutorService service = Executors.newFixedThreadPool(5);
    Future<BigInteger> prime1 = service.submit(new RandomPrimeSearch(512));
    Future<BigInteger> prime2 = service.submit(new RandomPrimeSearch(512));
    Future<BigInteger> prime3 = service.submit(new RandomPrimeSearch(512));

    try {
      BigInteger bigger = (prime1.get().multiply(prime2.get())).multiply(prime3.get());
      out.println(bigger);
    } catch (InterruptedException e) {
      e.printStackTrace(out);
    } catch (ExecutionException e) {
      e.printStackTrace(out);
    }
  }

  public static void main(String[] args) {
    ThreadTester tester = new ThreadTester();
    try {
      tester.testBubbleSort(System.out);
      tester.testQueue(System.out);
      tester.testCallable(System.out);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

}

class BubbleSortThread extends Thread {

  private int[] numbers;

  public BubbleSortThread(int[] numbers) {
    setName("Simple Thread");
    setUncaughtExceptionHandler(
      new SimpleThreadExceptionHandler());
    this.numbers = numbers;
  }

  public void run() {
    int index = numbers.length;
    boolean finished = false;
    while (!finished) {
      index--;
      finished = true;
      for (int i=0; i<index; i++) {
        // Create error condition
        if (numbers[i+1] < 0) {
          throw new IllegalArgumentException(
            "Cannot pass negative numbers into this thread!");
        }

        if (numbers[i] > numbers[i+1]) {
          // swap
          int temp = numbers[i];
          numbers[i] = numbers[i+1];
          numbers[i+1] = temp;

          finished = false;
        }
      }
    }    
  }
}

class SimpleThreadExceptionHandler implements
    Thread.UncaughtExceptionHandler {

  public void uncaughtException(Thread t, Throwable e) {
    System.err.printf("%s: %s at line %d of %s%n",
        t.getName(), 
        e.toString(),
        e.getStackTrace()[0].getLineNumber(),
        e.getStackTrace()[0].getFileName());
  }
}

class Consumer extends Thread {
  private BlockingQueue q;
  private PrintStream out;

  public Consumer(String name, BlockingQueue q, 
                  PrintStream out) {
    setName(name);
    this.q = q;
    this.out = out;
  }

  public void run() {
    try {
      while (true) {
        process(q.take());
      }
    } catch (InterruptedException e) {
      out.printf("%s interrupted: %s", getName(), e.getMessage());
    }
  }

  private void process(Object obj) {
    out.printf("%s processing object:%n         '%s'%n", 
                getName(), obj.toString());
  }
}



class Producer extends Thread {

  private BlockingQueue q;
  private PrintStream out;

  public Producer(BlockingQueue q, PrintStream out) {
    setName("Producer");
    this.q = q;
    this.out = out;
  }

  public void run() {
    try {
      while (true) {
        q.put(produce());
      }
    } catch (InterruptedException e) { out.printf("%s interrupted: %s", getName(), e.getMessage());
    }
  }

  private String produce() {
    while (true) {
      double r = Math.random();

      // Only goes forward 1/10 of the time
      if ((r*100) < 10) {
        String s = String.format("Inserted at %tc", new Date());
        return s;
      }
    }
  }
}
class RandomPrimeSearch implements Callable<BigInteger> {

  private static final Random prng = new SecureRandom();
  private int bitSize;
  public RandomPrimeSearch(int bitSize) {
    this.bitSize = bitSize;
  }

  public BigInteger call() {
    return BigInteger.probablePrime(bitSize, prng);
  }
}