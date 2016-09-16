package core;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

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
    
    public static void main(String[] args) {
        Path r = new Path();
        try {
            PathFindingJob job = new PathFindingJob(new Point(50,50),new Point(1280,1280),new Arena("mansion",false),r);
            job.run();
            for (Point2D p:r.path) {
                System.out.println(p);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
