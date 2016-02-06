//Project 3, DV Simulator, 2015
package prj3;

public class Packet
{
    private int source;
    private int dest;
    private int[] mincost;
    private boolean linkchange;
    
    public Packet(Packet p)
    {
        source = p.getSource();
        dest = p.getDest();
        mincost = new int[NetworkSimulator.NUMENTITIES];
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            mincost[i] = p.getMincost(i);
        }
        this.linkchange = p.getLinkChange();
    }
    
    public Packet(int s, int d, int[] mc)
    {
        source = s;
        dest = d;
        this.linkchange = false;
        mincost = new int[NetworkSimulator.NUMENTITIES];
        if (mc.length != NetworkSimulator.NUMENTITIES)
        {
            System.out.println("Packet(): Invalid data format.");
            System.exit(1);
        }
        
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            mincost[i] = mc[i];
        }

    }    
    public Packet(int s, int d, int[] mc, boolean Linkchange )
    {
        source = s;
        dest = d;
        this.linkchange = Linkchange;
        mincost = new int[NetworkSimulator.NUMENTITIES];
        if (mc.length != NetworkSimulator.NUMENTITIES)
        {
            System.out.println("Packet(): Invalid data format.");
            System.exit(1);
        }
        
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            mincost[i] = mc[i];
        }
        
     
    }   
    
    public boolean getLinkChange(){
    	return linkchange;
    }
    
    public int getSource()
    {
        return source;
    }
    
    public int getDest()
    {
        return dest;
    }
    
    public int getMincost(int ent)
    {
        return mincost[ent];
    }

    public String toString()
    {
        String str;
        str = "source: " + source + "  dest: " + dest + "  mincosts: ";
        
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            str = str + i + "=" + getMincost(i) + " ";
        }
        
        return str;
        
    }
    
}
