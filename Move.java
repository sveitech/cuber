
/***************************************************
 * Denne klasse representerer et enkelt flyt på terningen.
 * 
 * @author Daniel Sveistrup
 *
 */
public class Move
{
	private boolean positiveRotation;
	private int     axis;
	private int     disk;
	
	/************************************************
	 * axis: 0-2 ( I, J, K )
	 * disk: 0-2
	 * 
	 * @param positiveRotation
	 * @param axis
	 * @param disk
	 */
	public Move( boolean positiveRotation, int axis, int disk )
	{
		this.positiveRotation = positiveRotation;
		this.axis             = axis;
		this.disk             = disk;
	}//constructor move end
	
	public boolean getPositiveRotation() { return this.positiveRotation; }
	public int     getAxis() { return this.axis; }
	public int     getDisk() { return this.disk; }
	
}//class Move end
