import java.awt.*;
import java.awt.event.*;

import javax.swing.*; 
import javax.swing.event.*; 
import javax.swing.text.*; 

import java.text.*; 

public class TextFieldDemo extends JFrame {
    //Values for the text fields
    private double amount = 100000;
    private double rate = 7.5;  //7.5 %
    private int numPeriods = 30;
    private double payment = 0.0;

    //Labels to identify the text fields
    private JLabel amountLabel;
    private JLabel rateLabel;
    private JLabel numPeriodsLabel;
    private JLabel paymentLabel;

    //Strings for the labels
    private static String amountString = "Loan Amount: ";
    private static String rateString = "APR (%): ";
    private static String numPeriodsString = "Years: ";
    private static String paymentString = "Monthly Payment: ";

    //Text fields for data entry
    private DecimalField amountField;
    private DecimalField rateField;
    private WholeNumberField numPeriodsField;
    private DecimalField paymentField;

    //Formats to format and parse numbers
    private NumberFormat moneyFormat;
    private NumberFormat percentFormat;
    private DecimalFormat paymentFormat;

    private boolean focusIsSet = false;

    public TextFieldDemo() {
        super("TextFieldDemo");

        setUpFormats();

        payment = computePayment(amount, rate, numPeriods);

        //Create the labels.
        amountLabel = new JLabel(amountString);
        rateLabel = new JLabel(rateString);
        numPeriodsLabel = new JLabel(numPeriodsString);
        paymentLabel = new JLabel(paymentString);

        //Create the text fields and set them up.
        MyDocumentListener myDocumentListener = new MyDocumentListener();

        amountField = new DecimalField(amount, 10, moneyFormat);
        amountField.getDocument().addDocumentListener(myDocumentListener);
        amountField.getDocument().putProperty("name", "amount");

        rateField = new DecimalField(rate, 10, percentFormat);
        rateField.getDocument().addDocumentListener(myDocumentListener);
        rateField.getDocument().putProperty("name", "rate");

        numPeriodsField = new WholeNumberField(numPeriods, 10);
        numPeriodsField.getDocument().addDocumentListener(myDocumentListener);
        numPeriodsField.getDocument().putProperty("name", "numPeriods");

        paymentField = new DecimalField(payment, 10, paymentFormat);
        paymentField.setEditable(false);
        paymentField.setForeground(Color.red);

        //Tell accessibility tools about label/textfield pairs.
        amountLabel.setLabelFor(amountField);
        rateLabel.setLabelFor(rateField);
        numPeriodsLabel.setLabelFor(numPeriodsField);
        paymentLabel.setLabelFor(paymentField);

        //Layout the labels in a panel.
        JPanel labelPane = new JPanel();
        labelPane.setLayout(new GridLayout(0, 1));
        labelPane.add(amountLabel);
        labelPane.add(rateLabel);
        labelPane.add(numPeriodsLabel);
        labelPane.add(paymentLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridLayout(0, 1));
        fieldPane.add(amountField);
        fieldPane.add(rateField);
        fieldPane.add(numPeriodsField);
        fieldPane.add(paymentField);

        //Put the panels in another panel, labels on left,
        //text fields on right.
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(labelPane, BorderLayout.CENTER);
        contentPane.add(fieldPane, BorderLayout.EAST);

        setContentPane(contentPane);
    }

    class MyDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            calculateValue(e);
        }
        public void removeUpdate(DocumentEvent e) {
            calculateValue(e);
        }
        public void changedUpdate(DocumentEvent e) {
            // we won't ever get this with PlainDocument
        }
        private void calculateValue(DocumentEvent e) {
            Document whatsup = e.getDocument();
            if (whatsup.getProperty("name").equals("amount"))
                amount = amountField.getValue();
            else if (whatsup.getProperty("name").equals("rate"))
                rate = rateField.getValue();
            else if (whatsup.getProperty("name").equals("numPeriods"))
                numPeriods = numPeriodsField.getValue();
            payment = computePayment(amount, rate, numPeriods);
            paymentField.setValue(payment);
        }
    }

    public static void main(String[] args) {
        final TextFieldDemo demo = new TextFieldDemo();

        demo.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            //Whenever window gets the focus, let the
            //TextFieldDemo set the initial focus.
            public void windowActivated(WindowEvent e) {
                demo.setFocus();
            }
        });
        demo.pack();
        demo.setVisible(true);
    }

    private void setFocus() {
        if (!focusIsSet) {
            amountField.requestFocus();
            focusIsSet = true;
        }
    }

    // Compute the monthly payment based on the loan amount,
    // APR, and length of loan.
    double computePayment(double loanAmt, double rate, int numPeriods) {
        double I, partial1, denominator, answer;

        I = rate / 100.0 / 12.0;         // get monthly rate from annual
        numPeriods *= 12;        // get number of months
        partial1 = Math.pow((1 + I), (0.0 - numPeriods));
        denominator = (1 - partial1) / I;
        answer = (-1 * loanAmt) / denominator;
        return answer;
    }

    // Create and set up number formats. These objects also
    // parse numbers input by user.
    private void setUpFormats() {
        moneyFormat = NumberFormat.getNumberInstance();
        //XXXX: Workaround. With an empty positive suffix
        //the format allows letters in the number.
        ((DecimalFormat)moneyFormat).setPositiveSuffix(" ");

        percentFormat = NumberFormat.getNumberInstance();
        percentFormat.setMinimumFractionDigits(3);
        //XXXX: Workaround. With an empty positive suffix
        //the format allows letters in the number.
        ((DecimalFormat)percentFormat).setPositiveSuffix(" ");

        paymentFormat = (DecimalFormat)NumberFormat.getNumberInstance();
        paymentFormat.setMaximumFractionDigits(2);
        paymentFormat.setNegativePrefix("(");
        paymentFormat.setNegativeSuffix(")");
    }
}
