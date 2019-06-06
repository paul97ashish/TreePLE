package ca.mcgill.ecse321.project6.environment;

import java.sql.Date;
import java.util.Calendar;
import java.util.stream.Stream;

import ca.mcgill.ecse321.project6.environment.SpeciesInformation.Type;
import ca.mcgill.ecse321.project6.model.Tree;

public class EnvironmentInformation {
	private static final double poundsToKilograms = 0.4535924;
	private static final double carbonToEquivalentCO2 = 3.67;
	
	@SuppressWarnings("deprecation")
	public static double carbonOffsetPerYear(Stream<Tree> trees) {
		// in kilograms
		return trees.reduce(0D, (acc, t) -> {
			String species = t.getSpecies();
			if(!SpeciesInformation.speciesInformation.containsKey(species)) {
				species = species.toLowerCase();
				String[] items = species.split("\\W");
				species = items[items.length - 1];
				species += ", ";
				for (int i= 0; i < items.length - 1; i++) {
					species += items[i] + " ";
				}
				species = species.trim();
				if(!SpeciesInformation.speciesInformation.containsKey(species)) {
					species = items[items.length - 1];
					if(!SpeciesInformation.speciesInformation.containsKey(species)) {
						return acc;
					}
				}
			}
			Type treeType = SpeciesInformation.speciesInformation.get(species);
			int treeAge;
			if(t.getEntries().size() > 0) {
				Date plantDate = t.getEntry(0).getRecordedOn();	// assuming this is the date the tree was planted
				Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
				treeAge = currentDate.getYear() - plantDate.getYear();
			} else {
				treeAge = 10; // this is entirely arbitrary
			}
			double[] sequestrationByAge;
			switch(treeType) {
			case HardwoodSlow:
				sequestrationByAge = SpeciesInformation.sequestrationRateHS;
				break;
			case HardwoodModerate:
				sequestrationByAge = SpeciesInformation.sequestrationRateHM;
				break;
			case HardwoodFast:
				sequestrationByAge = SpeciesInformation.sequestrationRateHF;
				break;
			case ConiferSlow:
				sequestrationByAge = SpeciesInformation.sequestrationRateCS;
				break;
			case ConiferModerate:
				sequestrationByAge = SpeciesInformation.sequestrationRateCM;
				break;
			default:
				sequestrationByAge = SpeciesInformation.sequestrationRateCF;
				break;
			}
			return acc + sequestrationByAge[treeAge < 60 ? treeAge : 59] * poundsToKilograms;	//chart only goes to 59 years and is in pounds
		}, (a, b) -> a + b);
	}
	
	public static double CO2OffsetPerYear(Stream<Tree> trees) {
		return carbonToEquivalentCO2 * carbonOffsetPerYear(trees);
	}
}
