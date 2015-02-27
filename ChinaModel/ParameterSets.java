package ChinaModel;


/**
 * This class provides pre-package ParameterSets to facilitate repeatable demo, experiments, and
 * simulations.
 */
public class ParameterSets {

	/** @return - The default ParameterSet. */
	public static ParameterSet defaultParameters() {

		ParameterSet.Builder builder = ParameterSet.getBuilder();

		//set all parameters here
		builder.maxNumSteps(300).
				stepsPerYear(52).
				alpha(0.6). //Q = AK^alphaL^Beta	
				beta(0.4).
				gamma(0.0281).
				randomEntryProb(0.25).
				initialTechnologyStock(1.0).
				weightWage(0.7).
				weightDistCurrent(0.0).
				weightDistOrigin(0.0).
				weightNetwork(0.1).
				mapWidth(250).
				mapHeight(250).
				initialFamilySize(2).
				probNetworkAdd(0.01).
				probNetworkRemove(0.01).
				maxNumberOfFriends(7).
				offerGrowthRate(0.02);

		return builder.build();
	}


	public static ParameterSet keyVariablesExperimentParameters(double w_w, double w_DC, double w_DO, double w_N, double p_a, double p_r) {

		ParameterSet.Builder builder = ParameterSet.getBuilder();

		//set all parameters here
		builder.maxNumSteps(1000).
				stepsPerYear(52).
				alpha(0.6). //Q = AK^alphaL^Beta	
				beta(0.4).
				gamma(0.0281).
				randomEntryProb(0.25).
				initialTechnologyStock(1.0).
				weightWage(w_w).
				weightDistCurrent(w_DC).
				weightDistOrigin(w_DO).
				weightNetwork(w_N).
				mapWidth(250).
				mapHeight(250).
				initialFamilySize(2).
				probNetworkAdd(p_a).
				probNetworkRemove(p_r).
				maxNumberOfFriends(7).
				offerGrowthRate(0.02);
		return builder.build();
	}
}
