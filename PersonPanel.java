import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.*;
import java.text.*;

class PersonPanel extends JPanel implements ActionListener,MouseListener
{
	Person person = null;

	JPanel panel;

    boolean unsavedChanges = false;
	
    JTextField name;
    JTextField email;
    JCheckBox finalized;
	
	JRadioButton expenseSharing;
	JRadioButton laborSharing;

	JRadioButton hourly;
	JRadioButton salary;
	
	JTextField wage;
    JTextField deductions;
    JTextField hoursWorked;
    JTextField adjustedWage;
	
	JTextField desiredHouseQuota;
	JTextField desiredIncomeQuota;
	JTextField minIncomeQuota;
	JTextField maxIncomeQuota;

	JTextField laborNeeds;
	JTextField moneyNeeds;
	
	JTextField estQuota;
	JTextField actualQuota;
	JTextField quotaDiff;
	
	static int labelWidth = 200;

	//Where instance variables are declared:
	JPanel cards;
	final static String HIDEPANEL = "HIDE";
	final static String PERSONPANEL = "PERSON";

    DocumentListener textListener = new DocumentListener() {
	    public void changedUpdate(DocumentEvent e) {
		unsavedChanges = true;
	    }

	    public void insertUpdate(DocumentEvent e) {
		unsavedChanges = true;
	    }

	    public void removeUpdate(DocumentEvent e) {
		unsavedChanges = true;
	    }
	};

	PersonPanel()
	{
		setLayout(new CardLayout());
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.addMouseListener(this);
		//panel.setBorder(BorderFactory.createLineBorder(Color.blue));
		//add(panel, BorderLayout.NORTH);
		JPanel border = new JPanel(new BorderLayout());
		border.add(panel, BorderLayout.NORTH);
		add(border, PERSONPANEL);
		add(new JPanel(), HIDEPANEL);
		
		addSpacer();
		
		name = new JTextField(10);
		name.addActionListener(this);
		name.addMouseListener(this);
		name.getDocument().addDocumentListener(textListener);
		panel.add(new LabelComponent("Name: ", labelWidth, name));
		email = new JTextField(40);
		email.addActionListener(this);
		email.addMouseListener(this);
		panel.add(new LabelComponent("E-mail: ", labelWidth, email));

		finalized = new JCheckBox("Finalized?");
		finalized.addActionListener(this);
		panel.add(new LabelComponent("", labelWidth, finalized));
		
		addSpacer();
		
		expenseSharing = new JRadioButton("Expense Sharing");
		expenseSharing.addActionListener(this);
		panel.add(new LabelComponent("Mode: ", labelWidth, expenseSharing));
		
		laborSharing   = new JRadioButton("Labor/Income Sharing");
		laborSharing.addActionListener(this);
		panel.add(new LabelComponent("", labelWidth, laborSharing));
	
		ButtonGroup mode = new ButtonGroup();
	    mode.add(expenseSharing);
    	mode.add(laborSharing);

	hourly = new JRadioButton("Hourly");
	hourly.addActionListener(this);
	//panel.add(new LabelComponent("Work Type: ", labelWidth, hourly));

	salary = new JRadioButton("Salary");
	salary.addActionListener(this);
	//panel.add(new LabelComponent("", labelWidth, salary));

	ButtonGroup worktype = new ButtonGroup();
	worktype.add(hourly);
	worktype.add(salary);
    	
    	addSpacer();
    	
    	wage = new JTextField(6);
		wage.addActionListener(this);
		wage.addMouseListener(this);
		panel.add(new LabelComponent("Average (Post-tax) Wage: ", labelWidth, wage));
		deductions = new JTextField(6);
		deductions.addActionListener(this);
		deductions.addMouseListener(this);
		panel.add(new LabelComponent("Deductions: ", labelWidth, deductions));
		hoursWorked = new JTextField(6);
		hoursWorked.addActionListener(this);
		hoursWorked.addMouseListener(this);
		panel.add(new LabelComponent("Hours Worked: ", labelWidth, hoursWorked));
		adjustedWage = new JTextField(6);
		adjustedWage.setEnabled(false);
		panel.add(new LabelComponent("Adjusted Wage: ", labelWidth, adjustedWage));
		addSpacer();
		
    	desiredHouseQuota = new JTextField(6);
		desiredHouseQuota.addActionListener(this);
		desiredHouseQuota.addMouseListener(this);
		panel.add(new LabelComponent("Desired House Quota: ", labelWidth, desiredHouseQuota));
		
		addSpacer();
		
		desiredIncomeQuota = new JTextField(6);
		desiredIncomeQuota.addActionListener(this);
		desiredIncomeQuota.addMouseListener(this);
		panel.add(new LabelComponent("Desired Income Quota: ", labelWidth, desiredIncomeQuota));
		
		minIncomeQuota = new JTextField(6);
		//		minIncomeQuota.addActionListener(this);
		minIncomeQuota.setEnabled(false);
		panel.add(new LabelComponent("Min. Income Quota: ", labelWidth, minIncomeQuota));
		
		maxIncomeQuota = new JTextField(6);
		maxIncomeQuota.setEnabled(false);
		//		maxIncomeQuota.addActionListener(this);
		panel.add(new LabelComponent("Max. Income Quota: ", labelWidth, maxIncomeQuota));
		
		addSpacer();
		
		laborNeeds = new JTextField(6);
		laborNeeds.addActionListener(this);
		laborNeeds.addMouseListener(this);
		panel.add(new LabelComponent("Labor Needs: ", labelWidth, laborNeeds));
		
		addSpacer();
		
		moneyNeeds = new JTextField(6);
		moneyNeeds.addActionListener(this);
		moneyNeeds.addMouseListener(this);
		panel.add(new LabelComponent("Money Needs: ", labelWidth, moneyNeeds));
		
		addSpacer();
		
				estQuota = new JTextField(10);
				estQuota.setEnabled(false);
				//panel.add(new LabelComponent("Estimated Quota: ", labelWidth, estQuota));
		
				actualQuota = new JTextField(10);
				actualQuota.setEnabled(false);
				//panel.add(new LabelComponent("Actual Quota: ", labelWidth, actualQuota));
		
				quotaDiff = new JTextField(10);
				quotaDiff.setEnabled(false);
				//panel.add(new LabelComponent("Difference: ", labelWidth, quotaDiff));
		
				addSpacer();
	}
	
	void addSmallSpacer() {panel.add(Box.createRigidArea(new Dimension(0,2)));}
	void addSpacer() {panel.add(Box.createRigidArea(new Dimension(0,8)));}

    public void mouseExited(MouseEvent e) {
	return;
    }

    public void mouseEntered(MouseEvent e) {
	return;
    }

    public void mouseReleased(MouseEvent e) {
	return;
    }

    public void mouseClicked(MouseEvent e) {
	return;
    }

    public void mousePressed(MouseEvent e) {
	if (person == null) return;
	if (e.getSource() instanceof JTextField) {
	    JTextField tf = (JTextField)e.getSource();
	    int offset = tf.viewToModel(e.getPoint());
	    saveFields(tf);
	    tf.setCaretPosition(offset);
	} else {
	    saveFields(e.getSource());
	}
    }

    public void caretUpdate(CaretEvent e) {
	
    }

    public void saveFields() {
	try {
	    person.setWage(Float.parseFloat(wage.getText().replace('$',' ')));
	    person.setDeductions(Float.parseFloat(deductions.getText().replace('$',' ')));
	    person.setHoursWorked(Integer.parseInt(hoursWorked.getText()));
	    person.setLaborSharing(laborSharing.isSelected());
	    person.setFinalized(finalized.isSelected());
	    toggleFields(person, !person.isFinalized, person.isLaborSharing);
	    person.name = name.getText();
	    person.email = email.getText();
	    //person.setDesiredHouseQuota(Integer.parseInt(desiredHouseQuota.getText()));
	    //person.setDesiredIncomeQuota(Integer.parseInt(desiredIncomeQuota.getText()));
	    person.setLaborNeeds(Float.parseFloat(laborNeeds.getText()));
	    person.setMoneyNeeds(Float.parseFloat(moneyNeeds.getText().substring(1)));
	} catch (Exception ex) {}

        setPerson(person);
        firePropertyChange("PersonChanged",null,null); // this will redraw table
    }

	public void saveFields(Object src) {
		try {
			if (src == desiredHouseQuota)
				person.setDesiredHouseQuota(Integer.parseInt(desiredHouseQuota.getText()));
			else if (src == desiredIncomeQuota)
				person.setDesiredIncomeQuota(Integer.parseInt(desiredIncomeQuota.getText()));
			else {
				person.setDesiredHouseQuota(Integer.parseInt(desiredHouseQuota.getText()));
				person.setDesiredIncomeQuota(Integer.parseInt(desiredIncomeQuota.getText()));
			}
		} catch (Exception ex) {}
		saveFields();
	}

	public void actionPerformed(ActionEvent e) {
  		if (person == null) return;
		Object src = e.getSource();
		saveFields(src);
	}
	
	void setPerson(Person p)
	{
		this.person = p;
		
		CardLayout cl = (CardLayout)getLayout();
		if (person == null) {
			cl.show(this, HIDEPANEL);
		}
		else {
			cl.show(this, PERSONPANEL);
			
			name.setText(p.name);
			email.setText(p.email);
			
			expenseSharing.setSelected(!p.isLaborSharing);
			laborSharing.setSelected(p.isLaborSharing);

			finalized.setSelected(p.isFinalized);
			toggleFields(person, !p.isFinalized, p.isLaborSharing);

			hourly.setSelected(p.isHourly);
			salary.setSelected(!p.isHourly);
			
			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			
			wage.setText( formatter.format(p.wage) );
			deductions.setText(formatter.format(p.deductions));
			hoursWorked.setText(String.valueOf(p.hoursWorked));
			adjustedWage.setText(formatter.format(p.calculateAdjustedWage()));
			
			desiredHouseQuota.setText( String.valueOf((int)p.desiredHouseQuota) );
			desiredIncomeQuota.setText( String.valueOf((int)p.desiredIncomeQuota) );
			//minIncomeQuota.setText( String.valueOf((int)p.minIncomeQuota) );
			//maxIncomeQuota.setText( String.valueOf((int)p.maxIncomeQuota) );
	
			laborNeeds.setText( String.valueOf(p.laborNeeds) );
			moneyNeeds.setText( formatter.format(p.moneyNeeds) );
			
			/*
			int estq = (int)( p.estQuota );
			int actq = (int)( p.houseQuota + p.incomeQuota );
			estQuota.setText( String.valueOf( estq ));
			actualQuota.setText( String.valueOf( actq ));
			quotaDiff.setText( String.valueOf( actq - estq ));
			*/
			
			if (p.isLaborSharing) {
				estQuota.setText( String.valueOf( p.estQuota  ));
				actualQuota.setText( String.valueOf( p.houseQuota + p.incomeQuota ));
				quotaDiff.setText( String.valueOf( p.houseQuota + p.incomeQuota - p.estQuota ));
				minIncomeQuota.setText(String.valueOf(p.calculateMinIncomeQuota()));
				maxIncomeQuota.setText(String.valueOf(p.calculateMaxIncomeQuota()));
			} else {
				estQuota.setText("");
				actualQuota.setText("");
				quotaDiff.setText("");
				minIncomeQuota.setText("0");
				maxIncomeQuota.setText("10000");
			}
		}
	}

    void toggleFields(Person p, boolean on, boolean ls) {
	this.person = p;

	expenseSharing.setEnabled(on);
	laborSharing.setEnabled(on);
	laborNeeds.setEnabled(on);
	moneyNeeds.setEnabled(on);

	if (!ls)
	    on = false;

	hourly.setEnabled(on);
	salary.setEnabled(on);
	wage.setEnabled(on);
	deductions.setEnabled(on);
	hoursWorked.setEnabled(on);
	desiredHouseQuota.setEnabled(on);
	desiredIncomeQuota.setEnabled(on);
    }
}
