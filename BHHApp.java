import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.print.*;

/*

todo:
 - keep same percent desire split when needs change
 - negative house quota sometimes????

*/

public class BHHApp extends JFrame
    implements ActionListener, ListSelectionListener, PropertyChangeListener {
	
	static int WIDTH = 500;
	static int HEIGHT = 450;
	static int DIVIDE = 225;
	/*
    private boolean DEBUG = true;
    private JButton quitButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton calcButton;
    private JButton addButton;
    private JButton killButton;		
	*/
	
	private House house;
	TradeTableModel model;
	PersonPanel personPanel;
	JTable table;
	File openFile = null;
	
    public BHHApp() {
        super("Labor Swapping Gizmo");
		
		// TABLE

		model = new TradeTableModel();
		house = new House();
		//house.addPerson();
		model.setHouse(house);
		
        table = new JTable(model);
        ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(this);
    
		Person.setColumns(table, model);
		
		// set column widths
		TableColumn column = null;
		for (int i = 0; i < 8; i++) {
		    column = table.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setPreferredWidth(100);
		    } else {
		        column.setPreferredWidth(70);
		    }
		}
		
		// apply currency renderer
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc;
		tc = tcm.getColumn(4);
		tc.setCellRenderer(new CurrencyRenderer());
      	tc = tcm.getColumn(7);
		tc.setCellRenderer(new CurrencyRenderer());
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(WIDTH, DIVIDE));
        JScrollPane listScrollPane = new JScrollPane(table);
		
		// add adapter
		//ExcelAdapter myAd = new ExcelAdapter(table);

		// MENUBAR
	
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// FILE MENU //
		
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Open...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("load");
		menu.add(menuItem);

		menuItem = new JMenuItem("Save");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("save");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save As...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("saveas");
		menu.add(menuItem);

		menuItem = new JMenuItem("Print...");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("print");
		menu.add(menuItem);

		menuItem = new JMenuItem("Quit");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("quit");
		menu.add(menuItem);
		
//		menuItem = new JMenuItem("test");
//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
//		menuItem.setMnemonic(KeyEvent.VK_T);
//		menuItem.addActionListener(this);
//		menuItem.setActionCommand("test");
//		menu.add(menuItem);
		
		// FILE MENU //
		
		menu = new JMenu("Person");
		//menu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Add Person");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		//menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("add");
		menu.add(menuItem);

		menuItem = new JMenuItem("Remove Person");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		//menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("kill");
		menu.add(menuItem);

		menuItem = new JMenuItem("Move Person Up");
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.ALT_MASK));
		//menuItem.setMnemonic(KeyEvent.VK_U);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("up");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Move Person Down");
		//menuItem.setMnemonic(KeyEvent.VK_D);
		//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.ALT_MASK));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("down");
		menu.add(menuItem);
		
		// BUTTONS
		
		/*
		quitButton = new JButton("Quit");
		quitButton.setActionCommand("quit");
        quitButton.addActionListener(this);

        loadButton = new JButton("Load");
        loadButton.setActionCommand("load");
        loadButton.addActionListener(this);
		
        saveButton = new JButton("Save");
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
		
        calcButton = new JButton("Calculate");
        calcButton.setActionCommand("calc");
        calcButton.addActionListener(this);
		
		addButton = new JButton("Add Person");
        addButton.setActionCommand("add");
        addButton.addActionListener(this);

        killButton = new JButton("Remove Person");
        killButton.setActionCommand("kill");
        killButton.addActionListener(this);
		
		JPanel buttonpanel = new JPanel(new FlowLayout())
        buttonpanel.add(quitButton);
        buttonpanel.add(loadButton);
        buttonpanel.add(saveButton);

        JPanel buttonpanelbottom = new JPanel(new FlowLayout())
        buttonpanel.add(addButton);
        buttonpanel.add(killButton);
        buttonpanel.add(calcButton);
		*/
		
		// PERSON EDIT AREA
		
		personPanel = new PersonPanel();
		personPanel.addPropertyChangeListener(this);
		JScrollPane personScrollPane = new JScrollPane(personPanel);
		//JScrollPane personScrollPane = new JScrollPane(new JPanel());
		personScrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT-DIVIDE));

		
		// SPLIT PANE
		
		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, personScrollPane);
		//splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(DIVIDE);

		//Provide minimum sizes for the two components in the split pane
		//Dimension minimumSize = new Dimension(100, 50);
		//listScrollPane.setMinimumSize(minimumSize);
		//personScrollPane.setMinimumSize(minimumSize);
										
        // Add to content pane
        getContentPane().add(splitPane, BorderLayout.CENTER);
		//getContentPane().add(buttonpanel, BorderLayout.NORTH);
		//getContentPane().add(buttonpanelbottom, BorderLayout.SOUTH);
				
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
		personPanel.saveFields(e.getSource());
		if (personPanel.unsavedChanges) {
		    Object[] options = {"Save and Quit","Quit (without saving)"};
		    int opt = JOptionPane.showOptionDialog(null,"Save your changes?","Unsaved Changes",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null, options, options[0]);
		    switch (opt) {
		    case 0:
			if (openFile != null)
			    try {
				house.save(openFile);
			    } catch(Exception ex) {
				ex.printStackTrace();
			    }
			table.changeSelection(model.getRowCount()-1, -1, false, false);
			break;
		    case 1:
			break;
		    default:
			break;
		    }
		}
                System.exit(0);
            }
        });
        
        //setSize(2000, 2000);
    }

	// button actions:
	public void actionPerformed(ActionEvent e) {
		try
		{
		    personPanel.saveFields(e.getSource());
		    String cmd = e.getActionCommand();
		    if (cmd.equals("quit")) {
			if (personPanel.unsavedChanges) {
			    Object[] options = {"Save and Quit","Quit (without saving)", "Cancel"};
			    int opt = JOptionPane.showOptionDialog(null,"Save your changes?","Unsaved Changes",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null, options, options[2]);
			    switch (opt) {
			    case 0:
				if (openFile != null)
				    house.save(openFile);
                                table.changeSelection(model.getRowCount()-1, -1, false, false);
				break;
			    case 1:
				break;
			    case 2:
				return;
			    default:
				return;
			    }
			}
			System.exit(0);
		    } else if (cmd.equals("load")) {
				final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				CustomFileFilter filter = new CustomFileFilter();
				fc.setFileFilter(filter);				
				int returnVal = fc.showOpenDialog(this);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
        			File file = fc.getSelectedFile();
					house.load(file);
					openFile = file;
					setTitle(openFile.getAbsolutePath());
					model.setHouse(house);
					house.calculate();
				}
				table.changeSelection(model.getRowCount()-1, -1, false, false);
		    } else if (cmd.equals("saveas")) {
		    	if (openFile == null) return;
			
			final JFileChooser fc = new JFileChooser(openFile);
				int returnVal = fc.showSaveDialog(this);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
        			File file = fc.getSelectedFile();
					house.save(file);
					openFile = file;
					setTitle(openFile.getAbsolutePath());
				}
				table.changeSelection(model.getRowCount()-1, -1, false, false);
				personPanel.unsavedChanges = false;
		    } else if (cmd.equals("save")) {
				if (openFile != null)
					house.save(openFile);
				table.changeSelection(model.getRowCount()-1, -1, false, false);
				personPanel.unsavedChanges = false;
		    } else if (cmd.equals("add")) {
				int row = house.addPerson();
				model.fireTableDataChanged();
				table.changeSelection(row, 0, false, false);
		    } else if (cmd.equals("kill")) {
				int row = table.getSelectedRow();
				house.removePerson(house.getPerson(row));
				model.fireTableDataChanged();
				table.changeSelection(row, -1, false, false);
			} else if (cmd.equals("up")) {
				int row = table.getSelectedRow();
				table.clearSelection();
				if (row == model.getRowCount()-1) return; // skip totals
				if (row-1 >= 0)
				{
					model.moveUp(row);
					model.fireTableDataChanged();
					table.changeSelection(row-1, -1, false, false);
				}
			} else if (cmd.equals("down")) {
				int row = table.getSelectedRow();
				table.clearSelection();
				if (row == model.getRowCount()-1) return; // skip totals
				if (row+1 <= model.getRowCount())
				{
					model.moveDown(row);
					model.fireTableDataChanged();
					table.changeSelection(row+1, -1, false, false);
				}
			} else if (cmd.equals("test")) {
				setSize((int)(600*Math.random()+300), (int)(600*Math.random()+300));
				
			}
			else if (cmd.equals("print")) 
			{
				table.clearSelection();
				((TradeTableModel)table.getModel()).setHeaderShade(false);
				PrinterJob printJob = PrinterJob.getPrinterJob();
				//PageFormat pf = job.pageDialog(job.defaultPage());
				TablePrinter printer = new TablePrinter(table);
				printJob.setPrintable(printer);
				if (printJob.printDialog()) {
					try {
						printJob.print();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
		        }
				((TradeTableModel)table.getModel()).setHeaderShade(true);
			}
		}
		catch(Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        int index = lsm.getMinSelectionIndex();
        if (lsm.isSelectionEmpty()) {
            return;
        } else {
         	Person person = house.getPerson(index);
         	personPanel.setPerson(person);
        }
    }

	public void propertyChange(PropertyChangeEvent evt)
	{
		// the personpanel fires one of these when we need to refresh the table.
		model.fireTableDataChanged();	
	}
	
	public void setTitle(String title)
	{
		super.setTitle("Labor Swapping Gizmo - "+title);	
	}
	
    public static void main(String[] args) {
        BHHApp frame = new BHHApp();
        frame.actionPerformed(new ActionEvent(frame, 0, "load"));
        frame.pack();
        frame.setVisible(true);
    }
}
