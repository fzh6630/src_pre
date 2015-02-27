package dataCollection;


import ChinaModel.ChineseProvince;
import ChinaModel.ParameterSet;
import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import utilities.Utilities;


class ModelingData {

	private final ChineseProvince trackThisProvince;

	private final List<Integer> populationStream = new ArrayList<>();

	private final List<Integer> numFarmersStream = new ArrayList<>();

	private final List<Integer> numNonFarmersStream = new ArrayList<>();

	private final List<Double> minWageStream = new ArrayList<>();

	private final List<Double> maxWageStream = new ArrayList<>();

	private final List<Double> avgWageStream = new ArrayList<>();

	private final List<Double> stdDevWageStream = new ArrayList<>();

	private final List<Double> avgDistanceMovedtream = new ArrayList<>();

	private final List<Double> stdDevDistanceMovedStream = new ArrayList<>();


	ModelingData(ChineseProvince trackThisProvince) {
		this.trackThisProvince = trackThisProvince;
	}


	void addDistances(Collection<Double> distances) {
		avgDistanceMovedtream.add(DoubleMath.mean(Doubles.toArray(distances)));
		stdDevDistanceMovedStream.add(stdDevOf(Doubles.toArray(distances)));
	}


	void addWages(Collection<Double> wages) {
		//using Google's Guava package for all but Stardard Dev
		minWageStream.add(Doubles.min(Doubles.toArray(wages)));
		maxWageStream.add(Doubles.max(Doubles.toArray(wages)));
		avgWageStream.add(DoubleMath.mean(Doubles.toArray(wages)));
		stdDevWageStream.add(stdDevOf(Doubles.toArray(wages)));
	}


	void addEmployment(Collection<Boolean> isFarmerData) {
		int numFarmers = 0;
		int numNonFarmers = 0;
		for (Boolean isFarmer : isFarmerData) {
			if (isFarmer) {
				numFarmers++;
			} else {
				numNonFarmers++;
			}
		}
		this.numFarmersStream.add(numFarmers);
		this.numNonFarmersStream.add(numNonFarmers);
	}


	void addPopulation(int population) {
		populationStream.add(population);
	}


	private Double stdDevOf(double[] values) {
		return Utilities.meanAndSD(values)[1];
	}


	String dataReportAsText(int runNumber, ParameterSet params) {
		StringBuilder fileContents = new StringBuilder();
				
		for (int i = 0; i < populationStream.size(); i++) {
			fileContents.append(runNumber).append(",");
			fileContents.append(trackThisProvince.toString()).append(",");
			fileContents.append(i).append(",");
			fileContents.append(populationStream.get(i)).append(",");
			fileContents.append(numFarmersStream.get(i)).append(",");
			fileContents.append(numNonFarmersStream.get(i)).append(",");
			fileContents.append(minWageStream.get(i)).append(",");
			fileContents.append(maxWageStream.get(i)).append(",");
			fileContents.append(avgWageStream.get(i)).append(",");
			fileContents.append(stdDevWageStream.get(i)).append(",");
			fileContents.append(avgDistanceMovedtream.get(i)).append(",");
			fileContents.append(stdDevDistanceMovedStream.get(i));
			fileContents.append(params.asDataOutputString());
			fileContents.append("\n");
		}

		return fileContents.toString();
	}


	static String titleRow(ParameterSet params) {

		StringBuilder sb = new StringBuilder();

		sb.append("RUN_NUM").append(",");
		sb.append("PROVINCE").append(",");
		sb.append("DATA_INDEX").append(",");
		sb.append("POPULATION").append(",");
		sb.append("NUM_FARMERS").append(",");
		sb.append("NUM_NON_FARMERS").append(",");
		sb.append("MIN_WAGE").append(",");
		sb.append("MAX_WAGE").append(",");
		sb.append("AVG_WAGE").append(",");
		sb.append("STD_DEV_WAGE").append(",");
		sb.append("AVG_DIST_MOVED").append(",");
		sb.append("STD_DEV_DIST_MOVED");
		sb.append(params.asTitleRow()).append("\n");

		return sb.toString();
	}
}
