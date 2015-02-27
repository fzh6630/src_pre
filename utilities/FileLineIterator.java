package utilities;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;


/**
 * A FileLineIterator returns one line from a text file each time the "next()" operation is called.
 */
public class FileLineIterator implements Iterator<String> {

	private BufferedReader reader;

	private String nextLine;


	public FileLineIterator(String fileName) {
		this(new File(fileName));
	}


	public FileLineIterator(File textFile) {
		this(makeFileReader(textFile));
	}


	public FileLineIterator(Reader reader) {
		this.reader = new BufferedReader(reader);
		updateNext();
	}


	/**
	 * this method is static so that the above constructor does not reference "this" before the
	 * object is constructed.
	 */
	private static FileReader makeFileReader(File textFile) {
		try {
			return new FileReader(textFile);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.exit(0);
		}
		throw new AssertionError("Could not load textFile :: " + textFile.getName());
	}


	@Override
	public boolean hasNext() {
		return nextLine != null;
	}


	/** @return - The next line from the text file. */
	@Override
	public String next() {

		String returnMe = nextLine;

		updateNext(); //overwrites nextLine;

		return returnMe;
	}


	/** Read the next line from the text file, save that value in the "nextLine" field. */
	private void updateNext() {

		try {
			this.nextLine = reader.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException("A LineIterator cannot manipulate a text file.");
	}
}
