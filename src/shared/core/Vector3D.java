package shared.core;

import java.io.Serializable;

/**
 * Allows for the 2-dimensional representation of various aspects of particles and the other
 * calculations involving them.
 * 
 * @author Anh Pham
 */

public class Vector3D implements Serializable {
	private static final long serialVersionUID = -1633556996771476061L;
	
	public double x; // represents the first dimension.
	public double y; // represents the second dimension.
	public double z;
    
    public Vector3D() {
        this(0,0,0);
    }
    
    public Vector3D(double x, double y, double z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }


	/**
	 * Divides the the 2 dimensions. by the given number as a parameter.
	 * 
	 * @param num
	 *            The number to divide by.
	 */
	public void div(double num) {
		x /= num;
		y /= num;
		z /= num;
	}

	/**
	 * Multiplies the the 2 dimensions by the given number as a parameter.
	 * 
	 * @param num
	 *            The number to be multiplied by.
	 */
	public void mult(double num) {
		x *= num;
		y *= num;
		z *= num;
	}

	/**
	 * Allows addition of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be added.
	 */
	public void add(Vector3D other) {
		x += other.x;
		y += other.y;
		z += other.z;
	}

	/**
	 * Allows subtraction of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be subtracted.
	 */
	public void sub(Vector3D other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
	}

	/**
	 * Allows multiplication of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be multiplied.
	 */
	public void mult(Vector3D other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
	}
	
	public double magSqr() {
		return x*x + y*y;
	}
	
    public double get2DDirection() {
        return Math.atan2(y, x);
    }
    
    public Vector3D clone() {
    	return new Vector3D(x,y,z);
    }
}
