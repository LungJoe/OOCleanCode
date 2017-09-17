package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RecordProcessorEdited {

	private static String[] firstnames;
	private static String[] lastnames;
	private static String[] employeeTypes;
	private static int[] ages;
	private static double[] pay;
	private static int numberOfPeople;

	private static File file;
	private static Scanner scanner;
	private static StringBuffer stringBuff = new StringBuffer();

	private static int ageSum = 0;
	private static double commissionSum = 0;
	private static double hourlySum = 0;
	private static double salarySum = 0;

	private static int numberOfCommissionPaidEmployees = 0;
	private static int numberOfHourlyPaidEmployees = 0;
	private static int numberOfSalaryPaidEmployees = 0;

	private static int countOfSameName;
	private static int sortIndex;

	public static String processFile(String fileName) {
		file = new File(fileName);
		scanner = initializeScanner(file);

		numberOfPeople = countNonemptyLinesInFile(scanner);
		initialzeEmployeeAttributeArrays(numberOfPeople);

		scanner.close();
		scanner = initializeScanner(file);

		if (checkIfFileIsEmpty(numberOfPeople) == true)
			return closeAndExit();

		numberOfPeople = 0;

		putEmployeesIntoAttributeArraysByReadingFile();
		printFileFormat();
		calculatePaySums();
		putAveragesInOutputString();
		createHashMapsOfNames("First", firstnames);
		createHashMapsOfNames("Last", lastnames);
		scanner.close();

		return stringBuff.toString();
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

	private static String closeAndExit() {
		try {
			scanner.close();
		} catch (Exception err) {
			// Normal situation; scanner is already closed
		}
		return null;
	}

	public static void putEmployeesIntoAttributeArraysByReadingFile() {
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			if (currentLine.length() > 0) {
				sortAllAttributeListsByLastName(currentLine);
				setEmployeeValues(sortIndex, currentLine);
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
				pushPersonBackwardsInLists(sortIndex);
				break;
			}
		}
		numberOfPeople++;
	}

	private static void pushPersonBackwardsInLists(int index) {
		for (int i = numberOfPeople; i > index; i--) {
			firstnames[i] = firstnames[i - 1];
			lastnames[i] = lastnames[i - 1];
			ages[i] = ages[i - 1];
			employeeTypes[i] = employeeTypes[i - 1];
			pay[i] = pay[i - 1];
		}
	}

	private static void setEmployeeValues(int sortIndex, String currentLine) {
		String[] wordsInCurrentLine = currentLine.split(",");
		firstnames[sortIndex] = wordsInCurrentLine[0];
		lastnames[sortIndex] = wordsInCurrentLine[1];
		employeeTypes[sortIndex] = wordsInCurrentLine[3];

		checkForInvalidValue(sortIndex, wordsInCurrentLine);
	}

	private static void checkForInvalidValue(int currentLineIndex, String[] wordsInCurrentLine) {
		try {
			ages[currentLineIndex] = Integer.parseInt(wordsInCurrentLine[2]);
			pay[currentLineIndex] = Double.parseDouble(wordsInCurrentLine[4]);
		} catch (Exception e) {
			throw new NumberFormatException();
		}
	}

	private static void printFileFormat() {
		stringBuff.append(String.format("# of people imported: %d\n", firstnames.length));

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
		for (int i = 0; i < firstnames.length; i++)
			stringBuff.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstnames[i] + " " + lastnames[i], ages[i], employeeTypes[i], pay[i]));
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
		stringBuff.append(String.format("\nAverage age:         %12.1f\n", ageAverage));

		double commissionAverage = commissionSum / numberOfCommissionPaidEmployees;
		stringBuff.append(String.format("Average commission:  $%12.2f\n", commissionAverage));

		double hourlyAverage = hourlySum / numberOfHourlyPaidEmployees;
		stringBuff.append(String.format("Average hourly wage: $%12.2f\n", hourlyAverage));

		double salaryAverage = salarySum / numberOfSalaryPaidEmployees;
		stringBuff.append(String.format("Average salary:      $%12.2f\n", salaryAverage));
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
			printDuplicateNameOccurance(hashCountingUniqueNames, nameType);
		} else
			stringBuff.append(String.format("All %s names are unique", nameType.toLowerCase()));
	}

	private static void printDuplicateNameOccurance(HashMap<String, Integer> hashCountingUniqueNames, String typeOfName) {
		stringBuff.append(String.format("\n" + typeOfName + " names with more than one person sharing it:\n"));
		Set<String> set = hashCountingUniqueNames.keySet();
		for (String str : set) {
			if (hashCountingUniqueNames.get(str) > 1) {
				stringBuff.append(String.format("%s, # people with this name: %d\n", str, hashCountingUniqueNames.get(str)));
			}
		}
	}
}
