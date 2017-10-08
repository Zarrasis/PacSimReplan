import java.awt.Point;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
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
 private List <Point> path;
 private List <Plan> plans;
 private List <Point> foodArray;
 private Plan solution;
 private int costTable[][];
 private int simTime;
 private int pathCount;
 private boolean rNNAcomplete;

 class Plan {
  private int totalCost;
  private List <Food> food;
  public Plan() {
      totalCost = 0;
      food = new ArrayList<Food>();
  }
 }

 class Food {
   private int cost;
   private Point point;
   public Food(int c, Point p) {
      cost = c;
      point = p;
  }
 }

 class PlanSort implements Comparator<Plan> {
    @Override
    public int compare(Plan p1, Plan p2) {
      if(p1.totalCost > p2.totalCost)
        return 1;
      if(p2.totalCost > p1.totalCost)
        return -1;
      return 0;
    }
  }

 public PacSimRNNA(String fname) {
  PacSim sim = new PacSim(fname);
  sim.init(this);
 }

 public static void main(String[] args) {
  System.out.println("\nTSP using RNNA by Matthew Taubler and Brooke Norton:");
  System.out.println("\nMaze : " + args[0] + "\n");
  new PacSimRNNA(args[0]);
 }

 @Override
 public void init() {
  simTime = 0;
  pathCount = 0;
  path = new ArrayList();
  plans = new ArrayList<Plan>();
  foodArray = new ArrayList();
  rNNAcomplete = false;
 }

 @Override
 public PacFace action(Object state) {

  PacCell[][] grid = (PacCell[][]) state;
  PacmanCell pc = PacUtils.findPacman(grid);

  // Make sure Pac-Man is in game
  if (pc == null)
   return null;

  if (!rNNAcomplete) {
   foodArray = PacUtils.findFood(grid);
   costTable = getCostTable(pc, grid);
   printCostTable();
   printFoodArray();

   // Start timer
   int startTime = (int)System.currentTimeMillis();

   // Compute RNNA for this game
   populateFirst(pc, grid);
   rNNA(pc, grid);

   // End timer and set total time
   int endTime = (int)System.currentTimeMillis();
   simTime = endTime - startTime;

   System.out.println("\nTime to generate plan: " + simTime + " msec");
   System.out.println("\nSolution moves:\n");

   rNNAcomplete = true;
  }

  // If current path is completed (or just starting out),
  // select a the nearest food using the lowest cost plan
  if(path.isEmpty()) {
     Point tgt = solution.food.remove(0).point;
     path = BFSPath.getPath(grid, pc.getLoc(), tgt);
  }

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
 public int[][] getCostTable(PacmanCell pc, PacCell[][] grid) {
  // Add the initial Pac-Man position to the begining of the food array
  foodArray.add(0, pc.getLoc());
  costTable = new int[foodArray.size()][foodArray.size()];
  for (int i = 0; i < costTable.length; i++) {
   Point p = foodArray.get(i);
   for (int j = 0; j < costTable[0].length; j++) {
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
	one closest neighbor and returns the optimal, lowest cost plan for Pac-Man.
*/
 public void rNNA(PacmanCell pc, PacCell[][] grid) {
   for (int step = 1; step < foodArray.size(); step++) {
    System.out.println("\nPopulation at step " + (step + 1) + " :");

    for (int i = 0; i < plans.size(); i++) {
      Plan plan = plans.get(i);
      Point current = plan.food.get(plan.food.size() - 1).point;

      // Lookup the cost from the cost table
      int row [] = costTable[getIndex(current) + 1];
      Arrays.sort(row);
      int cost = row[step + 1];

      // Add food to current plan
      plan.totalCost += cost;
      //plan.food.add(new Food(cost, foodArray.get(k)))

      // Check if there is any equidistant food
      System.out.println("findex:" + getIndex(current));
      System.out.println("cost:" + cost);
       for (int j = step + 2; j < row.length; j++){
         if (row[j] == cost) {
           System.out.println("Branching for cost " + row[j]);
           Plan p = new Plan();
           p.totalCost = plan.totalCost + cost;
           List <Food> currentFood = plan.food;
           //currentFood.add(new Food(cost, foodArray.get(k)));
           p.food = currentFood;
           // Add to possible plans
           //plans.add(p);
         }
       }
     }

    Collections.sort(plans, new PlanSort());
    printPlans();
  }
  // Set lowest cost plan
  solution = plans.get(0);
 }

public void populateFirst(PacmanCell pc, PacCell[][] grid){
  System.out.println("Population at step 1 :");
   for (int i = 0; i < foodArray.size(); i++) {
     Food f = new Food(costTable[0][i+1], foodArray.get(i));
     Plan p = new Plan();
     p.totalCost = f.cost;
     p.food.add(f);
     // Add to possible plans
     plans.add(p);
   }
   Collections.sort(plans,new PlanSort());
   printPlans();
}

public int getIndex(Point point) {
   for (int i = 0; i < foodArray.size(); i++){
     if (foodArray.get(i).x == point.x && foodArray.get(i).y == point.y) {
       return i;
     }
   }
   return -1;
}

 public void printCostTable() {
  System.out.println("\nCost Table:\n");
  for (int i = 0; i < costTable.length; i++) {
   for (int j = 0; j < costTable[0].length; j++) {
    System.out.printf("%4d", costTable[i][j]);
   }
   System.out.println();
  }
 }

 public void printFoodArray() {
  int count = 0;
  System.out.println("\nFood Array:\n");
  for (Point p : foodArray) {
   System.out.println(count + " : (" + p.x + ", " + p.y + ")");
   count++;
  }
  System.out.println();
 }

 public void printPlans() {
  int count = 0;
  for (Plan p : plans) {
    System.out.print(count + " : " + "cost=" + p.totalCost + " : ");
    for (Food f : p.food) {
      System.out.print("[(" + f.point.x + "," + f.point.y + "), " + f.cost + "]");
    }
    count++;
    System.out.println();
   }
 }
}
