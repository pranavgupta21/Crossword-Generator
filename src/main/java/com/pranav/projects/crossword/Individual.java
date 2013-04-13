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
	private double penalty_avg_word_length;
	private double penalty_word_length;
	private double penalty_avg_black_length;
	private double penalty_black_length;
	private double penalty_check;
	private double penalty_nWords;
	
	// weights for all the constraints //
	static double weight_density = 3;
	static double weight_intersection = 3;
	static double weight_symmetry = 1;
	static double weight_avg_word_length = 3;
	static double weight_word_length = 3;
	static double weight_avg_black_legnth = 1;
	static double weight_black_length = 1;
	static double weight_check = 1;
	static double weight_nWords = 0;
	
	// Feasibility variable
	private boolean feasible;
	
	// Constants
	static double optimal_density = 0.67;
	static double optimal_intersections = 0.33;
	static double optimal_avg_word_len = 5;
	static int min_word_len = 3;
	static int max_word_len = 10;
	static double optimal_avg_black_length = 1.5;
	static int min_contiguous_black_cells = 1;
	static int max_contiguous_black_cells = 3;
	static double optimal_check_ratio = 0.5;
	static int optimal_nWords = 11;
	
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
		return penalty_black_length;
	}
	
	public double getAvgContBlackCellsPenalty(){
		return penalty_avg_black_length;
	}
	
	public double getAvgCheckPenalty(){
		return penalty_check;
	}
	
	public double getnWordsPenalty(){
		return penalty_nWords;
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
		int totalWordLength = 0, totalBlackWordLength = 0;
		int minWordLength = Integer.MAX_VALUE, maxWordLength = Integer.MIN_VALUE;
		int nWords = 0, colRunningWhites[] = new int[cols], rowRunningWhite;
		int nBlackWords = 0, colRunningBlacks[] = new int[cols], rowRunningBlack;
		int totalWordLengthViolations = 0, wordLengthViolations = 0;
		int totalBlackLengthViolations = 0, blackLengthViolations = 0;
		double totalCheckRatioViolation = 0;
		int rowNChecks = 0, colNChecks[] = new int[cols];
		
		for (int colNo = 0; colNo < cols; colNo++){
			colRunningWhites[colNo] = 0;
			colRunningBlacks[colNo] = 0;
			colNChecks[colNo] = 0;
		}
		// Iterate through the cells
		for(int r = 0; r < rows; r++)
		{			
			rowRunningWhite = 0;
			rowRunningBlack = 0;
			rowNChecks = 0;
			for (int c = 0; c < cols; c++)
			{
				whiteCells += (matrix[r][c] == WHITE ? 1 : 0);
				boolean isIntersection = isIntersection(r, c);
				nIntersections += (isIntersection ? 1 : 0);
				asymmetries += (matrix[r][c] != matrix[rows - 1 - r][cols - 1 - c] ? 1 : 0);	// TODO : verify		
				
				if (matrix[r][c] == BLACK)
				{
					rowRunningBlack += 1;
					colRunningBlacks[c] += 1;
					// checking for END OF WORD - for counting total number of words //
					// horizontal word
					if(rowRunningWhite > 0){
						nWords++;
						totalWordLength += rowRunningWhite;
						minWordLength = Math.min (minWordLength, rowRunningWhite);
						maxWordLength = Math.min (maxWordLength, rowRunningWhite);
						wordLengthViolations += (rowRunningWhite > max_word_len || rowRunningWhite < min_word_len) ? 1 : 0;
						totalWordLengthViolations += (rowRunningWhite > max_word_len ? 1 : 0) * (rowRunningWhite - max_word_len)
								+ (rowRunningWhite < min_word_len && rowRunningWhite > 1 ? 1 : 0) * (min_word_len - rowRunningWhite);
						totalCheckRatioViolation += rowNChecks * 1.0/rowRunningWhite;
						rowNChecks = 0;
						rowRunningWhite = 0;
					}
					// vertical word
					if(colRunningWhites[c] > 0){
						nWords++;
						totalWordLength += colRunningWhites[c];
						minWordLength = Math.min (minWordLength, colRunningWhites[c]);
						maxWordLength = Math.min (maxWordLength, colRunningWhites[c]);
						wordLengthViolations += (colRunningWhites[c] > max_word_len || colRunningWhites[c] < min_word_len) ? 1 : 0;
						totalWordLengthViolations += (colRunningWhites[c] > max_word_len ? 1 : 0) * (colRunningWhites[c] - max_word_len)
								+ (colRunningWhites[c] < min_word_len && colRunningWhites[c] > 1 ? 1 : 0) * (min_word_len - colRunningWhites[c]);
						totalCheckRatioViolation += colNChecks[c] * 1.0/colRunningWhites[c];
						colNChecks[c] = 0;
						colRunningWhites[c] = 0;
					}

					// checking for END OF CONTIGUOUS BLACK CELLS //
					// horizontal direction
					if(c == cols - 1){
						nBlackWords++;
						totalBlackWordLength += rowRunningBlack;
						minContigBlackCells = Math.min(minContigBlackCells, rowRunningBlack);
						maxContigBlackCells = Math.max(maxContigBlackCells, rowRunningBlack);
						blackLengthViolations += (rowRunningBlack > max_word_len || rowRunningBlack < min_word_len) ? 1 : 0;
						totalBlackLengthViolations += (rowRunningBlack > max_word_len ? 1 : 0) * (rowRunningBlack - max_word_len)
								+ (rowRunningBlack < min_word_len ? 1 : 0) * (min_word_len - rowRunningBlack);
					}
					// vertical direction
					if(r == rows - 1){
						nBlackWords++;
						totalBlackWordLength += colRunningBlacks[c];
						minContigBlackCells = Math.min(minContigBlackCells, colRunningBlacks[c]);
						maxContigBlackCells = Math.max(maxContigBlackCells, colRunningBlacks[c]);
						blackLengthViolations += (colRunningBlacks[c] > max_word_len || colRunningBlacks[c] < min_word_len) ? 1 : 0;
						totalBlackLengthViolations += (colRunningBlacks[c] > max_word_len ? 1 : 0) * (colRunningBlacks[c] - max_word_len)
								+ (colRunningBlacks[c] < min_word_len ? 1 : 0) * (min_word_len - colRunningBlacks[c]);
					}
				}
				else
				{
					rowRunningWhite++;
					colRunningWhites[c] += 1;
					rowNChecks += isIntersection ? 1 : 0;
					colNChecks[c] += isIntersection ? 1 : 0;
					// checking for END OF CONTIGUOUS BLACK CELLS //
					// horizontal direction
					if(rowRunningBlack > 0){
						nBlackWords++;
						totalWordLength += rowRunningBlack;
						minContigBlackCells = Math.min(minContigBlackCells, rowRunningBlack);
						maxContigBlackCells = Math.max(maxContigBlackCells, rowRunningBlack);
						blackLengthViolations += (rowRunningBlack > max_word_len || rowRunningBlack < min_word_len) ? 1 : 0;
						totalBlackLengthViolations += (rowRunningBlack > max_word_len ? 1 : 0) * (rowRunningBlack - max_word_len)
								+ (rowRunningBlack < min_word_len ? 1 : 0) * (min_word_len - rowRunningBlack);
					}
					// vertical direction
					if(colRunningBlacks[c] > 0){
						nBlackWords++;
						totalWordLength += colRunningBlacks[c];
						minContigBlackCells = Math.min(minContigBlackCells, colRunningBlacks[c]);
						maxContigBlackCells = Math.max(maxContigBlackCells, colRunningBlacks[c]);
						blackLengthViolations += (colRunningBlacks[c] > max_word_len || colRunningBlacks[c] < min_word_len) ? 1 : 0;
						totalBlackLengthViolations += (colRunningBlacks[c] > max_word_len ? 1 : 0) * (colRunningBlacks[c] - max_word_len)
								+ (colRunningBlacks[c] < min_word_len ? 1 : 0) * (min_word_len - colRunningBlacks[c]);
					}
					
					// checking for END OF WORD - for counting total number of words //
					// horizontal word
					if(c == cols - 1){
						nWords++;
						totalWordLength += rowRunningWhite;
						minWordLength = Math.min (minWordLength, rowRunningWhite);
						maxWordLength = Math.min (maxWordLength, rowRunningWhite);
						wordLengthViolations += (rowRunningWhite > max_word_len || rowRunningWhite < min_word_len) ? 1 : 0;
						totalWordLengthViolations += (rowRunningWhite > max_word_len ? 1 : 0) * (rowRunningWhite - max_word_len)
								+ (rowRunningWhite < min_word_len && rowRunningWhite > 1 ? 1 : 0) * (min_word_len - rowRunningWhite);
						totalCheckRatioViolation += rowNChecks * 1.0/rowRunningWhite;
						rowNChecks = 0;
					}
					// vertical word
					if(r == rows - 1){
						nWords++;
						totalWordLength += colRunningWhites[c];
						minWordLength = Math.min (minWordLength, colRunningWhites[c]);
						maxWordLength = Math.min (maxWordLength, colRunningWhites[c]);
						wordLengthViolations += (colRunningWhites[c] > max_word_len || colRunningWhites[c] < min_word_len) ? 1 : 0;
						totalWordLengthViolations += (colRunningWhites[c] > max_word_len && colRunningWhites[c] > 1 ? 1 : 0) * (colRunningWhites[c] - max_word_len)
								+ (colRunningWhites[c] < min_word_len && colRunningWhites[c] > 1 ? 1 : 0) * (min_word_len - colRunningWhites[c]);
						totalCheckRatioViolation += colNChecks[c] * 1.0/colRunningWhites[c];
						colNChecks[c] = 0;
					}
				}
			}
		}
		asymmetries /= 2;	// Every asymmetry counted twice
		
		// Feasibility 
		feasible = minWordLength >= min_word_len && maxWordLength <= max_word_len
				&& minContigBlackCells >= min_contiguous_black_cells 
				&& maxContigBlackCells <= max_contiguous_black_cells;
		
		// Compute the penalties
		penalty_density = Math.abs(whiteCells * 1.0 / (rows * cols) - optimal_density);
		penalty_intersection = Math.abs(nIntersections - rows * cols * optimal_intersections);
		penalty_symmetry = asymmetries;
		penalty_avg_word_length = Math.abs(optimal_avg_word_len - totalWordLength * 1.0 / nWords);
		penalty_word_length = totalWordLengthViolations * 1.0/wordLengthViolations;
		penalty_avg_black_length = Math.abs(optimal_avg_black_length - totalBlackWordLength * 1.0 / nBlackWords);
		penalty_black_length = totalBlackLengthViolations * 1.0/blackLengthViolations;
		penalty_check = Math.abs(totalCheckRatioViolation/nWords - optimal_check_ratio);
		penalty_nWords = Math.abs(nWords - optimal_nWords);
		
		// Final step; compute fitness
		double penaltiesSum = (weight_density*penalty_density + weight_intersection*penalty_intersection + weight_symmetry*penalty_symmetry		// TODO : Sum up all penalties		
							  + weight_avg_word_length*penalty_avg_word_length + weight_word_length*penalty_word_length 
							  + weight_avg_black_legnth*penalty_avg_black_length + weight_black_length*penalty_black_length 
							  + weight_check*penalty_check + weight_nWords*penalty_nWords);
							  
		fitness = 1.0 / penaltiesSum;	
	}
}
