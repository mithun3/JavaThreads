package threads.advanced.simpleThreads;

public class RunnableThread implements Runnable {
  private int countDown = 5;
  public String toString() {
    return "#" + Thread.currentThread().getName() +
      ": " + countDown;
  }
  public void run() {
    while(true) {
      System.out.println(this);
      if(--countDown == 0) return;
    }
  }
  public static void main(String[] args) {
    for(int i = 1; i <= 5; i++)
      new Thread(new RunnableThread(), "" + i).start();
    // Output is like SimpleThread.java
  }
} ///:~