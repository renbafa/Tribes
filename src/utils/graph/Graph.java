package utils.graph;

import java.util.HashMap;

public class Graph {

    private HashMap<Integer, Node> nodes;
    public GraphPathfinder pathfinder;
    public boolean VERBOSE = false;
    public int MAX_CAPACITY = 10000;

    public Graph()
    {
        nodes = new HashMap<>();
    }

    public Node getNode(int x, int y)
    {
        int id = calcNodeId(x, y);
        return getNode(id);
    }

    public Node getNode(int id)
    {
        return nodes.get(id);
    }

    public void addNode(Node n)
    {
        nodes.put(n.getId(), n);
    }

    public void addEdge(int x0, int y0, int x1, int y1, boolean reverse)
    {
        int id0 = calcNodeId(x0, y0);
        int id1 = calcNodeId(x1, y1);
        this.addEdge(id0, id1);
        if(reverse)
            this.addEdge(id1, id0);
    }

    private void addEdge(int id0, int id1)
    {
        Node n0 = nodes.get(id0);
        Node n1 = nodes.get(id1);
        if(n0==null || n1==null)
        {
            System.out.println("WARNING: nodes not found for adding edge from "
                    + id0 + " (" + n0 + ") to " + id1 + " (" + n1 + ")");
            return;
        }
        n0.addNeighbour(n1);
    }

    public void removeEdge(int id0, int id1)
    {
        Node n0 = nodes.get(id0);
        Node n1 = nodes.get(id1);
        if(n0==null || n1==null)
        {
            System.out.println("WARNING: nodes not found for removing edge from "
                    + id0 + " (" + n0 + ") to " + id1 + " (" + n1 + ")");
            return;
        }
        n0.removeNeighbour(n1);
    }


    public Graph copy() {
        Graph gr = new Graph();
        for(Node n : nodes.values())
        {
            gr.addNode(n.copy());
        }
        return gr;
    }

    /**
     * Sets the data for this graph. For each true value in 'navigableData', a new node is
     * created. It also create links between contiguous nodes (horizontal, vertical and diagonally)
     * @param navigableData data required
     */
    public void setData (boolean[][] navigableData)
    {
        if(navigableData.length >= MAX_CAPACITY)
            System.out.println("(Graph.java, setData()) ERROR: too long of a map.");

        for(int i = 0; i < navigableData.length; ++i)
            for(int j = 0; j < navigableData[0].length; ++j)
            {
                if(navigableData[i][j])
                {
                    //This assumes a theoretical maximim grid size of 10000x10000. Should be fine!
                    int nodeId = calcNodeId(i, j);
                    Node n = new Node(nodeId, i,j);
                    nodes.put(nodeId, n);

                    //Add neighbours:
                    addLinksTo(n, calcNodeId(i-1,j-1));     //up left
                    addLinksTo(n, calcNodeId(i,j-1));     //up center
                    addLinksTo(n, calcNodeId(i+1,j-1));     //up right
                    addLinksTo(n, calcNodeId(i-1,j));     //center left
                }
            }
    }


    private void addLinksTo(Node origin, int nodeId)
    {
        Node to = nodes.get(nodeId);
        if(to != null)
        {
            to.addNeighbour(origin);
            origin.addNeighbour(to);
        }
    }

    private int calcNodeId(int x, int y)
    {
        return x * MAX_CAPACITY + y;
    }


    public void reset() {
        for(Node n : nodes.values())
        {
           n.setVisited(false);
        }
    }
}
