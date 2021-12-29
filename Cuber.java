import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.devil.IL;
import org.lwjgl.devil.ILU;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/****************************************************
 * 
 * @author Daniel Sveistrup
 *
 * Spil-styring:
 * 
 * I : vælger I-akse
 * J : vælger J-akse
 * K : vælger K-akse
 * H : Hide/Unhide disk selector 
 *  R : random
 * 
 * Up: rotates disk clockwise
 * Down: rotates disk counterclockwise
 * Left: move selector left
 * Right: move selector right
 * 
 * Esc: Exit program
 */
public class Cuber
{
	public static boolean running = true;
	public static Cube[][][] cubes = new Cube[3][3][3];
	public static boolean renderWithTexture = true;
	
	public static Cube selectorBlock = new Cube( 0.0f, 0.0f, 0.0f, 1.0f );
	public static boolean selectorBlockActive = true;
	public static boolean randomizing         = false;
	public static int[] selectorBlockState = {-1, 1,-1}; 
	public static boolean keyPressedLeft = false;
	public static boolean keyPressedRight = false;
	public static boolean keyPressedH     = false;
	public static boolean keyPressedR     = false;
	
	public static float diskAngle = 0.0f;
	public static boolean animationRunning = false;
	public static boolean positiveRot      = true;
	public static int[] disk = {-1,-1,-1};                   //{i,j,k}
	public static float diskSpeed = 1.0f;
	
	public static float cubeRotationX = 45.0f;
	public static float cubeRotationY = 45.0f;
	
	public static float blockSpacing = 1.1f; 
	
	public static ArrayList<TextureHandle> textures = new ArrayList<TextureHandle>();
	
	/*
	 * Cube representation.
	 * 
	 * Terningen er organiseret s�dan her:
	 * 
	 * 				222
	 * 				222
	 * 				222
	 * 				111
	 * 				111
	 * 				111
	 * 			 555000444
	 * 			 555000444
	 * 			 555000444
	 * 				333
	 * 				333
	 * 				333
	 * 
	 *  0 vender ud mod bruger langs Z+.
	 *  2 vender væk fra bruger langs Z-.
	 *  5 vender til venstre mod X-.
	 *  4 vender til højre mod X+.
	 *  1 vender op mod Y+.
	 *  3 vender ned mod Y-.	 
	 */
	public static int[][][] sides = {{ {0,0,0},
									   {0,0,0},
									   {0,0,0} },
									   
	                                 { {1,1,1},
									   {1,1,1},
									   {1,1,1} },
									   
	                                 { {2,2,2},
								   	   {2,2,2},
									   {2,2,2} },
									   
	                                 { {3,3,3},
									   {3,3,3},
									   {3,3,3} },
									   
	                                 { {4,4,4},
									   {4,4,4},
									   {4,4,4} },
									   
	                                 { {5,5,5},
									   {5,5,5},
									   {5,5,5} }};
	
	/*
	 * main 
	 */
	public static void main( String[] args )
	{
		System.out.println( "Cuber" );
		
		//generate cubes
		for( int i = 0; i < 3; i++ ) 
		{
			for( int j = 0; j < 3; j++ )
			{
				for( int k = 0; k < 3; k++ )
				{
					cubes[i][j][k] = new Cube( 0.0f, 0.0f, 0.0f, 1.0f );
				}
			}
		}
		
		repaintCube();
		
		initialize();
		
		selectorBlock.setColor( 0, 0 );
		selectorBlock.setColor( 1, 0 );
		selectorBlock.setColor( 2, 0 );
		selectorBlock.setColor( 3, 0 );
		selectorBlock.setColor( 4, 0 );
		selectorBlock.setColor( 5, 0 );

		run();
	}//main end
	
	public static void shutdown()
	{
		try
		{
			IL.destroy();
			ILU.destroy();
			Display.destroy();
		}
		catch( Exception e )
		{
			System.out.println( "Could not terminate openGL stuff." );
		}
		
		System.exit( 0 );
	}
	
	/***********************************************
	 * Siderne mappes her ind på cubens koordinater, så siderne
	 * tegnes rigtigt.
	 *
	 */
	public static void repaintCube()
	{
		//Rens først alle cubes
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
				for( int k = 0; k < 3; k++ )
				{
					cubes[i][j][k].resetColor();
				}
		
		//Tegn nu siderne
		
		//mal side 0 ( remapped correctly! )
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
			{
				cubes[i][j][2].setColor(0, sides[0][2-j][i] );
			}
		
		//mal side 1 ( remapped correctly! )
		for( int i = 0; i < 3; i++ )
				for( int k = 0; k < 3; k++ )
				{
					cubes[i][2][k].setColor(1, sides[1][k][i] );
				}
		
		//mal side 2 ( remapped correctly! )
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
			{
				cubes[i][j][0].setColor(2, sides[2][j][i] );
			}
		
		//mal side 3 ( remapped correctly! )
		for( int i = 0; i < 3; i++ )
			for( int k = 0; k < 3; k++ )
			{
				cubes[i][0][k].setColor(3, sides[3][2-k][i] );
			}
		
		//mal side 4 ( remapped correctly! )
		for( int j = 0; j < 3; j++ )
			for( int k = 0; k < 3; k++ )
			{
				cubes[2][j][k].setColor(4, sides[4][2-j][2-k] );
			}
		
		//mal side 5 ( remapped correctly! )
		for( int j = 0; j < 3; j++ )
			for( int k = 0; k < 3; k++ )
			{
				cubes[0][j][k].setColor(5, sides[5][2-j][k] );
			}
	}
	
	public static void render()
	{
		GL11.glMatrixMode (GL11.GL_PROJECTION);    /* prepare for and then */ 
		GL11.glLoadIdentity ();               /* define the projection */
		
		float ratio = (float)Display.getDisplayMode().getWidth() / (float)Display.getDisplayMode().getHeight();
		
		GL11.glFrustum ( -ratio , ratio, -1.0f, 1.0f, /* transformation */
	                   1.0, 200.0); 
		GL11.glMatrixMode (GL11.GL_MODELVIEW);  /* back to modelview matrix */
		GL11.glViewport (0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight() );     /* define the viewport */
	
		
		//rens sk�rmen
		GL11.glEnable( GL11.GL_DEPTH_TEST );
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );   
	    GL11.glPolygonMode( GL11.GL_FRONT, GL11.GL_FILL );
	    GL11.glPolygonMode( GL11.GL_BACK, GL11.GL_FILL );
	    
	    GL11.glLoadIdentity();
	    GL11.glTranslatef( 0.0f, 0.0f, -7.0f );
	    GL11.glRotatef( cubeRotationX, 1.0f, 0.0f, 0.0f );
	    GL11.glRotatef( cubeRotationY, 0.0f, 1.0f, 0.0f );
	     
	    renderCube();
	    renderHUD();
	    
	    GL11.glFlush();
	}
	
	public static void renderHUD()
	{
		GL11.glMatrixMode (GL11.GL_PROJECTION);    /* prepare for and then */ 
		GL11.glLoadIdentity ();               /* define the projection */
		
		GL11.glOrtho( 0.0f, Display.getDisplayMode().getWidth(), 0.0f, Display.getDisplayMode().getHeight(), /* transformation */
	                   0.0f, 10.0f); 
		GL11.glMatrixMode (GL11.GL_MODELVIEW);  /* back to modelview matrix */
		GL11.glViewport (0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight() );     /* define the viewport */
		
		GL11.glPolygonMode( GL11.GL_FRONT, GL11.GL_FILL );
	    GL11.glPolygonMode( GL11.GL_BACK, GL11.GL_FILL );
		
		//Start 2D tegning
		GL11.glLoadIdentity();
		
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, textures.get( 7 ).getTextureHandle() );
		
		GL11.glBegin( GL11.GL_QUADS );
		{
			GL11.glTexCoord2f( 0.0f, 1.0f ); GL11.glVertex2f( 1.0f, Display.getDisplayMode().getHeight() - 1.0f );
			GL11.glTexCoord2f( 0.5f, 1.0f ); GL11.glVertex2f( 1.0f + 256.0f, Display.getDisplayMode().getHeight() - 1.0f );
			GL11.glTexCoord2f( 0.5f, 0.0f ); GL11.glVertex2f( 1.0f + 256.0f, Display.getDisplayMode().getHeight() - 512.0f - 1.0f );
			GL11.glTexCoord2f( 0.0f, 0.0f ); GL11.glVertex2f( 1.0f, Display.getDisplayMode().getHeight() - 512.0f - 1.0f );
		}
		GL11.glEnd();
	}
	
	public static void renderCube()
	{
		//Check om der er en rotation der er i gang
		//if( animationRunning )
		//{
		//tegn den skive der roterer
			
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
				for( int k = 0; k < 3; k++ )
				{
					GL11.glPushMatrix();
					
					if( animationRunning && i == disk[0] ) GL11.glRotatef( diskAngle, 1.0f, 0.0f, 0.0f );
					else if( animationRunning && j == disk[1] ) GL11.glRotatef( diskAngle, 0.0f, 1.0f, 0.0f );
					else if( animationRunning && k == disk[2] ) GL11.glRotatef( diskAngle, 0.0f, 0.0f, 1.0f );
					GL11.glTranslatef( i * blockSpacing - blockSpacing, j * blockSpacing - blockSpacing, k * blockSpacing - blockSpacing );	
					if( renderWithTexture ) cubes[i][j][k].renderWithTexture();
					else cubes[i][j][k].render();
					GL11.glPopMatrix();
				}
			
			/*
			// I
			if( disk[0] >= 0 )
			{
				for( int i = 0; i < 3; i++ )
					for( int j = 0; j < 3; j++ )
						for( int k = 0; k < 3; k++ )
						{
							GL11.glPushMatrix();
							
							if( i == disk[0] ) GL11.glRotatef( diskAngle, 1.0f, 0.0f, 0.0f );
							GL11.glTranslatef( i * blockSpacing - blockSpacing, j * blockSpacing - blockSpacing, k * blockSpacing - blockSpacing );	
							if( renderWithTexture ) cubes[i][j][k].renderWithTexture();
							else cubes[i][j][k].render();
							GL11.glPopMatrix();
						}
			}
			
			// J
			else if( disk[1] >= 0 )
			{
				for( int i = 0; i < 3; i++ )
					for( int j = 0; j < 3; j++ )
						for( int k = 0; k < 3; k++ )
						{
							GL11.glPushMatrix();
							if( j == disk[1] ) GL11.glRotatef( diskAngle, 0.0f, 1.0f, 0.0f );
							GL11.glTranslatef( i * blockSpacing - blockSpacing, j * blockSpacing - blockSpacing, k * blockSpacing - blockSpacing );
							if( renderWithTexture ) cubes[i][j][k].renderWithTexture();
							else cubes[i][j][k].render();
							GL11.glPopMatrix();
						}
			}
			
			// K
			else if( disk[2] >= 0 )
			{
				for( int i = 0; i < 3; i++ )
					for( int j = 0; j < 3; j++ )
						for( int k = 0; k < 3; k++ )
						{
							GL11.glPushMatrix();
							if( k == disk[2] ) GL11.glRotatef( diskAngle, 0.0f, 0.0f, 1.0f );
							GL11.glTranslatef( i * blockSpacing - blockSpacing, j * blockSpacing - blockSpacing, k * blockSpacing - blockSpacing );
							if( renderWithTexture ) cubes[i][j][k].renderWithTexture();
							else cubes[i][j][k].render();
							GL11.glPopMatrix();
						}
		   } */
		//}//if end
		
		//Ingen rotation.
		
		/*
		else
		{
		    for( int i = 0; i < 3; i++ ) 
			{
				for( int j = 0; j < 3; j++ )
				{
					for( int k = 0; k < 3; k++ )
					{
						GL11.glPushMatrix();
						GL11.glTranslatef( i * blockSpacing - blockSpacing, j * blockSpacing - blockSpacing, k * blockSpacing - blockSpacing );
						if( renderWithTexture ) cubes[i][j][k].renderWithTexture();
						else cubes[i][j][k].render();
						GL11.glPopMatrix();
					}
				}
			}
		} */
		
		if( selectorBlockActive )
		{		
			GL11.glPushMatrix();
			
			GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE );
			GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE );
			
			if( selectorBlockState[0] >= 0 )
			{
				GL11.glScalef( 1.1f, 4.0f, 4.0f );
				GL11.glTranslatef( selectorBlockState[0] * blockSpacing - blockSpacing, 0.0f, 0.0f );
			}
			else if( selectorBlockState[1] >= 0 )
			{
				GL11.glScalef( 4.0f, 1.1f, 4.0f );
				GL11.glTranslatef( 0.0f, selectorBlockState[1] * blockSpacing - blockSpacing, 0.0f );
			}
			else if( selectorBlockState[2] >= 0 )
			{
				GL11.glScalef( 4.0f, 4.0f, 1.1f );
				GL11.glTranslatef( 0.0f, 0.0f, selectorBlockState[2] * blockSpacing - blockSpacing );
			}
			
			GL11.glLineWidth( 3.0f );
			
			selectorBlock.render(); 
			
			GL11.glLineWidth( 1.0f );
			
			GL11.glPopMatrix();
		}
	}
	
	public static void logic()
	{	
		//Roter view med mus
		cubeRotationX -= Mouse.getDY();
		cubeRotationY += Mouse.getDX();
		
		if( cubeRotationX > 90.0f ) cubeRotationX = 90.0f;
		if( cubeRotationX < -90.0f ) cubeRotationX = -90.0f;
		
		//Hvis der er en animation kørende, opdater da animationsvinklen
		if( animationRunning )
		{
			if( positiveRot == true )
			{
				if( randomizing ) diskAngle += 2 * diskSpeed;
				else diskAngle += 2 * diskSpeed;
			}
			else
			{
				if( randomizing ) diskAngle -= 2 * diskSpeed;
				else diskAngle -= 2 * diskSpeed;
			}
			
			//Hvis animationen er færdig, sluk den, og opdater matricerne så
			//de stemmer overens med den nye cube.
			if( Math.abs( diskAngle ) >= 90.0f )
			{
				diskAngle = 0.0f;
				
				//opdater bræt
				
				if( disk[0] >= 0 )
				{
					if( positiveRot == true ) rotate( 0, disk[0], true );
					else  rotate( 0, disk[0], false );
				}
				else if( disk[1] >= 0 )
				{
					if( positiveRot == true ) rotate( 1, disk[1], true );
					else  rotate( 1, disk[1], false );
				}
				else if( disk[2] >= 0 )
				{
					if( positiveRot == true ) rotate( 2, disk[2], true );
					else  rotate( 2, disk[2], false );
				}
				
				if( randomizing )
				{
					randomMove();
					animationRunning = true;
				}
				else
				{
					//opdater cube
					repaintCube();
					
				    animationRunning = false;
					disk[0] = -1;
					disk[1] = -1;
					disk[2] = -1;
				}
			}
		}
		
		if( Keyboard.isKeyDown( Keyboard.KEY_I ) )
		{
			selectorBlockState[0] =  1;
			selectorBlockState[1] = -1;
			selectorBlockState[2] = -1;
		}
		
		else if( Keyboard.isKeyDown( Keyboard.KEY_J ) )
		{

			selectorBlockState[0] = -1;
			selectorBlockState[1] =  1;
			selectorBlockState[2] = -1;
		}
		
		else if( Keyboard.isKeyDown( Keyboard.KEY_K ) )
		{
			selectorBlockState[0] = -1;
			selectorBlockState[1] = -1;
			selectorBlockState[2] =  1;
		}
		else if ( Keyboard.isKeyDown( Keyboard.KEY_LEFT ) && ( keyPressedLeft == false ) )
		{
			keyPressedLeft = true;
			
			for( int i = 0; i < selectorBlockState.length; i++ )
			{
				if( selectorBlockState[i] > 0 )
				{
					selectorBlockState[i]--;
				}
			}
		}
		else if ( Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) && ( keyPressedRight == false ) )
		{
			keyPressedRight = true;
			
			for( int i = 0; i < selectorBlockState.length; i++ )
			{
				if( selectorBlockState[i] >= 0 && selectorBlockState[i] < 2 )
				{
					selectorBlockState[i]++;
				}
			}
		}
		else if( (!animationRunning) && Keyboard.isKeyDown( Keyboard.KEY_UP ) )
		{
			//start positiv animation
			animationRunning = true;
			positiveRot      = true;
			
			disk[0] = selectorBlockState[0];
			disk[1] = selectorBlockState[1];
			disk[2] = selectorBlockState[2];
		}
		else if( (!animationRunning) && Keyboard.isKeyDown( Keyboard.KEY_DOWN ) )
		{
			//start positiv animation
			animationRunning = true;
			positiveRot      = false;
			
			disk[0] = selectorBlockState[0];
			disk[1] = selectorBlockState[1];
			disk[2] = selectorBlockState[2]; 
		}
		else if( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) )
		{
			shutdown();
		}
		else if( (!keyPressedH) && Keyboard.isKeyDown( Keyboard.KEY_H ) )
		{
			keyPressedH = true;
			
			if( selectorBlockActive == true ) selectorBlockActive = false;
			else selectorBlockActive = true;
		}
		else if( (!keyPressedR) && Keyboard.isKeyDown( Keyboard.KEY_R ) )
		{
			keyPressedR = true;
			
			if( animationRunning == false )
			{
				randomMove();
				animationRunning = true;
			}
			
			if( randomizing == true ) randomizing = false;
			else randomizing = true;
		}
		
		if( ( !Keyboard.isKeyDown( Keyboard.KEY_R) ) )
		{
			keyPressedR = false;
		}
		
		if( ( ! Keyboard.isKeyDown( Keyboard.KEY_H) ) )
		{
			keyPressedH = false;
		}
		
		if( ( ! Keyboard.isKeyDown( Keyboard.KEY_LEFT ) ) )
		{
			keyPressedLeft = false;
		}
		
		if( ( ! Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) ) )
		{
			keyPressedRight = false;
		}
	}
	
	public static void randomMove()
	{
		Random rand = new Random();
		
		int direction = rand.nextInt( 2 );
		int axis      = rand.nextInt( 3 );
		int dis       = rand.nextInt( 3 );
		
		if( direction >= 5 ) positiveRot = true;
		else positiveRot = false;
		
		for( int i = 0; i < 3; i++ )
		{
			if( axis == i )
			{
				disk[i] = dis;
			} 
			else
			{
				disk[i] = -1;
			}
		}
	}
	
	/*********************************************************
	 * rotate.
	 * 
	 * axis: 0 = I, 1 = J, 2 = K
	 * disk: 0-2.
	 * 
	 * @param axis
	 * @param disk
	 * @param clockwise
	 */
	public static void rotate( int axis, int disk, boolean positiveRotation )
	{
		switch( axis )
		{
			//I-rotationer
			case 0:
			{
				int[] temp = new int[3];
				
				if( positiveRotation )
				{
					temp[0] = sides[3][0][disk];
					temp[1] = sides[3][1][disk];
					temp[2] = sides[3][2][disk];
					
					sides[3][0][disk] = sides[0][0][disk];
					sides[3][1][disk] = sides[0][1][disk];
					sides[3][2][disk] = sides[0][2][disk];
					
					sides[0][0][disk] = sides[1][0][disk];
					sides[0][1][disk] = sides[1][1][disk];
					sides[0][2][disk] = sides[1][2][disk];
					
					sides[1][0][disk] = sides[2][0][disk];
					sides[1][1][disk] = sides[2][1][disk];
					sides[1][2][disk] = sides[2][2][disk];
					
					sides[2][0][disk] = temp[0];
					sides[2][1][disk] = temp[1];
					sides[2][2][disk] = temp[2];
					
					if( disk == 0 )
					{
						rotateSide( 5, true );
					}
					else if( disk == 2 )
					{
						rotateSide( 4, false );
					}
				}
				else
				{
					temp[0] = sides[3][0][disk];
					temp[1] = sides[3][1][disk];
					temp[2] = sides[3][2][disk];
					
					sides[3][0][disk] = sides[2][0][disk];
					sides[3][1][disk] = sides[2][1][disk];
					sides[3][2][disk] = sides[2][2][disk];
					
					sides[2][0][disk] = sides[1][0][disk];
					sides[2][1][disk] = sides[1][1][disk];
					sides[2][2][disk] = sides[1][2][disk];
					
					sides[1][0][disk] = sides[0][0][disk];
					sides[1][1][disk] = sides[0][1][disk];
					sides[1][2][disk] = sides[0][2][disk];
					
					sides[0][0][disk] = temp[0];
					sides[0][1][disk] = temp[1];
					sides[0][2][disk] = temp[2];
					
					if( disk == 0 )
					{
						rotateSide( 5, false );
					}
					else if( disk == 2 )
					{
						rotateSide( 4, true );
					}
				}		
				break;
			}
			//J-rotationer
			case 1:
			{
				int[] temp = new int[3];
				
				if( positiveRotation )
				{
					temp[0] = sides[5][2-disk][0];
					temp[1] = sides[5][2-disk][1];
					temp[2] = sides[5][2-disk][2];
					
					sides[5][2-disk][0] = sides[2][disk][2];
					sides[5][2-disk][1] = sides[2][disk][1];
					sides[5][2-disk][2] = sides[2][disk][0];
					
					sides[2][disk][0] = sides[4][2-disk][2];
					sides[2][disk][1] = sides[4][2-disk][1];
					sides[2][disk][2] = sides[4][2-disk][0];
					
					sides[4][2-disk][0] = sides[0][2-disk][0];
					sides[4][2-disk][1] = sides[0][2-disk][1];
					sides[4][2-disk][2] = sides[0][2-disk][2];
					
					sides[0][2-disk][0] = temp[0];
					sides[0][2-disk][1] = temp[1];
					sides[0][2-disk][2] = temp[2];
					
					if( disk == 0 )
					{
						rotateSide( 3, true );
					}
					else if( disk == 2 )
					{
						rotateSide( 1, false );
					}
				}
				else
				{
					temp[0] = sides[5][2-disk][0];
					temp[1] = sides[5][2-disk][1];
					temp[2] = sides[5][2-disk][2];
					
					sides[5][2-disk][0] = sides[0][2-disk][0];
					sides[5][2-disk][1] = sides[0][2-disk][1];
					sides[5][2-disk][2] = sides[0][2-disk][2];
					
					sides[0][2-disk][0] = sides[4][2-disk][0];
					sides[0][2-disk][1] = sides[4][2-disk][1];
					sides[0][2-disk][2] = sides[4][2-disk][2];
					
					sides[4][2-disk][0] = sides[2][disk][2];
					sides[4][2-disk][1] = sides[2][disk][1];
					sides[4][2-disk][2] = sides[2][disk][0];
					
					sides[2][disk][0] = temp[2];
					sides[2][disk][1] = temp[1];
					sides[2][disk][2] = temp[0];
					
					if( disk == 0 )
					{
						rotateSide( 3, false );
					}
					else if( disk == 2 )
					{
						rotateSide( 1, true );
					}
				}
				
				break;
			}
			//K-rotationer
			case 2:
			{
				int[] temp = new int[3];
				
				if( positiveRotation )
				{
					temp[0] = sides[1][disk][0];
					temp[1] = sides[1][disk][1];
					temp[2] = sides[1][disk][2];
					
					sides[1][disk][0] = sides[4][0][2-disk];
					sides[1][disk][1] = sides[4][1][2-disk];
					sides[1][disk][2] = sides[4][2][2-disk];
					
					sides[4][0][2-disk] = sides[3][2-disk][2];
					sides[4][1][2-disk] = sides[3][2-disk][1];
					sides[4][2][2-disk] = sides[3][2-disk][0];
					
					sides[3][2-disk][0] = sides[5][0][disk];
					sides[3][2-disk][1] = sides[5][1][disk];
					sides[3][2-disk][2] = sides[5][2][disk];
					
					sides[5][0][disk] = temp[2];
					sides[5][1][disk] = temp[1];
					sides[5][2][disk] = temp[0];
					
					if( disk == 0 )
					{
						rotateSide( 2, true );
					}
					else if( disk == 2 )
					{
						rotateSide( 0, false );
					}
				}
				else
				{
					temp[0] = sides[1][disk][0];
					temp[1] = sides[1][disk][1];
					temp[2] = sides[1][disk][2];
					
					sides[1][disk][0] = sides[5][2][disk];
					sides[1][disk][1] = sides[5][1][disk];
					sides[1][disk][2] = sides[5][0][disk];
					
					sides[5][0][disk] = sides[3][2-disk][0];
					sides[5][1][disk] = sides[3][2-disk][1];
					sides[5][2][disk] = sides[3][2-disk][2];
					
					sides[3][2-disk][0] = sides[4][2][2-disk];
					sides[3][2-disk][1] = sides[4][1][2-disk];
					sides[3][2-disk][2] = sides[4][0][2-disk];
					
					sides[4][0][2-disk] = temp[0];
					sides[4][1][2-disk] = temp[1];
					sides[4][2][2-disk] = temp[2];
					
					if( disk == 0 )
					{
						rotateSide( 2, false );
					}
					else if( disk == 2 )
					{
						rotateSide( 0, true );
					}
				}
				
				break;
			}
		}//switch end
		
		repaintCube();
	}
	
	/*********************************************************
	 * rotateSide.
	 * 
	 * Roter en enkelt side ( 0-5 )
	 * 
	 * @param side
	 * @param clockWise
	 */
	public static void rotateSide( int side, boolean clockWise )
	{
		int[][] temp = new int[3][3];
		
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
			{
				temp[i][j] = sides[side][i][j];
			}
		
		if( clockWise == true )
		{
			for( int i = 0; i < 3; i++ )
				for( int j = 0; j < 3; j++ )
				{
					sides[side][i][j] = temp[2-j][i];
				}
		}
		else
		{
			for( int i = 0; i < 3; i++ )
				for( int j = 0; j < 3; j++ )
				{
					sides[side][i][j] = temp[j][2-i];
				}
		}
	}
	
	/*
	 * initialize openGL
	 */
	public static void initialize()
	{
		try
		{
			Display.setTitle( "Cuber Game" );
			Display.setFullscreen( true );
			Display.setVSyncEnabled( true );
			Display.create();
			
			Mouse.setGrabbed( true );
		}
		catch( Exception error )
		{
			System.out.println( "Could not create OpenGL device." );
		}
		
		//OpenGL initalisering.
		GL11.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		
		GL11.glMatrixMode (GL11.GL_PROJECTION);    /* prepare for and then */ 
		GL11.glLoadIdentity ();               /* define the projection */
		
		float ratio = (float)Display.getDisplayMode().getWidth() / (float)Display.getDisplayMode().getHeight();
		
		GL11.glFrustum ( -ratio , ratio, -1.0f, 1.0f, /* transformation */
	                   1.0, 200.0); 
		GL11.glMatrixMode (GL11.GL_MODELVIEW);  /* back to modelview matrix */
		GL11.glViewport (0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight() );     /* define the viewport */
	
		
		//enable textures
		GL11.glEnable( GL11.GL_TEXTURE_2D );
		
		for( int i = 0; i < 7; i++ )
		{
			textures.add( loadTexture( "side" + i + ".png" ) );
		}
		
		textures.add( loadTexture( "info2.png" ) );
		
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
				for( int k = 0; k < 3; k++ )
				{
					cubes[i][j][k].setTextures( textures );
				}
		
	}//initialize end
	
	public static void run()
	{
		while( running )
		{
			Display.update();
			
			if( Display.isCloseRequested() )
			{
				running = false;
			}//if end
			else if( Display.isActive() )
			{
				logic();
				render();
			}//if end
			else
			{
				try
				{
					Thread.sleep( 100 );
				}
				catch( Exception error )
				{
					
				}
				logic();
			}//else end
			
			if( Display.isVisible() || Display.isDirty() )
			{
				render();
			}
		}//while end
		
	}//run end
	
	/**
	 * Loads the named texture from the classpath
	 * 
	 * @param name Name of texture to load
	 * @param flip Whether to flip image
	 * @return Loaded texture or null
	 */
	public static TextureHandle loadTexture(String name ) 
	{
	  TextureHandle texture = null;
	  ByteBuffer imageData = null;
	  int ilImageHandle;
	  int oglImageHandle;
	  IntBuffer scratch = BufferUtils.createIntBuffer(1);
	 
	  try
	  {
		  IL.create();
		  ILU.create();
	  }
	  catch( Exception e) 
	  {
		  System.out.println( "Could not create Il and ILU." );
	  }
	  
	  // create image in DevIL and bind it
	  IL.ilGenImages(scratch);
	  IL.ilBindImage(scratch.get(0));
	  ilImageHandle = scratch.get(0);
		
	  try
	  {
		  // load the image
		  if(!IL.ilLoadFromURL(IL.class.getClassLoader().getResource(name))) 
		  {
		    return null;
		  }
	  }
	  catch( Exception e ) {}
	 
	  // convert image to RGBA
	  IL.ilConvertImage(IL.IL_RGBA, IL.IL_BYTE);
	 
	    ILU.iluFlipImage();
	 
	  // get image attributes
	  int width = IL.ilGetInteger(IL.IL_IMAGE_WIDTH);
	  int height = IL.ilGetInteger(IL.IL_IMAGE_HEIGHT);

	    imageData = IL.ilGetData();
	 
	  // create OpenGL counterpart
	  GL11.glGenTextures(scratch);
	  GL11.glBindTexture(GL11.GL_TEXTURE_2D, scratch.get(0));
	  oglImageHandle = scratch.get(0);
	  
	  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	  GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 
	                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
	 
	    texture = new TextureHandle( oglImageHandle, width, height );
	 
	  // delete Image in DevIL
	  scratch.put(0, ilImageHandle);
	  IL.ilDeleteImages(scratch);
	  
	  // revert the gl state back to the default so that accidental texture binding doesn't occur
	  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	 
	  // return OpenGL texture handle
	  return texture;
	}
	
}//class Cuber end
