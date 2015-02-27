package utilities;


import ChinaModel.Patch;


public class Utilities {


	public static double calcGeometricDistance(int ax, int ay, int bx, int by) {
		return Math.hypot(ax - bx, ay - by);
	}


	public static double calcGeometricDistance(Patch p1, Patch p2) {
		return calcGeometricDistance(
				p1.location().x, p1.location().y,
				p2.location().x, p2.location().y);
	}


	/**
	 * Calculates the mean and standard deviation of an array of numbers.
	 * see Knuth's The Art Of Computer Programming Volume II:
	 * Seminumerical Algorithms.
	 *
	 * This algorithm is slower, but more resistant to error propagation.
	 *
	 * Note: For a collection of size 1, the standDev returned is -1
	 *
	 * @param data - A collection of numbers to analyze
	 *
	 * @return - An array where returned[0] = mean and returned[1] = standDev.
	 */
	public static double[] meanAndSD(double[] data) {
		final int n = data.length;

		double avg = data[0];
		double sum = 0;
		for (int i = 1; i < data.length; i++) {
			double newavg = avg + (data[i] - avg) / (i + 1);
			sum += (data[i] - avg) * (data[i] - newavg);
			avg = newavg;
		}

		return new double[]{
			avg,
			(n >= 2) ? Math.sqrt(sum / (n - 1)) : -1
		};
	}
}
