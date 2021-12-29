import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*************************************************************************\
 * FORFATTER  : Daniel Sveistrup
 * DATO       : 2. April 2006
 * 
 * BESKRIVELSE: Denne klasse indlæser en enkelt tekstur, og returnerer den
 * 				som en bytebuffer,
 * NOTER      : 2. April 2006 --- Fil oprettet.
\*************************************************************************/

public class Texture
{
	/*********************************************************************\
	 * CONSTRUCTOR
	 * 
	 * ARGS: String<imageName>
	\*********************************************************************/
	public Texture( String imageName )
	{
		loadImage( imageName );
	}//constructor end
	
	/*********************************************************************\
	 * loadImage().
	 * 
	 * ARGS: String<imageName>
	 * 
	 * Denne funktion sørger for at indlæse billedet, og konvertere
	 * det til en byteBuffer.
	\*********************************************************************/
	private boolean loadImage( String imageName )
	{
		//Læs billed-bytes
		byte[] bytes     = loadImageBytes( imageName );
		
		Image javaImage  = Toolkit.getDefaultToolkit().createImage( bytes, 0, bytes.length );
		
		//Vent indtil billedet er indlæst.
		while( javaImage.getHeight( null ) < 0 )
		{
			
		}
		
		textureWidth  = javaImage.getWidth( null );
		textureHeight = javaImage.getHeight( null );
		
		System.out.println( "Image width : " + textureWidth );
		System.out.println( "Image height: " + textureHeight );
		
		//Lad Java hente pixels ud af billedet
		int[] pixels  = getImagePixels( javaImage );
		
		textureBytes = convertImagePixels( pixels );
		
		if( textureBytes != null )
		{
			System.out.println( "Image successfully loaded." );
		}
		
		return true;
	}//loadImage end
	
	/*
	 * convertImagePixels().
	 * 
	 * Konverterer de pixels som java har læst, så de er på RGBA-form, og returnerer dem som en
	 * ByteBuffer.
	 */
	private ByteBuffer convertImagePixels( int[] pixels )
	{
		//indeholder bytes på RGBA form
		byte[] bytes;
		
		//Før vi kan bruge vores bytes, skal de "flippes". Bitmaps gemmes nemlig spejlvendt.
		pixels = flipPixels( pixels );
		bytes  = ARGBToRGBA( pixels );
		
		ByteBuffer buffer = ByteBuffer.allocateDirect( bytes.length).order( ByteOrder.nativeOrder() );
		buffer.put( bytes ).flip();
		
		return buffer;

	}//convertImagePixels end
	
	/*
	 * ARGBToRGBA().
	 * 
	 * Omdanner ARGB til RGBA format.
	 */
	private byte[] ARGBToRGBA( int[] pixels )
	{
		//Dette array indeholder bytes i RGBA format.
		byte[] bytes = new byte[ textureWidth * textureHeight * 4 ];
		
		int pixel, red, green, blue, alpha;
		
		for( int i = 0, j = 0; i < pixels.length; i++, j += 4 )
		{
			pixel = pixels[ i ];
			
			//læs de individuelle bytes
			alpha   = ( pixel >> 24 ) & 0xFF;
			red     = ( pixel >> 16 ) & 0xFF;
			green   = ( pixel >> 8  ) & 0xFF;
			blue    = pixel & 0xFF;
			
			//gem pixels i bytes-array
			bytes[ j ]     = (byte) red;
			bytes[ j + 1 ] = (byte) green;
			bytes[ j + 2 ] = (byte) blue;
			bytes[ j + 3 ] = (byte) alpha;
		}//for end
		
		return bytes;
		
	}//ARGBToRGBA end
	
	/*
	 * flipPixels().
	 * 
	 * bytter rundt på rækkefælgen af pixels, så rækkerne ikke er spejlvendte.
	 */
	private int[] flipPixels( int[] pixels )
	{
		int[] flippedPixels = null;
		
		if( pixels != null )
		{
			flippedPixels = new int[ textureWidth * textureHeight ];
			
			//flip pixels
			for( int y = 0; y < textureHeight; y++ )
			{
				for( int x = 0; x < textureWidth; x++ )
				{
					flippedPixels[ ( (textureHeight - y - 1) * textureWidth) + x] = pixels[ (y * textureWidth) + x];
				}//for end
				
			}//for end
			
		}//if end
		
		return flippedPixels;
		
	}//flipPixels end
	
	/*
	 * getImagePixels().
	 * 
	 * læser billeddata fra java-Image.
	 */
	private int[] getImagePixels( Image javaImage )
	{
		int[] pixels = new int[ textureWidth * textureHeight ];
		
		//Opret pixel-grapper objekt
		PixelGrabber grabber = new PixelGrabber( javaImage, 0, 0, textureWidth, textureHeight, pixels, 0, textureWidth );
		
		try
		{
			grabber.grabPixels();
		}//try end
		catch( Exception e )
		{
			System.out.println( "Could not grap pixels from image" );
		}//catch end
		
		return pixels;
		
	}//getImagePixels
	
	/*
	 * readImageBytes().
	 * 
	 * Denne funktion åbner filen, og indlæser bytes.
	 */
	private byte[] loadImageBytes( String fileName )
	{
		byte[] bytes                = null;						//Array til læste bytes
		FileInputStream inputStream = null;
		File imageFile              = null;						//Billedet
		
		//åben fil.
		try
		{
			imageFile   = new File( fileName );
			inputStream = new FileInputStream( imageFile );
		}//try end
		catch( Exception e )
		{
			System.out.println( "Could not read bytes from " + fileName );
		}//catch end
		
		//Læs bytes og kopier til array
		if( inputStream != null )
		{
			int numberOfBytes = 0;
			
			//Find antal bytes i filen.
			try
			{
				numberOfBytes = inputStream.available();
			}//try end
			catch( Exception e )
			{
				System.out.println( "No available bytes in file " + fileName );
			}//catch end
			
			bytes = new byte[ numberOfBytes ];
			
			//Læs bytes over i array
			for( int i = 0; i < numberOfBytes; i++ )
			{
				try
				{
					bytes[ i ] = (byte) inputStream.read();
				}//try end
				catch( Exception e )
				{
					System.out.println( "Could not read byte from file " + fileName );
				}//catch end
			}//for end
			
		}//if (inputStream != null) end
		
		return bytes;
		
	}//loadImageBytes end
	
	public ByteBuffer getTexture()
	{
		return textureBytes;
	}
	
	public int getTextureWidth()
	{
		return textureWidth;
	}
	
	public int getTextureHeight()
	{
		return textureHeight;
	}
	
	/***********************************************************
	 * Privat data
	 ***********************************************************/
	private int textureWidth        = 0;
	private int textureHeight       = 0;
	
	private ByteBuffer textureBytes = null;
}//class texture end
