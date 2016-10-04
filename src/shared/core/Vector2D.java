package shared.core;

/**
 * Allows for the 2-dimensional representation of various aspects of particles and the other
 * calculations involving them.
 * 
 * @author Anh Pham
 */

public class Vector2D {

	public double x; // represents the first dimension.
	public double y; // represents the second dimension.

	/**
	 * Creates a Vector2D object.
	 * 
	 * @param num1
	 *            represents the first dimension.
	 * @param num2
	 *            represents the second dimension.
	 */
	public Vector2D(double num1, double num2) {
		this.x = num1;
		this.y = num2;
	}

	/**
	 * Adds the given number as a parameter to the 2 dimensions.
	 * 
	 * @param num
	 *            The number to be added.
	 */
	public void add(double num) {
		x += num;
		y += num;
	}

	/**
	 * Subtracts the given number as a parameter to the 2 dimensions.
	 * 
	 * @param num
	 *            The number to be subtracted.
	 */
	public void sub(double num) {
		x -= num;
		y -= num;
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
	}

	/**
	 * Allows addition of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be added.
	 */
	public void add(Vector2D other) {
		x += other.x;
		y += other.y;
	}

	/**
	 * Allows subtraction of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be subtracted.
	 */
	public void sub(Vector2D other) {
		x -= other.x;
		y -= other.y;
	}

	/**
	 * Allows division of one vector by another.
	 * 
	 * @param other
	 *            Vector2D used for dividing.
	 */
	public void div(Vector2D other) {
		x /= other.x;
		y /= other.y;
	}

	/**
	 * Allows multiplication of 2 vectors.
	 * 
	 * @param other
	 *            Vector2D to be multiplied.
	 */
	public void mult(Vector2D other) {
		x *= other.x;
		y *= other.y;
	}
	
	public double magSqr() {
		return x*x + y*y;
	}
	
}
