package com.pranav.projects.crossword;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Xwords")
public class CrosswordGenerator extends HttpServlet {
	public static int dimRows;
	public static int dimCols;
	public static int populationSize;
	public static int maxIterations;
	public static int maxExperiments;
	
	public static void initialize(){
		populationSize = 500;
		dimRows = 10;
		dimCols = 10;
		maxIterations = 700;
		maxExperiments = 5;
	}
	/**
	 * Todo
	 * initialize configuration
	 * generate initial population
	 * loop:
	 * 		selection
	 * 		variation
	 * 		survival
	 * 		postProcessing
	 * return best individual
	 */
	public static void main(String args[]){
		initialize();
		Individual best = getBestCrossword();
		printCrosswordPlain(best, System.out);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set a cookie for the user, so that the counter does not increate
		// everytime the user press refresh
		HttpSession session = request.getSession(true);
		// Set the session valid for 5 secs
		session.setMaxInactiveInterval(5);
		//response.setContentType("text/html");
		
		initialize();
		Individual best = getBestCrossword();
		printCrosswordHTML(best, response.getWriter());
	}

	public static Individual getBestCrossword(){
		SelectionOperators selOp = new SelectionOperators();
		CrossoverOperators crossOp = new CrossoverOperators();
		MutationOperators mutateOp = new MutationOperators();
		PostProcessor postProcessor = new PostProcessor();
		
		int bestIndividual = 0;
		Individual bestInd = null;
		// generate Initial Population //
		List<Individual> P = genInitialPopulation();
		
		// Iterate for maximum number of experiments/trials //
		double maxFitness = 0;
		for (int expNo = 0; expNo < maxExperiments; expNo++){
			System.out.println("Experiment Number : " + expNo);
			// Iterate for Maximum Iterations //
			for (int iterNo = 0; iterNo < maxIterations; iterNo++){
				if(iterNo % 200 == 0 || maxIterations <= 500){
					System.out.println("Iteration Number : " + iterNo);
				}
				
				// Selection //
				List<Integer> selected = selOp.SRWS_SUS(P, populationSize/2);
				//System.out.println("Selected : " + selected.size());
				
				// Crossover //
				for (int crossNo = 0; crossNo < populationSize/4; crossNo++){
					int p1 = (int) Math.floor(Math.random() * selected.size());
					int p2 = (int) Math.floor(Math.random() * selected.size());
					//System.out.println("P1 : " + p1 + "," + "P2 : " + p2);
					P.addAll(crossOp.squarePatchCross(P.get(selected.get(p1)), P.get(selected.get(p2))));
				}
				
				// Mutation //
				for(int selectedNo = 0; selectedNo < selected.size(); selectedNo++){
					P.add(mutateOp.mutateEachCell(P.get(selected.get(selectedNo))));
				}
				
				// Survival //
				List<Integer> survivors = selOp.SRWS_SUS(P, populationSize);
				//System.out.println("Survivors : " + survivors.size());
				List<Individual> PNext = new ArrayList<Individual>();
				for(int survivorNo = 0; survivorNo < selected.size(); survivorNo++){
					PNext.add(P.get(survivors.get(survivorNo)));
				}
				P = PNext;
				
				/*if(iterNo % 100 == 0){
					for (int indNo = 0; indNo < P.size(); indNo++){
						postProcessor.postProcess(P.get(indNo));
						P.get(indNo).computeFitness();
					}			
				}*/
			}
		
			// find best crossword //
			int firstFeasible = 0;
			while(firstFeasible < P.size() && !P.get(firstFeasible).isFeasible()){
				firstFeasible++;
			}
			
			bestIndividual = firstFeasible;
			if(firstFeasible == P.size()){
				System.out.println("NO FEASIBLE SOLUTIONS ! RETURNING BEST INFEASIBLE SOLUTION !");
				bestIndividual = 0;
			}
			
			double highestFitness = P.get(bestIndividual).getFitness();
			for (int indNo = bestIndividual + 1; indNo < P.size(); indNo++){
				if ((firstFeasible == P.size() || P.get(indNo).isFeasible()) && P.get(indNo).getFitness() > highestFitness){
					highestFitness = P.get(indNo).getFitness();
					bestIndividual = indNo;
				}
			}
			//postProcessor.processWordLength(P.get(bestIndividual));
			postProcessor.processAsymmetries(P.get(bestIndividual));
			P.get(bestIndividual).computeFitness();
			if (P.get(bestIndividual).getFitness() > maxFitness){
				maxFitness = P.get(bestIndividual).getFitness();
				bestInd = P.get(bestIndividual);
			}
			System.out.println("Highest Fitness till now : " + maxFitness);
		}
		
		return bestInd;
	}
	
	public static List<Individual> genInitialPopulation(){
		List<Individual> P = new ArrayList<Individual>();
		for (int indNo = 0; indNo < populationSize; indNo++){
			double cell_rand = 0;
			Integer mask_matrix[][] = new Integer[dimRows][dimCols];
			
			/**
			 * Todo
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
	
	public static void printCrosswordPlain(Individual best, PrintStream out){
		// print the crossword //
		out.println("Density\t : \t" + best.getDensityPenalty());
		out.println("Intersection\t : \t" + best.getIntersectionPenalty());
		out.println("Symmetry\t : \t" + best.getSymmetryPenalty());
		out.println("Avg Word Length\t : \t" + best.getAvgWordLengthPenalty());
		out.println("Word Length\t : \t" + best.getWordLengthPenalty());
		out.println("Avg Contiguous Black Seq\t : \t" + best.getAvgContBlackCellsPenalty());
		out.println("Avg Check\t : \t" + best.getAvgCheckPenalty());
		out.println("nWords\t : \t " + best.getnWordsPenalty());
		out.println("Fitness of Best Individual : " + best.getFitness());
		out.println();
		for (int colNo = 0; colNo < dimCols; colNo++){
			out.print("___");
		}
		out.println();
		for (int rowNo = 0; rowNo < dimRows; rowNo++){
			out.print("|");
			for (int colNo = 0; colNo < dimCols; colNo++){
				if(best.getCell(rowNo, colNo) == 0){
					out.print("##|");
				}
				else{
					out.print("__|");
				}
			}
			out.println();
		}
	}
	
	public static void printCrosswordHTML(Individual best, PrintWriter out){
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Crossword Generator</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<center>");
		out.println("Density\t : \t" + best.getDensityPenalty());
		out.println("<br />");
		out.println("Intersection\t : \t" + best.getIntersectionPenalty());
		out.println("<br />");
		out.println("Symmetry\t : \t" + best.getSymmetryPenalty());
		out.println("<br />");
		out.println("Avg Word Length\t : \t" + best.getAvgWordLengthPenalty());
		out.println("<br />");
		out.println("Word Length\t : \t " + best.getWordLengthPenalty());
		out.println("<br />");
		out.println("Avg Contiguous Black Seq\t : \t" + best.getAvgContBlackCellsPenalty());
		out.println("<br />");
		out.println("Avg checks\t : \t" + best.getAvgCheckPenalty());
		out.println("<br />");
		out.println("nWords\t : \t" + best.getnWordsPenalty());
		out.println("<br />");
		out.println("<br />");
		out.println("<table style=\"border:2px solid black;\"><tbody>");
		for (int rowNo = 0; rowNo < dimRows; rowNo++){
			out.println("<tr>");
			for (int colNo = 0; colNo < dimCols; colNo++){
				if(best.getCell(rowNo, colNo) == 0){
					out.println("<td style=\"height:30;width:30;background-color:black;\"></td>");
				}
				else{
					out.println("<td style=\"height:30;width:30;background-color:#e0e0e0;\"></td>");
				}
			}
			out.println("</tr>");
		}
		out.println("</tbody></table>");
		out.println("</center>");
		out.println("</body>");
		out.println("</html>");
	}
}
