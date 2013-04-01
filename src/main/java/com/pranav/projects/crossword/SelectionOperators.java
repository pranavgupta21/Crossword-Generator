package com.pranav.projects.crossword;

import java.util.ArrayList;
import java.util.List;

public class SelectionOperators {
	public List<Integer> RWS_SUS(List<Individual> P, int numSelect){
		double totalFitness = 0;
		for (int indNo = 0; indNo < P.size(); indNo++){
			totalFitness += P.get(indNo).getFitness();
		}
		
		List<Integer> selected = new ArrayList<Integer>();
		double increment = totalFitness/P.size();
		double wheelPosition = Math.random() * totalFitness;
		double totalWheelPosition = wheelPosition;
		double cumulativeProb = P.get(0).getFitness();
		double totalCumulativeProb = cumulativeProb;
		for (int selectionNo = 0; selectionNo < numSelect; selectionNo++){
			int indNo = 1;
			while (totalWheelPosition > totalCumulativeProb){
				totalCumulativeProb += totalCumulativeProb + P.get(indNo).getFitness();
				cumulativeProb = totalCumulativeProb % totalFitness;
				indNo = (indNo + 1) % P.size();
			}
			selected.add(indNo);
			totalWheelPosition += increment;
			wheelPosition = totalWheelPosition % totalFitness;
		}
		return selected;
	}
}
