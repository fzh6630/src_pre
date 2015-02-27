package ChinaModel;


import java.util.Comparator;


public class DiffComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		Agent a1 = (Agent) o1;
		Agent a2 = (Agent) o2;

		double ret = a1.education() - a2.education();

		if (ret > 0) {
			return 1;
		} else if (ret < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
