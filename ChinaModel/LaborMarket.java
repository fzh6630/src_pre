package ChinaModel;


/**
 * This is a Java Program to Implement a Many-to-One Gale-Shapley Deferred
 * Acceptance labor matching market. Adapted by Michael Makowsky
 */
import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;
import ec.util.*;
import java.io.*;


public class LaborMarket implements Steppable {

	public int rejections;

	public int acceptances;

	public Bag candidates = new Bag();

	public CHModel model;


	/**
	 * Constructor *
	 * We create (and destroy) a fresh set of firm/agent matrices each time step
	 */
	public LaborMarket() {
		rejections = 0;
	}
	//Job Matching sub-step


	@Override
	public void step(SimState state) {
		model = (CHModel) state;
		createCandidatePool();
		firmsSortCandidates();
		//if there are job candidates, create the labor matching market
		if (candidates.numObjs > 0) {
			runLaborMatchingMarket();
			firmsMakeFinalHires();
		}
	}


	public void createCandidatePool() {
		candidates.clear();

		for (int i = 0; i < model.numberOfAgents; i++) {
			if (model.agents[i].onTheMarket == true) {
				//add agents on the market the to LaborMarket's Bag of candidates
				candidates.add(model.agents[i]);
			}
		}
		//DEBUG OUTPUT
                //System.out.println("candidate pool=" + candidates.numObjs);
	}


	public void firmsSortCandidates() {
		for (int i = 0; i < model.numberOfFirms(); i++) {
			model.firms[i].sortCandidates(candidates);
		}
	}


	public void runLaborMatchingMarket() {

		rejections = 999;

		while (rejections > 0) {
			rejections = 0;

			for (int i = 0; i < model.numberOfFirms(); i++) {
				model.firms[i].makeJobOffers(this);
			}

			if (rejections == 0) {
				break;
			}
		}
	}


	public void firmsMakeFinalHires() {
		for (int i = 0; i < model.numberOfFirms(); i++) {
			model.firms[i].makeFinalHires();
		}
	}
}