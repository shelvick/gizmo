
public class PersonTotals extends Person
{   
	PersonTotals(House house) {super(house);}
	
	Object getValue(int col)
	{
		switch (col)
		{
		case 0: return "House Totals";
		case 1: return new Integer((int)house.totalDesiredHouseQuota);
		case 2: return new Integer((int)house.totalDesiredIncomeQuota);
		case 3: return new Integer((int)house.totalLaborNeeds);
		case 4: return new Integer((int)house.totalMoneyNeeds);
		case 5: return new Integer((int)house.totalHouseQuota);
		case 6: return new Integer((int)house.totalIncomeQuota);
		case 7: return new Float(house.totalIncome);
		}
		return null;
	}
	
	boolean setValue(int col, Object value) {return false;}
	boolean editable(int col) {return false;}
}
