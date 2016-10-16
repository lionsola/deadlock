package server.ai;


import java.awt.geom.Point2D;
import java.util.List;

import server.world.Arena;

/**
 * Used for the logic behind our path finding.
 * 
 * @author Anh Pham
 */
public class PathFinder {
    // OPTIMIZATION: could use a 2d array of booleans to avoid pointer chasing
    // but then, if the map state changes with time (destructible obstacles, etc.)
    // have to update it as well
    private Arena arena;
    
    public PathFinder(Arena arena) {
        this.arena = arena;
    }
    
    public Path findPath(Point2D self, Point2D point2d) {
    	if (self==null || point2d==null)
    		return new Path();
    	else
    		return findPath(self.getX(),self.getY(),point2d.getX(),point2d.getY());
    }
    
    // At the moment find path is single threaded, might try multi threaded
    // if AI's performance becomes a problem 
    public Path findPath(double sx, double sy, double dx, double dy) {
        Path result = new Path();
        new PathFindingJob(new Point2D.Double(sx,sy),new Point2D.Double(dx,dy),arena,result).run();
        return result;
    }
    
    /**
     * A result object, mostly used for getting the resulting path
     * in a multi-threaded implementation. 
     */
    public static class Path {
        public List<Point2D> path = null;
    }
}
