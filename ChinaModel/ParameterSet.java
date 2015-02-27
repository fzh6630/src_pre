package ChinaModel;


import java.util.Map;
import java.util.TreeMap;


/**
 * A ParameterSet contains a collection of parameters that can be used during program execution.
 * The purposed of this class is to enable parameters to be easily declared programmatically so that
 * "canned" experiments and computations can always be rerun.
 *
 * A ParameterSet object must be built by a ParameterSet.Builder. Any ParameterSet parameter not set
 * during the builder's setup up phase (i.e. before calling Builder.build()) cannot be accessed.
 * This forces ParameterSets to be "fully declared" when the program or experiment is setup.
 */
public final class ParameterSet {

	/** The unique ID String for "maxNumSteps" set/get methods. */
	private static final String MAXIMUM_NUM_STEPS = "totalSteps";

	/** The unique ID String for "stepsPerYear" set/get methods. */
	private static final String STEPS_PER_YEAR = "stepsPerYear";

	/** The unique ID String for "alpha" set/get methods. */
	private static final String ALPHA = "alpha";

	/** The unique ID String for "beta" set/get methods. */
	private static final String BETA = "beta";

	/** The unique ID String for "gamma" set/get methods. */
	private static final String GAMMA = "gamma";

	/** The unique ID String for "randomEntryProb set/get methods. */
	private static final String RANDOM_ENTRY_PROB = "randomEntryProb";

	/** The unique ID String for "initialTechnologyStock set/get methods. */
	private static final String INITIAL_TECH_STOCK = "initialTechnologyStock";

	/** The unique ID String for "weightWage" set/get methods. */
	private static final String WEIGHT_WAGE = "weightWage";

	/** The unique ID String for "weightDistCurrent" set/get methods. */
	private static final String WEIGHT_DIST_CURRENT = "weightDistCurrent";

	/** The unique ID String for "weightDistOrigin" set/get methods. */
	private static final String WEIGHT_DIST_ORIGIN = "weightDistOrigin";

	/** The unique ID String for "weightNetwork" set/get methods. */
	private static final String WEIGHT_NETWORK = "weightNetwork";

	/** The unique ID String for "mapWidth" set/get methods. */
	private static final String MAP_WIDTH = "mapWidth";

	/** The unique ID String for "mapHeight" set/get methods. */
	private static final String MAP_HEIGHT = "mapHeight";

	/** Store parameters here, put/get using the static final ID strings above. */
	private final Map<String, Object> parameters;

	/** The unique ID String for "initialFamilySize" set/get methods. */
	private static final String INITIAL_FAMILY_SIZE = "initialFamilySize";

	/** The unique ID String for "initialFamilySize" set/get methods */
	private static final String PROB_NETWORK_ADD = "probNetworkAdd";

	/** The unique ID String for "initialFamilySize" set/get methods. */
	private static final String PROB_NETWORK_REMOVE = "probNetworkRemove";

	/** The unique ID String for "maxNumberOfFriends" set/get methods. */
	private static final String MAX_NUMBER_OF_FRIENDS = "maxNumberOfFriends";

	/** The rate at which firms increase and decrease the number of employees they try to hire */
	private static final String OFFER_GROWTH_RATE = "offerGrowthRate";


	private ParameterSet(Builder b) {
		this.parameters = b.parameters;
	}


	/** @return - The total number of steps allowed during the simulation. */
	public long maxNumSteps() {
		return (long) get(MAXIMUM_NUM_STEPS);
	}


	public long stepsPerYear() {
		return (long) get(STEPS_PER_YEAR);
	}


	public double alpha() {
		return (double) get(ALPHA);
	}


	public double beta() {
		return (double) get(BETA);
	}


	public double gamma() {
		return (double) get(GAMMA);
	}


	public double randomEntryProb() {
		return (double) get(RANDOM_ENTRY_PROB);
	}


	public double initialTechnologyStock() {
		return (double) get(INITIAL_TECH_STOCK);
	}


	public double weightWage() {
		return (double) get(WEIGHT_WAGE);
	}


	public double weightDistCurrent() {
		return (double) get(WEIGHT_DIST_CURRENT);
	}


	public double weightDistOrigin() {
		return (double) get(WEIGHT_DIST_ORIGIN);
	}


	public double weightNetwork() {
		return (double) get(WEIGHT_NETWORK);
	}


	public int mapWidth() {
		return (int) get(MAP_WIDTH);
	}


	public int mapHeight() {
		return (int) get(MAP_HEIGHT);
	}


	public int initialFamilySize() {
		return (int) get(INITIAL_FAMILY_SIZE);
	}


	public double probNetworkAdd() {
		return (double) get(PROB_NETWORK_ADD);
	}


	public double probNetworkRemove() {
		return (double) get(PROB_NETWORK_REMOVE);
	}


	public int maxNumberOfFriends() {
		return (int) get(MAX_NUMBER_OF_FRIENDS);
	}

	public double offerGrowthRate() {
		return (double) get(OFFER_GROWTH_RATE);
	}
        
	private Object get(String fixedKey) {
		if (parameters.containsKey(fixedKey)) {
			return parameters.get(fixedKey);
		} else {
			throw new IllegalStateException("The \"" + fixedKey + "\" parameter was not set");
		}
	}


	public static Builder getBuilder() {
		return new Builder();
	}


	public String asDataOutputString() {
		
		StringBuilder sb = new StringBuilder();		
		for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
			sb.append(",").append(entry.getValue());
		}
		
		return sb.toString();		
		
//		return "This_string_lists_all_parameter_values_(commaDelimited)";
	}


	public String asTitleRow() {
		
		StringBuilder sb = new StringBuilder();		
		for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
			sb.append(",").append(entry.getKey());
		}
		
		return sb.toString();
		
//		return "This_string_lists_all_parameter_names_(commaDelimited)";
	}

	public static class Builder {

		Map<String, Object> parameters = new TreeMap<>();


		/** Set the total number of steps allowed during the simulation. */
		public Builder maxNumSteps(long totalSteps) {
			return set(MAXIMUM_NUM_STEPS, totalSteps);
		}


		/** Set the steps per year in the simulation. */
		public Builder stepsPerYear(long stepsPerYear) {
			return set(STEPS_PER_YEAR, stepsPerYear);
		}


		/** Set alpha . */
		public Builder alpha(double alpha) {
			return set(ALPHA, alpha);
		}


		/** Set beta. */
		public Builder beta(double beta) {
			return set(BETA, beta);
		}


		/** Set gamma. */
		public Builder gamma(double gamma) {
			return set(GAMMA, gamma);
		}


		/** Set the probability of random entry. */
		public Builder randomEntryProb(double randomEntryProb) {
			return set(RANDOM_ENTRY_PROB, randomEntryProb);
		}


		/** Set the initial technology stock parameter. */
		public Builder initialTechnologyStock(double initialTechnologyStock) {
			return set(INITIAL_TECH_STOCK, initialTechnologyStock);
		}


		/** Set the weightWage parameter. */
		public Builder weightWage(double weightWage) {
			return set(WEIGHT_WAGE, weightWage);
		}


		/** Set the weightDistCurrent parameters. */
		public Builder weightDistCurrent(double weightDistCurrent) {
			return set(WEIGHT_DIST_CURRENT, weightDistCurrent);
		}


		/** Set the weightDistOrigin parameters. */
		public Builder weightDistOrigin(double weightDistOrigin) {
			return set(WEIGHT_DIST_ORIGIN, weightDistOrigin);
		}


		/** Set the weightNetwork parameters. */
		public Builder weightNetwork(double weightNetwork) {
			return set(WEIGHT_NETWORK, weightNetwork);
		}


		/** Set the map width. */
		public Builder mapWidth(int mapWidth) {
			return set(MAP_WIDTH, mapWidth);
		}


		/** Set the map height. */
		public Builder mapHeight(int mapHeight) {
			return set(MAP_HEIGHT, mapHeight);
		}


		/** Set the family size. */
		public Builder initialFamilySize(int initialFamilySize) {
			return set(INITIAL_FAMILY_SIZE, initialFamilySize);
		}


		public Builder probNetworkAdd(double probNetworkAdd) {
			return set(PROB_NETWORK_ADD, probNetworkAdd);
		}


		public Builder probNetworkRemove(double probNetworkRemove) {
			return set(PROB_NETWORK_REMOVE, probNetworkRemove);
		}


		public Builder maxNumberOfFriends(int maxNumberOfFriends) {
			return set(MAX_NUMBER_OF_FRIENDS, maxNumberOfFriends);
		}


		public Builder offerGrowthRate(double offerGrowthRate) {
			return set(OFFER_GROWTH_RATE, offerGrowthRate);
		}


		private Builder set(String fixedKey, Object value) {
			if (parameters.containsKey(fixedKey)) {
				throw new IllegalStateException("The \"" + fixedKey + "\" parameter was already set");
			} else {
				parameters.put(fixedKey, value);
			}

			return this;
		}


		/** Create a fully formed ParameterSet. */
		public ParameterSet build() {
			return new ParameterSet(this);
		}
	}
}
