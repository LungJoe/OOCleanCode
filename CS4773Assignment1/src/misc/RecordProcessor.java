package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RecordProcessor {
    private static String[] firstname;
    private static String[] lastname;
    private static int[] a;
    private static String[] tp;
    private static double[] py;
    private static int count;
    private static File file;
    
    public static void initializeFile(String fileName) {
	file = new File(fileName);
    }
    
    public static Scanner initializeScanner(File file) {
	try {
	    return new Scanner(file);
	} catch (FileNotFoundException err) {
	    System.err.println(err.getMessage());
	    return null;
	}
    }
    
    public static int countLinesInFile(Scanner scanner) {
	count = 0;
	while (scanner.hasNextLine()) {
	    String currentLine = scanner.nextLine();
	    if (currentLine.length() > 0)
		count++;
	}
	return count;
    }
    
    public static String processFile(String fileName) {
	initializeFile(fileName);
	StringBuffer stringBuff = new StringBuffer();
	Scanner scanner = initializeScanner(file);

	count = countLinesInFile(scanner);
	

	firstname = new String[count];
	lastname = new String[count];
	a = new int[count];
	tp = new String[count];
	py = new double[count];
	scanner.close();
	scanner = initializeScanner(file);

	count = 0;
	while (scanner.hasNextLine()) {
	    String l = scanner.nextLine();
	    if (l.length() > 0) {

		String[] words = l.split(",");

		int c2 = 0;
		for (; c2 < lastname.length; c2++) {
		    if (lastname[c2] == null)
			break;

		    if (lastname[c2].compareTo(words[1]) > 0) {
			for (int i = count; i > c2; i--) {
			    firstname[i] = firstname[i - 1];
			    lastname[i] = lastname[i - 1];
			    a[i] = a[i - 1];
			    tp[i] = tp[i - 1];
			    py[i] = py[i - 1];
			}
			break;
		    }
		}

		firstname[c2] = words[0];
		lastname[c2] = words[1];
		tp[c2] = words[3];

		try {
		    a[c2] = Integer.parseInt(words[2]);
		    py[c2] = Double.parseDouble(words[4]);
		} catch (Exception e) {
		    System.err.println(e.getMessage());
		    scanner.close();
		    return null;
		}

		count++;
	    }
	}

	if (count == 0) {
	    System.err.println("No records found in data file");
	    scanner.close();
	    return null;
	}

	// print the rows
	stringBuff.append(String.format("# of people imported: %d\n", firstname.length));

	stringBuff.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
	for (int i = 0; i < 30; i++)
	    stringBuff.append(String.format("-"));
	stringBuff.append(String.format(" ---  "));
	for (int i = 0; i < 12; i++)
	    stringBuff.append(String.format("-"));
	stringBuff.append(String.format(" "));
	for (int i = 0; i < 12; i++)
	    stringBuff.append(String.format("-"));
	stringBuff.append(String.format("\n"));

	for (int i = 0; i < firstname.length; i++) {
	    stringBuff.append(
		    String.format("%-30s %-3d  %-12s $%12.2f\n", firstname[i] + " " + lastname[i], a[i], tp[i], py[i]));
	}

	int sum1 = 0;
	float avg1 = 0f;
	int c2 = 0;
	double sum2 = 0;
	double avg2 = 0;
	int c3 = 0;
	double sum3 = 0;
	double avg3 = 0;
	int c4 = 0;
	double sum4 = 0;
	double avg4 = 0;
	for (int i = 0; i < firstname.length; i++) {
	    sum1 += a[i];
	    if (tp[i].equals("Commission")) {
		sum2 += py[i];
		c2++;
	    } else if (tp[i].equals("Hourly")) {
		sum3 += py[i];
		c3++;
	    } else if (tp[i].equals("Salary")) {
		sum4 += py[i];
		c4++;
	    }
	}
	avg1 = (float) sum1 / firstname.length;
	stringBuff.append(String.format("\nAverage age:         %12.1f\n", avg1));
	avg2 = sum2 / c2;
	stringBuff.append(String.format("Average commission:  $%12.2f\n", avg2));
	avg3 = sum3 / c3;
	stringBuff.append(String.format("Average hourly wage: $%12.2f\n", avg3));
	avg4 = sum4 / c4;
	stringBuff.append(String.format("Average salary:      $%12.2f\n", avg4));

	HashMap<String, Integer> hm = new HashMap<String, Integer>();
	int c1 = 0;
	for (int i = 0; i < firstname.length; i++) {
	    if (hm.containsKey(firstname[i])) {
		hm.put(firstname[i], hm.get(firstname[i]) + 1);
		c1++;
	    } else {
		hm.put(firstname[i], 1);
	    }
	}

	stringBuff.append(String.format("\nFirst names with more than one person sharing it:\n"));
	if (c1 > 0) {
	    Set<String> set = hm.keySet();
	    for (String str : set) {
		if (hm.get(str) > 1) {
		    stringBuff.append(String.format("%s, # people with this name: %d\n", str, hm.get(str)));
		}
	    }
	} else {
	    stringBuff.append(String.format("All first names are unique"));
	}

	HashMap<String, Integer> hm2 = new HashMap<String, Integer>();
	int c21 = 0;
	for (int i = 0; i < lastname.length; i++) {
	    if (hm2.containsKey(lastname[i])) {
		hm2.put(lastname[i], hm2.get(lastname[i]) + 1);
		c21++;
	    } else {
		hm2.put(lastname[i], 1);
	    }
	}

	stringBuff.append(String.format("\nLast names with more than one person sharing it:\n"));
	if (c21 > 0) {
	    Set<String> set = hm2.keySet();
	    for (String str : set) {
		if (hm2.get(str) > 1) {
		    stringBuff.append(String.format("%s, # people with this name: %d\n", str, hm2.get(str)));
		}
	    }
	} else {
	    stringBuff.append(String.format("All last names are unique"));
	}

	// close the file
	scanner.close();

	return stringBuff.toString();
    }

}
