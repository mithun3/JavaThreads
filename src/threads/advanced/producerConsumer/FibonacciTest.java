package threads.advanced.producerConsumer;

import java.util.*;
import java.util.concurrent.*;

public class FibonacciTest {

    public static void main(String[] args) {

        ArrayBlockingQueue<Integer> queue;
        queue = new ArrayBlockingQueue<Integer>(10);
        new FibonacciProducer(queue);

        int nThreads = Integer.parseInt(args[0]);
        for (int i = 0; i < nThreads; i++)
            new FibonacciConsumer(queue);
    }
}
class Fibonacci {
    private ConcurrentMap<Integer, Integer> values =
              new ConcurrentHashMap<Integer, Integer>();

    public int calculate(int x) {
        if (x < 0)
            throw new IllegalArgumentException("positive numbers only");
        if (x <= 1)
            return x;
        return calculate(x-1) + calculate(x-2);
    }

    public int calculateWithCache(int x) {
        Integer key = new Integer(x);
        Integer result = values.get(key);

        if (result == null) {
            result = new Integer(calculate(x));
            values.putIfAbsent(key, result);
        }
        return result.intValue();                
    }

    public int calculateOnlyWithCache(int x) {
        Integer v1 = values.get(new Integer(x-1));
        Integer v2 = values.get(new Integer(x-2));
        Integer key = new Integer(x);
        Integer result = values.get(key);
        if (result != null)
            return result.intValue();

        if ((v1 == null) || (v2 == null))
            throw new IllegalArgumentException("values not in cache");
        result = new Integer(v1.intValue() + v2.intValue());

        values.putIfAbsent(key, result);
        return result.intValue();
    }              

    public void calculateRangeInCache(int x, int y) {
        calculateWithCache(x++);
        calculateWithCache(x++);
        while (x <= y) {
            calculateOnlyWithCache(x++);
        }
    }
}
class FibonacciProducer implements Runnable {
    private Thread thr;
    private BlockingQueue<Integer> queue;

    public FibonacciProducer(BlockingQueue<Integer> q) {
        queue = q;
        thr = new Thread(this);
        thr.start();
    }

    public void run() {
        try {
            for(int x=0;;x++) {
                Thread.sleep(1000);
                queue.put(new Integer(x));
                System.out.println("Produced request " + x);
            }
        } catch (InterruptedException ex) { }
    }
}
class FibonacciConsumer implements Runnable {
    private Fibonacci fib = new Fibonacci();
    private Thread thr;
    private BlockingQueue<Integer> queue;

    public FibonacciConsumer(BlockingQueue<Integer> q) {
        queue = q;
        thr = new Thread(this);
        thr.start();
    }

    public void run() {
        int request, result;
        try {
            while (true) {
                request = queue.take().intValue();
                result = fib.calculateWithCache(request);
                System.out.println("Calculated result of " + result + " from " + request);
            }
        } catch (InterruptedException ex) {

        }
    }
}