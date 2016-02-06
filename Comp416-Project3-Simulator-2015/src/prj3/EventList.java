//Project 3, DV Simulator, 2015
package prj3;

public interface EventList
{
    public boolean add(Event e);
    public Event removeNext();
    public String toString();
    public double getLastPacketTime(int entityFrom, int entityTo);
}
