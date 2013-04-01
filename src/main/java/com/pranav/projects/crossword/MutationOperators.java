package com.pranav.projects.crossword;

public class MutationOperators {
	/**
	 * randomly decides mutation for each cell
	 */
	public Individual mutateEachCell(Individual p){
		int nRows = p.getRows();
		int nCols = p.getCols();
		
		Integer i_matrix[][] = new Integer[nRows][nCols];
		for (int rowNo = 0; rowNo < nRows; rowNo++){
			for (int colNo = 0; colNo < nCols; colNo++){
				double mutate_rand = Math.random();
				if(mutate_rand < 0.5){
					i_matrix[rowNo][colNo] = (p.getCell(rowNo, colNo) + 1) % 2;
				}
				else{
					i_matrix[rowNo][colNo] = p.getCell(rowNo, colNo);
				}
			}
		}
		
		return new Individual(i_matrix);
	}
}
