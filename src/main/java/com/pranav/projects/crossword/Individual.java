package com.pranav.projects.crossword;

public class Individual {
	private Integer matrix[][];
	
	private float penalty_density;
	private float penalty_intersection;
	private float penalty_symmetry;
	private float penalty_word_length;
	private float penalty_avg_word_length;
	private float penalty_cont_black_cells;
	private float penalty_avg_cont_black_cells;
	private float penalty_diagonal_black_cells;
	
	private float fitness;
	
	void Individual(Integer[][] mat){
		matrix = mat;
		computeFitness();
	}
	
	public Integer getCell(int row, int col){
		return matrix[row][col];		
	}

	public float getDensityPenalty(){
		return penalty_density;
	}
	
	public float getIntersectionPenalty(){
		return penalty_intersection;
	}

	public float getSymmetryPenalty(){
		return penalty_symmetry;
	}
	
	public float getWordLengthPenalty(){
		return penalty_word_length;
	}
	
	public float getAvgWordLengthPenalty(){
		return penalty_avg_word_length;
	}
	
	public float getContBlackCellsPenalty(){
		return penalty_cont_black_cells;
	}
	
	public float getFitness(){
		return fitness;
	}
	
	void computeFitness(){
	}
}
