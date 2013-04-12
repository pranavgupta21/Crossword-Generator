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
		
		// place the pointer at some random initial position //
		double wheelPosition = Math.random() * totalFitness;
		double totalWheelPosition = wheelPosition;
		
		// add the fitness values starting from the first individual on the wheel till the pointer position is reached //
		double cumulativeProb = P.get(0).getFitness();
		double totalCumulativeProb = cumulativeProb;
		int indNo = 1;
		for (int selectionNo = 0; selectionNo < numSelect; selectionNo++){
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

	public List<Integer> SRWS_SUS(List<Individual> P, int numSelect){
		double sumFitness = 0;
		for (int indNo = 0; indNo < P.size(); indNo++){
			sumFitness += P.get(indNo).getFitness();
		}
		
		double totalFitness = 0;
		double fitness[] = new double[P.size()];
		List<Integer> selected = new ArrayList<Integer>();
		for (int indNo = 0; indNo < P.size(); indNo++){
			double expected = (P.get(indNo).getFitness()/sumFitness) * numSelect;
			int assign = (int) Math.floor(expected);
			fitness[indNo] = expected - assign;
			for (int addNo = 0; addNo < assign; addNo++){
				selected.add(indNo);
			}
			totalFitness += fitness[indNo];
		}
		int assigned = selected.size();
		//System.out.println("NumSelect : " + numSelect + " " + "Assigned : " + assigned);
		
		// place the pointer at some random initial position //
		double wheelPosition = Math.random() * totalFitness;
		double totalWheelPosition = wheelPosition;
		double increment = totalFitness/fitness.length;
		
		// add the fitness values starting from current individual till the pointer position is reached, then increment //
		double cumulativeProb = fitness[0];
		double totalCumulativeProb = cumulativeProb;
		int indNo = 1;
		for (int selectionNo = 0; selectionNo < (numSelect - assigned); selectionNo++){
			while (totalWheelPosition > totalCumulativeProb){
				totalCumulativeProb += totalCumulativeProb + fitness[indNo];
				cumulativeProb = totalCumulativeProb % totalFitness;
				indNo = (indNo + 1) % P.size();
			}
			selected.add(indNo);
			totalWheelPosition += increment;
			wheelPosition = totalWheelPosition % totalFitness;
		}
		//System.out.println("SRWS_SUS completed !");
		return selected;
	}
}
