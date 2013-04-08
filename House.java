import java.util.Vector;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Date;
import java.io.*;
import java.text.NumberFormat;
import javax.mail.*;
import javax.mail.internet.*;

public class House extends Object implements Serializable
{
	Vector people;
	float averageWage;
    double austerityValue = .4;
    double spendingCap = 416.67;
	
	float totalMoneyNeeds;
	float totalLaborNeeds;
	
	// for information only:
	float totalDesiredHouseQuota;
	float totalDesiredIncomeQuota;
	float totalHouseQuota;
	float totalIncomeQuota;
	float totalIncome;

	String errors;
	
//*** CHANGE MAIL CONFIG HERE ***

    String mailsrv = "mail.example.com";
    int mailport = 465;
    String mailfrom = "foobar@example.com";
    String mailuser = "foobar@example.com";
    String mailpass = "supersecretpassword";

//*** END MAIL CONFIG ***

        House()
	{
		people = new Vector();
	}
	
	int getHouseSize()
	{
		return people.size();
	}
	
	Person getPerson(int id)
	{
		if (id >= people.size() )
			return null;
		return (Person) people.get(id);
	}

	// called by gui
	// returns row where person put
	int addPerson()
	{
		Person person = new Person(this);
		people.insertElementAt(person, 0);	
		person.setDesiredHouseQuota(person.laborNeeds);
		return 0;
	}
	
	void removePerson(Person p)
	{
	    if (p == null) return;
		if (p instanceof PersonTotals) return;
		
		people.remove(p);
	}
	
	//void setPerson(int id, Person p)
	//{
	//	if (id+1 >= people.size())
	//		return;
	//	people.setElementAt(p, id);
	//}
	
	/////////////////////////////////
	// FILE STORAGE
	
	void save(File file) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		
		out.write(Person.columnNames());
		out.write('\n');
		
		Person person;
		for (Enumeration e = people.elements(); e.hasMoreElements();)	
		{	
			person = (Person) e.nextElement();
			out.write(person.toString());
			out.write('\n');
		}
		out.close();

		if (isEveryoneFinalized())
                    sendCheckReminders();
	}
	
	void load(File file) throws IOException
	{
		people.clear();
		BufferedReader in = new BufferedReader(new FileReader(file));
		in.readLine(); // get rid of headers (todo: parse column labels and match with data)
		Person person;
		String line;
		while( (line = in.readLine()) != null)
		{
			person = new Person(this);
			people.addElement(person);
			person.fromString(line);
		}
	}
	
	/////////////////////////////////
	// QUOTA DISTRIBUTION
	
	// for vital house data used to calculate people's quota
    void updateNeeds()
    {
		Person person;
		totalMoneyNeeds = 0;
		totalLaborNeeds = 0;
		for (Enumeration e = people.elements(); e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();			
			totalMoneyNeeds         += person.moneyNeeds;
			totalLaborNeeds         += person.laborNeeds;
		}
	}

/*
	double getNetIncome()
	{
		Person person;
		totalMoneyNeeds = 0;
		double totalIncome = 0;
		double netIncome;
		for (Enumeration e = people.elements(); e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();			
			totalMoneyNeeds += person.moneyNeeds;
			if (person.isLaborSharing)
				totalIncome += person.incomeQuota * person.wage;
			else
				totalIncome += person.moneyNeeds;
		}
		netIncome = totalIncome - totalMoneyNeeds;
		return Math.round(netIncome * 1000000) / 1000000; // round to a fixed decimal place. 
	}
*/
	
	double getNetIncome()
	{
		Person person;
		double netIncome = -1 * totalMoneyNeeds;
		for (Enumeration e = people.elements(); e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			netIncome += person.owed;
		}
		//System.out.println("ni: "+netIncome);
		//return netIncome;
		return Math.round(netIncome * 100.0) / 100.0;
		// round to a fixed decimal place. 
		// this is very important!!!
		// if we do not, we might loop forever
	}
	
	void updateAverageWage()
	{
		Person person;
		float wagesum = 0;
		int laborSharers = 0;
		for (Enumeration e = people.elements(); e.hasMoreElements();)	
		{
			person = (Person) e.nextElement();
			if (person.isLaborSharing)
			{
				laborSharers++;
				person.adjustedWage = person.calculateAdjustedWage();
				wagesum += person.adjustedWage;
			}
		}
		averageWage = wagesum/laborSharers;
	}
	
	/**
	
	updateDesires insures that our desired quota actually totals
	to the estimated quota we should have.
	
	it is very important that desired quota == estimated quota
	
	depends:
	  averageWage
	  person.laborNeeds
	  person.moneyNeeds
	result:
	  person.desiredLaborQuota
	  person.desiredIncomeQuota
	  person.estQuota
	**/
	void updateDesires()
	{
		Person p;
		for (Enumeration e = people.elements(); e.hasMoreElements() ;)	
		{
			p = (Person) e.nextElement();			
			
			p.estQuota = p.laborNeeds + p.moneyNeeds/averageWage;
			p.minIncomeQuota = p.calculateMinIncomeQuota();
			p.maxIncomeQuota = p.calculateMaxIncomeQuota();
			p.adjustedWage = p.calculateAdjustedWage();
			
			float percent = ((float)p.desiredIncomeQuota) / (p.desiredIncomeQuota + p.desiredHouseQuota);
			
			p.desiredIncomeQuota = p.estQuota * percent;
			if (p.desiredIncomeQuota > p.maxIncomeQuota)
				p.desiredIncomeQuota = p.maxIncomeQuota;
			else if (p.desiredIncomeQuota < p.minIncomeQuota)
				p.desiredIncomeQuota = p.minIncomeQuota;
			p.desiredHouseQuota = p.estQuota - p.desiredIncomeQuota;
			    		
    		// sanity check for people with no income
    		if (p.adjustedWage == 0) {
    			p.desiredHouseQuota = p.estQuota;
    			p.desiredIncomeQuota = 0;
    		}
		}
	}
	
	// for informational sums
	// nothing depends on these numbers.
	void updateSums()
	{
		Person person;
		totalDesiredHouseQuota = 0;
		totalDesiredIncomeQuota = 0;
		totalHouseQuota = 0;
		totalIncomeQuota = 0;
		totalIncome = 0;
		
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			totalDesiredHouseQuota  += person.desiredHouseQuota;
			totalDesiredIncomeQuota += person.desiredIncomeQuota;
			totalHouseQuota         += person.houseQuota;
			totalIncomeQuota        += person.incomeQuota;
			totalIncome             += person.owed;
		}
	}
	
	/** 
	  depends:
         totalLaborNeeds
         laborSharers
         person.desiredHouseQuota
         person.isLaborSharing
         person.laborNeeds
      result:
      	person.houseQuota
    **/
	boolean distributeHouseQuota()
	{
		Person person;
		float hoursSoFar = 0;
		int lscount = 0; // number of labor sharers
		
		// first, do expense sharers
		// give each house quota equal to their labor needs.
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			if (!person.isLaborSharing) {
				person.houseQuota = person.laborNeeds;
				hoursSoFar += person.houseQuota;
			}
			else {
				lscount ++;
			}
		}
		
		// now do labor sharers
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			if (person.isLaborSharing) {
				person.houseQuota = person.estQuota - person.incomeQuota;
				hoursSoFar += person.houseQuota;
			}
		}
		
		double netQuota = totalLaborNeeds - hoursSoFar;
		// netQuota might be positive or negative. distribute evenly to labor sharers
		
		
		double off = 0; //
		double totaloff = 0; //
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();	
			if (person.isLaborSharing)
			{
				person.houseQuota += netQuota/lscount; // distribute net evenly
				
				// caculate for our next loop how much people
				// are off from their estimated quota.
				off = person.houseQuota + person.incomeQuota - person.estQuota;
				totaloff += off;
			}
		}
		
		// we want to insure that everyone is off from 
		// their estimated quota by the same number of hours, to keep everything fair.
		// also, return true if someone ends up with negative quota.
		
		boolean negativeQuota = false;
		double averageoff = totaloff/lscount;
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();	
			if (person.isLaborSharing) {
				off = person.houseQuota + person.incomeQuota - person.estQuota;
				person.houseQuota += averageoff - off;
				if (person.houseQuota < 0)
					negativeQuota = true;
			}
		}
		
		return negativeQuota;
	}
	
	/**
	depends:
	  
	  person.isLaborSharing
	  person.moneyNeeds
	  person.adjustedWage
	result:
	  person.incomeQuota
	  person.owed
	**/
	void distributeIncomeQuota()
	{
		double moneySoFar = 0;
		HashSet exempt = new HashSet(); // people in this hash are exempt from further quota changes!
		Person person;
		
		// EXPENSE SHARERS
		
		// give each expense sharers quota equal to their labor needs.
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			if (person.isLaborSharing && person.adjustedWage == 0) // non-wage earners cannot have income quota
			{
				person.incomeQuota = 0;
				person.owed = 0;
				exempt.add(person);
			}
			else if (!person.isLaborSharing)
			{
				person.incomeQuota = 0;
				person.owed = person.moneyNeeds;
				moneySoFar += person.moneyNeeds;
				exempt.add(person);
			}
		}

		// LABOR SHARERS
				
		// step 1: give everyone what they want
		
		for (Enumeration e = people.elements() ; e.hasMoreElements() ;)	
		{
			person = (Person) e.nextElement();
			if (exempt.contains(person)) continue;
			
			person.incomeQuota = person.desiredIncomeQuota;
			person.owed = person.incomeQuota * person.adjustedWage;
		}
		
		// step 2: continually increase or decrease people's income quota
		// by safe little steps until we cannot any longer or we have made 
		// up all our deficit.
		
		double netIncome;  // positive if we have a surplus, negative if we have a deficit. our target is zero.
		int nonExemptCount; // number of people we can still adjust
		
		netIncome = getNetIncome();
		nonExemptCount = people.size() - exempt.size();
		//System.out.println("---------------------------------");
		System.out.println("-");
		int i = 0;
		System.out.println("net income: "+netIncome);
		while (netIncome != 0.0 && nonExemptCount > 0) // while we still have a deficit and not everyone is exempt
		{
			if (i++ > 30)
			{
				// sanity check. if we haven't exited yet, we are messed up
				System.out.println("ERROR: infinite loop...");
				break; 
			}
			double wageTotal = 0;
			double possibleIncrease =  100000; // arbitrary big number
			double possibleDecrease = -100000; // arbitrary small number
			
			// first, find averagewage and possible increase or decrease
			for (Enumeration e = people.elements(); e.hasMoreElements();)	
			{	
				person = (Person) e.nextElement();
				//System.out.println(person.name+": "+person.maxIncomeQuota);
				if (exempt.contains(person))
				{
					//System.out.println("exempt: "+person.name);
					continue;
				}
				wageTotal += person.adjustedWage;
				
				if (netIncome > 0) // we need to decrease income quota
				{
					if (person.incomeQuota == person.minIncomeQuota)
						exempt.add(person);
					else
						possibleDecrease = Math.max(possibleDecrease, person.minIncomeQuota - person.incomeQuota);
				}
				if (netIncome < 0) // we need to increase quota
				{
					
					if (person.incomeQuota==person.maxIncomeQuota || person.estQuota-person.incomeQuota<=0 ) {
						// exempt people who have maxed out or who if we increase any more then 
						// they end up with negative house quota, which is not possible.
						exempt.add(person);
					}
					else {
						// we can increase so long as we do not exceed max quota or reduce house quota below zero.
						possibleIncrease = Math.min(possibleIncrease, person.maxIncomeQuota - person.incomeQuota);
						possibleIncrease = Math.min(possibleIncrease, person.estQuota-person.incomeQuota);
					}
				}
			}
			
			double tmpAverageWage = wageTotal/nonExemptCount;
			double desiredChange = -1.0 * netIncome/tmpAverageWage/nonExemptCount;
			double actualChange = 0;
			if (netIncome > 0) // we need to decrease quota
				actualChange = Math.max(possibleDecrease, desiredChange);
			if (netIncome < 0) // we need to increase quota
				actualChange = Math.min(possibleIncrease, desiredChange);
			
			//System.out.println("-");
			//System.out.println("netIncome: "+netIncome);
			//System.out.println("nonExemptCount: "+nonExemptCount);
			//System.out.println("tmpAverageWage: "+tmpAverageWage);
			//System.out.println("desiredChange: "+desiredChange);
			//System.out.println("possibleIncrease: "+possibleIncrease);
			//System.out.println("possibleDecrease: "+possibleDecrease);
			//System.out.println("actualChange: "+actualChange);
			
			if (actualChange == 0) // we are so fucked, so bail
			{
				System.out.println("Target income unreachable!");
				break;
			}
			
			// change everyone's income quota by actual change
			for (Enumeration e = people.elements(); e.hasMoreElements();)	
			{	
				person = (Person) e.nextElement();
				if (exempt.contains(person)) continue;
				
				person.incomeQuota += actualChange;
				person.owed = person.incomeQuota * person.adjustedWage;
				
				//if (person.estQuota - person.incomeQuota < 0)
				//	System.out.println("Yikes!! "+person.name+" has negative house quota");
				//System.out.println(person.name+" "+ (person.estQuota - person.incomeQuota));
				
				if (netIncome > 0 && person.minIncomeQuota == person.incomeQuota)
					exempt.add(person);
				if (netIncome < 0 && person.maxIncomeQuota == person.incomeQuota)
					exempt.add(person);								
				
				// to make sure that people with less than half an hour don't end up 
				// actually getting any quota.
				if (person.incomeQuota < 0.5)
				{
					person.incomeQuota = 0;
					person.owed = 0;
					exempt.add(person);
				}
			}
			nonExemptCount = people.size() - exempt.size();
			netIncome = getNetIncome();
			System.out.println("net income: "+netIncome);
			System.out.println("weight average wage: "+averageWage);
			System.out.flush();
			//try { System.in.read(); } catch (Exception e) {}
		}
	}
	
	void fixNegativeQuota()
	{
		Person person;
		for (Enumeration e = people.elements(); e.hasMoreElements();)	
		{	
			person = (Person) e.nextElement();
			if (person.houseQuota < 0)
			{
				person.setMaxIncomeQuota( (float) Math.floor( person.incomeQuota+person.houseQuota ), false );
				System.out.println(person.name+" limited to "+person.maxIncomeQuota);
				// hopefully, setting the max to be the income quota less the 
				// amount we are under zero with house quota will
				// create a house quota of zero.
			}
		}
	}
	
	void restoreLimits()
	{
		Person person;
		for (Enumeration e = people.elements(); e.hasMoreElements();)	
		{	
			person = (Person) e.nextElement();
			person.setMaxIncomeQuota(person.tempMax, false);
		}
	}
	
	void storeLimits()
	{
		Person person;
		for (Enumeration e = people.elements(); e.hasMoreElements();)	
		{	
			person = (Person) e.nextElement();
			person.tempMax = person.calculateMaxIncomeQuota();
		}
	}
	
	void calculate()
	{
		// the order of these calls is very important!
		storeLimits();
		updateNeeds();
		updateAverageWage();
		updateDesires(); 
		for(int i=0; i<3; i++)
			recursiveCalc();
		restoreLimits();
		updateSums();
	}
	
	void recursiveCalc()
	{
		boolean negativeQuotaExists;
		
		distributeIncomeQuota();
		negativeQuotaExists = distributeHouseQuota(); 
		
		if (negativeQuotaExists)
		{
			System.out.println("entering negative land");
			//System.out.flush();
			fixNegativeQuota();   // prevent negative quota by decreasing max income quota
			//recursiveCalc();
		}
	}

    boolean isEveryoneFinalized()
    {
	boolean finalized = true;
	for(Enumeration e = people.elements(); e.hasMoreElements();) {
	    Person person = (Person) e.nextElement();
	    if (!person.isFinalized)
		finalized = false;
	}
	return finalized;
    }

    private class SMTPAuthenticator extends Authenticator {
	public PasswordAuthentication getPasswordAuthentication() {
	    return new PasswordAuthentication(mailuser, mailpass);
	}
    }

    void mailPerson(String to, String subject, String text) {
	Properties props = new Properties();
	props.put("mail.host", mailsrv);
	props.put("mail.smtps.port", mailport);
	props.put("mail.smtps.ssl.enable", "true");
	props.put("mail.debug", "true");
	props.put("mail.smtps.auth", "true");
	
	SMTPAuthenticator auth = new SMTPAuthenticator();
	Session session = Session.getInstance(props, auth);
	session.setDebug(true);
	try {
	    MimeMessage msg = new MimeMessage(session);
	    msg.setText(text);
	    msg.setSubject(subject);
	    msg.setFrom(new InternetAddress(mailfrom));
	    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	    Transport tr = session.getTransport("smtps");
	    tr.connect(mailsrv, mailport, mailuser, mailpass);
	    tr.sendMessage(msg, msg.getAllRecipients());
	    tr.close();
	} catch (MessagingException mex) {                                                                                                            
            System.out.println("E-mail send failed: " + mex);
	}
    }

    /*    void mailPerson(String to, String subject, String text) {
	//	Properties props = new Properties();
	//	props.put("mail.debug", "true");
	//	props.put("mail.smtps.ssl.enable", "true");
	//	props.put("mail.smtps.port", mailport);
	//	props.put("mail.smtps.auth", "true");
	//	props.put("mail.host", mailsrv);
	//	props.put("mail.from", mailfrom);
	Properties props = System.getProperties();
	Session session = Session.getDefaultInstance(props, null);
	//	Session session = Session.getInstance(props, null);

	try {
	    //	    Transport tr = session.getTransport("smtp");
	    MimeMessage msg = new MimeMessage(session);
	    //tr.connect(mailsrv, mailuser, mailpass);
	    //	    msg.saveChanges();
	    msg.setFrom();
	    msg.setRecipients(Message.RecipientType.TO,to);
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());
	    msg.setText(text);
	    Transport tr = session.getTransport("smtp");
	    tr.connect(mailsrv, mailport, mailuser, mailpass);
	    tr.send(msg);
	    tr.close();
	} catch (MessagingException mex) {
	    System.out.println("E-mail send failed: " + mex);
	}
	}*/

    void sendCheckReminders() {
	for(Enumeration e = people.elements(); e.hasMoreElements();) {
	    Person person = (Person) e.nextElement();
	    if(person.email != "") {
		Float m = (Float)person.getValue(7);
		//		DecimalFormat f = new DecimalFormat("#.##");
		NumberFormat f = NumberFormat.getCurrencyInstance();
		String text = new StringBuilder("Hi ").append(person.name).append("! Your quotas for last month are:\n\nIn-house hours: ").append(person.getValue(5)).append("\nOut-of-house hours: ").append(person.getValue(6)).append("\nMoney: $").append(f.format(m)).append("\n\n<3 Gizmo").toString();
		mailPerson(person.email, "Gizmo Finalized!", text);
	    }
        }
    }
}
