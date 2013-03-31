package com.pranav.projects.crossword;

public class Individual {
	// Primary matrix
	private Integer matrix[][];
	
	// TODO : Use enums
	int BLACK = 0;
	int WHITE = 1;
	
	// Variables required for matrix operations
	int rows, cols;
	
	// Penalties
	private double penalty_density;
	private double penalty_intersection;
	private double penalty_symmetry;
	private double penalty_word_length;
	private double penalty_avg_word_length;
	private double penalty_cont_black_cells;
	private double penalty_avg_cont_black_cells;
	private double penalty_diagonal_black_cells;
	
	// Constants
	static double optimal_density = 0.5;
	static double  optimal_intersections = 0.25;	
	
	
	private double fitness;
	
	void Individual(Integer[][] mat){
		matrix = mat;
		rows = matrix.length;
		cols = matrix[0].length;
		computeFitness();
	}
	
	public Integer getCell(int row, int col){
		return matrix[row][col];		
	}

	public double getDensityPenalty(){
		return penalty_density;
	}
	
	public double getIntersectionPenalty(){
		return penalty_intersection;
	}

	public double getSymmetryPenalty(){
		return penalty_symmetry;
	}
	
	public double getWordLengthPenalty(){
		return penalty_word_length;
	}
	
	public double getAvgWordLengthPenalty(){
		return penalty_avg_word_length;
	}
	
	public double getContBlackCellsPenalty(){
		return penalty_cont_black_cells;
	}
	
	public double getFitness(){
		return fitness;
	}
	
	
	boolean isIntersection (int row, int col){
		assert (row < rows && col < cols);
		
		if (matrix[row][col] == WHITE)
		{
			boolean horizontalStrip = false, verticalStrip = false;
			
			//TODO: Check if this cell is part of a horizontal strip
			
			
			//TODO: Check if this cell is part of a vertical strip 
			
			return horizontalStrip && verticalStrip;
		}
		else
			return false;
	}
	
	void computeFitness(){		
		int whiteCells = 0, nIntersections = 0, asymmetries = 0;		
		
		// Iterate through the cells
		for(int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				whiteCells += (matrix[i][j] == WHITE ? 1 : 0);
				nIntersections += (isIntersection(i, j) ? 1 : 0);
				asymmetries += (matrix[i][j] != matrix[j][i] ? 1 : 0);	// TODO : verify			
			}
		}
		asymmetries /= 2;	// Every asymmetry counted twice
		
		// Compute the penalties
		penalty_density = Math.abs(whiteCells * 1.0 / (rows * cols) - optimal_density);
		penalty_intersection = Math.abs(nIntersections - whiteCells * optimal_intersections);
		penalty_symmetry = asymmetries;
		
		
		// Final step; compute fitness
		double penaltiesSum = penalty_density;		// Sum up all penalties		
		fitness = 1.0 / penaltiesSum;	
	}
}