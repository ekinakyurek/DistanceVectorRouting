//Project 3, DV Simulator, 2015
package prj3;
import java.util.Random;

public class NetworkSimulator
{    
    // This is the number of entities in the simulator
    public static final int NUMENTITIES = 5;

    // These constants are possible events
    public static final int FROMLAYER2 = 0;
    public static final int LINKCHANGE = 1;    
    // Parameters of the simulation
    private boolean linkChanges;
    private static int traceLevel;
    private static EventList eventList;
    private static Random rand;
    
    // Data used for the simulation
    private Entity[] entity;
    public static int[][] cost;
    private static double time;

    // Initializes the simulator
    public NetworkSimulator(boolean hasLinkChange, int trace, long seed)
    {
        linkChanges = hasLinkChange;
        traceLevel = trace;
        eventList = new EventListImpl();
        rand = new Random(seed);
        time = 0.0;
        
      //Weight of the edges in the graph
        cost = new int[NUMENTITIES][NUMENTITIES];
        cost[0][0] = 0;
        cost[0][1] = 4;
        cost[0][2] = 3;
		cost[0][3] = 999;
        cost[0][4] = 999;
        
		cost[1][0] = 4;
        cost[1][1] = 0;
        cost[1][2] = 999;
        cost[1][3] = 2;
		cost[1][4] = 8;
		
		cost[2][0] = 3;
        cost[2][1] = 999;
        cost[2][2] = 0;
        cost[2][3] = 2;
		cost[2][4] = 999;
        
		cost[3][0] = 999;
        cost[3][1] = 2;
        cost[3][2] = 2;
        cost[3][3] = 0;
		cost[3][4] = 3;
		
		cost[4][0] =999 ;
		cost[4][1] = 8;
		cost[4][2] = 999;
		cost[4][3] = 3;
		cost[4][4] = 0;

        entity = new Entity[NUMENTITIES];
        entity[0] = new Entity0();
        entity[1] = new Entity1();
        entity[2] = new Entity2();
        entity[3] = new Entity3();
		entity[4] = new Entity4();

        if (linkChanges)
        {
            eventList.add(new Event(10000.0, LINKCHANGE, 0));
        }

    }
    
    // Starts the simulation. It will end when no more packets are in the
    // medium
    public void runSimulator()
    {
        Event next;
        Packet p;
        
        while(true)
        {
            next = eventList.removeNext();
            
            if (next == null)
            {
                break;
            }
            
            if (traceLevel > 1)
            {
                System.out.println();
                System.out.println("main(): event received.  t=" +
                                   next.getTime() +", node=" + 
                                   next.getEntity());
                if (next.getType() == FROMLAYER2)
                {
                    p = next.getPacket();
                    System.out.print("  src=" + p.getSource() + ", ");
                    System.out.print("dest=" + p.getDest() + ", ");
                    System.out.print("contents=[");
                    for (int i = 0; i < NUMENTITIES - 1; i++)
                    {
                        System.out.print(p.getMincost(i) + ", ");
                    }
                    System.out.println(p.getMincost(NUMENTITIES - 1) + "]");
                }
                else if (next.getType() == LINKCHANGE)
                {
                	System.out.println("State of the system at t=" + time +", before the link cost change. ");     
                	displayResults();
                    System.out.println("  Link cost change.");
                }
            }
            
            time = next.getTime();
            
            if (next.getType() == FROMLAYER2)
            {
                p = next.getPacket();
                if ((next.getEntity() < 0) || (next.getEntity() >= NUMENTITIES))
                {
                    System.out.println("main(): Panic. Unknown event entity.");
                }
                else
                {
                    entity[next.getEntity()].update(p);
                }
            }
            else if (next.getType() == LINKCHANGE)
            {
                if (time < 10001.0)
                {
                    cost[0][1] = 20;
                    cost[1][0] = 20;
                    entity[0].linkCostChangeHandler(1, 20);
                    entity[1].linkCostChangeHandler(0, 20);
                }
                else
                {
                    cost[0][1] = 1;
                    cost[1][0] = 1;
                    entity[0].linkCostChangeHandler(1, 1);
                    entity[1].linkCostChangeHandler(0, 1);
                }
            }
            else
            {
                System.out.println("main(): Panic.  Unknown event type.");
            }    
        }
        
        System.out.println("Simulator terminated at t=" + time +
                           ", no packets in medium.");     
        displayResults();
    }
    
    // Sends a packet into the medium
    /**** Warning!  This will allow an entity to send packets that they
          couldn't possibly send (e.g. Entity 1 could send a packet from
          0 to 3).  This should be fixed later... ****/
    public static void toLayer2(Packet p)
    {
        Packet currentPacket;
        double arrivalTime;
    
        if ((p.getSource() < 0) || (p.getSource() >= NUMENTITIES))
        {
            System.out.println("toLayer2(): WARNING: Illegal source id in " +
                               "packet; ignoring.");
            return;
        }
        if ((p.getDest() < 0) || (p.getDest() >= NUMENTITIES))
        {
            System.out.println("toLayer2(): WARNING: Illegal destination id " +
                               "in packet; ignoring.");
            return;
        }
        if (p.getSource() == p.getDest())
        {
            System.out.println("toLayer2(): WARNING: Identical source and " +
                               "destination in packet; ignoring.");
            return;
        }
        if (cost[p.getSource()][p.getDest()] == 999)
        {
            System.out.println("toLayer2(): WARNING: Source and destination " +
                               "not connected; ignoring.");
            return;
        }
        
        
        if (traceLevel > 2)
        {
            System.out.println("toLayer2(): source=" + p.getSource() + 
                               " dest=" + p.getDest());
            System.out.print("             costs:");
            for (int i = 0; i < NUMENTITIES; i++)
            {
                System.out.print(" " + p.getMincost(i));
            }
            System.out.println();
        }
        
        arrivalTime = eventList.getLastPacketTime(p.getSource(), p.getDest());
        if (arrivalTime == 0.0)
        {
            arrivalTime = time;
        }
        arrivalTime = arrivalTime + 1.0 + (rand.nextDouble() * 9.0);
        
        if (traceLevel > 2)
        {
            System.out.println("toLayer2(): Scheduling arrival of packet.");
        }

        currentPacket = new Packet(p);
        eventList.add(new Event(arrivalTime, FROMLAYER2, 
                                currentPacket.getDest(), currentPacket));
    }
    
    public void displayResults(){
    	System.out.print("\t");
    	for(int i=0; i<5; i++) System.out.print(i+"\t");    	System.out.println(); 
    	
    	System.out.print("0:\t");
    	for(int i=0; i<5; i++) System.out.print(Entity0.distanceVector[i]+"\t");    	System.out.println(); 
    	 
    	System.out.print("1:\t");
    	for(int i=0; i<5; i++) System.out.print(Entity1.distanceVector[i]+"\t");    	System.out.println();
    	
    	System.out.print("2:\t");
    	for(int i=0; i<5; i++) System.out.print(Entity2.distanceVector[i]+"\t");    	System.out.println();
    	
    	System.out.print("3:\t");
    	for(int i=0; i<5; i++) System.out.print(Entity3.distanceVector[i]+"\t");    	System.out.println();
    	
    	System.out.print("4:\t");
    	for(int i=0; i<5; i++) System.out.print(Entity4.distanceVector[i]+"\t");    	System.out.println();
    	
    }


}
