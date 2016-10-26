package server.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import server.ai.PathFinder.Path;
import server.world.Arena;
import server.world.Geometry;
import server.world.Terrain;

/**
 * Represents a path finding / searching problem and the solving algorithm using A* search.
 * Created separated from PathFinder mostly to prepare for multithreading.
 */
public class PathFindingJob implements Runnable {
    private static final byte UNEXPLORED = 0;
    private static final byte EXPLORING = 1;
    private static final byte EXPLORED = 2;
    private Path result;
    private Point source;
    private Point dest;
    private Point2D pixelDest;
    private Arena arena;
    
    /**
     * Constructor
     * @param source The source of the path
     * @param dest The destination of the path
     * @param arena The arena in which we are trying to find the path
     * @param result The result object to return the resulting path, asynchronously. 
     */
    public PathFindingJob(Point2D source, Point2D dest, Arena arena, Path result) {
        this.arena = arena;
        this.source = new Point((int)(source.getX()/Terrain.tileSize),(int)(source.getY()/Terrain.tileSize));
        this.pixelDest = dest;
        this.dest = new Point((int)(pixelDest.getX()/Terrain.tileSize),(int)(pixelDest.getY()/Terrain.tileSize));
        this.result = result;
    }
    
    /**
     * Run the path finding algorithm.
     */
    @Override
    public void run() {
        int w = arena.getWidth(),h=arena.getHeight();
        Point[][] from = new Point[w][h];
        byte[][] exploreState = new byte[w][h];
        
        // OPTIMIZATION: two array = more memory and a bit less calculation - memory vs speed
        //float[][] distFromSrc = new float[w][h];
        float[][] estTotalDist = new float[w][h];
        for (byte[] row:exploreState)
            Arrays.fill(row,UNEXPLORED);
        
        List<Point> open = new LinkedList<Point>();
        open.add(source);

        Point current = null;
        while (!open.isEmpty()) {
            // explore the most promising option
            float min = Float.MAX_VALUE;
            for (Point p:open) {
                if (estTotalDist[p.x][p.y]<min) {
                    min = estTotalDist[p.x][p.y];
                    current = p;
                }
            }
            
            // if it is the goal, end the search
            if (current.equals(dest)) {
                result.path = genFinalPath(from);
                return;
            }
            
            // else
            open.remove(current);
            exploreState[current.x][current.y] = EXPLORED;
            float distCurSrc = estTotalDist[current.x][current.y] - heuristic(current);
            
            // generate nearby empty tiles
            List<Point> nearby = genAdjPositions(current);
            for (Point p:nearby) {
                if (exploreState[p.x][p.y]==EXPLORED)
                    continue;
                float newTotalDist = distCurSrc + Geometry.diagonalDistance(p,current) + heuristic(p);
                if (exploreState[p.x][p.y]==UNEXPLORED || newTotalDist<estTotalDist[p.x][p.y]) {
                    from[p.x][p.y] = current;
                    estTotalDist[p.x][p.y] = newTotalDist;
                    if (exploreState[p.x][p.y]==UNEXPLORED) {
                        open.add(p);
                        exploreState[p.x][p.y] = EXPLORING;
                    }
                }
            }
        }
    }
    
    /**
     * Calculate the heuristic of any given state.
     */
    private float heuristic (Point p) {
        return Geometry.diagonalDistance(p,dest);
    }
    
    /**
     * Generate adjacent valid positions (i.e empty tiles) of any given nodes.
     */
    private List<Point> genAdjPositions (Point p) {
        List<Point> adjPositions = new LinkedList<Point>();
        Point top = new Point(p.x,p.y-1);
        Point btm = new Point(p.x,p.y+1);
        Point left = new Point(p.x-1,p.y);
        Point right = new Point(p.x+1,p.y);
        Point topright = new Point(p.x+1,p.y-1);
        Point topleft = new Point(p.x-1,p.y-1);
        Point btmright = new Point(p.x+1,p.y+1);
        Point btmleft = new Point(p.x-1,p.y+1);
        if (arena.get(top).isTraversable())    
            adjPositions.add(top);
        if (arena.get(btm).isTraversable())
            adjPositions.add(btm);
        if (arena.get(left).isTraversable())   
            adjPositions.add(left);
        if (arena.get(right).isTraversable())
            adjPositions.add(right);
        if (arena.get(top).isTraversable() && arena.get(right).isTraversable() && arena.get(topright).isTraversable())
            adjPositions.add(topright);
        if (arena.get(top).isTraversable() && arena.get(left).isTraversable() && arena.get(topleft).isTraversable())
            adjPositions.add(topleft);
        if (arena.get(btm).isTraversable() && arena.get(right).isTraversable() && arena.get(btmright).isTraversable())
            adjPositions.add(btmright);
        if (arena.get(btm).isTraversable() && arena.get(left).isTraversable() && arena.get(btmleft).isTraversable())
            adjPositions.add(btmleft);
        return adjPositions;
    }
    

    /**
     * Generate the final path from source to destination
     * using the result of the search.
     */
    private List<Point2D> genFinalPath (Point[][] from){
        // TODO smoothen the path
        Point current = from[dest.x][dest.y];
        List<Point2D> path = new LinkedList<Point2D>();
        path.add(pixelDest);
        while (current!=null && !current.equals(source)) {
            path.add(0,server.world.Utils.tileToMeter(current));
            current = from[current.x][current.y];
        }
        return path;
    }
}