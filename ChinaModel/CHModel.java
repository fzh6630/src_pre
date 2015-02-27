/*
 China Migration CHModel
 * 7/3/14
 * Michael Makowsky, Lingxin Hao, and Andrew Cherlin
 * Designed by MIchael Makowsky and Lingxin Hao
 * Coded by Michael Makowsky and Jon Parker
 */
package ChinaModel;


import com.vividsolutions.jts.geom.Envelope;
import dataCollection.DataCollector;
import ec.util.MersenneTwisterFast;
import inputData.InitialPopulationData;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.*;
import sim.field.grid.SparseGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.network.Network;
import sim.io.geo.*;
import sim.portrayal.network.SpatialNetwork2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.geo.MasonGeometry;
import sim.field.geo.GeomGridField.GridDataType;

//import java.commons-math-2.2-src.*;

// model of the group migration agent-based modeling
public class CHModel extends SimState {

	private ParameterSet parameters;
	
	private DataCollector dataCollector = new DataCollector();
	//
	// static atributes
	//
	// simulation
	// number of iterations (without GUI)
	//Edges are bounds (non-toroidal)

	public static final boolean DOES_NOT_WRAP = false;
	//MICRO preference parameters

	public static int urbanSearchRadius = 5;
	// grid	and network
	//public ObjectGrid2D patchLattice;

	public SparseGrid2D lattice;

	public Network network;

	public SpatialNetwork2D world; //wrapper for grid and network
	//GIS stuff

	public GeomVectorField provinceVectorField;

	public GeomVectorField prefectureVectorField;

	public GeomVectorField countyVectorField;

	public GeomGridField gridField;

	public Patch[][] patches; //matrix of all spaces

	public Bag activePatches; //non-null territory on the grid

	public final int NETWORK_HEIGHT = 30;	// grid dimensions	

	public final int NETWORK_WIDTH = 30;

	public static int MAX_COLOR_POPULATION = 3;

	public static int MAX_COLOR_WAGE = 5;

	public static int initialAge = 1;

	public static double initialFarmingWage = 1;

	public static int initialNetworkSize = 2;

	public int numberOfAgents;

	public Agent[] agents;

	public Firm[] firms;

	public HashMap<ChineseProvince, Province> provinces;

	public HashMap<MasonGeometry, ProvinceSeed> catchmentProvinceMap = new HashMap<>();

	public EconomicMarket economicMarket;

	public LaborMarket laborMarket;

	//SUMMARY STATISTICS
	//culled from agents

	public double medianWage;

	public double meanWage;

	public double minWage;

	public int employedCount;

	public int farmingCount;

	public int populationCount;

	public int movedCount;

	public int changedEmployerCount;

	public double meanDistanceMoved;

	public double totalDistanceMoved;

	//culled from firms

	public int offersAcceptedCount;

	public int offersMadeCount;

	public int offersRejectedCount;
		
	private int runNumber;


	//
	// methods 
	//
	//GET/SET Methods
	public int getNumberOfAgents() {
		return numberOfAgents;
	}


	/**
	 * @return - The parameters governing this modeling simulation.
	 */
	public ParameterSet params() {
		return parameters;
	}


	public int numberOfFirms() {
		return this.firms.length;
	}


	public int getinitialAge() {
		return initialAge;
	}


	public void setinitialAge(int val) {
		if (val > 0) {
			initialAge = val;
		}
	}


	public double getinitialFarmingWage() {
		return initialFarmingWage;
	}


	public void setinitialFarmingWage(double val) {
		if (val > 0) {
			initialFarmingWage = val;
		}
	}


	public int getinitialNetworkSize() {
		return initialNetworkSize;
	}


	public void setinitialNetworkSize(int val) {
		if (val > 0) {
			initialNetworkSize = val;
		}
	}


	public double getAlpha() {
		return parameters.alpha();
	}


	public double getBeta() {
		return parameters.beta();
	}


	public int getMAX_COLOR_POPULATION() {
		return MAX_COLOR_POPULATION;
	}


	public void setMAX_COLOR_POPULATION(int val) {
		if (val > 0) {
			MAX_COLOR_POPULATION = val;
		}
	}


	public int getMAX_COLOR_WAGE() {
		return MAX_COLOR_WAGE;
	}


	public void setMAX_COLOR_WAGE(int val) {
		if (val > 0) {
			MAX_COLOR_WAGE = val;
		}
	}


	// base constructor
	public CHModel(long seed, int runNumber) {
		super(new MersenneTwisterFast(seed), new Schedule());
		this.runNumber = runNumber;
		//super (new MersenneTwisterFast(seed), new Schedule(1));
	}


	public void setParameters(ParameterSet param) {
		this.parameters = param;
	}


	// CHModel Initializetion 
	@Override
	public void start() {

		super.start();

		loadGISData();

		// construct an Sparsegrid lattice that the agents live on off of the ascii province grid
		//patchLattice = new ObjectGrid2D(gridField.getGridWidth(), gridField.getGridHeight());
		lattice = new SparseGrid2D(gridField.getGridWidth(), gridField.getGridHeight());

		// construct an undirected network
		network = new Network(false);
		world = new SpatialNetwork2D(lattice, network);

		// constructs the patches, agents, and networks
		economicMarket = new EconomicMarket();
                createProvinces();
		createPatches();
		createFirms();
		setNumAgents();
		createAgents();
		setFamilyEdges();
		laborMarket = new LaborMarket();

		//SOCIAL NETWORK SUBSTEP creates the network
		Steppable updateSocialNetwork = new Steppable() {
			@Override
			public void step(SimState state) {

				CHModel model = (CHModel) state;
				for (int i = 0; i < numberOfAgents; i++) {
					agents[i].updatePersonalNetwork();

				}
			}
		};

		//STEPSTART SUBSTEP The beginning portion of every step 
		Steppable stepStart = new Steppable() {
			@Override
			public void step(SimState state) {
				CHModel m = (CHModel) state;
				calcStatistics();
			}
		};

		// MIGRATION SUBSTEP Movees agents to their new locations 
		Steppable migration = new Steppable() {

			public void step(SimState state) {

				CHModel m = (CHModel) state;
				totalDistanceMoved = 0;
				for (int i = 0; i < numberOfAgents; i++) {
					agents[i].moveAgent();
					totalDistanceMoved += agents[i].distanceMoved;
					if (agents[i].moved) {
						movedCount++;
					}
				}

				meanDistanceMoved = totalDistanceMoved / (double) movedCount;
			}


		};

		//SET THE FULL MODEL SCHEDULE 
		// 1) starts the step
		schedule.scheduleRepeating(Schedule.EPOCH, 0, stepStart);

		// 2) updates the network
		schedule.scheduleRepeating(Schedule.EPOCH, 1, updateSocialNetwork);
		// 3) sets prices,FDI, and farming returns
		schedule.scheduleRepeating(Schedule.EPOCH, 2, economicMarket);

		// 4)sets number to hire (scheduling included in constuct)
		//schedule.scheduleRepeating(Schedule.EPOCH, 3, firms);                    
		// 5) agents set skills, enter Job Market (scheduling included in constuct)
		//schedule.scheduleRepeating(Schedule.EPOCH, 4, agents);                   
		// 6a) create candidate pool, firms sort candidates
		// 6b) matching market algorithm is fun
		// 6c) firms make final hires/agents accept jobs
		schedule.scheduleRepeating(Schedule.EPOCH, 5, laborMarket);

		// 7) moves agents to their homes
		schedule.scheduleRepeating(Schedule.EPOCH, 6, migration);
	}


	public void simulateUntilComplete() {
		// simulation
		long steps;

		do {

			// stops if it can't process another iteration of the model
			if (!this.schedule.step(this)) {
				break;
			}

			// number of steps: for DEBUG
			steps = this.schedule.getSteps();
			System.out.println("step: " + steps);
			printOutRecords();
			
			if (steps % params().stepsPerYear() == params().stepsPerYear() - 1) {
				collectData();
			}

		} while (steps < params().maxNumSteps() && year() < ProvinceSeed.NUM_YEARS);

		dataCollector.printReport(runNumber, parameters);
	}


	/** @return - The current year as an index that can access FDI arrays. */
	public int year() {
		return (int) (this.schedule.getSteps() / params().stepsPerYear());
	}


	public void createProvinces() {

		this.provinces = new HashMap<>();

		ProvinceSeed[] seeds = this.createProvincesFromFile();
		for (int i = 0; i < seeds.length; i++) {

			ProvinceSeed provinceSeed = seeds[i];

			provinces.put(provinceSeed.provinceName(),
						  new Province(this, provinceSeed));
		}
	}


	public void createPatches() {

		patches = new Patch[gridWidth()][gridHeight()];
		activePatches = new Bag();

		for (int x = 0; x < gridWidth(); x++) {
			for (int y = 0; y < gridHeight(); y++) {

				ChineseProvince provID = getGISProvinceID(x, y);

//				Province parentProvince = this.provinces.get(provID);

				Patch newPatch = new Patch(x, y, this.provinces.get(provID));
				patches[x][y] = newPatch;

				if (provID.isPartOfChina()) {
					activePatches.add(newPatch);
					provinces.get(provID).addPatch(newPatch);
				} else {
					//do nothing when creating a Patch that is not a part of China...
				}

				//replace objectGrid2D int (provinceID) from ascii grid with 
				// Patch Object, with provinceID stored as an attribute 
				lattice.setObjectLocation(newPatch, new Int2D(x, y));
			}
		}
	}


	public void createAgents() {

		// construct the agents
		agents = new Agent[numberOfAgents];
		int runningTotal = 0;

		for (Province province : provinces.values()) {

			List<Agent> localAgents = province.createInitalPopulation();

			for (Agent agent : localAgents) {
				agents[runningTotal] = agent;
				runningTotal++;

				network.addNode(agent);

				schedule.scheduleRepeating(Schedule.EPOCH, 4, agent);
			}
		}
		this.numberOfAgents = runningTotal;
	}


	private ProvinceSeed[] createProvincesFromFile() {

		final String DATA_FILE = "provinceData.csv";

		File dataFile = new File(DATA_FILE);

		if (!dataFile.exists() || dataFile == null) {
			throw new IllegalStateException(
					"Problem loading the province data file :: " + DATA_FILE);
		}

		return ProvinceMethods.loadProvinceSeeds(dataFile);
	}


	//create the firms
	public void createFirms() {

		// construct the firms		
		this.firms = new Firm[provinces.size()];

		int counter = 0;
		for (Province province : this.provinces.values()) {

			Patch firmPatch = province.getRandomPatch(randDraw());
			Firm newFirm = new Firm(counter, firmPatch, economicMarket, this);
			province.addFirm(newFirm);
			schedule.scheduleRepeating(Schedule.EPOCH, 3, newFirm); // schedules the citzen agent
			firms[counter] = newFirm;
			counter++;
		}
	}


	public Patch randomPatchInChina() {

		while (true) {

			Patch randomPatch = this.patches[random.nextInt(this.gridWidth())][random.nextInt(this.
					gridHeight())];

			if (randomPatch.isPartOfChina()) {
				return randomPatch;
			}
		}
	}


	//create the initial network connections (permanent) between familial agents
	public void setFamilyEdges() {

		// creates the network edges
		for (int i = 0; i < numberOfAgents; i++) {

			// determines how many agents the current agent is going to be connected 
			int numConnections = parameters.initialFamilySize();

			//  connects one agent to a random number of other agents
			for (int j = 0; j < numConnections; j++) {

				Agent parent = agents[i].province().randomAgent();
				//redraw the parent because the parent of agent[i] can't be agent[i]
				while (parent == agents[i]) {
					parent = agents[i].province().randomAgent();
				}
				network.addEdge(agents[i], parent, i + " " + parent.agentID);
			}
		}
	}





	public void calcStatistics() {

		double totalWage = 0;
		//reset startingPopulation stats
		employedCount = 0;
		offersAcceptedCount = 0;
		offersMadeCount = 0;
		offersRejectedCount = 0;

		for (int i = 0; i < numberOfAgents; i++) {

			if (agents[i].isEmployed()) {
				employedCount++;
				totalWage += agents[i].wage;
			}
			if (agents[i].isFarmer) {
				farmingCount++;
			}
			if (agents[i].changedEmployer) {
				changedEmployerCount++;
			}


		}

		for (int i = 0; i < numberOfFirms(); i++) {
			offersAcceptedCount += firms[i].offersAcceptedCount;
			offersMadeCount += firms[i].offersMadeCount;
			offersRejectedCount += firms[i].offersRejectedCount;
		}

		meanWage = totalWage / (double) employedCount;
		System.out.println("TW " + totalWage + " NA " + numberOfAgents);


		//DEBUG OUTPUT
		for (int i = 0; i < 4; i++) {
			int sampleFirm = random.nextInt(31);
			System.out.println("Firm_" + sampleFirm + " Employs " + firms[sampleFirm].numEmployees);
		}

		for (int i = 0; i < 4; i++) {
			int sampleAgent = random.nextInt(numberOfAgents);

			System.out.println(
					"Agent_" + sampleAgent + " wage " + agents[sampleAgent].wage
					+ " #friends " + agents[sampleAgent].numFriends());
		}
	}


	public void printOutRecords() {
		//DEBUG OUTPUT
		System.out.println("Employed " + employedCount + " " + "meanWage " + meanWage);
		System.out.println("Moved " + movedCount + " " + "meanDistance " + meanDistanceMoved + "TD " + totalDistanceMoved);
	}
	
	private void collectData() {
		
		//for each province...
		for (Province province : provinces.values()) {
			dataCollector.addDistanceMoved(province.harvestDistanceMoved(), province.provinceName());
			dataCollector.addWages(province.harvestWages(), province.provinceName());
			dataCollector.addEmployment(province.harvestIsFarmer(), province.provinceName());
			dataCollector.addPopulation(province.currentPopulation(), province.provinceName());
		}
	}


	private void loadGISData() {

		//GIS Vector/Polygon importing
		provinceVectorField = new GeomVectorField(parameters.mapWidth(), parameters.mapHeight());
		prefectureVectorField = new GeomVectorField(parameters.mapWidth(), parameters.mapHeight());
		countyVectorField = new GeomVectorField(parameters.mapWidth(), parameters.mapHeight());

		try {
			//Read Ascii GIS Grid data
			gridField = new GeomGridField();
			InputStream inputStream = CHModel.class.
					getResourceAsStream("data/chinaprovinceasciigrid.txt");
			ArcInfoASCGridImporter.read(inputStream, GridDataType.INTEGER, gridField);

			//Read the GIS Vector data (legal/political boundaries)
			ShapeFileImporter.read(CHModel.class.getResource("data/CHN_adm1.shp"),
								   provinceVectorField);
			ShapeFileImporter.read(CHModel.class.getResource("data/CHN_adm2.shp"),
								   prefectureVectorField);
			ShapeFileImporter.read(CHModel.class.getResource("data/CHN_adm3.shp"),
								   countyVectorField);

			// Make all the bounding rectangles match one another
			Envelope MBR = provinceVectorField.getMBR();
			provinceVectorField.setMBR(MBR);
			prefectureVectorField.setMBR(MBR);
			countyVectorField.setMBR(MBR);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public int gridWidth() {
		return gridField.getGridWidth();
	}


	public int gridHeight() {
		return gridField.getGridHeight();
	}


	private ChineseProvince getGISProvinceID(int x, int y) {
		int id = (int) ((IntGrid2D) gridField.getGrid()).get(x, y);
		return ChineseProvince.provinceFromID(id);
	}


	public double randDraw() {
		return random.nextDouble();
	}


	public int randInt(int n) {
		return random.nextInt(n);
	}


	private void setNumAgents() {
		this.numberOfAgents = computeNumAgents();
	}


	private int computeNumAgents() {		
		return InitialPopulationData.listOfPeople().size();
	}

}
