import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

/* RNNA by Matthew Taubler and Brooke Norton */


public class PacSimRNNA implements PacAction
{
	private List<Point> path;
   	private int simTime;


	public PacSimRNNA( String fname ) {
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
   		path = new ArrayList();
   }


   @Override
   public PacFace action( Object state ){

   	 PacCell[][] grid = (PacCell[][]) state;
     PacmanCell pc = PacUtils.findPacman( grid );

     // Check to see if Pacman is in game 
     if (pc == null) 
     	return null;
     
     if(path.isEmpty() ){
     	Point target =PacUtils.nearestFood(pc.getLoc(), grid); 
     	System.out.println(target);

     }



   	PacFace face = PacUtils.direction(pc.getLoc(), pc.getLoc());
   	return face;

   }

}