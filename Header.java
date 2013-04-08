import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;

class Header implements TableCellRenderer 
{
	JLabel label;
	
	Header(String labelstr, Color color)
	{
		label = new JLabel(labelstr);
		label.setBackground(color);
		label.setForeground(Color.black);
		
		label.setOpaque(true);
		label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		return label;
	}
	
	public String getText()
	{
		return label.getText();
	}
	
	public void setShade(boolean shade)
	{
		label.setOpaque(shade);
	}
}
