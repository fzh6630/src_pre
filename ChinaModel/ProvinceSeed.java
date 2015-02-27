package ChinaModel;


import com.google.common.collect.ComparisonChain;
import java.util.Arrays;


/**
 * ProvinceSeeds are Immutable objects that "know" the initial state of a Province.
 *
 * ProvinceSeeds are currently sorted by startingPopulation, then province number
 */
public class ProvinceSeed implements Comparable<ProvinceSeed> {
	//*****CRITICAL
	//**** EXOGENOUS PARAMETERS FOR TESTING, DO NOT IGNORE!!!!!

	public static int NUM_YEARS = 5;
	///**********

	private final ChineseProvince provinceNum;

	private final String name;

	//private final int ruralYouth;

	private final int firmCount;

	private final double birthRate;

	private final double[] fdiValues;


	public ProvinceSeed(String lineOfCSVFile) {
		//separate the 1st, 2nd, 3rd,... entries in the CSV file into different array entries
		String[] parts = lineOfCSVFile.split(",");

		this.provinceNum = ChineseProvince.provinceFromID(Integer.parseInt(parts[0]));
		this.name = parts[1];
		//this.ruralYouth = Integer.parseInt(parts[2]);
		this.firmCount = Integer.parseInt(parts[6]);
		this.birthRate = Double.parseDouble(parts[7]);

		String[] fdiStrings = Arrays.copyOfRange(parts, 8, 13);
		this.fdiValues = toDoubles(fdiStrings);

//		this.education_Mean = Double.parseDouble(parts[18]);
//		this.education_SD = Double.parseDouble(parts[19]);
	}


	@Override
	public int compareTo(ProvinceSeed t) {

		return ComparisonChain.start()
				.compare(this.provinceNum, t.provinceNum)
				.result();
	}


	public ChineseProvince provinceName() {
		return this.provinceNum;
	}


	public int firmCount() {
		return this.firmCount;
	}


	public double[] fdiValues1995to1999() {
		return this.fdiValues;
	}


	/**
	 * Convert the Strings (extracted from the CSV file) that represent Foreign Direct Investment
	 * by year to doubles.
	 */
	private double[] toDoubles(String[] fdiStrings) {

		double[] output = new double[fdiStrings.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = Double.parseDouble(fdiStrings[i]);
		}

		return output;
	}
}
