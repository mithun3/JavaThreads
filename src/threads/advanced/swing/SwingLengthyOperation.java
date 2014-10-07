package threads.advanced.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SwingLengthyOperation extends JPanel {
  private JButton searchBn = new JButton("Search");

  private JButton cancelBn = new JButton("Cancel Search");

  private JLabel balanceL = new JLabel();

  private volatile Thread lookupThread;

  public SwingLengthyOperation() {
    cancelBn.setEnabled(false);

    JPanel innerButtonP = new JPanel();
    innerButtonP.setLayout(new GridLayout(1, -1, 5, 5));
    innerButtonP.add(searchBn);
    innerButtonP.add(cancelBn);

    JPanel buttonP = new JPanel();
    buttonP.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonP.add(innerButtonP);

    JLabel balancePrefixL = new JLabel("Account Balance:");

    JPanel balanceP = new JPanel();
    balanceP.setLayout(new FlowLayout(FlowLayout.CENTER));
    balanceP.add(balancePrefixL);
    balanceP.add(balanceL);

    JPanel northP = new JPanel();
    northP.setLayout(new GridLayout(-1, 1, 5, 5));
    northP.add(buttonP);
    northP.add(balanceP);

    setLayout(new BorderLayout());
    add(northP, BorderLayout.NORTH);

    searchBn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        search();
      }
    });

    cancelBn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelSearch();
      }
    });
  }

  private void search() {
    ensureEventThread();

    searchBn.setEnabled(false);
    cancelBn.setEnabled(true);
    balanceL.setText("SEARCHING ...");

    lookupAsync();
  }

  private void lookupAsync() {
    Runnable lookupRun = new Runnable() {
      public void run() {
        String bal = lookupBalance();
        setBalanceSafely(bal);
      }
    };

    lookupThread = new Thread(lookupRun, "lookupThread");
    lookupThread.start();
  }

  private String lookupBalance() {
    try {
      Thread.sleep(5000);

      return "1,234.56";
    } catch (InterruptedException x) {
      return "SEARCH CANCELLED";
    }
  }

  private void setBalanceSafely(String newBal) {
    final String newBalance = newBal;

    Runnable r = new Runnable() {
      public void run() {
        try {
          setBalance(newBalance);
        } catch (Exception x) {
          x.printStackTrace();
        }
      }
    };

    SwingUtilities.invokeLater(r);
  }

  private void setBalance(String newBalance) {
    ensureEventThread();

    balanceL.setText(newBalance);
    cancelBn.setEnabled(false);
    searchBn.setEnabled(true);
  }

  private void cancelSearch() {
    ensureEventThread();

    cancelBn.setEnabled(false);

    if (lookupThread != null) {
      lookupThread.interrupt();
    }
  }

  private void ensureEventThread() {
    if (SwingUtilities.isEventDispatchThread()) {
      return;
    }

    throw new RuntimeException("only the event "
        + "thread should invoke this method");
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    f.setContentPane(new SwingLengthyOperation());
    f.setSize(400, 150);
    f.setVisible(true);
  }
} 
 
 
