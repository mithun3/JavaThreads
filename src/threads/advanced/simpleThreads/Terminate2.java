package threads.advanced.simpleThreads;

public class Terminate2
{
   public static void main (String [] args)
   {
      ThreadB tb = new ThreadB ();
      ThreadA ta = new ThreadA (tb);

      tb.start ();
      ta.start ();
   }
}

class ThreadA extends Thread
{
   private ThreadB tb;

   ThreadA (ThreadB tb)
   {
      this.tb = tb;
   }

   public void run ()
   {
      try
      {
          Thread.sleep (5000);
      }
      catch (InterruptedException e)
      {
      }

      tb.finished = true;
   }
}

class ThreadB extends Thread
{
   public volatile boolean finished = false;

   private int sum = 0;

   public void run ()
   {
      while (!finished)
      {
         System.out.println ("sum = " + sum++);
         Thread.yield ();
      }
   }
}