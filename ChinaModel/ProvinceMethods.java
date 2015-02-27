package ChinaModel;


import java.io.File;
import utilities.FileLineIterator;
import java.util.ArrayList;
import java.util.List;


public class ProvinceMethods {

	/**
	 * @return - An array of ProvinceSeed objects that reflect a particular .csv file
	 */
	public static ProvinceSeed[] loadProvinceSeeds(File csvFile) {

		FileLineIterator iter = new FileLineIterator(csvFile);


		//remove the first line of the CSV file (which labels the columns)
		System.out.println("Igonoring :: " + iter.next());

		List<ProvinceSeed> list = new ArrayList<>();

		while (iter.hasNext()) {

			String lineOfCSV = iter.next();

			ProvinceSeed p = new ProvinceSeed(lineOfCSV);

			list.add(p);
		}

		return list.toArray(new ProvinceSeed[0]);
	}
}
