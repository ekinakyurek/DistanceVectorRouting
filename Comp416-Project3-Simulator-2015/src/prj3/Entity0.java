//Project 3, DV Simulator, 2015
package prj3;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class Entity0 extends Entity
{    

	int thisNode = 0;
	static int distanceVector [] = new int[NetworkSimulator.NUMENTITIES];
	boolean IsMyLinkChanged = false;
	FileOutputStream fileOutput;
	PrintStream OutPut ;
	
    public Entity0()
    {
    	try{
    		fileOutput = new FileOutputStream("Logs.txt");
    		OutPut = new PrintStream(fileOutput);
    		System.setOut(OutPut);
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    	// We first initialize the distance table with infinity distances, to avoid initialization by 0. 
    	for(int i=0; i<NetworkSimulator.NUMENTITIES; i++){
    		distanceVector[i] = 999;
    		for(int j=0; j<NetworkSimulator.NUMENTITIES; j++){
    			distanceTable[i][j]=999;
    		}
    	}
    	// Later on, we get the Distance Vector of this node and the Distance Table overall from the 
    	// node-to-node cost matrix that is given at the Network Simulator class. 
    	for(int i=0; i<NetworkSimulator.NUMENTITIES; i++){
    		distanceVector[i] = NetworkSimulator.cost[thisNode][i];
    		distanceTable[i][i] = NetworkSimulator.cost[thisNode][i];
    	}
    	
    	/* Distance Vector Algorithm runs in this loop: 
    	 	For all nodes in the topology,
    	 	If a node (other than ours) has a "finite" distance to our node, 
    	 		--in other words if a node is a direct neighbor to our node--
    	 	We send a routing packet to inform that node, with our routing information.
    	 * 
    	 */
    	for(int j=0; j<NetworkSimulator.NUMENTITIES; j++){
    		if( j != thisNode && NetworkSimulator.cost[thisNode][j] != 999 ){
    			
    			// Source of packet is this node, destination is the neighbor j, routing information is our distanceVector. 
    			Packet packet = new Packet(thisNode, j, distanceVector); 
    			
    			NetworkSimulator.toLayer2(packet); 		//Packet is sent to other node via method .toLayer2(p)
    			
    			System.out.println("LOG--------: Packet has been sent from "+thisNode+" to "+ j);
    		}
    	}
    	//Perform any necessary 
    	//initialization in the constructor 
    }
    
    // Handle updates when a packet is received. 
    // Students will need to call NetworkSimulator.toLayer2() 
    // with new packets based upon what they
    // send to update.  Be careful to construct 
    // the source and destination of the packet 
    // correctly.  Read the warning in NetworkSimulator.java 
    // for more details.
    public void update(Packet p)
    {   
    	/* When the update function is called with a packet, we update the destination node with the 
    	 		routing information in the given packet. 
    	 The packet methods used in this function are explained in the file Packet.java
    	 Initialize the new cost as INF, get the origin of the packet and set the "changed" FLAG to false.
    	 If the "changed" FLAG becomes true, we will know an update has been used and new packets are sent to 
    			the neighbors of the updated node to continue the iteration. 
    	*/
    	int newCost = 999;
    	int src = p.getSource();   
    	int [] nextDistanceVector = new int[NetworkSimulator.NUMENTITIES];
    	boolean changeHandler = false;
    	boolean linkchange = p.getLinkChange();
    	/*
    	 * This is the crucial loop of the update function. i represents 
    	 *   the newCost variable is initially set to be equal to the combination of
    	 *   the cost of getting from the node to source, plus getting from source to node i. 
    	 * 
    	 *  Later on, the new value for the distance table is the minimum of either the newcost or the 
    	 *  already given value at the distanceTable. 
    	 * 
    	 * The new distance vector is saved as the nextDistanceVector, for updating purposes.  
    	 */
    	for(int i=0; i<NetworkSimulator.NUMENTITIES; i++){
    	    newCost = distanceVector[src]+ p.getMincost(i);
    	    distanceTable[i][src] = newCost < distanceTable[i][src] ? newCost: distanceTable[i][src];
    	    nextDistanceVector[i]  = Math.min(newCost,distanceVector[i]); 
    	    // If any one of the values of the distance vector is updated, the FLAG becomes
    	    // 		true, which means a packet has to be sent to direct neighbors of this node. 
    		if(nextDistanceVector[i] != distanceVector[i]) changeHandler = true;
    	}
    	
    	//The distanceVector is updated with the "next distance vector. 
    	distanceVector = nextDistanceVector ;
    	
    	
    	System.out.println("LOG--------Entity "+thisNode+" says: Node "+thisNode+" is updated.");
    	printDT();	
    	
    	if(changeHandler || ( linkchange && !IsMyLinkChanged)){	
    		Packet packet;
    		for(int j=0; j<NetworkSimulator.NUMENTITIES; j++){
				if( j != thisNode && NetworkSimulator.cost[thisNode][j] != 999){
					packet = new Packet(thisNode,j, distanceVector);
					NetworkSimulator.toLayer2(packet);
					System.out.println("LOG--------At Node "+ thisNode + ", the distance vector has been updated.");
					System.out.println("LOG--------Packet sent from " + thisNode + " to " +  j );
				}
			}  	 	
    	}
    	// Read the NetworkSimulator.java 
        // for more details.
    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
     	//adjust changes in the local distance values
    	IsMyLinkChanged = true;
        distanceTable[whichLink][whichLink] = newCost;
        distanceVector[whichLink] = newCost;
    	
    	//This is the loop where we find the "direct" neighbors of our node and update our "direct" neighbors. 
    	for(int i=0; i<NetworkSimulator.NUMENTITIES; i++){
    		if( i != thisNode && NetworkSimulator.cost[thisNode][i] != 999 ){
    			//Packet source is this node, destination is i, and routing information is the distanceVector. 
    			Packet packet = new Packet(thisNode,i, distanceVector,true);
    			//Packet sent via the .tolayer2(p) method. 
    			NetworkSimulator.toLayer2(packet);
				System.out.println("LOG--------Packet sent from " + thisNode + " to " +  i );
    		}
    	}
    }
    
 // Print destination table
    public void printDT()
    {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D0 |   1   2  ");
        System.out.println("----+----------");
        for (int i = 1; i < NetworkSimulator.NUMENTITIES; i++)
        {
        	 if (i == 0)
             {
                 continue;
             }
            System.out.print("   " + i + "|");
            for (int j = 1; j < 3; j++)
            {
            	
                if (distanceTable[i][j] < 10)
                {    
                    System.out.print("   ");
                }
                else if (distanceTable[i][j] < 100)
                {
                    System.out.print("  ");
                }
                else 
                {
                    System.out.print(" ");
                }
                
                System.out.print(distanceTable[i][j]);
            }
            System.out.println();
        }
    }

}
