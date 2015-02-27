package ChinaModel;


import sim.engine.*;
import sim.util.*;


public class Firm implements Steppable {
	
	public final int firmID;

	public String firmName;

	public int[] prefRankings;

	private Patch firmPatch;

	// How many agents will the firm hire that turn?
	private int numberOfOffersToMake;

	public double hiringWage;

	public int numOutstandingOffers;

	public Bag outstandingOfferAgents = new Bag();

	public Bag candidates = new Bag();

	private Bag employees = new Bag();

	//records
	public int offersAcceptedCount;

	public int offersRejectedCount;

	public int offersMadeCount;

	// Macroeconomic inputs - Labor (L), Capital (K), technology (A)
	public double numEmployees;
        public double foreignDirectInvestment;
	public double technologyStock;

	public CHModel model;

	public EconomicMarket market;

	public LaborMarket laborMarket;


	public Firm(int id, Patch p, EconomicMarket em, CHModel m) {
		model = m;
                market = em;
                firmID = id;
                this.firmPatch = p;
		
                //set initial Migrant Employee Wages
                setCurrentFirmInputs(0);
                double w = calcMPL(technologyStock, foreignDirectInvestment, numEmployees + 1) * market.marketPrice;
                for (int i=0; i< employees().numObjs; i++) {
                    ((Agent) employees().objs[i]).setWage(w);
                }
               
               //initial number of hires to attempt
               numberOfOffersToMake = (int) numEmployees + 1;

	}


	public void step(SimState state) {
		// Calculate the optimal L, find the difference from current L, then
		// create a hiring plan 

		//the overall mdoel and market are linked to the firm 
		model = (CHModel) state;
		market = model.economicMarket;
		setCurrentFirmInputs();

		//reset offers/candidates to zero
		numOutstandingOffers = 0;
		outstandingOfferAgents.clear();
		candidates.clear();

		//DEBUG OUTPUT
//                System.out.println("FirmID= " + firmID + " FDI= "
//				+ foreignDirectInvestment + " L =" + numEmployees);
	}
       
        public void setCurrentFirmInputs() {
		//the firm has a minimum of one employee (i.e. the entrepreneur)
		this.numEmployees = employees.numObjs + 1 ;
		this.foreignDirectInvestment = this.province().foreignDirectInvestment()[model.year()];
		this.technologyStock = market.technologyStock;
	}
                
	public void setCurrentFirmInputs(int year) {
		//the firm has a minimum of one employee (i.e. the entrepreneur)
		this.numEmployees = employees.numObjs + 1;
		this.foreignDirectInvestment = this.province().foreignDirectInvestment()[year];
		this.technologyStock = market.technologyStock;
	}

	//The hiring wage is simply marginal product of the next unit of Labor
	public double calcMPL(double A, double K, double L) {
                
            /// AD HOC CODE HERE ***********
            /// Toying around with it to get the proper ratios
                //K = K * 10000;
                //L = L * 5000;
               
                double mpl;
		//Q = AK^alphaL^Beta	
		double term1 = A * Math.pow(K, model.getAlpha());
		double term2 = Math.pow(L, model.getBeta() - 1);
		mpl = model.getBeta() * term1 * term2;

		//DEBUG OUTPUT
		//	System.out.println("A , mpl "+ A + " " + mpl + " K, L " + K +" " + L);
		return mpl;
	}


	public void makeJobOffers(LaborMarket laborMarket) {

		boolean offerAccepted;
		double wageOffers[] = new double[numberOfOffersToMake];

		//make offers so long as the number of outstanding offers is less than
		//the desired number of hires
		if (candidates.numObjs > 0) {
			for (int i = numOutstandingOffers; i < numberOfOffersToMake; i++) {
				wageOffers[i] = calcMPL(technologyStock, foreignDirectInvestment, numEmployees + i + 1) * market.marketPrice;

				offerAccepted = ((Agent) candidates.objs[i]).evaluateJobOffer(
						this,
						wageOffers[i],
						laborMarket);

				if (offerAccepted) {
					numOutstandingOffers++;
					outstandingOfferAgents.add(candidates.objs[i]);

				}
				if (!offerAccepted) {
					candidates.remove(candidates.objs[i]);
					laborMarket.rejections++;

				}
			}
		}

		//System.out.println();
	}


	public void sortCandidates(Bag c) {
		candidates.clear();
		candidates.addAll(c);
		candidates.sort(new DiffComparator());
	}


	public void makeFinalHires() {
		employees.addAll(outstandingOfferAgents);
		for (int i = 0; i < outstandingOfferAgents.numObjs; i++) {
			((Agent) outstandingOfferAgents.objs[i]).setEmployer();
		}

		if (outstandingOfferAgents.numObjs > 0) {
			float n = numberOfOffersToMake;
			n = (float) (n * (1 + model.params().offerGrowthRate()));
			numberOfOffersToMake = Math.round(n);

			//System.out.println("Offers To Make:" + numberOfOffersToMake);
		} else {
			float n = numberOfOffersToMake;
			n = (float) (n * (1 - model.params().offerGrowthRate()));
			numberOfOffersToMake = Math.round(n);
		}
	}


	public void offerRejected(Agent a) {
		outstandingOfferAgents.remove(a);
		candidates.remove(a);
		numOutstandingOffers--;
	}


	public Patch patch() {
		return this.firmPatch;
	}

        public Bag employees() {
            return employees;
        }

	public final Province province() {
		return this.firmPatch.province();
	}
        
        public void addEmployee(Agent a) {
            employees.add(a);
        }
}
