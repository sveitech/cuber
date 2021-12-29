import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;


public class Cube
{
	//The center of the cube
	private float centerX;
	private float centerY;
	private float centerZ;
	private float size;
	
	private float[][] color = { { 1.0f, 1.0f, 1.0f },
			 				  	{ 1.0f, 0.0f, 0.0f },
							 	{ 0.0f, 1.0f, 0.0f },
							 	{ 0.0f, 0.0f, 1.0f },
							 	{ 1.0f, 0.5f, 0.2f }, 
							 	{ 1.0f, 1.0f, 0.0f },
							 	{ 0.2f, 0.2f, 0.2f } };
	
	private ArrayList<TextureHandle> textures = new ArrayList<TextureHandle>();
	
	private int[] sidesColor = {0, 1, 2, 3, 4, 5 };
	
	/************************************************************
	 * Constructor.
	 *  
	 * @param x
	 * @param y
	 * @param z
	 * @param size
	 */
	public Cube( float x, float y, float z, float size )
	{
		this.centerX = x;
		this.centerY = y;
		this.centerZ = z;
		this.size    = size;
	}//constructor Cube end
	
	public void setTextures( ArrayList<TextureHandle> textures )
	{
		this.textures = textures;
	}
	
	/*************************************************************
	 * resetColor.
	 *
	 */
	public void resetColor()
	{
		for( int i = 0; i < sidesColor.length; i++ )
			sidesColor[i] = 6;
	}
	
	/*************************************************************
	 * setColor.
	 * 
	 * @param side
	 * @param color
	 */
	public void setColor( int side, int color )
	{
		sidesColor[side] = color;
	}
	
	/*************************************************************
	 * rotate.
	 * 
	 * Rotates the cube clockwise or counter-clockwise around the
	 * specified axis. ( 0 = x, 1 = y, 2 = z )
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate( boolean counterclockwise, int axis )
	{
		//Roter kuben ved at �ndre farverne p� siderne.
		
		// x-rotation. flyt 0-1-2-3 til 1-2-3-0
		if( axis == 0 )
		{
			exchangeSides( 0, 1 );
			exchangeSides( 1, 2 );
			exchangeSides( 2, 3 );
		}
		//y-rotation. flyt 0-4-2-5 til 5-0-4-2
		else if( axis == 1 )
		{
			exchangeSides( 0, 5 );
			exchangeSides( 4, 5 );
			exchangeSides( 2, 5 );
		}
		//z-rotation. flyt 5-3-4-1 til 1-5-3-4
		else if( axis == 2 )
		{
			exchangeSides( 5, 1 );
			exchangeSides( 3, 1 );
			exchangeSides( 4, 1 );
		}
	}//rotate end
	
	/*************************************************************
	 * exchangeSides.
	 * 
	 * @param a
	 * @param b
	 */
	private void exchangeSides( int a, int b )
	{
		int temp = sidesColor[a];
		sidesColor[a] = sidesColor[b];
		sidesColor[b] = temp;
	}
	
	/*************************************************************
	 * print.
	 *
	 */
	public void print()
	{
		System.out.print( "Color : " );
		
		for( int i = 0; i < sidesColor.length; i++ )
		{
			System.out.print( " <" + sidesColor[i] + ">" );
		}
	}
	
	/*************************************************************
	 * render
	 */
	public void render()
	{
		//bevar matrix
		GL11.glPushMatrix();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			//side 0
			GL11.glColor3f( color[sidesColor[0]][0], color[sidesColor[0]][1], color[sidesColor[0]][2] );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			
			//side 1
			GL11.glColor3f( color[sidesColor[1]][0], color[sidesColor[1]][1], color[sidesColor[1]][2] );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			
			//side 2
			GL11.glColor3f( color[sidesColor[2]][0], color[sidesColor[2]][1], color[sidesColor[2]][2] );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			
			//side 3
			GL11.glColor3f( color[sidesColor[3]][0], color[sidesColor[3]][1], color[sidesColor[3]][2] );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
		
			//side 4
			GL11.glColor3f( color[sidesColor[4]][0], color[sidesColor[4]][1], color[sidesColor[4]][2] );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
		
			//side 5
			GL11.glColor3f( color[sidesColor[5]][0], color[sidesColor[5]][1], color[sidesColor[5]][2] );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}//render end
	
	/*************************************************************
	 * renderWithTexture
	 */
	public void renderWithTexture()
	{
		//bevar matrix
		GL11.glPushMatrix();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[0] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			//side 0
			//GL11.glColor3f( color[sidesColor[0]][0], color[sidesColor[0]][1], color[sidesColor[0]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[1] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			//side 1
			//GL11.glColor3f( color[sidesColor[1]][0], color[sidesColor[1]][1], color[sidesColor[1]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[2] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{	
			
			//side 2
			//GL11.glColor3f( color[sidesColor[2]][0], color[sidesColor[2]][1], color[sidesColor[2]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[3] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{	
			//side 3
			//GL11.glColor3f( color[sidesColor[3]][0], color[sidesColor[3]][1], color[sidesColor[3]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[4] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			//side 4
			//GL11.glColor3f( color[sidesColor[4]][0], color[sidesColor[4]][1], color[sidesColor[4]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX + (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( sidesColor[5] ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			//side 5
			//GL11.glColor3f( color[sidesColor[5]][0], color[sidesColor[5]][1], color[sidesColor[5]][2] );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ + (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 0.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY - (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 1.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ - (this.size/2) );
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex3f( this.centerX - (this.size/2), this.centerY + (this.size/2), this.centerZ + (this.size/2) );
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}//render end
	
}//class Cube end