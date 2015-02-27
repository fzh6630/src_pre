package executables;


import ChinaModel.CHModel;
import ChinaModel.ParameterSets;


public class InitialExperimentCode {

	private static int BATCH_SIZE = 400;


	// main method: runs the non-graphical simulation 
	public static void main(String[] args) {

		for (int i = 0; i < BATCH_SIZE; i++) {
			System.out.println("Run # " + i);
			// builds model
			CHModel model = new CHModel(System.currentTimeMillis(), i);

			double w1 = model.random.nextDouble() * 0.5;
			double w2 = model.random.nextDouble() * 0.5;
			double w3 = model.random.nextDouble() * 0.5;
			double w4 = model.random.nextDouble() * 0.5;
			double p_a = model.random.nextDouble() * 0.1;
			double p_r = model.random.nextDouble() * 0.1;

			model.setParameters(ParameterSets.keyVariablesExperimentParameters(
					w1,
					w2,
					w3,
					w4,
					p_a,
					p_r));

			model.start();
			model.simulateUntilComplete();
			model.finish();
			System.gc();
		}
		System.exit(0);
	}


}
