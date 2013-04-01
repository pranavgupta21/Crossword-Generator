package com.pranav.projects.crossword;

import java.util.ArrayList;
import java.util.List;

public class CrosswordGenerator {
	public static int dimRows;
	public static int dimCols;
	public static int populationSize;
	public static int maxIterations;
	
	public static void init(){
		populationSize = 1000;
		dimRows = 15;
		dimCols = 15;
		maxIterations = 500;
	}
	/**
	 * Todo
	 * initialize configuration
	 * generate initial population
	 * loop:
	 * 		selection
	 * 		variation
	 * 		survival
	 * return best individual
	 */
	public static void main(String args[]){
		init();
		SelectionOperators selOp = new SelectionOperators();
		CrossoverOperators crossOp = new CrossoverOperators();
		MutationOperators mutateOp = new MutationOperators();
		
		// generate Initial Population //
		List<Individual> P = genInitialPopulation();
		
		// Iterate for Maximum Iterations //
		for (int iterNo = 0; iterNo < maxIterations; iterNo++){
			System.out.println("Iteration Number : " + iterNo);
			
			// Selection //
			List<Integer> selected = selOp.RWS_SUS(P, populationSize/2);
			
			// Crossover //
			for(int selectedNo = 0; selectedNo < selected.size(); selectedNo += 2){
				P.addAll(crossOp.squarePatchCross(P.get(selected.get(selectedNo)), P.get(selected.get(selectedNo + 1))));
			}
			
			// Mutation //
			for(int selectedNo = 0; selectedNo < selected.size(); selectedNo++){
				P.add(mutateOp.mutateEachCell(P.get(selected.get(selectedNo))));
			}
			
			// Survival //
			List<Integer> survivors = selOp.RWS_SUS(P, populationSize);
			List<Individual> PNext = new ArrayList<Individual>();
			for(int survivorNo = 0; survivorNo < selected.size(); survivorNo++){
				PNext.add(P.get(survivors.get(survivorNo)));
			}
			P = PNext;
		}
		
		// find best crossword //
		int bestIndividual = 0;
		double highestFitness = P.get(0).getFitness();
		for (int indNo = 1; indNo < P.size(); indNo++){
			if (P.get(indNo).getFitness() > highestFitness){
				highestFitness = P.get(indNo).getFitness();
				bestIndividual = indNo;
			}
		}
		Individual best = P.get(bestIndividual);
		
		// print the crossword //
		System.out.println("Density\t : \t" + best.getDensityPenalty());
		System.out.println("Intersection\t : \t" + best.getIntersectionPenalty());
		System.out.println("Symmetry\t : \t" + best.getSymmetryPenalty());
		System.out.println("Avg Word Length\t : \t" + best.getAvgWordLengthPenalty());
		System.out.println("Avg Contiguous Black Seq\t : \t" + best.getAvgContBlackCellsPenalty());
		System.out.println();
		for (int rowNo = 0; rowNo < dimRows; rowNo++){
			for (int colNo = 0; colNo < dimCols; colNo++){
				if(best.getCell(rowNo, colNo) == 0){
					System.out.print("$");
				}
				else{
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
	
	public static List<Individual> genInitialPopulation(){
		List<Individual> P = new ArrayList<Individual>();
		for (int indNo = 0; indNo < populationSize; indNo++){
			double cell_rand = 0;
			Integer mask_matrix[][] = new Integer[dimRows][dimCols];
			
			/**
			 * optimize the generation of a random matrix
			 */
			for (int rowNo = 0; rowNo < dimRows; rowNo++){
				for (int colNo = 0; colNo < dimCols; colNo++){
					cell_rand = Math.random();
					if (cell_rand < 0.5){
						mask_matrix[rowNo][colNo] = 0;
					}
					else{
						mask_matrix[rowNo][colNo] = 1;
					}
				}
			}
			P.add(new Individual(mask_matrix));
		}
		
		return P;
	}
}
