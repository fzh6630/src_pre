package ChinaModel;


import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;
import ec.util.*;
import java.io.*;


public class Patch implements Steppable {

	public CHModel model;

	private Int2D location;

	private Province province;

	public int arableValue; //scale 1-10, 10 is most arable

	public Bag localAgents = new Bag();


	public Patch(int x, int y, Province province) {
		location = new Int2D(x, y);
		this.province = province;
		
//		System.out.println("Creating patch :: " + x + ", " + y + " :: " + provinceID);
	}


	public Province province() {
		return this.province;
	}


	public void step(SimState state) {
		// Calculate the optimal L, find the difference from current L, then
		// create a hiring plan 

		model = (CHModel) state;
	}


	public void addAgentToPatch(Agent a) {
//		System.out.println("Adding an agent to a patch in " + this.province.provinceName());
		localAgents.add(a);
		province.addAgentToProvince(a);
	}


	public void removeAgentFromPatch(Agent a) {
		localAgents.remove(a);
		province.removeAgentFromProvince(a);
	}


	public int localPopulation() {
		return localAgents.numObjs;
	}


	public Int2D location() {
		return this.location;
	}
	

	public boolean isPartOfChina() {
		return this.province.provinceName().isPartOfChina();
	}


	public double getColorLevel() {
		
		if (this.isPartOfChina()) {
			return this.province.provinceName().provinceNum;
		} else {
			return ChineseProvince.NOT_CHINA.provinceNum;
		}
	}
}
