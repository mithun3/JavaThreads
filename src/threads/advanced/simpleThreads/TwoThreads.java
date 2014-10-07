package threads.advanced.simpleThreads;

public class TwoThreads extends Thread {
  private Thread creatorThread;

  public TwoThreads() {
    creatorThread = Thread.currentThread();
  }

  public void run() {
    for ( int i = 0; i < 10; i++ ) {
      printMsg();
    }
  }

  public void printMsg() {
    Thread t = Thread.currentThread();

    if ( t == creatorThread ) {
      System.out.println("Creator thread");
    } else if ( t == this ) {
      System.out.println("New thread");
    } else {
      System.out.println("Unexpected threads!");
    }
  }

  public static void main(String[] args) {
    TwoThreads tt = new TwoThreads();
    tt.start();

    for ( int i = 0; i < 10; i++ ) {
      tt.printMsg();
    }
  }
} 
 
 
