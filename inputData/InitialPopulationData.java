package inputData;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import utilities.FileLineIterator;


/**
 * This class "wraps" ChinaStartupPopulation.csv and supplies a convenient interface to allow access
 * to data.
 */
public class InitialPopulationData {

	private static final String DATA_FILE = "ChinaStartupPopulation.csv";


	private static final List<PersonFromCSVFile> people = readInPopulation();


	private static List<PersonFromCSVFile> readInPopulation() {

		List<PersonFromCSVFile> list = new ArrayList<>(275_000); //assume 275k rows in the data file 

		FileLineIterator iter = new FileLineIterator(DATA_FILE);
		iter.next(); //remove the first line of the CSV file (which labels the columns)

		while (iter.hasNext()) {

			String lineOfCSV = iter.next();

			PersonFromCSVFile p = new PersonFromCSVFile(lineOfCSV);

			list.add(p);
		}

		return list;
	}


	public static Iterator<PersonFromCSVFile> iterator() {
		return people.iterator();
	}


	public static List<PersonFromCSVFile> listOfPeople() {
		return people;
	}
}
