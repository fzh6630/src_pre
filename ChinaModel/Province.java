package ChinaModel;


import inputData.InitialPopulationData;
import inputData.PersonFromCSVFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import sim.engine.*;
import sim.util.*;


public class Province implements Steppable {

	private CHModel model;

	public String name;

	public String type;

	public int arableValue; //scale 1-10, 10 is most arable

	private final ProvinceSeed provSeed;

	private final HashMap<Int2D, Patch> patches;

	private Bag agentsHere = new Bag();

	private Firm localFirm;


	public Province(CHModel model, ProvinceSeed provSeed) {
		this.model = model;
		this.provSeed = provSeed;
		this.patches = new HashMap<>();

		System.out.println("Building the Province :: " + provSeed.provinceName());
	}


	public Firm firm() {
		return this.localFirm;
	}


	public void addPatch(Patch p) {
		this.patches.put(p.location(), p);
	}


	public Patch getRandomPatch(double draw) {

		if (patches.isEmpty()) {
			throw new IllegalStateException(
					"Cannot draw a random Patch when no patches exist :: "
					+ this.provSeed.provinceName());
		}

		int randomIndex = (int) (draw * patches.size());

		List<Patch> localList = new ArrayList<>(patches.values());
		return localList.get(randomIndex);
	}


	public List<Agent> createInitalPopulation() {

		List<Agent> newAgents = new ArrayList<>();
			
		for (PersonFromCSVFile person : InitialPopulationData.listOfPeople()) {
			
			if(person.provinceID() == provinceName().provinceNum) {
				//build Agent
				//add Agent
				Agent newAgent = new Agent(
						model,
						person,
						model.params().weightWage(),
						model.params().weightDistCurrent(),
						model.params().weightDistOrigin(),
						model.params().weightNetwork(),
						JobMarketEntryRule.ALL_ENTER_MARKET);

				newAgents.add(newAgent);

				Patch randomPatch = this.getRandomPatch(this.model.randDraw());
				Int2D agentLocation = randomPatch.location();

				//update the new agent
                                newAgent.currentPatch = randomPatch;
				newAgent.originPatch = randomPatch;
				randomPatch.addAgentToPatch(newAgent);
                                
                                //make the agent a farmer 
                                // or initial migrant firm employee
                                newAgent.assignInitialEmployer();
                                               
			}
          
                        }
                        
                

//		for (int i = 0; i < startingPopulation(); i++) {
//
//			Agent newAgent = new Agent(model,
//									   model.initialAge,
//									   model.initialFarmingWage,
//									   (model.random.nextGaussian() * provSeed.education_SD() + provSeed.
//									   education_Mean()),
//									   model.params().weightWage(),
//									   model.params().weightDistCurrent(),
//									   model.params().weightDistOrigin(),
//									   model.params().weightNetwork(),
//									   JobMarketEntryRule.ALL_ENTER_MARKET);
//
//			newAgents.add(newAgent);
//
//			Patch randomPatch = this.getRandomPatch(this.model.randDraw());
//			Int2D agentLocation = randomPatch.location();
//
//			//update the new agent
//			newAgent.currentPatch = randomPatch;
//			newAgent.originPatch = randomPatch;
//			randomPatch.addAgentToPatch(newAgent);
//		}

		return newAgents;
	}


	public void step(SimState state) {
		// Calculate the optimal L, find the difference from current L, then
		// create a hiring plan 

		model = (CHModel) state;
	}


	/** The Paracel Islands are not represented in the GIS data. */
	boolean hasNoPatches() {
		return this.patches.isEmpty();
	}


	public void addAgentToProvince(Agent a) {
		agentsHere.add(a);
	}


	public ChineseProvince provinceName() {
		return this.provSeed.provinceName();
	}


	public double[] foreignDirectInvestment() {
		return this.provSeed.fdiValues1995to1999();
	}


	public int startingPopulation_Urban() {
		return countUrbanAgents();
	}


	private int countUrbanAgents() {
		
		int numUrban = 0;
		
		for (int i = 0; i < this.agentsHere.numObjs; i++) {
			Agent agentI = (Agent) agentsHere.objs[i];
			if(!agentI.isFarmer) {
				numUrban++;
			}			
		}
                if (numUrban > 1) System.out.println(numUrban);
		return numUrban;
	}


	void addFirm(Firm aFirm) {

		//this must be true...or an error is thrown.
		assert (this.localFirm == null) : "A Province's local firm was already set";

		this.localFirm = aFirm;
	}


	public int currentPopulation() {
		return this.agentsHere.size();
	}


	void removeAgentFromProvince(Agent a) {
		agentsHere.remove(a);
	}


	Agent randomAgent() {
//		System.out.println("Random selection in box of size :: " + agentsHere.size());
		return (Agent) agentsHere.get(model.randInt(agentsHere.numObjs));
	}


	Collection<Double> harvestDistanceMoved() {

		ArrayList<Double> distances = new ArrayList<>();

		for (int i = 0; i < agentsHere.numObjs; i++) {
			Agent agentI = (Agent) agentsHere.objs[i];
			distances.add(agentI.distanceMoved);
		}
		return distances;
	}


	Collection<Double> harvestWages() {

		ArrayList<Double> wages = new ArrayList<>();

		for (int i = 0; i < agentsHere.numObjs; i++) {
			Agent agentI = (Agent) agentsHere.objs[i];
			wages.add(agentI.wage);
		}
		return wages;
	}


	Collection<Boolean> harvestIsFarmer() {

		ArrayList<Boolean> isFarmerData = new ArrayList<>();

		for (int i = 0; i < agentsHere.numObjs; i++) {
			Agent agentI = (Agent) agentsHere.objs[i];
			isFarmerData.add(agentI.isFarmer);
		}
		return isFarmerData;
	}
}
