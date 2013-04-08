import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.*;


class TradeTableModel extends AbstractTableModel {

    private boolean DEBUG = true;
	House house;
	Vector headers = new Vector();
	PersonTotals sumRow;
	
	TradeTableModel()
	{
		
	}
	
    public int getColumnCount() {
        return headers.size();
    }
    
    public String getColumnName(int col) {
        return ((Header)headers.get(col)).getText();
    }
    
    public int getRowCount() {
        return house.getHouseSize()+1;
    }

    public Object getValueAt(int row, int col) {
        Person p;
		
		if (row == house.getHouseSize() && sumRow != null)
			return sumRow.getValue(col);
		else
		{
			p = house.getPerson(row);
			if (p == null) return null;
			return p.getValue(col);
		}
    }

	public void setHouse(House house)
	{
		if (house != null)
		{
			this.house = house;
			sumRow = new PersonTotals(house);
		}
		else
		{
			System.out.println("house is null");
			System.exit(0);	
		}
		fireTableDataChanged();
	}
	
	
    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        Person p;
		
        p = house.getPerson(row);
        if (p == null)
        	return false;
        
		return p.editable(col);
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        Person p;
        boolean ret;
			
        p = house.getPerson(row);
        if (p == null)
		{
			return;
			//p = new Person(house);
			//house.setPerson(row, p);
		}
		if (p.setValue(col, value))
        {
        	// fireTableCellUpdated(row, col);
			fireTableDataChanged(); // any cell in the whole table might have changed...        	
		}
    }
	
	void addColumn(JTable table, String colname, java.awt.Color c)
	{
		headers.add(new Header(colname, c));
	}
	
	void setHeaderShade(boolean shade)
	{
		for (Enumeration e = headers.elements() ; e.hasMoreElements() ;)
		{
			Header header = (Header) e.nextElement();
			header.setShade(shade);
		}
	}
	
	void installColumns(JTable table)
	{
		fireTableStructureChanged();
		
		for (Enumeration e = headers.elements() ; e.hasMoreElements() ;)
		{
			Header header = (Header) e.nextElement();
			TableColumn column = table.getColumn(header.getText());
			if (column != null)
				column.setHeaderRenderer(header);
			else
				System.out.println("NO COLUMN ");
		}
	}
	
	void moveUp(int row)
	{	
		if (row <= 0) return; // don't move first row
		
		Person p1 = (Person) house.people.get(row);
		if (p1 == null) return;
		Person p2 = (Person) house.people.get(row-1);
		if (p2 == null) return;
		
		house.people.setElementAt(p1, row-1);
		house.people.setElementAt(p2, row);
	}
	
	void moveDown(int row)
	{	
		if (row >= house.people.size()-1) return; // don't move last row
		
		Person p1 = (Person) house.people.get(row);
		if (p1 == null) return;
		Person p2 = (Person) house.people.get(row+1);
		if (p2 == null) return;
		
		house.people.setElementAt(p1, row+1);
		house.people.setElementAt(p2, row);
		
		
	}
}
