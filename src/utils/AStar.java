package utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AStar {

    private int[][] grid;
    private int width, height;
    private Node start, goal;
    private Map<Node, Node> cameFrom;
    private Map<Node, Double> gScore;
    private Map<Node, Double> fScore;
    
    public AStar(int[][] grid) {
        this.grid = grid;
        this.width = grid.length;
        this.height = grid[0].length;
        this.cameFrom = new HashMap<Node, Node>();
        this.gScore = new HashMap<Node, Double>();
        this.fScore = new HashMap<Node, Double>();
    }
    
    public List<Node> findPath(Node start, Node goal) {
        this.start = start;
        this.goal = goal;
        this.cameFrom.clear();
        this.gScore.clear();
        this.fScore.clear();
        List<Node> openSet = new ArrayList<Node>();
        List<Node> closedSet = new ArrayList<Node>();
        gScore.put(start, 0.0);
        fScore.put(start, heuristicCostEstimate(start, goal));
        openSet.add(start);
        while (!openSet.isEmpty()) {
            Node current = lowestFScore(openSet);
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }
            openSet.remove(current);
            closedSet.add(current);
            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                double tentativeGScore = gScore.get(current) + distanceBetween(current, neighbor);
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristicCostEstimate(neighbor, goal));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }
    
    
    private double distanceBetween(Node a, Node b) {
        if (a.getX() == b.getX() || a.getY() == b.getY()) {
            return 1;
        } else {
            return 1.5;
        }
    }
    
    private double heuristicCostEstimate(Node start, Node goal) {
        return Math.sqrt(Math.pow(start.getX() - goal.getX(), 2) + Math.pow(start.getY() - goal.getY(), 2));
    }
    
    private Node lowestFScore(List<Node> set) {
        Node minNode = null;
        double minScore = Double.POSITIVE_INFINITY;
        for (Node node : set) {
            double score = fScore.getOrDefault(node, Double.POSITIVE_INFINITY);
            if (score < minScore) {
                minNode = node;
                minScore = score;
            }
        }
        return minNode;
    }
    
    private List<Node> getNeighbors(Node node) {
        int x = node.getX();
        int y = node.getY();
        List<Node> neighbors = new ArrayList<Node>();
        if (x > 0 && grid[x-1][y] == 0) {
            neighbors.add(new Node(x-1, y));
        }
        if (y > 0 && grid[x][y-1] == 0) {
            neighbors.add(new Node(x, y-1));
        }
        if (x < width-1 && grid[x+1][y] == 0) {
            neighbors.add(new Node(x+1, y));
        }
        if (y < height-1 && grid[x][y+1] == 0) {
            neighbors.add(new Node(x, y+1));
        }
        return neighbors;
    }
    
    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<Node>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}
