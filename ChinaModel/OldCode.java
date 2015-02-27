package ChinaModel;

/**
 *
 * @author Jon Parker (jon.i.parker@gmail.com)
 */
public class OldCode {
	
	
	//REMOVED FROM AGENT.JAVA BECAUSE IT WAS NO LONGER NECESSARY	
	
//	/** @return - The location (from the Bag of neighbors) with the fewest agents. */
//	private Int2D findLocalBestLocation(Bag neighbors) {
//
//		if (neighbors.isEmpty()) {
//			throw new IllegalStateException("The Bag of neighbors was empty");
//		}
//
//		double curMinPopulation = Integer.MAX_VALUE;
//		Patch curBestPatch = null;
//
//		for (int i = 0; i < neighbors.numObjs; i++) {
//			
//			assert (neighbors.objs[i] != null) : "Found a null neighbor";
//
//			Patch neighbor = (Patch) neighbors.objs[i];
//
//			//skip patches that correspond to non-china places
//			if (neighbor.province == ChineseProvince.NOT_CHINA) {
//				System.out.println("skipped a NOT_CHINA neighbor");
//				continue;
//			}
//
//			if (neighbor.localPopulation() < curMinPopulation) {
//				curMinPopulation = neighbor.localPopulation();
//				curBestPatch = neighbor;
//			}
//		}
//		
//		assert (curBestPatch != null) : "curBestPatch was not set.  "
//				+ "I could be that all neighbor patches were NOT_CHINA Patches";
//
//		return curBestPatch.location();
//	}
	
	
	//REMOVED FROM CHMODEL.JAVA BECAUSE IT WAS THROWING ERRORS (DUE TO AGENT SPARSITY)
	
//	//create the initial network connections (permanent) between familial agents
//	public void setFamilyEdges() {
//
//		// creates the network edges
//		for (int i = 0; i < numberOfAgents; i++) {
//
//			// determines how many agents the current agent is going to be connected 
//			int numConnections = parameters.initialFamilySize();
//
//			//In this simple version, the agent picks too random neighbors at birth to be his family
//			Bag birthNeighbors = new Bag();
//			Bag neighborhood = new Bag();
//			//establish the neighborhood
//			int x = agents[i].currentPatch.location().x;
//			int y = agents[i].currentPatch.location().y;
//			
//			neighborhood = this.lattice.getMooreNeighbors(
//					x,
//					y,
//					5,
//					Grid2D.BOUNDED,
//					neighborhood,
//					null,
//					null);
//
//
//			//add all of the neighboring agents to the neighbor bag
//			for (int j = 0; j < neighborhood.numObjs; j++) {
//				Patch p = (Patch) neighborhood.objs[j];
//				//Bag b = p.localAgents; 
//				birthNeighbors.addAll(p.localAgents);
//			}
//
//			//  connects one agent to a random number of other agents
//			for (int j = 0; j < numConnections; j++) {
//				int r = random.nextInt(birthNeighbors.numObjs);
//				Agent parent = (Agent) birthNeighbors.objs[r];
//				network.addEdge(agents[i], parent, i + " " + parent.agentID);
//				birthNeighbors.remove(parent);
//			}
//			
//			Agent parent = agents[i].province().randomAgent();
//			//redraw the parent because the parent of agent[i] can't be agent[i]
//			while (parent == agents[i]) {
//				parent = agents[i].province().randomAgent();
//			}
//			network.addEdge(agents[i], parent, i + " " + parent.agentID);
//		}
//	}
}
