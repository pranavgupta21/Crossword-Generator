package com.pranav.projects.crossword;

public class Individual {
	// Primary matrix
	private Integer matrix[][];
	
	// TODO : Use enums
	public static int BLACK = 0, WHITE = 1;
	
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
	
	// Feasibility variable
	private boolean feasible;
	
	// Constants
	static double optimal_density = 0.5;
	static double optimal_intersections = 0.25;
	static int contiguous_black_threshold = 5;
	static int min_contiguous_black_cells = 1;
	static int max_contiguous_black_cells = 1;
	static int optimal_avg_word_len = 7;
	static int min_word_len = 3;
	static int max_word_len = 15;
	
	private double fitness;
	
	Individual(Integer[][] mat){
		matrix = mat;
		rows = matrix.length;
		cols = matrix[0].length;
		computeFitness();
	}
	public int getRows(){
		return rows;
	}
	
	public int getCols(){
		return cols;
	}
	
	public boolean isFeasible(){
		return feasible;		
	}
	
	public Integer getCell(int row, int col){
		return matrix[row][col];		
	}

	public void setCell(int row, int col, int val){
		matrix[row][col] = val;
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
	
	public double getAvgContBlackCellsPenalty(){
		return 	penalty_avg_cont_black_cells;
	}
	
	public double getFitness(){
		return fitness;
	}
	
	
	boolean isIntersection (int row, int col){
		assert (row < rows && col < cols);
		
		if (matrix[row][col] == WHITE)
		{
			boolean horizontalStrip = false, verticalStrip = false;
			
			// Check if this cell is part of a horizontal strip
			if ( (row < rows - 1 && matrix[row + 1][col] == WHITE) || (row > 0 && matrix[row - 1][col] == WHITE))
			{
				horizontalStrip = true;
			}			
			
			// Check if this cell is part of a vertical strip
			if ((col < cols - 1 && matrix[row][col + 1] == WHITE) || (col > 0 && matrix[row][col - 1] == WHITE))
			{
				verticalStrip = true;
			}
			
			return horizontalStrip && verticalStrip;
		}
		else
			return false;
	}
	
	void computeFitness(){		
		int whiteCells = 0, nIntersections = 0, asymmetries = 0;
		int extraContiguousBlackCells = 0, contiguousBlackCells; 
		int minContigBlackCells = Integer.MAX_VALUE, maxContigBlackCells = Integer.MIN_VALUE;
		int wordLength, totalWordLength = 0, wordCount = 0;
		int minWordLength = Integer.MAX_VALUE, maxWordLength = Integer.MIN_VALUE;
		
		// Iterate through the cells
		for(int r = 0; r < rows; r++)
		{			
			contiguousBlackCells = 0;
			wordLength = 0;
			for (int c = 0; c < cols; c++)
			{
				whiteCells += (matrix[r][c] == WHITE ? 1 : 0);
				nIntersections += (isIntersection(r, c) ? 1 : 0);
				asymmetries += (matrix[r][c] != matrix[c][r] ? 1 : 0);	// TODO : verify		
				
				if (matrix[r][c] == BLACK)
				{
					contiguousBlackCells++;
					if (contiguousBlackCells > contiguous_black_threshold)
						extraContiguousBlackCells++;					
				}
				else
				{
					if (contiguousBlackCells > 0)
					{
						minContigBlackCells = Math.min(minContigBlackCells, contiguousBlackCells);
						maxContigBlackCells = Math.max(maxContigBlackCells, contiguousBlackCells);
						contiguousBlackCells = 0;
					}					
				}
				
				if (matrix[r][c] == WHITE)
				{					
					wordLength++;					
				}
				else
				{
					if (wordLength > 0)
					{
						minWordLength = Math.min (minWordLength, wordLength);
						maxWordLength = Math.min (maxWordLength, wordLength);
						totalWordLength += wordLength;
						wordCount++;
						wordLength = 0;
					}
				}
			}
		}
		asymmetries /= 2;	// Every asymmetry counted twice
		
		// Iterate column-wise now		
		for(int c = 0; c < cols; c++)
		{
			contiguousBlackCells = 0;
			wordLength = 0;
			
			for (int r = 0; r < rows; r++)
			{
				// TODO : Take away this repetitive code! 
				if (matrix[r][c] == BLACK)
				{
					contiguousBlackCells++;
					if (contiguousBlackCells > contiguous_black_threshold)
						extraContiguousBlackCells++;					
				}
				else
				{
					if (contiguousBlackCells > 0)
					{
						minContigBlackCells = Math.min(minContigBlackCells, contiguousBlackCells);
						maxContigBlackCells = Math.max(maxContigBlackCells, contiguousBlackCells);
						contiguousBlackCells = 0;
					}					
				}
				
				if (matrix[r][c] == WHITE)
				{					
					wordLength++;					
				}
				else
				{
					if (wordLength > 0)
					{
						minWordLength = Math.min (minWordLength, wordLength);
						maxWordLength = Math.min (maxWordLength, wordLength);
						totalWordLength += wordLength;
						wordCount++;
						wordLength = 0;						
					}
				}
			}
		}

		// Feasibility 
		feasible = minWordLength >= min_word_len && maxWordLength <= max_word_len
				&& minContigBlackCells >= min_contiguous_black_cells 
				&& maxContigBlackCells <= max_contiguous_black_cells;
		
		// Compute the penalties
		penalty_density = Math.abs(whiteCells * 1.0 / (rows * cols) - optimal_density);
		penalty_intersection = Math.abs(nIntersections - whiteCells * optimal_intersections);
		penalty_symmetry = asymmetries;
		penalty_cont_black_cells = extraContiguousBlackCells;	// TODO : Validate!?
		penalty_word_length = Math.abs(optimal_avg_word_len - totalWordLength * 1.0 / wordCount);
		
						
		// Final step; compute fitness
		double penaltiesSum = penalty_density + penalty_symmetry + penalty_symmetry		// TODO : Sum up all penalties		
							  + penalty_cont_black_cells + penalty_word_length;
		fitness = 1.0 / penaltiesSum;	
	}
}
