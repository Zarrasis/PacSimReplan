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
  private ArrayList <Food> food;
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
 public void rNNA(PacCell[][] grid) {
   for (int step = 1; step < foodArray.size(); step++) {
    System.out.println("\nPopulation at step " + (step + 1) + " :");
    ArrayList<Plan> branches = new ArrayList<Plan>();

    for (int i = 0; i < plans.size(); i++) {
      Plan plan = plans.get(i);
      Point current = plan.food.get(plan.food.size() - 1).point;

      // Lookup the cost from the cost table
      int cost = getCost(step, current);
      // Store copy of total cost for curent plan
      int totalCost = plan.totalCost;
      // Store copy of current food for this plan
      ArrayList<Food> currentFood = new ArrayList<Food>();
      for (Food f : plan.food){
        currentFood.add(new Food(f.cost, f.point));
      }

      int row [] = costTable[getIndex(current) + 1];
      boolean added = false;

      for (int j = step; j < row.length; j++) {
        Point food = foodArray.get(j-1);
         if (row[j] == cost && !isEaten(plan.food, food)) {
           if (!added) {
             // Add food to current plan
             plan.totalCost += cost;
             plan.food.add(new Food(cost, food));
             added = true;
           } else {
             // Create new plan branch
             Plan p = new Plan();
             p.totalCost = totalCost + cost;
             for (Food f : currentFood){
               p.food.add(new Food(f.cost, f.point));
             }
             p.food.add(new Food(cost, food));
             branches.add(p);
           }
         }
       }
     }
     // Add branches to plans
     for (Plan b : branches){
       plans.add(b);
     }
     // Clear branches
     branches = new ArrayList<Plan>();

     Collections.sort(plans, new PlanSort());
     printPlans();
  }
  // Set lowest cost plan
  solution = plans.get(0);
 }

public void rNNA2 (cost, row, food) {
  if (size == 0){
    return
  }
  if ()
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

public boolean isEaten(ArrayList<Food> food, Point p) {
  for (Food f : food) {
    if (f.point.x == p.x && f.point.y == p.y){
      return true;
    }
  }
  return false;
}

public int getCost(int step, Point current) {
  int row [] = costTable[getIndex(current) + 1];
  // Set the min to the maximum cost of the grid
  int min = (costTable.length - 1) + (costTable.length - 1);
  for (int i = step; i < row.length; i++) {
    if (row[i] != 0 && row[i] < min) {
      min = row[i];
    }
  }
  return min;
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
    System.out.print("    " + count + " : " + "cost=" + p.totalCost + " : ");
    for (Food f : p.food) {
      System.out.print("[(" + f.point.x + "," + f.point.y + "), " + f.cost + "]");
    }
    count++;
    System.out.println();
   }
 }
}
