package threads.advanced.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SafeCollectionIteration extends Object {
  public static void main(String[] args) {
    List wordList = Collections.synchronizedList(new ArrayList());

    wordList.add("Iterators");
    wordList.add("require");
    wordList.add("special");
    wordList.add("handling");

    synchronized (wordList) {
      Iterator iter = wordList.iterator();
      while (iter.hasNext()) {
        String s = (String) iter.next();
        System.out.println("found string: " + s + ", length="
            + s.length());
      }
    }
  }
}