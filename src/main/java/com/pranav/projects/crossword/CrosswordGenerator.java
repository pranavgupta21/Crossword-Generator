package com.pranav.projects.crossword;

import java.util.ArrayList;
import java.util.List;

public class CrosswordGenerator {
	public static int populationSize;
	public static int dimRows;
	public static int dimCols;
	
	public static void init(){
		populationSize = 1000;
		dimRows = 15;
		dimCols = 15;
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
		
		// generate Initial Population //
		List<Individual> P = new ArrayList<Individual>();
		for (int indNo = 0; indNo < populationSize; indNo++){
			Integer mask_matrix[][] = new Integer[dimRows][dimCols];
			
			P.add(new Individual(mask_matrix));
		}
	}
	
	public static void genInitialPopulation(){
		
	}
}
