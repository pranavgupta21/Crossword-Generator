package com.pranav.projects.crossword;

public class PostProcessor {
	public Individual postProcess(Individual i){
		processWordLength(i);
		//processAsymmetries(i);
		return i; 
	}
	
	public Individual processWordLength(Individual i){
		int nRows = i.getRows(), nCols = i.getCols();
		
		// maintain current word length for all columns and rows //
		int[] colRunningWordLengths = new int[nCols];
		for(int colNo = 0; colNo < nCols; colNo++){
			colRunningWordLengths[colNo] = 0;
		}
		int rowRunningWordLength;
		
		// traverse the matrix to find word slots of length less than min word length and fix them //
		for (int rowNo = 0; rowNo < nRows; rowNo++){
			rowRunningWordLength = 0;
			for (int colNo = 0; colNo < nCols; colNo++){
				if (i.getCell(rowNo, colNo) == i.WHITE){
					rowRunningWordLength += 1;
					colRunningWordLengths[colNo] += 1;
				}
				else{
					// check horizontal word slot //
					if(rowRunningWordLength > 0 && rowRunningWordLength < 2/*i.min_word_len*/){
						boolean problemAbove = false, problemBelow = false;
						if(rowNo == 0 || i.getCell(rowNo-1, colNo-1) == i.BLACK){
							problemAbove = true;
						}
						if(rowNo == nRows - 1 || i.getCell(rowNo+1, colNo-1) == i.BLACK){
							problemBelow = true;
						}
						if(problemAbove && problemBelow){
							i.setCell(rowNo, colNo, i.WHITE);
							rowRunningWordLength += 1;								
						}
						else{
							rowRunningWordLength = 0;						
						}
					}
					
					// check vertical word slot //
					if(colRunningWordLengths[colNo] > 0 && colRunningWordLengths[colNo] < i.min_word_len){
						boolean problemLeft = false, problemRight = false;
						if(colNo == 0 || i.getCell(rowNo-1, colNo-1) == i.BLACK){
							problemLeft = true;
						}
						if(colNo == nCols - 1 || i.getCell(rowNo-1, colNo+1) == i.BLACK){
							problemRight = true;
						}
						if(problemLeft && problemRight){
							i.setCell(rowNo, colNo, i.WHITE);
							colRunningWordLengths[colNo] += 1;								
						}
						else{
							colRunningWordLengths[colNo] = 0;						
						}
					}
				}
			}
		}
		
		return i;		
	}
	
	public Individual processAsymmetries(Individual i){
		int nRows = i.getRows(), nCols = i.getCols();
		
		// traverse the matrix to find asymmetries and fix them //
		for (int rowNo = 0; rowNo < nRows; rowNo++){
			for (int colNo = rowNo; colNo < nCols; colNo++){
				if (i.getCell(rowNo, colNo) != i.getCell(nRows - 1 - rowNo, nCols - 1 - colNo)){
					// make the black one white, because making white to black may violate min word length constraint //
					// CAN USE OTHER SCHEMES ALSO //
					if(i.getCell(rowNo, colNo) == i.BLACK){
						i.setCell(rowNo, colNo, i.WHITE);
					}
					else{
						i.setCell(nRows - 1 - rowNo, nCols - 1 - colNo, i.WHITE);
					}
				}
			}
		}
		
		return i;
	}
}
