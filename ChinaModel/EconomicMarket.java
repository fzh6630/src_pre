package ChinaModel;


import sim.engine.*;


public class EconomicMarket implements Steppable {

	public double marketPrice;

	private double farmingWage;

	public double technologyStock;

	public CHModel model;


	public void step(SimState state) {

		model = (CHModel) state;

		setMarketPrice();
		setTechnologyStock();
                setFarmingWage(model.initialFarmingWage);
		
	}


	public void setMarketPrice() {
		marketPrice = 1;
	}

	//CURRENTLY SET TO RANDOM FDI
	//set the amounts of Foreign Direct Investment (FDI) going to each city


	public void setTechnologyStock() {
		long t = model.year();
		technologyStock = model.params().initialTechnologyStock() * Math.exp(
				model.params().gamma() * t);
	}
        
        public double farmingWage() {
            return farmingWage;
        }
        
        public void setFarmingWage(double fw) {
            farmingWage = fw;
        }
}
