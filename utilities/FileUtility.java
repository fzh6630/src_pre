package utilities;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.Callable;


/**
 * A collection of useful functions that are all associated with creating/reading/editing files.
 */
public class FileUtility {

	/** Don't let anyone instantiate this class. */
	private FileUtility() {
	}


	/**
	 * Completely redirect the System.out PrintStream to a newly created text file. After this
	 * method has been called nothing more will be written to the console (except for the System.err
	 * stream)
	 */
	public static void redirectSystemOutTo(String fileName) {

		System.out.println("Redirecting System.out to :: " + fileName);

		try {
			PrintStream out = new PrintStream(new FileOutputStream(fileName));
			System.setOut(out);
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not redirect System.out to " + fileName);
			fnfe.printStackTrace();
			throw new IllegalStateException("failed");
		}
	}


	/**
	 * Write a String to a new File.
	 *
	 * @param fileName - The filename for the created file
	 * @param writeMe - The String to write
	 *
	 * @throws java.lang.Exception
	 */
	public static void writeToNewFile(String fileName, String writeMe) throws Exception {

		FileOutputStream fos = new FileOutputStream(fileName);
		PrintWriter dout = new PrintWriter(fos);

		dout.write(writeMe);

		dout.close();
		fos.close();
	}


	/**
	 * Append a String to an (perhaps) existing file.
	 * <p>
	 *
	 * This method is very inefficient if used repeatedly.
	 *
	 * @param fileName - The name of the file you wish to append text to. This fileName can include
	 * a path. However, if a path is include all directories must exist otherwise an error will be
	 * thrown.
	 *
	 * @param writeMe - The text you want to have added to the file.
	 *
	 * @throws Exception - Throws multiple IO related exceptions
	 */
	public static void appendToFile(String fileName, String writeMe) throws Exception {

		FileOutputStream fos = new FileOutputStream(fileName, true);
		PrintWriter dout = new PrintWriter(fos);

		dout.write(writeMe);

		dout.close();
		fos.close();
	}


	/**
	 * Convert a text file into a String. This method reads each line of the file, and appends it to
	 * a StringBuffer (after resinserting the '\n' char that is removed by the readLine() method).
	 * After all lines are read the buffer is returned.
	 *
	 * @param f - A File
	 *
	 * @return A String that contains the entire text of the argument file.
	 * @throws java.lang.Exception
	 */
	public static String readTextFile(File f) throws Exception {
		BufferedReader buffer = new BufferedReader(new FileReader(f));
		StringBuilder stringBuffer = new StringBuilder();
		String line = buffer.readLine();

		while (line != null) {
			stringBuffer.append(line).append("\n");
			line = buffer.readLine();
		}
		return stringBuffer.toString();
	}
}
