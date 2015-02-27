package inputData;


public class PersonFromCSVFile {

	final int provinceID;

	final int age;

	final int yearsOfEducation;

	final boolean isMale;

	final boolean isMigrant;


	PersonFromCSVFile(String row) {
		String[] fields = row.split(",");
		this.provinceID = Integer.parseInt(fields[0]);
		this.age = Integer.parseInt(fields[1]);
		this.yearsOfEducation = Integer.parseInt(fields[2]);
		this.isMale = Boolean.parseBoolean(fields[3]);
		this.isMigrant = Boolean.parseBoolean(fields[4]);
	}


	public int provinceID() {
		return provinceID;
	}


	public int age() {
		return this.age;
	}


	public int yearsOfEducation() {
		return this.yearsOfEducation;
	}


	public boolean isMale() {
		return this.isMale;
	}


	public boolean isMigrant() {
		return this.isMigrant;
	}
}
