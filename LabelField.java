import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LabelField extends JComponent {
	JTextField field;
	
	LabelField(int length, String text)
	{
		FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
		flow.setVgap(0);
		flow.setHgap(0);
		setLayout(flow);
		//setBorder(BorderFactory.createEmptyBorder());
		field = new JTextField(length);
		//field.setBorder(BorderFactory.createLineBorder(Color.red));
		
		JLabel label = new JLabel(text);
		//label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setPreferredSize(new Dimension(200, 20));
		//label.setMinimumSize(new Dimension(100, 20));
		
		add(label, BorderLayout.CENTER);
		add(field, BorderLayout.EAST);
		
		//setAlignmentX(Component.LEFT_ALIGNMENT);
		//field.setText(getAlignmentX()+"");
		
		//setBorder(BorderFactory.createLineBorder(Color.red));
	}

	public void addActionListener(ActionListener listen)
	{
		field.addActionListener(listen);	
	}
	
	public void setText(String text)
	{
		field.setText(text);	
	}
	
	public String getText()
	{
		return field.getText();	
	}
	
	class Label extends JLabel
	{
		Label(String str){ super(str); }
		
		public Dimension getPreferredSize()
		{
			return new Dimension(200, 20);
		}
	}
}