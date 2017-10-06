import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.abs;
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


public class PacSimRNNA implements PacAction
{
	private List<Point> path;
	private List<Point> foodArray = new ArrayList();
   	private int simTime;
   	private	int foodCount = 0;
   	private boolean config = false;



	public PacSimRNNA( String fname ) {
     PacSim sim = new PacSim( fname );
     sim.init(this);
	}

	public static void main(String[] args){
		 System.out.println("\nTSP using RNNA by Matthew Taubler and Brooke Norton:");
		 System.out.println("\nMaze : " + args[ 0 ] + "\n" );
		 new PacSimRNNA( args[ 0 ] );
		 System.out.println("\n");
	}

	@Override
   public void init(){
      	simTime = 0; 
   		path = new ArrayList();
   }


   @Override
   public PacFace action( Object state ){

   	PacCell[][] grid = (PacCell[][]) state;
    PacmanCell pc = PacUtils.findPacman(grid);

    // Check to see if Pacman is in game 
    if (pc == null) 
    	return null;

    // Print Food Array
    if (!config){
    	int foodGrid[][] = getFoodGrid(grid);
  		getFoodArray(grid);

    	int costTable[][] = getCostTable(pc, grid);
    	printCostTable(costTable);
    	printFoodArray(foodGrid);
		config = true;
	}
     
    if(path.isEmpty()){

     	Point target = PacUtils.nearestFood(pc.getLoc(), grid); 
     	int distance = getDistance(pc.getLoc(), target);
     	checkNeighbors(pc.getLoc(), distance, grid);


     	//System.out.println(distance);

     	path = BFSPath.getPath(grid, pc.getLoc(), target); 

     }


    Point next = path.remove(0); 
   	PacFace face = PacUtils.direction(pc.getLoc(), next);
   	return face;

   }

   // Gets distance to compare
   public int getDistance(java.awt.Point pacMan, java.awt.Point target) {
   		return (int)(abs(pacMan.getX() - target.getX()) + abs(pacMan.getY() - target.getY()));
   }

   public int[][] getCostTable(PacmanCell pc, PacCell[][] grid) {
   		int[][] costTable = new int[foodArray.size()+1][foodArray.size()+1];
   		Point p = pc.getLoc();
   		//int fsize = foodArray.size() - 1;
   		for (int i = 0; i < costTable.length; i++){
   			for(int j = 0; j < costTable[0].length; j++){
   				costTable[i][j] = BFSPath.getPath(grid, p, foodArray.get(j)).size();
   			}
   			p = foodArray.get(i);
   			
   		}
   		return costTable;
   }

   public void printCostTable(int[][] costTable)
   	{
   		System.out.println("\nCost Table:\n");
   		for (int i = 0; i < costTable.length; i++){
   			for(int j = 0; j < costTable[0].length; j++){
	   				System.out.print(costTable[i][j] + "  ");
	   			}
	   			System.out.println();
   			}	
   	}

    // its fine	
  	public int[][] getFoodGrid(PacCell[][] grid) {
   		int[][] foodArray = new int[grid.length][grid[0].length];
   		for (int i = 0; i < grid.length; i++){
   			for(int j = 0; j < grid[0].length; j++){
	   			if (PacUtils.food(i, j, grid)){
	   				foodCount++;
	   				foodArray[i][j] = 1;
	   			}
	   			else
	   				foodArray[i][j] = 0;
   			}	
   		}
   		return foodArray;
   	}

   	// its fine
   	public void printFoodArray(int[][] foodGrid) {
   		foodCount = -1; 
   		System.out.println("\nFood Array:\n");
   		for (int i = 0; i < foodGrid.length; i++){
   			for(int j = 0; j < foodGrid[0].length; j++){
	   			if (foodGrid[i][j] == 1){
	   				foodCount++;
	   				System.out.println(foodCount + " : " + "(" + i + "," + j + ")");
	   			}
   			}	
   		}
   	}

   	// its NOT fine
   	public void getFoodArray(PacCell[][] grid){

   		for (int i = 0; i < grid.length; i++){
   			for(int j = 0; j < grid[0].length; j++){
   				if(PacUtils.food(i, j, grid)){
   					Point p = new Point(i,j);
   					foodArray.add(p);
   				}
   			}
   		}
   	}

   public void checkNeighbors(java.awt.Point cPL, int distance, PacCell[][] grid){

   		/*
   		int DX [] = {0, 1, 0, -1};
   		int DY [] = {1, 0, -1, 0};
   		int foodCount = 0;
   		for (int i = 0; i < grid.length; i++){
   			for(int j = 0; j < grid[0].length; j++){
   				for (int k = 0; k < 4; k++) {
	   				if (something(grid[i+DX[k]][j+DY[k]])){
	   					
	   					// do something
	   				}
	   			}
   			}
   		}*/

   		// if(PacUtils.food((int)cPL.getX() + distance, (int)cPL.getY(),grid))
   		// 	System.out.println("Point is food");
   		// if(PacUtils.food((int)cPL.getX() - distance, (int)cPL.getY(),grid))
   		// 	System.out.println("Point is food");
   		// if(PacUtils.food((int)cPL.getX(), (int)cPL.getY() + distance,grid))
   		// 	System.out.println("Point is food");
   		// if(PacUtils.food((int)cPL.getX(), (int)cPL.getY() - distance,grid))
   		// 	System.out.println("Point is food");

   		// if(PacUtils.food( cPL.getX() + distance, cPL.getY(), grid))
   		// 	neighbors.add(Point(int)(cPL.getX() + distance, cPL.getY()));
   		 
   		// if(PacUtils.food( cPL.getX() - distance, cPL.getY(), grid))
   		// 	neighbors.add(Point(int)(cPL.getX() - distance, cPL.getY()));
   		
   		// if(PacUtils.food( cPL.getX(), cPL.getY() + distance, grid))
   		// 	neighbors.add(Point(int)(cPL.getX(), cPL.getY() + distance));
   		
   		// if(PacUtils.food( cPL.getX(), cPL.getY() - distance, grid))
   		// 	neighbors.add(Points(int)(cPL.getX(), cPL.getY() - distance));

   }

}