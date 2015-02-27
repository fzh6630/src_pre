package ChinaModel;

import java.util.HashMap;

public enum ChineseProvince {

	NOT_CHINA(-9999),
	ANHUI(1),
	BEIJING(2),
	CHONGQING(3),
	FUJIAN(4),
	GANSU(5),
	GUANGDONG(6),
	GUANGXI(7),
	GUIZHOU(8),
	HAINAN(9),
	HEBEI(10),
	HEILONGJIANG(11),
	HENAN(12),
	HUBEI(13),
	HUNAN(14),
	JIANGSU(15),
	JIANGXI(16),
	JILIN(17),
	LIAONING(18),
	NEI_MONGOL(19),
	NINGXIA_HUI(20),
	PARACEL_ISLANDS(21),
	QINGHAI(22),
	SHAANXI(23),
	SHANDONG(24),
	SHANGHAI(25),
	SHANXI(26),
	SICHUAN(27),
	TIANJIN(28),
	XINJIANG_UYGUR(29),
	XIZANG(30),
	YUNNAN(31),
	ZHEJIANG(32);

	final int provinceNum;

	static HashMap<Integer, ChineseProvince> lookupTable = buildLookupTable();

	ChineseProvince(int id) {
		this.provinceNum = id;
	}

	/**
	 * @return - True if this ChineseProvince is a part of China (this is only false for the
	 * NOT_CHINA value).
	 */
	public boolean isPartOfChina() {
		return (this.provinceNum != -9999);
	}

	private static HashMap<Integer, ChineseProvince> buildLookupTable() {

		HashMap<Integer, ChineseProvince> map = new HashMap<>();
		for (ChineseProvince province : values()) {
			map.put(province.provinceNum, province);
		}
		return map;
	}

	/** @return - The ChineseProvince with the integer ID given. */
	public static ChineseProvince provinceFromID(int provinceID) {

		ChineseProvince found = lookupTable.get(provinceID);

		if (found == null) {
			throw new IllegalArgumentException(
					"No ChineseProvince has the provinceID :: " + provinceID);
		} else {
			return found;
		}
	}

}
