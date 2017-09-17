package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;
import org.junit.BeforeClass;
import org.junit.Test;
import misc.RecordProcessor;

public class TestRecordProcessor {
	private static String expectedFromData1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StringBuffer fileContents = new StringBuffer();
		Scanner fileInput = new Scanner(new File("expected1.txt"));
		while (fileInput.hasNextLine())
			fileContents.append(fileInput.nextLine() + "\n");
		expectedFromData1 = fileContents.toString();
		fileInput.close();
	}

	@Test
	public void testFileData1() {
		assertEquals(expectedFromData1, RecordProcessor.printEmployeeRecords("data1.txt"));
	}

	@Test(expected = RuntimeException.class)
	public void testFileData2() {
		assertEquals("File is empty", RecordProcessor.printEmployeeRecords("data2.txt"));
	}

	@Test(expected = NumberFormatException.class)
	public void testFileData3() {
		assertEquals(null, RecordProcessor.printEmployeeRecords("data3.txt"));
	}
}
