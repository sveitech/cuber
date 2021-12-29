
/*************************************************************************\
 * FORFATTER  : Daniel Sveistrup
 * DATO       : 2. April 2006
 * 
 * BESKRIVELSE: Denne klasse holder data omkring en enkelt tekstur.
 *              Alle objekter der ønsker at benytte en tekstur,
 *              har en instans af dette objekt.
 * 
 * NOTER      : 2. April 2006 --- Fil oprettet.
\*************************************************************************/

public class TextureHandle
{
	/*********************************************************************\
	 * CONSTRUCTOR
	 * 
	 * ARGS: int<textureID>, float<width>, float<height>
	\*********************************************************************/
	public TextureHandle( int textureHandle, float width, float height )
	{
		this.textureHandle = textureHandle;
		this.width     = width;
		this.height    = height;
	}
	
	/*********************************************************************\
	 * GET AND SET METHODS
	 * 
	\*********************************************************************/
	public int getTextureHandle()
	{
		return textureHandle;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	private int   textureHandle;
	private float width;
	private float height;
}