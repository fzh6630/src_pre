package dataCollection;


import ChinaModel.ChineseProvince;
import ChinaModel.ParameterSet;
import java.util.Collection;
import java.util.HashMap;
import utilities.FileUtility;


public class DataCollector {

	private HashMap<ChineseProvince, ModelingData> dataByProvince;


	public DataCollector() {
		dataByProvince = new HashMap<>();
		//add 1 empty ModelingData object for each province, prevent Null Pointers when adding data
		for (ChineseProvince province : ChineseProvince.values()) {
			dataByProvince.put(province, new ModelingData(province));
		}
	}


	/** Record a "distanceMoved" value for each person living in a particular province. */
	public void addDistanceMoved(Collection<Double> distances, ChineseProvince province) {
		ModelingData md = dataByProvince.get(province);
		md.addDistances(distances);
	}


	/** Record a "wage" value for each person living in a particular province. */
	public void addWages(Collection<Double> wages, ChineseProvince province) {
		ModelingData md = dataByProvince.get(province);
		md.addWages(wages);
	}


	/** Record a "isFarmer" value for each person living in a particular province. */
	public void addEmployment(Collection<Boolean> isFarmer, ChineseProvince province) {
		ModelingData md = dataByProvince.get(province);
		md.addEmployment(isFarmer);
	}


	/** Record the population of a particular province. */
	public void addPopulation(int population, ChineseProvince province) {
		ModelingData md = dataByProvince.get(province);
		md.addPopulation(population);
	}


	/** Print a report listing all known data for the current run. */
	public void printReport(int runNumber, ParameterSet params) {
		StringBuilder textReport = new StringBuilder(ModelingData.titleRow(params));
		
		for (ModelingData md : dataByProvince.values()) {
			textReport.append(md.dataReportAsText(runNumber, params));
		}
				
		try {
			FileUtility.appendToFile(
					"allOutputData.txt",
					textReport.toString());
		} catch (Exception ex) {
			System.out.println("Encountered error creating a Data report");
			ex.printStackTrace();
			System.exit(0);
		}
	}
}
