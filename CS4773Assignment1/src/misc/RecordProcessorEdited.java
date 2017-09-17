package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Lung Joe, 
 * 		   Jh'on West, 
 * 		   Jonathan Fisher, 
 *	       Jonathan Jimenez
 *
 */
public class RecordProcessorEdited {

	private static String[] firstnames;
	private static String[] lastnames;
	private static String[] employeeTypes;
	private static int[] ages;
	private static double[] pay;
	private static int numberOfEmployees;

	private static File file;
	private static Scanner fileReader;
	private static StringBuffer outputString = new StringBuffer();

	private static int ageSum = 0;
	private static double commissionSum = 0;
	private static double hourlySum = 0;
	private static double salarySum = 0;

	private static int numberOfCommissionPaidEmployees = 0;
	private static int numberOfHourlyPaidEmployees = 0;
	private static int numberOfSalaryPaidEmployees = 0;

	private static int countOfSameName;
	private static int sortIndex;

	/**
	 * @param fileName - name of file that is read
	 * @return 	null - if file is empty
	 * 			outputString - if file is not empty, prints format and 
	 * 						   Employee attributes
	 * 			
	 */
	
	public static String processFile(String fileName) {
		file = new File(fileName);
		fileReader = initializeScanner(file);

		numberOfEmployees = countNonemptyLinesInFile(fileReader);
		initialzeEmployeeAttributeArrays(numberOfEmployees);

		fileReader.close();
		fileReader = initializeScanner(file);

		if (checkIfFileIsEmpty(numberOfEmployees) == true)
			return closeScannerAndExit();

		numberOfEmployees = 0;

		putEmployeesIntoAttributeArraysByReadingFile();
		printFileFormat();
		calculatePaySums();
		putAveragesInOutputString();
		createHashMapsOfNames("First", firstnames);
		createHashMapsOfNames("Last", lastnames);
		fileReader.close();

		return outputString.toString();
	}

	private static int countNonemptyLinesInFile(Scanner scanner) {
		int lineCount = 0;
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			if (currentLine.length() > 0)
				lineCount++;
		}
		return lineCount;
	}

	private static Scanner initializeScanner(File file) {
		try {
			return new Scanner(file);
		} catch (FileNotFoundException err) {
			System.err.println(err.getMessage());

			return null;
		}
	}

	private static void initialzeEmployeeAttributeArrays(int numberOfPeople) {
		firstnames = new String[numberOfPeople];
		lastnames = new String[numberOfPeople];
		ages = new int[numberOfPeople];
		employeeTypes = new String[numberOfPeople];
		pay = new double[numberOfPeople];
	}

	private static boolean checkIfFileIsEmpty(int nonEmptyLinesInFile) {
		if (nonEmptyLinesInFile == 0) {
			System.err.println("No records found in data file");
			return true;
		}
		return false;
	}

	private static String closeScannerAndExit() {
		try {
			fileReader.close();
		} catch (Exception err) {
			// Normal situation; scanner is already closed
		}
		return null;
	}

	public static void putEmployeesIntoAttributeArraysByReadingFile() {
		while (fileReader.hasNextLine()) {
			String currentLine = fileReader.nextLine();
			if (currentLine.length() > 0) {
				sortAllAttributeListsByLastName(currentLine);
				putEmployeeValuesIntoAttributeArrays(sortIndex, currentLine);
			}
		}
	}

	private static void sortAllAttributeListsByLastName(String currentLine) {
		String[] wordsInCurrentLine = currentLine.split(",");
		sortIndex = 0;

		for (; sortIndex < lastnames.length; sortIndex++) {
			if (lastnames[sortIndex] == null)
				break;
			if (lastnames[sortIndex].compareTo(wordsInCurrentLine[1]) > 0) {
				pushEmployeeBackwardsInLists(sortIndex);
				break;
			}
		}
		numberOfEmployees++;
	}

	private static void pushEmployeeBackwardsInLists(int index) {
		for (int i = numberOfEmployees; i > index; i--) {
			firstnames[i] = firstnames[i - 1];
			lastnames[i] = lastnames[i - 1];
			ages[i] = ages[i - 1];
			employeeTypes[i] = employeeTypes[i - 1];
			pay[i] = pay[i - 1];
		}
	}
	
	private static void putEmployeeValuesIntoAttributeArrays(int currentLineIndex, String currentLine) {
		try {
			String[] wordsInCurrentLine = currentLine.split(",");
			firstnames[sortIndex] = wordsInCurrentLine[0];
			lastnames[sortIndex] = wordsInCurrentLine[1];
			employeeTypes[sortIndex] = wordsInCurrentLine[3];
			ages[currentLineIndex] = Integer.parseInt(wordsInCurrentLine[2]);
			pay[currentLineIndex] = Double.parseDouble(wordsInCurrentLine[4]);
		} catch (Exception e) {
			throw new NumberFormatException();
		}
	}

	private static void printFileFormat() {
		outputString.append(String.format("# of people imported: %d\n", firstnames.length));

		outputString.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for (int i = 0; i < 30; i++)
			outputString.append(String.format("-"));

		outputString.append(String.format(" ---  "));
		for (int i = 0; i < 12; i++)
			outputString.append(String.format("-"));

		outputString.append(String.format(" "));
		for (int i = 0; i < 12; i++)
			outputString.append(String.format("-"));

		outputString.append(String.format("\n"));
		for (int i = 0; i < firstnames.length; i++)
			outputString.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstnames[i] + " " + lastnames[i], ages[i], employeeTypes[i], pay[i]));
	}

	private static void calculatePaySums() {
		for (int i = 0; i < firstnames.length; i++) {
			ageSum += ages[i];
			if (employeeTypes[i].equals("Commission")) {
				commissionSum += pay[i];
				numberOfCommissionPaidEmployees++;
			} else if (employeeTypes[i].equals("Hourly")) {
				hourlySum += pay[i];
				numberOfHourlyPaidEmployees++;
			} else if (employeeTypes[i].equals("Salary")) {
				salarySum += pay[i];
				numberOfSalaryPaidEmployees++;
			}
		}
	}

	private static void putAveragesInOutputString() {
		float ageAverage = (float) ageSum / firstnames.length;
		outputString.append(String.format("\nAverage age:         %12.1f\n", ageAverage));

		double commissionAverage = commissionSum / numberOfCommissionPaidEmployees;
		outputString.append(String.format("Average commission:  $%12.2f\n", commissionAverage));

		double hourlyAverage = hourlySum / numberOfHourlyPaidEmployees;
		outputString.append(String.format("Average hourly wage: $%12.2f\n", hourlyAverage));

		double salaryAverage = salarySum / numberOfSalaryPaidEmployees;
		outputString.append(String.format("Average salary:      $%12.2f\n", salaryAverage));
	}

	private static void createHashMapsOfNames(String nameType, String[] nameList) {
		HashMap<String, Integer> hashCountingUniqueNames = new HashMap<String, Integer>();
		countOfSameName = 0;

		for (int i = 0; i < nameList.length; i++) {
			if (hashCountingUniqueNames.containsKey(nameList[i])) {
				hashCountingUniqueNames.put(nameList[i], hashCountingUniqueNames.get(nameList[i]) + 1);
				countOfSameName++;
			} else
				hashCountingUniqueNames.put(nameList[i], 1);
		}
		checkOccuranceOfDuplicateNames(nameType, hashCountingUniqueNames);
	}

	private static void checkOccuranceOfDuplicateNames(String nameType, HashMap<String, Integer> hashCountingUniqueNames) {
		if (countOfSameName > 0) {
			printOccuranceOfDuplicateNames(hashCountingUniqueNames, nameType);
		} else
			outputString.append(String.format("All %s names are unique", nameType.toLowerCase()));
	}

	private static void printOccuranceOfDuplicateNames(HashMap<String, Integer> hashCountingUniqueNames, String nameType) {
		outputString.append(String.format("\n" + nameType + " names with more than one person sharing it:\n"));
		Set<String> set = hashCountingUniqueNames.keySet();
		for (String str : set) {
			if (hashCountingUniqueNames.get(str) > 1) {
				outputString.append(String.format("%s, # people with this name: %d\n", str, hashCountingUniqueNames.get(str)));
			}
		}
	}
}
