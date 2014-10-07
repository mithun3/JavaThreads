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
import javax.swing.JTextField;

public class BalanceLookupCantCancel extends JPanel {
  private JButton searchB = new JButton("Search");

  private JButton cancelB = new JButton("Cancel Search");

  private JLabel balanceL;

  public BalanceLookupCantCancel() {
    buildGUI();
    hookupEvents();
  }

  private void buildGUI() {
    cancelB.setEnabled(false);

    JPanel innerButtonP = new JPanel();
    innerButtonP.setLayout(new GridLayout(1, -1, 5, 5));
    innerButtonP.add(searchB);
    innerButtonP.add(cancelB);

    JPanel buttonP = new JPanel();
    buttonP.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonP.add(innerButtonP);

    JLabel balancePrefixL = new JLabel("Account Balance:");
    balanceL = new JLabel("BALANCE UNKNOWN");

    JPanel balanceP = new JPanel(new FlowLayout(FlowLayout.CENTER));
    balanceP.add(balancePrefixL);
    balanceP.add(balanceL);

    JPanel northP = new JPanel();
    northP.add(buttonP);
    northP.add(balanceP);

    setLayout(new BorderLayout());
    add(northP, BorderLayout.NORTH);
  }

  private void hookupEvents() {
    searchB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        search();
      }
    });

    cancelB.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelSearch();
      }
    });
  }

  private void search() {
    // better be called by event thread!
    searchB.setEnabled(false);
    cancelB.setEnabled(true);
    balanceL.setText("SEARCHING ...");

    String bal = lookupBalance();
    setBalance(bal);
  }

  private String lookupBalance() {
    try {
      // Simulate a lengthy search that takes 5 seconds
      // to communicate over the network.
      Thread.sleep(5000);

      // result "retrieved", return it
      return "1,234.56";
    } catch (InterruptedException x) {
      return "SEARCH CANCELLED";
    }
  }

  private void setBalance(String newBalance) {
    // better be called by event thread!
    balanceL.setText(newBalance);
    cancelB.setEnabled(false);
    searchB.setEnabled(true);
  }

  private void cancelSearch() {
    System.out.println("in cancelSearch()");
    // Here's where the code to cancel would go if this
    // could ever be called!
  }

  public static void main(String[] args) {
    BalanceLookupCantCancel bl = new BalanceLookupCantCancel();

    JFrame f = new JFrame("Can't Cancel");
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    f.setContentPane(bl);
    f.setSize(400, 150);
    f.setVisible(true);
  }
} 
 
 
