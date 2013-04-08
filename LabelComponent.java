import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LabelComponent extends JComponent {
	JLabel label;
	LabelComponent(String labelText, int width, JComponent comp)
	{
		FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
		flow.setVgap(0);
		flow.setHgap(0);
		setLayout(flow);

		JLabel label = new JLabel(labelText);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setPreferredSize(new Dimension(width, 20));
		
		add(label, BorderLayout.CENTER);
		add(comp, BorderLayout.EAST);
	}
}