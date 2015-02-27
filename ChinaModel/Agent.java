package ChinaModel;


import inputData.PersonFromCSVFile;
import java.util.ArrayList;
import java.util.Collection;
import utilities.Utilities;
import sim.engine.*;
import sim.util.*;
import sim.field.network.Edge;


public class Agent implements Steppable {

	private static int idCounter = 0;


	private static int newAgentID() {
		int val = idCounter;
		idCounter++;
		return val;
	}

	public final int NO_JOB_OFFER = -999;

	public int agentID;

	public double age;

	private double education;

	public double skill;
	//employment

	public double wage;

	public boolean isFarmer;

	public boolean onTheMarket;

	public Firm deferredAcceptanceFirm;

	public double deferredAcceptanceWage;

//	public int employerID;
	private Firm employer = null;

	private Firm startingEmployer = null;

	//migration

	public boolean moved;

	public double distanceMoved;
	//location and origin coordinates

	public Patch currentPatch;

	public Patch originPatch;
	//preference weights

	public double weight_wage;

	public double weight_distanceCurrent;

	public double weight_distanceOrigin;

	public double weight_network;

	public boolean changedEmployer = false;

	public JobMarketEntryRule entryRule;

	public CHModel model;


//	public Agent(CHModel m, int a, double w, double e, double wW, double wDC, double wDO, double wN, JobMarketEntryRule r) {
//		agentID = newAgentID();
//		this.model = m;
//		isFarmer = true;
//		age = a;
//		wage = w;
//		education = e;
//		weight_wage = wW;
//		weight_distanceCurrent = wDC;
//		weight_distanceOrigin = wDO;
//		weight_network = wN;
//		this.entryRule = r;
//
////		System.out.println("created agent :: " + id);
//	}
	
	public Agent(CHModel m, PersonFromCSVFile row, double wW, double wDC, double wDO, double wN, JobMarketEntryRule r) {
		this.agentID = newAgentID();
		this.model = m;
		this.age = row.age();
		this.education = row.yearsOfEducation();
		this.isFarmer = !row.isMigrant();
		this.weight_wage = wW;
		this.weight_distanceCurrent = wDC;
		this.weight_distanceOrigin = wDO;
		this.weight_network = wN;
		this.entryRule = r;
	}

        public void assignInitialEmployer() {
          
            if (this.isFarmer) {
                  this.setWage(model.initialFarmingWage);    
            }
            else {
                this.employer = this.province().firm();
                this.employer.addEmployee(this);      
                moveTo(this.employer.patch()); 
            }
        }
	public Province province() {
		return this.currentPatch.province();
	}


	public Province originProvince() {
		return this.originPatch.province();
	}


	public Firm employer() {
		return this.employer;
	}


	@Override
	public void step(SimState state) {

		model = (CHModel) state;
		moved = false;
		//updated agent skill is a function of age and education
		setSkill();
		//the agent makes himself available to receive job offers from firms
		enterJobMarket();
	}


	public void enterJobMarket() {

		startingEmployer = employer;

		//start off the market,  with no job offer
		deferredAcceptanceFirm = null;
		onTheMarket = false;

		if (this.entryRule == JobMarketEntryRule.ALL_ENTER_MARKET) {

			this.onTheMarket = true;

		} else if (this.entryRule == JobMarketEntryRule.ENTER_IF_BELOW_MEAN_WAGE) {

			if (model.meanWage > this.wage) {
				this.onTheMarket = true;
			}

		} else if (this.entryRule == JobMarketEntryRule.ENTER_RANDOM) {

			if (model.random.nextDouble() > model.params().randomEntryProb()) {
				this.onTheMarket = true;
			}
		} else {
			throw new AssertionError("Should not get here");
		}

	}


	public double computeJobOfferValue(Firm firm, double w) {

		if (firm == null) {
			//implies offer comes from the farming sector...
			return model.economicMarket.farmingWage();
		}

//		System.out.println(((Patch) model.firms[id].patch).location.x + " " + ((Patch) model.firms[id].patch).location.y);

		double distanceFromCurrent = Utilities.calcGeometricDistance(
				this.currentPatch,
				firm.patch());

		double distanceFromOrigin = Utilities.calcGeometricDistance(
				this.originPatch,
				firm.patch());

		int numberOfFriendsAtNewProvince = friendsWithinProvince(firm.province()).size();

		double value = (this.weight_wage * w)
				+ (this.weight_distanceCurrent * distanceFromCurrent)
				+ (this.weight_distanceOrigin * distanceFromOrigin)
				+ (this.weight_network * numberOfFriendsAtNewProvince);

		return value;
	}


	public boolean evaluateJobOffer(Firm firmMakingOffer, double offerWage, LaborMarket laborMarket) {
		boolean accept;

		double offerValue = computeJobOfferValue(firmMakingOffer, offerWage);
		double currentValue = computeJobOfferValue(this.employer, this.wage);

		if (offerValue > currentValue) {
			accept = true;
			if (deferredAcceptanceFirm != null) {
				//reject old offer, defer new offer
				rejectPreviousOffer(model.firms[deferredAcceptanceFirm.firmID]);
				deferredAcceptanceFirm = firmMakingOffer;
				deferredAcceptanceWage = offerWage;
				//stats update
				laborMarket.rejections++;
			} else {
				deferredAcceptanceFirm = firmMakingOffer;
				deferredAcceptanceWage = offerWage;
				laborMarket.acceptances++;
			}
		} else {
			accept = false;
		}

//        System.out.println(
//                "CurentEmployer= " + employerID
//                + "OV= " + offerValue
//                + "CV= " + currentValue
//                + " Accept?= " + accept
//                + " deferID= " + deferredAcceptanceFirmID);
		return accept;
	}


	public void rejectPreviousOffer(Firm f) {
		f.outstandingOfferAgents.remove(this);
		f.candidates.remove(this);
		f.numOutstandingOffers--;
	}


	public void setEmployer() {
		if (deferredAcceptanceFirm != null) {
			this.employer = deferredAcceptanceFirm;
			wage = deferredAcceptanceWage;
			isFarmer = false;
			//System.out.println("new wage: " + wage);
		}
		//else { }
	}


	public boolean isEmployed() {
		return this.employer != null;
	}


	public void setSkill() {
		skill = Math.pow(age, 0.5) * education;
	}


	public void moveAgent() {
		distanceMoved = 0;
		moved = false;

		if (isEmployed() & (employer != startingEmployer)) {
			//System.out.println("Moving an agent who changed employment");

			distanceMoved = Utilities.calcGeometricDistance(
					this.currentPatch,
					employer.patch());
			//System.out.println("DM" + distanceMoved);
			moved = true;

			moveTo(this.employer.patch());
		}

		if (!isEmployed() & this.currentPatch != this.originPatch) {
			moveTo(this.originPatch);
			//System.out.println("Moving an agent without employment home");
		}
	}


	private void moveTo(Patch newPatch) {

		assert (newPatch.isPartOfChina()) : "Agents should only be moving to parts of China";

		this.currentPatch.removeAgentFromPatch(this);

		//update the new patch
		newPatch.addAgentToPatch(this);
		this.currentPatch = newPatch;

		moved = true;
	}

///****** SOCIAL NETWORK METHODS

	public void updatePersonalNetwork() {

		//consider making a local friendship
		considerMakingALocalFriendship();
		//consider dissolving non-local friendship
		considerDissolvingAllNonLocalFriendship();
	}


	private void considerMakingALocalFriendship() {

		if (tooManyFriends()) {
			return;
		}
		if (tooFewLocalsInProvince()) {
			return;
		}

		Agent other = randomNonfriendWithinProvince();

		if (model.randDraw() < model.params().probNetworkAdd()) {
			addToSocialNetwork(other);
		}
	}


	private boolean tooManyFriends() {
		return (friends().length >= model.params().maxNumberOfFriends());
	}


	private boolean tooFewLocalsInProvince() {
		return this.province().currentPopulation() < model.params().maxNumberOfFriends() + 1;
	}


	private Agent randomNonfriendWithinProvince() {

		Agent other = this.province().randomAgent();

		//redraw if necessary.... 
		int overflowCounter = 1000;
		while (other == this || this.isFriendsWith(other)) {
			other = this.province().randomAgent();
			overflowCounter--;

			if (overflowCounter == 0) {
				throw new IllegalStateException("caught in while loop");
			}
		}

		return other;
	}


	/** @return - True if the "other" agent is already friends with this agent. */
	private boolean isFriendsWith(Agent other) {

		Agent[] currentFriends = friends();

		for (Agent aFriend : currentFriends) {
			if (other == aFriend) {
				return true;
			}
		}
		return false;
	}


	private Agent[] friends() {
		Bag bagOfEdges = new Bag();
		model.network.getEdges(this, bagOfEdges);
		//bagOfEdges = model.network.getEdges(this, bagOfEdges);

//		System.out.println("Found " + bagOfEdges.numObjs + " edges");

		Agent[] friends = new Agent[bagOfEdges.numObjs];

		for (int i = 0; i < friends.length; i++) {
			Edge edge = (Edge) bagOfEdges.objs[i];

			Agent from = (Agent) edge.from();
			Agent to = (Agent) edge.to();

//			System.out.println("  Found edge from " + from.agentID + " to " + to.agentID);

			//don't blindly set from or to, must check to find person who isn't you
			if (from == this) {
				friends[i] = to;
			} else {
				friends[i] = from;
			}

			assert (friends[i] != this) : "you can't be friends with yourself???";
		}

		return friends;
	}


	private Collection<Agent> nonLocalFriends() {

		Agent[] friends = friends();

		Collection<Agent> nonLocals = new ArrayList<>();
		for (Agent friend : friends) {
			if (this.province() != friend.province()) {
				nonLocals.add(friend);
			}
		}

		return nonLocals;
	}


	private Collection<Agent> friendsWithinProvince(Province p) {

		Agent[] friends = friends();

		Collection<Agent> friendsAt = new ArrayList<>();
		for (Agent friend : friends) {
			if (p == friend.province()) {
				friendsAt.add(friend);
			}
		}

		return friendsAt;
	}


	private void considerDissolvingAllNonLocalFriendship() {

		Collection<Agent> nonLocalFriends = nonLocalFriends();

		for (Agent nonLocalFriend : nonLocalFriends) {

			if (model.randDraw() < model.params().probNetworkRemove()) {
				//dissolve this edge...
				this.removeEdge(nonLocalFriend);
			}
		}
	}


	public void addToSocialNetwork(Agent a) {
		//System.out.println("Adding edge between " + this.agentID + " and " + a.agentID);
		model.network.addEdge(this, a, this.agentID + " " + a.agentID);
	}


	private void removeEdge(Agent a) {
		Edge e = model.network.getEdge(this, a);
		model.network.removeEdge(e);
	}


	public double calcProbRemoval(double d) {
		double p = model.params().probNetworkRemove();
//          This needs to be updated to increase with distance
		return p;
	}


	public double calcProbAdd() {
		double p = model.params().probNetworkAdd();
//          This needs to be updated to increase with distance
		return p;
	}


	public int numFriends() {
		return friends().length;
	}


	public double education() {
		return this.education;
	}

        public void setWage(double w) {
            this.wage = w;
        } 
}
