import java.io.Serializable;
import java.awt.Color;
import javax.swing.JTable;
import java.util.*;
//import java.lang.Float;

public class Person extends Object implements Serializable
{
	House house;
	String name;
    String email;
	
	// input
	boolean isLaborSharing;
	boolean isFinalized;
	boolean isHourly;

	float desiredHouseQuota;
	float desiredIncomeQuota;
	float minIncomeQuota;
	float maxIncomeQuota;
	float laborNeeds = 40;
	float moneyNeeds = 500;
	float wage = 10;
    float deductions = 0;
    int hoursWorked = 160;
    float adjustedWage;
	
	// derived, calculated by house;
	float houseQuota;
	float incomeQuota;
	float owed;
	float estQuota;
	  // estimated quota based on your personal budget choices and the average wage
	  // = housequota + incomequota/averagewage

	// used internally for calculation
	float tempMax;
	
	Person(House house)
	{
		this.house = house;
		name = "";
		email = "";
		isLaborSharing = true;
		isFinalized = false;
		isHourly = true;
		//minIncomeQuota = 0;
		//maxIncomeQuota = 10000;
	}
	
	//////////////////////////////////////////
	// FINANCIAL MODEL
	
	void setDesiredHouseQuota(float value)
    {
    	if (value < 0) return;
    	setDesiredIncomeQuota((float)estQuota - value);
    }
    
    void setDesiredIncomeQuota(float value)
    {
    	setDesiredIncomeQuota(value, true);
    }
    
    void setDesiredIncomeQuota(float value, boolean update)
    {
    	if (value < 0) return;
    	
    	if (adjustedWage == 0) {
    		desiredHouseQuota = estQuota;
    		desiredIncomeQuota = 0;
    	}
    	else {
    		desiredIncomeQuota = Math.min(value, estQuota);
    		if (desiredIncomeQuota > maxIncomeQuota)
    			desiredIncomeQuota = maxIncomeQuota;
    		else if (desiredIncomeQuota < minIncomeQuota)
    			desiredIncomeQuota = minIncomeQuota;
    		desiredHouseQuota = estQuota - desiredIncomeQuota;
    	}
    	
    	if (update)
    		house.calculate();
    }
	
	void setMinIncomeQuota(float value)
	{
		if (value < 0) return;
		
		if (value > maxIncomeQuota)
			minIncomeQuota = maxIncomeQuota;
		else
			minIncomeQuota = value;
			
		if (desiredIncomeQuota < minIncomeQuota)
			setDesiredIncomeQuota(minIncomeQuota);
		else
			house.calculate();
	}
	
	void setMaxIncomeQuota(float value)
	{
		setMaxIncomeQuota(value, true);
	}
	
	// calls house.calculate if update is true
	void setMaxIncomeQuota(float value, boolean update)
	{
		if (value < 0) return;
		
		if (value < minIncomeQuota)
			maxIncomeQuota = minIncomeQuota;
		else
			maxIncomeQuota = value;
			
		if (desiredIncomeQuota > maxIncomeQuota)
			setDesiredIncomeQuota(maxIncomeQuota, update);
		else if (update)
			house.calculate();
	}

    int calculateMinIncomeQuota() {
	maxIncomeQuota = calculateMaxIncomeQuota();
	return (int)Math.min(maxIncomeQuota,Math.round(house.austerityValue * (houseQuota + incomeQuota)));
    }

    int calculateMaxIncomeQuota() {
	adjustedWage = calculateAdjustedWage();
	if (adjustedWage == 0) return 0;
	return (int)Math.round((adjustedWage * hoursWorked - house.spendingCap) / adjustedWage);
    }

    float calculateAdjustedWage() {
	if (hoursWorked <= 0) return 0;
	return (wage * hoursWorked - deductions) / hoursWorked;
    }
	
	void setLaborSharing(boolean value)
	{
		isLaborSharing = value;
		house.calculate();
	}

	void setFinalized(boolean value)
	{
		isFinalized = value;
	}

    void setHourly(boolean value)
    {
	isHourly = value;
	house.calculate();
    }
	
	void setWage(float value)
	{
		if (value < 0) return;
		
		wage = value;
		house.calculate();
	}

    void setDeductions(float value) {
	if (value < 0) return;

	deductions = value;
	house.calculate();
    }

    void setHoursWorked(int value)
    {
	if (value < 0) return;

	hoursWorked = value;
	house.calculate();
    }
	
	void setLaborNeeds(float value)
	{
		if (value < 0) return;
		
		laborNeeds = value;
		house.calculate();	
	}
	
	void setMoneyNeeds(float value)
	{
		if (value < 0) return;
		
		moneyNeeds = value;
		house.calculate();	
	}
	
	////////////////////////////////////////////
	// GUI STUFF
	
	// columns
	static private final String [] columnNames = {
		"<html><font color=black><b>Name<b></font>", 
		"<html><font color=black><b>Desired<p>House<p>Quota<b></font>",
		"<html><font color=black><b>Desired<p>Income<p>Quota<b></font>",
		"<html><font color=black><b>Labor<p>Needs<b></font>",
		"<html><font color=black><b>Money<p>Needs<b></font>",
		"<html><font color=black><b>House<p>Quota<b></font>",
		"<html><font color=black><b>Income<p>Quota<b></font>",
		"<html><font color=black><b>Owed to<p>House<b></font>"
    };
    
	Object getValue(int col)
	{
		switch (col)
		{
		case 0: return name;
		case 1: return isLaborSharing ? new Integer((int)desiredHouseQuota) : new Integer((int)laborNeeds);
		case 2: return isLaborSharing ? new Integer((int)desiredIncomeQuota) : new Integer(0);
		case 3: return new Float(laborNeeds);
		case 4: return new Float(moneyNeeds);
		case 5: return new Integer((int)houseQuota);
		case 6: return isLaborSharing ? new Integer((int)incomeQuota): new Integer(0);
		case 7: return new Float(owed);
		}
		return null;
	}
	
	// table is redrawn if true is returned.
	boolean setValue(int col, Object value)
	{
		float ival = 0;
		
		if (value instanceof Integer)
			ival = value == null ? 0 : ((Integer)value).intValue();
		else if (value instanceof Float)
			ival = value == null ? 0 : ((Float)value).floatValue();
		
		switch (col)
		{
		case 0: name = (String) value; return false;
		case 1: setDesiredHouseQuota( ival ); return true;
		case 2: setDesiredIncomeQuota( ival ); return true;
		case 3: setLaborNeeds( ival ); return true;
		case 4: setMoneyNeeds( ival ); return true;
		default: return false;
		}
	}
	
	boolean editable(int col)
	{
		if (isLaborSharing && col <= 4)
			return true;
		else if (!isLaborSharing && col <= 4 && col != 2 )
			return true;
		else	
			return false;
	}
	
	static void setColumns(JTable table, TradeTableModel model)
	{
		Color light = new Color(0.8f, 0.8f, 0.8f);
		Color dark = new Color(0.7f, 0.7f, 0.7f);
		
		model.addColumn(table, columnNames[0], light);
		model.addColumn(table, columnNames[1], dark);
		model.addColumn(table, columnNames[2], dark);
		model.addColumn(table, columnNames[3], light);
		model.addColumn(table, columnNames[4], light);
		model.addColumn(table, columnNames[5], dark);		
		model.addColumn(table, columnNames[6], dark);
		model.addColumn(table, columnNames[7], light);
		
		model.installColumns(table);
	}
	
	////////////////////////
	// STORAGE
	
	static private final String [] exportColumns = {
		"Name",
		"Labor Sharing?", 
		"Desired House Quota",
		"Desired Income Quota",
		"Min Income Quota",
		"Max Income Quota",
		"Labor Needs",
		"Money Needs",
		"Post-tax Wage",
		"Finalized?",
		//"Hourly?",
		"E-mail",
		"Deductions",
		"Income Hours Worked"
    };
    
	static String columnNames()
	{
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<exportColumns.length; i++)
		{	 
			sb.append(exportColumns[i]);
			if (i+1 < exportColumns.length)
				sb.append(',');
		}
		return sb.toString();
	}
	
    /*	void fromString(String str)
	{
		StringTokenizer st = new StringTokenizer(str, ",");
		
		try {
			name = st.nextToken();
			isLaborSharing     = Boolean.valueOf(st.nextToken()).booleanValue();
			desiredHouseQuota  = Float.parseFloat( st.nextToken() );
			desiredIncomeQuota = Float.parseFloat( st.nextToken() );
			minIncomeQuota     = Float.parseFloat( st.nextToken() );
			maxIncomeQuota     = Float.parseFloat( st.nextToken() );
			laborNeeds         = Float.parseFloat( st.nextToken() );
			moneyNeeds         = Float.parseFloat( st.nextToken() );
			wage               = Float.parseFloat( st.nextToken() );
			isFinalized = st.hasMoreTokens() ? Boolean.valueOf(st.nextToken()).booleanValue() : false;
			//isHourly = st.hasMoreTokens() ? Boolean.valueOf(st.nextToken()).booleanValue() : true;
			email = st.hasMoreTokens() ? st.nextToken() : "";
			deductions = st.hasMoreTokens() ? Float.parseFloat(st.nextToken()) : 0;
			hoursWorked = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
		}
		catch (Exception e)
		{
			name = e.getMessage();	
		}
		} */

    void fromString(String str) {
	String[] values = str.split(",");
	int size = values.length;
	name = values[0];
	isLaborSharing = Boolean.valueOf(values[1]).booleanValue();
	desiredHouseQuota = Float.parseFloat(values[2]);
	desiredIncomeQuota = Float.parseFloat(values[3]);
	laborNeeds = Float.parseFloat(values[6]);
	moneyNeeds = Float.parseFloat(values[7]);
	wage = Float.parseFloat(values[8]);
	isFinalized = (size > 9) ? Boolean.valueOf(values[9]).booleanValue() : false;
	email = (size > 10) ? values[10] : "";
	deductions = (size > 11) ? Float.parseFloat(values[11]) : 0;
	hoursWorked = (size > 12) ? Integer.parseInt(values[12]) : 0;
    }
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(name);
		sb.append(',');
		sb.append(isLaborSharing);
		sb.append(',');
		sb.append(desiredHouseQuota);
		sb.append(',');
		sb.append(desiredIncomeQuota);
		sb.append(',');
		sb.append(minIncomeQuota);
		sb.append(',');
		sb.append(maxIncomeQuota);
		sb.append(',');
		sb.append(laborNeeds);
		sb.append(',');
		sb.append(moneyNeeds);
		sb.append(',');
		sb.append(wage);
		sb.append(',');
		sb.append(isFinalized);
		//sb.append(',');
		//sb.append(isHourly);
		sb.append(',');
		sb.append(email);
		sb.append(',');
		sb.append(deductions);
		sb.append(',');
		sb.append(hoursWorked);
		
		return sb.toString();
	}
}
