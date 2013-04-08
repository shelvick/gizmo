import java.text.NumberFormat;
import javax.swing.table.DefaultTableCellRenderer;

// from http://www-106.ibm.com/developerworks/library/j-jtable/?dwzone=java

public class CurrencyRenderer extends DefaultTableCellRenderer {

    public CurrencyRenderer() {
      super();
      setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    }

    public void setValue(Object value) {
      if ((value != null) && (value instanceof Number)) {
        Number numberValue = (Number) value;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        value = formatter.format(numberValue.doubleValue());
      } 
      super.setValue(value);
    } 

  } 
