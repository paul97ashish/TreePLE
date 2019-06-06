package ca.mcgill.ecse321.project6.environment;

import java.util.HashMap;
import java.util.Map;

// Basically all of this is from the 1998 US Department of Energy
// "Method for Calculating Carbon Sequestration by Trees in Urban
// and Suburban Settings" worksheet.
// https://www3.epa.gov/climatechange/Downloads/method-calculating-carbon-sequestration-trees-urban-and-suburban-settings.pdf

public class SpeciesInformation {
	
	protected static Map<String, Type> speciesInformation = initMap();
	
/*	protected static final double[] survivalFactorSlow = {
		0.873, 0.798, 0.736, 0.706, 0.678,
		0.658, 0.639, 0.621, 0.603, 0.585,
	};
	protected static final double[] survivalFactorModerate = {	
	};
	protected static final double[] survivalFactorFast = {
	};*/
	
	// sequestration rate is in pounds carbon/tree/year.
	// conversion to kilograms/tonnes/anything more sensible is later
	
	protected static final double[] sequestrationRateHS = {	
		1.3, 1.6, 2.0, 2.4, 2.8,
		3.2, 3.7, 4.1, 4.6, 5.0,
		5.5, 6.0, 6.5, 7.0, 7.5,
		8.1, 8.6, 9.1, 9.7, 10.2,
		10.8, 11.4, 12.0, 12.5, 13.1,
		13.7, 14.3, 15.0, 15.6, 16.2,
		16.8, 17.5, 18.1, 18.7, 19.4,
		20.0, 20.7, 21.4, 22.0, 22.7,
		23.4, 24.1, 24.8, 25.4, 26.1,
		26.8, 27.6, 28.3, 29.0, 29.7,
		30.4, 31.1, 31.9, 32.6, 33.4,
		34.1, 34.8, 35.6, 36.3, 37.1
	};
	protected static final double[] sequestrationRateHM = {
		1.9, 2.7, 3.5, 4.3, 5.2,
		6.1, 7.1, 8.1, 9.1, 10.2,
		11.2, 12.3, 13.5, 14.6, 15.8,
		16.9, 18.1, 19.4, 20.6, 21.9,
		23.2, 24.4, 25.8, 27.1, 28.4,
		29.8, 31.2, 32.5, 33.9, 35.3,
		36.8, 38.2, 39.7, 41.1, 42.6,
		44.1, 45.6, 47.1, 48.6, 50.2,
		51.7, 53.3, 54.8, 56.4, 58.0,
		59.6, 61.2, 62.8, 64.5, 66.1,
		67.8, 69.4, 71.1, 72.8, 74.5,
		76.2, 77.9, 79.6, 81.3, 83.0
	};
	protected static final double[] sequestrationRateHF = {
		2.7, 4.0, 5.4, 6.9, 8.5,
		10.1, 11.8, 13.6, 15.5, 17.4,
		19.3, 21.3, 23.3, 25.4, 27.5,
		29.7, 31.9, 34.1, 36.3, 38.6,
		41.0, 43.3, 45.7, 48.1, 50.6,
		53.1, 55.6, 58.1, 60.7, 63.3,
		65.9, 68.5, 71.2, 73.8, 76.5,
		79.3, 82.0, 84.8, 87.6, 90.4,
		93.2, 96.1, 99.0, 101.9, 104.8,
		107.7, 110.7, 113.6, 116.6, 119.6,
		122.7, 125.7, 128.8, 131.8, 134.9,
		138.0, 141.2, 144.3, 147.5, 150.6
	};
	protected static final double[] sequestrationRateCS = {
		0.7, 0.9, 1.1, 1.4, 1.6,
		1.9, 2.2, 2.5, 2.8, 3.1,
		3.5, 3.8, 4.2, 4.6, 4.9,
		5.3, 5.7, 6.1, 6.6, 7.0,
		7.4, 7.9, 8.3, 8.8, 9.2,
		9.7, 10.2, 10.7, 11.2, 11.7,
		12.2, 12.7, 13.3, 13.8, 14.3,
		14.9, 15.5, 16.0, 16.6, 17.2,
		17.7, 18.3, 18.9, 19.5, 20.1,
		20.7, 21.3, 22.0, 22.6, 23.2,
		23.9, 24.5, 25.2, 25.8, 26.5,
		27.2, 27.8, 28.5, 29.2, 29.9
	};
	protected static final double[] sequestrationRateCM = {
		1.0, 1.5, 2.0, 2.5, 3.1,
		3.7, 4.4, 5.1, 5.8, 6.6,
		7.4, 8.2, 9.1, 9.9, 10.8,
		11.8, 12.7, 13.7, 14.7, 15.7,
		16.7, 17.8, 18.9, 20.0, 21.1,
		22.2, 23.4, 24.6, 25.8, 27.0,
		28.2, 29.5, 30.7, 32.0, 33.3,
		34.7, 36.0, 37.3, 38.7, 40.1,
		41.5, 42.9, 44.3, 45.8, 47.2,
		48.7, 50.2, 51.7, 53.2, 54.8,
		56.3, 57.9, 59.4, 61.0, 62.6,
		64.2, 65.9, 67.5, 69.2, 70.8
	};
	protected static final double[] sequestrationRateCF = {
		1.4, 2.2, 3.1, 4.1, 5.2,
		6.4, 7.6, 8.9, 10.2, 11.7,
		13.2, 14.7, 16.3, 17.9, 19.6,
		21.4, 23.2, 25.0, 26.9, 28.8,
		30.8, 32.8, 34.9, 37.0, 39.1,
		41.3, 43.5, 45.7, 48.0, 50.3,
		52.7, 55.1, 57.5, 59.9, 62.4,
		64.9, 67.5, 70.1, 72.7, 75.3,
		78.0, 80.7, 83.4, 86.2, 80.9,
		91.8, 94.7, 97.5, 100.4, 103.4,
		106.3, 109.3, 112.3, 115.4, 118.4,
		121.5, 124.6, 127.8, 130.9, 134.1
	};
	
	protected enum Type {
		HardwoodSlow,
		HardwoodModerate,
		HardwoodFast,
		ConiferSlow,
		ConiferModerate,
		ConiferFast
	}
	
	private static Map<String, Type> initMap() {
		Map<String,Type> map = new HashMap<String,Type>();
		map.put("ailanthus", Type.HardwoodFast);
		map.put("alder, european", Type.HardwoodFast);
		map.put("ash", Type.HardwoodFast);
		map.put("ash, green", Type.HardwoodFast);
		map.put("ash, mountain", Type.HardwoodModerate);
		map.put("ash, white", Type.HardwoodFast);
		map.put("aspen, bigtooth", Type.HardwoodModerate);
		map.put("aspen, quaking", Type.HardwoodFast);
		map.put("baldcypress", Type.ConiferFast);
		map.put("basswood, american", Type.HardwoodFast);
		map.put("beech, american", Type.HardwoodSlow);
		map.put("birch, paper", Type.HardwoodModerate);
		map.put("birch, river", Type.HardwoodModerate);
		map.put("birch, yellow", Type.HardwoodSlow);
		map.put("boxelder", Type.HardwoodFast);
		map.put("buckeye, ohio", Type.HardwoodSlow);
		map.put("catalpa, northern", Type.HardwoodFast);
		map.put("cedar-red, eastern", Type.ConiferModerate);
		map.put("cedar-white, northern", Type.ConiferModerate);
		map.put("cherry, black", Type.HardwoodFast);
		map.put("cherry, pin", Type.HardwoodModerate);
		map.put("cottonwood, eastern", Type.HardwoodModerate);
		map.put("crabapple", Type.HardwoodModerate);
		map.put("cucumbertree", Type.HardwoodFast);
		map.put("dogwood, flowering", Type.HardwoodSlow);
		map.put("elm", Type.HardwoodFast);
		map.put("elm, american", Type.HardwoodFast);
		map.put("elm, chinese", Type.HardwoodModerate);
		map.put("elm, rock", Type.HardwoodSlow);
		map.put("elm, september", Type.HardwoodFast);
		map.put("elm, siberian", Type.HardwoodFast);
		map.put("elm, slippery", Type.HardwoodModerate);
		map.put("fir, balsam", Type.ConiferSlow);
		map.put("fir, douglas", Type.ConiferFast);
		map.put("ginkgo", Type.HardwoodSlow);
		map.put("hackberry", Type.HardwoodFast);
		map.put("hawthorne", Type.HardwoodModerate);
		map.put("hemlock, eastern", Type.ConiferModerate);
		map.put("hickory, bitternut", Type.HardwoodSlow);
		map.put("hickory, mockernut", Type.HardwoodModerate);
		map.put("hickory, shagbark", Type.HardwoodSlow);
		map.put("hickory, shellbark", Type.HardwoodSlow);
		map.put("hickory, pignut", Type.HardwoodModerate);
		map.put("holly, american", Type.HardwoodSlow);
		map.put("honeylocust", Type.HardwoodFast);
		map.put("hophornbeam, eastern", Type.HardwoodSlow);
		map.put("horsechestnut, common", Type.HardwoodFast);
		map.put("kentucky coffeetree", Type.ConiferFast);
		map.put("linden, little-leaf", Type.HardwoodFast);
		map.put("locust, black", Type.HardwoodFast);
		map.put("london plane tree", Type.HardwoodFast);
		map.put("magnolia, southern", Type.HardwoodModerate);
		map.put("maple", Type.HardwoodSlow);
		map.put("maple, bigleaf", Type.HardwoodSlow);
		map.put("maple, norway", Type.HardwoodModerate);
		map.put("maple, red", Type.HardwoodModerate);
		map.put("maple, silver", Type.HardwoodModerate);
		map.put("maple, sugar", Type.HardwoodSlow);
		map.put("mulberry, red", Type.HardwoodFast);
		map.put("oak", Type.HardwoodFast);
		map.put("oak, black", Type.HardwoodModerate);
		map.put("oak, blue", Type.HardwoodModerate);
		map.put("oak, bur", Type.HardwoodSlow);
		map.put("oak, california black", Type.HardwoodSlow);
		map.put("oak, california white", Type.HardwoodModerate);
		map.put("oak, canyon live", Type.HardwoodSlow);
		map.put("oak, chestnut", Type.HardwoodSlow);
		map.put("oak, chinkapin", Type.HardwoodModerate);
		map.put("oak, Laurel", Type.HardwoodFast);
		map.put("oak, live", Type.HardwoodFast);
		map.put("oak, northern red", Type.HardwoodFast);
		map.put("oak, overcup", Type.HardwoodSlow);
		map.put("oak, pin", Type.HardwoodFast);
		map.put("oak, scarlet", Type.HardwoodFast);
		map.put("oak, swamp white", Type.HardwoodModerate);
		map.put("oak, water", Type.HardwoodModerate);
		map.put("oak, willow", Type.HardwoodModerate);
		map.put("oak, white", Type.HardwoodSlow);
		map.put("pecan", Type.HardwoodSlow);
		map.put("pine", Type.ConiferFast);
		map.put("pine, european black", Type.ConiferSlow);
		map.put("pine, jack", Type.ConiferFast);
		map.put("pine, loblolly", Type.ConiferFast);
		map.put("pine, longleaf", Type.ConiferFast);
		map.put("pine, ponderosa", Type.ConiferFast);
		map.put("pine, red", Type.ConiferFast);
		map.put("pine, scotch", Type.ConiferSlow);
		map.put("pine, shortleaf", Type.ConiferFast);
		map.put("pine, slash", Type.ConiferFast);
		map.put("pine, virginia", Type.ConiferModerate);
		map.put("pine, white eastern", Type.ConiferFast);
		map.put("poplar, yellow", Type.HardwoodFast);
		map.put("redbud, eastern", Type.HardwoodModerate);
		map.put("sassafras", Type.HardwoodModerate);
		map.put("spruce", Type.ConiferModerate);
		map.put("spruce, black", Type.ConiferSlow);
		map.put("spruce, blue", Type.ConiferModerate);
		map.put("spruce, norway", Type.ConiferModerate);
		map.put("spruce, red", Type.ConiferSlow);
		map.put("spruce, white", Type.ConiferModerate);
		map.put("sugarberry", Type.HardwoodFast);
		map.put("sweetgum", Type.HardwoodFast);
		map.put("sycamore", Type.HardwoodFast);
		map.put("tamarack", Type.ConiferFast);
		map.put("walnut, black", Type.HardwoodFast);
		map.put("willow, black", Type.HardwoodFast);
		return map;
	}
}
