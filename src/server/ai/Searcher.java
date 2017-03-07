package server.ai;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jbt.execution.core.ExecutionTask.Status;
import server.world.Arena;
import server.world.Geometry;
import server.world.Terrain;
import server.world.Utils;

/**
 * Used for the logic behind our path finding.
 * 
 * @author Anh Pham
 */
public class Searcher <T,R> {
	
	public static Point2D searchAttackStandPoint(final Arena arena, Point2D self, final Point2D target, final double range, final double pen) {
		Searcher<Point,Point2D> searcher = new Searcher<Point,Point2D>();
    	final Point ts = Utils.meterToTile(self.getX(),self.getY());
    	final Point td = Utils.meterToTile(target.getX(),target.getY());
    	searcher.source = ts;
    	searcher.df = new ArenaDistanceFunction();
    	searcher.ag = new ArenaAdjacentWalkableGenerator(arena);
    	searcher.gv = new GoalVerifier<Point>() {
			@Override
			public boolean isGoal(Point state) {
				Point2D ms = Utils.tileToMeter(state);
				if (ms.distance(target)<range) {
					double block = 0;
					double CAST_DIST = 0.5; 
					List<Point2D> points = Geometry.getLineSamples(ms, target, CAST_DIST);
					for (Point2D p:points) {
						block += arena.getTileAt(p.getX(),p.getY()).coverType() * CAST_DIST/Terrain.tileSize;
						if (block>pen) {
							return false;
						}
					}
					return true;
				}
				else {
					return false;
				}
			}};
    	searcher.hf = new HeuristicFunction<Point> (){
			@Override
			public float evaluate(Point state) {
				return Geometry.diagonalDistance(state, td);
			}};
		searcher.gm = new GoalMapper<Point,Point2D>() {
			@Override
			public Point2D map(List<Point> state) {
				return Utils.tileToMeter(state.get(state.size()-1));
			}};
    	searcher.run();
    	
    	return searcher.result.path;
	}

	public static Point2D searchCheckStandPoint(final Arena arena, Point2D self, final Point2D target, final double range) {
		Searcher<Point,Point2D> searcher = new Searcher<Point,Point2D>();
    	final Point ts = Utils.meterToTile(self.getX(),self.getY());
    	final Point td = Utils.meterToTile(target.getX(),target.getY());
    	searcher.source = ts;
    	searcher.df = new ArenaDistanceFunction();
    	searcher.ag = new ArenaAdjacentWalkableGenerator(arena);
    	searcher.gv = new GoalVerifier<Point>() {
			@Override
			public boolean isGoal(Point state) {
				Point2D ms = Utils.tileToMeter(state);
				if (ms.distance(target)<range) {
					List<Point2D> points = Geometry.getLineSamples(ms, target, 0.2);
					for (Point2D p:points) {
						if (!(Utils.meterToTile(p).equals(td) || arena.getTileAt(p.getX(),p.getY()).isClear())) {
							return false;
						}
					}
					return true;
				}
				return false;
			}};
    	searcher.hf = new HeuristicFunction<Point> (){
			@Override
			public float evaluate(Point state) {
				return Geometry.diagonalDistance(state, td);
			}};
		searcher.gm = new GoalMapper<Point,Point2D>() {
			@Override
			public Point2D map(List<Point> state) {
				return Utils.tileToMeter(state.get(state.size()-1));
			}};
    	searcher.run();
    	
    	return searcher.result.path;
	}
	
	/** Find the shortest path from one point to another in the given map.
     * 
     */
    public static Path findPath(Arena arena, Point2D self, Point2D point2d) {
    	if (self==null || point2d==null)
    		return new Path();
    	else
    		return findPath(arena,self.getX(),self.getY(),point2d.getX(),point2d.getY());
    }
    
    /** Find the shortest path from one point to another in the given map.
     * 
     */
    public static Path findPath(Arena arena, double sx, double sy, double dx, double dy) {
        Path result = new Path();
        new PathFindingJob(new Point2D.Double(sx,sy),new Point2D.Double(dx,dy),arena,result).run();
        return result;
    }
    
    public static double getMoveDistance(Arena arena, Point2D p1, Point2D p2) {
    	return getMoveDistance(arena,p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }
    
    public static double getMoveDistance(Arena arena, double sx, double sy, double dx, double dy) {
    	List<Point2D> path = searchPath(arena,sx,sy,dx,dy);
    	if (path!=null) {
	    	double dist = 0;
	    	Point2D cur = new Point2D.Double(sx,sy);
	    	for (Point2D p:path) {
	    		dist += cur.distance(p);
	    		cur = p;
	    	}
	    	return dist;
    	} else {
    		return Double.MAX_VALUE;
    	}
    }
    
    /** Find the shortest path from one point to another in the given map.
     * 
     */
    public static List<Point2D> searchPath(Arena arena, Point2D self, Point2D point2d) {
		return searchPath(arena,self.getX(),self.getY(),point2d.getX(),point2d.getY());
    }
    
    public static List<Point2D> searchPath(Arena arena, double sx, double sy, final double dx, final double dy) {
    	Searcher<Point,List<Point2D>> searcher = new Searcher<Point,List<Point2D>>();
    	final Point ts = Utils.meterToTile(sx,sy);
    	final Point td = Utils.meterToTile(dx,dy);
    	searcher.source = ts;
    	searcher.df = new ArenaDistanceFunction();
    	searcher.ag = new ArenaAdjacentWalkableGenerator(arena);
    	searcher.gv = new ExactGoalVerifier<Point>(td);
    	searcher.hf = new HeuristicFunction<Point> (){
			@Override
			public float evaluate(Point state) {
				return Geometry.diagonalDistance(state, td);
			}};
		searcher.gm = new GoalMapper<Point,List<Point2D>>() {
			@Override
			public List<Point2D> map(List<Point> state) {
				List<Point2D> path = new LinkedList<Point2D>();
				for (Point p:state) {
					path.add(Utils.tileToMeter(p));
				}
				return path;
			}};
    	searcher.run();
    	
    	return searcher.result.path;
    }
    
    T source;
    
    HeuristicFunction<T> hf;
    DistanceFunction<T> df;
    AdjacentGenerator<T> ag;
    GoalVerifier<T> gv;
    HashMap<T,ExploreState> em = new HashMap<T,ExploreState>();
    HashMap<T,T> fm = new HashMap<T,T>();
    HashMap<T,Float> cm = new HashMap<T,Float>();
    SearchResult<R> result = new SearchResult<R>();
    GoalMapper <T,R> gm;
    float maxDistance = 30;
    
    public Searcher() {
    	result.status = Status.UNINITIALIZED;
    }
    
    public void run() {
        // OPTIMIZATION: two array = more memory and a bit less calculation - memory vs speed
        //float[][] distFromSrc = new float[w][h];
        result.status = Status.RUNNING;
        List<T> open = new LinkedList<T>();
        open.add(source);
        cm.put(source, hf.evaluate(source));

        T current = null;
        while (!open.isEmpty()) {
            // explore the most promising option
            float min = Float.MAX_VALUE;
            for (T p:open) {
                if (cm.get(p)<min) {
                    min = cm.get(p);
                    current = p;
                }
            }
            
            // if it is the goal, end the search
            if (gv.isGoal(current)) {
            	result.status = Status.SUCCESS;
                result.path = gm.map(genFinalPath(current));
                return;
            }
            
            // else
            open.remove(current);
            em.put(current, ExploreState.Explored);
            float distCurSrc = cm.get(current)-hf.evaluate(current);
            if (distCurSrc<maxDistance) {
	            // generate nearby empty tiles
	            List<T> nearby = ag.getAdjacentsStates(current);
	            for (T p:nearby) {
	                if (em.get(p)==ExploreState.Explored)
	                    continue;
	                float newTotalDist = distCurSrc + df.getDistance(p,current) + hf.evaluate(p);
	                if (em.get(p)==null || newTotalDist<cm.get(p)) {
	                    fm.put(p, current);
	                    cm.put(p,newTotalDist);
	                    if (em.get(p)==null) {
	                        open.add(p);
	                        em.put(p, ExploreState.Exploring);
	                    }
	                }
	            }
            }
        }
        result.status = Status.FAILURE;
    }
    
    public List<T> genFinalPath(T goal) {
    	T current = fm.get(goal);
        List<T> path = new LinkedList<T>();
        path.add(goal);
        while (current!=null && !current.equals(source)) {
            path.add(0,current);
            current = fm.get(current);
        }
        return path;
    }
    
    public static class SearchResult <R> {
    	Status status;
    	R path;
    }
    
    /**
     * A result object, mostly used for getting the resulting path
     * in a multi-threaded implementation. 
     */
    public static class Path {
        public List<Point2D> path = null;
    }
    
    public static enum ExploreState {Explored, Exploring, Unexplored}
    
    public interface HeuristicFunction <T> {
    	public float evaluate (T state);
    }
    
    public interface DistanceFunction <T> {
    	public float getDistance (T state1, T state2);
    }
    
    public interface AdjacentGenerator <T> {
    	public List<T> getAdjacentsStates(T state); 
    }
    
    public interface GoalVerifier <T> {
    	public boolean isGoal(T state);
    }
    
    public interface GoalMapper <T,R> {
    	public R map(List<T> state);
    }
    
    public static class ExactGoalVerifier<T> implements GoalVerifier<T> {
    	private T goal;
    	public ExactGoalVerifier(T goal) {
    		this.goal = goal;
    	}
		@Override
		public boolean isGoal(T state) {
			return goal.equals(state);
		}
    	
    }
    
    public static class ArenaDistanceFunction implements DistanceFunction<Point> {
		@Override
		public float getDistance(Point state1, Point state2) {
			return Geometry.diagonalDistance(state1,state2);
		}
    }
    
    public static class ArenaAdjacentWalkableGenerator implements AdjacentGenerator<Point> {
    	private Arena arena;
    	public ArenaAdjacentWalkableGenerator(Arena a) {
    		arena = a;
    	}
    	
		@Override
		public List<Point> getAdjacentsStates(Point p) {
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
    }

	
}
