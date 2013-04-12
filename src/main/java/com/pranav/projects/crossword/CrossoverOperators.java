package com.pranav.projects.crossword;

import java.util.ArrayList;
import java.util.List;

public class CrossoverOperators {
	/**
	 * single square patch crossover
	 * returns child1 and child2 in that order in a list
	 */
	public List<Individual> squarePatchCross(Individual p1, Individual p2){
		int nRows = p1.getRows();
		int nCols = p1.getCols();
		int sideLen = (int) Math.floor(Math.random() * 3) + 2;
		int patch1_x = 0, patch1_y = 0, patch2_x = 0, patch2_y = 0;
		patch1_x = (int) Math.floor(Math.random() * (nRows - sideLen + 1));
		patch1_y = (int) Math.floor(Math.random() * (nCols - sideLen + 1));
		patch2_x = (int) Math.floor(Math.random() * (nRows - sideLen + 1));
		patch2_y = (int) Math.floor(Math.random() * (nCols - sideLen + 1));
		
		int diffX = patch2_x - patch1_x, diffY = patch2_y - patch1_y;
		//System.out.println(patch1_x + ", " + patch1_y + "\t" + patch2_x + ", " + patch2_y);
		
		Integer i1_matrix[][] = new Integer[nRows][nCols];
		Integer i2_matrix[][] = new Integer[nRows][nCols];
		for (int rowNo = 0; rowNo < nRows; rowNo++){
			for (int colNo = 0; colNo < nCols; colNo++){
				// child 1 //
				if(rowNo >= patch1_x && rowNo < (patch1_x + sideLen) && colNo >= patch1_y && colNo < (patch1_y + sideLen)){
					i1_matrix[rowNo][colNo] = p2.getCell(rowNo + diffX, colNo + diffY);
				}
				else{
					i1_matrix[rowNo][colNo] = p1.getCell(rowNo, colNo);
				}
				
				// child 2 //
				if(rowNo >= patch2_x && rowNo < (patch2_x + sideLen) && colNo >= patch2_y && colNo < (patch2_y + sideLen)){
					i2_matrix[rowNo][colNo] = p1.getCell(rowNo - diffX, colNo - diffY);
				}
				else{
					i2_matrix[rowNo][colNo] = p2.getCell(rowNo, colNo);
				}
			}
		}
		
		List<Individual> children = new ArrayList<Individual>();
		children.add(new Individual(i1_matrix));
		children.add(new Individual(i2_matrix));
		
		return children;
	}
}
