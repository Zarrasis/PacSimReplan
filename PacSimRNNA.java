import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.*;
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;
import pacsim.FoodCell;

/*
	University of Central Florida
	CAP4630 - Fall 2017
	Authors: Matthew Taubler and Brooke Norton
	RNNA alogrithm for PacMan
*/

public class PacSimRNNA implements PacAction {
	private List<Point> path;
	private List<Point> foodArray;
	private int costTable [][];
  private int simTime;
	private int pathCount;
  private boolean rNNAcomplete;
  //private List<totalCosts> options = new ArrayList();

  class costPoint{
    int cost;
    Point point;
  }

  class totalCosts{
    private int tCost;
    private List<costPoint> points = new ArrayList();
  }

	public PacSimRNNA(String fname){
     PacSim sim = new PacSim( fname );
     sim.init(this);
	}

	public static void main(String[] args){
		 System.out.println("\nTSP using RNNA by Matthew Taubler and Brooke Norton:");
		 System.out.println("\nMaze : " + args[ 0 ] + "\n" );
		 new PacSimRNNA( args[ 0 ] );
	}

	@Override
   public void init(){
      simTime = 0;
			pathCount = 0;
   		path = new ArrayList();
			foodArray = new ArrayList();
			rNNAcomplete = false;
   }

   @Override
   public PacFace action( Object state ){

   	PacCell[][] grid = (PacCell[][]) state;
    PacmanCell pc = PacUtils.findPacman(grid);

    // Make sure Pac-Man is in game
    if (pc == null)
    	return null;

    if (!rNNAcomplete){
			foodArray = PacUtils.findFood(grid);
    	costTable = getCostTable(pc, grid);
    	printCostTable();
    	printFoodArray();

			// Start timer
			int startTime = (int)System.currentTimeMillis();

			// Compute RNNA for this game
      rNNA(pc, grid);

			// End timer and set total time
			int endTime = (int)System.currentTimeMillis();
			simTime = endTime - startTime;

			System.out.println("\nTime to generate plan: " + simTime + " msec");
			System.out.println("\nSolution moves:\n");

		  rNNAcomplete = true;
	   }

		// Use lowest cost path solution for Pac-Man
    Point next = path.remove(0);
   	PacFace face = PacUtils.direction(pc.getLoc(), next);
		pathCount++;
		System.out.println(pathCount + " : From [" + next.x + ", " + next.y + "] go " + face);

   	return face;
   }

	 /*
	 		Method determines the cost table - an (n+1) by (n+1) symmetric matrix of
			UCS distances from Pac-Man's initial position to each food dot in the
			initial configuration, where n is the number of food dots,
			and where the first row and column represent Pac-Man's initial location.
	 */
   public int[][] getCostTable(PacmanCell pc, PacCell [][] grid) {
   		// Add the initial Pac-Man position to the begining of the food array
   		foodArray.add(0, pc.getLoc());
   		costTable = new int[foodArray.size()][foodArray.size()];
   		for (int i = 0; i < costTable.length; i++){
        Point p = foodArray.get(i);
   			for(int j = 0; j < costTable[0].length; j++){
          // Determine the cost to each food from this position using BFS
   				costTable[i][j] = BFSPath.getPath(grid, p, foodArray.get(j)).size();
   			}
   		}
      foodArray.remove(0);
			return costTable;
   }

	 /*
	 	 Repetitive Nearest Neighbor Algorithm (RNNA)
		 Explores multiple branch possibilities whenever there is more than
		 one closest neighbor and returns the optimal, lowest cost path for Pac-Man.
	 */
    public void rNNA(PacmanCell pc, PacCell [][] grid){
      Point p = pc.getLoc();
      for (int i = 0, k =  1; i < costTable.length; i++){
				System.out.println("Population at step " + k + " :");
         for (int j = 0; j < foodArray.size(); j++){
          Point f = foodArray.get(j);
          int cost = costTable[i][j];
          System.out.println(j + " : " + "cost=" + cost + " : [(" + f.x + "," + f.y + "), " + cost + "]");
      	}
				k++;
				System.out.println();
      }

			// Set lowest cost path
			path = BFSPath.getPath(grid, pc.getLoc(), foodArray.get(0));
    }

    /*public void pop(PacmanCell pc){
      Point p = pc.getLoc();
      for (int j = 0; j < foodArray.size(); j++){
        totalCosts.points.point = new Point(foodArray.get(j));
        totalCosts.points.cost = FSPath.getPath(grid, p, foodArray.get(j)).size();
      }
    }*/

		public void printCostTable() {
			System.out.println("\nCost Table:\n");
			for (int i = 0; i < costTable.length; i++){
				for (int j = 0; j < costTable[0].length; j++){
					System.out.printf("%4d", costTable[i][j]);
				}
				System.out.println();
			}
		 }

		 public void printFoodArray() {
			 int count = 0;
			 System.out.println("\nFood Array:\n");
			 for (Point p : foodArray){
				 System.out.println(count + " : (" + p.x + ", " + p.y + ")");
				 count++;
			 }
			 System.out.println();
		 }
}
