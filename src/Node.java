import java.util.ArrayList;
import java.util.Random;

public class Node
{
	private static int count = 0;

	// Each node will store a costs vector, its own distance vector
	// and a distance vector for each of its neighbors
	private int[] neighbors;
	private int[] cost = new int[DVSimulator.NUMNODES];
	private int[] myDV = new int[DVSimulator.NUMNODES];
	private int[][] neighborDV = new int[DVSimulator.NUMNODES][DVSimulator.NUMNODES];
	private int id;
	// fwd table specifies for reaching each destination (index) from current node
	// which neighbor node we should visit first
	private int[] fwdTable = new int[DVSimulator.NUMNODES];
	// bestPath is a temporary changing forwarding table
	private int[] bestPath = new int[DVSimulator.NUMNODES];
	private int numUpdates = 0;

	public Node() {
		this.id = count++;

		// reading from the DVSimulator variables,
		// for each node:
		// 1. initialize its cost and myDV value
		// 2. specify the neighbors
		// 3. Initialize the forwarding table (bestPath variable)

		// BestPath to any node should be initialized as follows:
		// If node has id = this node's id, use id
		// Else if node is a direct neighbor, use the neighbor id
		// Otherwise, choose a random neighbor (see randomNeighbor method)

		
		//System.out.println("WRITE YOUR CODE HERE");
		for (int j = 0; j<DVSimulator.NUMNODES; j++) {	// A for loop is needed to initialize the cost required to get to other nodes and current node's distance vector.
			cost[j] = DVSimulator.cost[id][j];	//We get the values from DVSimulator class.
			myDV[j] = cost[j];	//Initially current node's distance vector has the same values with the cost vector.
			//System.out.println("Node: "+ id + " cost: "+ cost[j] + " ");
		}

		neighbors = new int[DVSimulator.neighbors[id].length]; //Initializing the neighbors array.
		ArrayList<Integer> neighborList= new ArrayList<Integer>(); //This arraylist is required for checking if a node is a neighbor of another node. Contains method helps to find easily.

		for(int j=0;j<DVSimulator.neighbors[id].length;j++){ //As the number of neighbors of the current node we initialized neighbors array and arraylist.
			neighbors[j] = DVSimulator.neighbors[id][j];
			//System.out.println("CHECK POINT: "+ neighbors[j]);
			neighborList.add(DVSimulator.neighbors[id][j]);
		}

		for(int i = 0; i<DVSimulator.NUMNODES; i++) {
			if(i==this.id){
				bestPath[i] = id; //If the current node is the same with the destination node (we can understand it with their ids) best_path becomes itself.
			}else if(neighborList.contains(i)){ //If the destination node is in the neighbors arraylist in other words a direct neighbor,
				bestPath[i] = i;	//Then we put its id to best_path
			}else{
				bestPath[i] = randomNeighbor(); //If the destination node is not a neighbor node, then a node is randomly selected from neighbors.
			}

			//System.out.println("Forwarding Table: "+ bestPath[i]);
		}
		// send initial DV to neighbors
		notifyNeighbors();
	}

	public int getId() {
		return id;
	}

	public void printDV() {
		System.out.print("i            " );
		for (int i = 0; i<DVSimulator.NUMNODES; i++) {
			System.out.print(i + "      ");
		}
		System.out.println();
		System.out.print("cost         " );
		for (int i = 0; i<DVSimulator.NUMNODES; i++) {
			System.out.print(myDV[i] + "      ");
		}
		System.out.println();
	}

	public void printFwdTable() {
		System.out.println("dest         next Node" );
		for (int i = 0; i<DVSimulator.NUMNODES; i++) {
			System.out.println(i + "            " + fwdTable[i]);
		}
	}

	public int randomNeighbor() {
		int rnd = new Random().nextInt(neighbors.length);
		return neighbors[rnd];
	}

	public void notifyNeighbors() {
		// for each neighbor, create a new packet (see Packet class)
		// with current node id as source, neighbor id as destination
		// and current node's DV as the dv
		// then send packet using helper method sendPacket in DVSimulator

		
		for(int i=0;i<neighbors.length;i++){ 
			Packet p = new Packet(id,neighbors[i],myDV); //For every neighbor current node has a packet is created and sent.
			DVSimulator.sendPacket(p);
		}
	}

	public void updateDV(Packet p) {
		// this method is called by the simulator each time a packet is received from a neighbor
		int neighbor_id = p.getSource();
		neighborDV[neighbor_id] = p.getDV();

		// for each value in the DV received from neighbor, see if it provides a cheaper path to
		// the corresponding node. If it does, update myDV and bestPath accordingly
		// current DV of i is min { current DV of i, cost to neighbor + neighbor's DV to i  }

		// If you do any changes to myDV:
		// 1. Notify neighbors about the new myDV using notifyNeighbors() method
		// 2. Increment the convergence measure numUpdates variable once

		
		boolean updated = false;	//a boolean is needed to understand if an update has been made or not.
		
		for(int i=0;i<neighborDV[neighbor_id].length;i++){ 
			if(myDV[i] > (cost[neighbor_id] + neighborDV[neighbor_id][i])){ //current DV of i := min { current DV of i, cost to neighbor + neighbor's DV to i }
				myDV[i] = cost[neighbor_id] + neighborDV[neighbor_id][i]; //If a better path is found update current node's distance vector
				updated = true; //change the boolean as true
				bestPath[i] = neighbor_id; //change bestPath array for the future forwarding table.
			}
		//	System.out.println("NeigborDV: " + neighborDV[neighbor_id][i] +", ");
		}
		if(updated == true){
			notifyNeighbors(); //If an update has been made notify neighbors.
			numUpdates++; //increment number of updates as one.
		}
	}

	public void buildFwdTable() {
		// just copy the final values of bestPath vector
		for (int i = 0; i < DVSimulator.NUMNODES; i++) {
			fwdTable[i] = bestPath[i];
		}
	}

	public int getNumUpdates() {
		return numUpdates;
	}
}

