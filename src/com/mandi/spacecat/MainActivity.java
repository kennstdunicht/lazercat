package com.mandi.spacecat;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class MainActivity extends SimpleBaseGameActivity
{
	private Camera camera;
	private BitmapTextureAtlas mTexSpaceCat;
	private BitmapTextureAtlas mTexBackground;
	
	private ITextureRegion mTRSpacecat;
	private ITextureRegion mTRBackground;
	private ITextureRegion mTRStarfield;
	
	private static final int CAMERA_WIDTH = 768;
	private static final int CAMERA_HEIGHT = 1280;
	
	private final float kCATSPEED = 500f;
	private final float kBGSPEED  = 80f;

	private Sprite mSpaceCat;
	
	private boolean mSteerRightPressed;
	private boolean mSteerLeftPressed;
	
    Entity mSpaceCatLayer = new Entity();
    Entity mBackgroundLayer = new Entity();
	
	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		new FillResolutionPolicy(), camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}

	@Override
	protected void onCreateResources()
	{
		// TODO Auto-generated method stub
		loadGraphics();
	}
	
	protected void loadGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mTexSpaceCat = new BitmapTextureAtlas(getTextureManager(), 512, 512, TextureOptions.DEFAULT);
		mTexBackground = new BitmapTextureAtlas(getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		
		mTRSpacecat = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexSpaceCat, this, "space_cat.png", 0, 0);
		
		mTRBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexBackground, this, "background.png", 0, 0);
		mTRStarfield = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexBackground, this, "starfield.png", 769, 0);
		
		mTexSpaceCat.load(); 
		mTexBackground.load();
	}
	
	private void createBackgroundSprite(ITextureRegion bg, final float initialHeight, final float speed)
	{
	    // background
	    Sprite background = new Sprite(0, initialHeight, bg, getVertexBufferObjectManager())
	    {
	    	@Override
	    	protected void onManagedUpdate(float pSecondsElapsed) 
	    	{
	    		this.setY(this.getY() + speed * pSecondsElapsed);
	    		if (getY() >= CAMERA_HEIGHT) setY(-CAMERA_HEIGHT);
	    		super.onManagedUpdate(pSecondsElapsed);
	    	}
	    };
	    
	    mBackgroundLayer.attachChild(background);
	}
	
	@Override
	protected Scene onCreateScene() 
	{
		Scene scene = new Scene();
	    scene.setBackground(new Background(0.0f, 0.0f, 0.0f));
	   
	    scene.attachChild(mBackgroundLayer);
	    scene.attachChild(mSpaceCatLayer);
	    
	    createBackgroundSprite(mTRBackground, 0, kBGSPEED);
	    createBackgroundSprite(mTRBackground, -CAMERA_HEIGHT, kBGSPEED);
	    createBackgroundSprite(mTRStarfield, 0, kBGSPEED * 2);
	    createBackgroundSprite(mTRStarfield, -CAMERA_HEIGHT, kBGSPEED * 2);
	    
	    // create spacecat
	    mSpaceCat = new Sprite((CAMERA_WIDTH - mTRSpacecat.getWidth()) / 2, 
	    		CAMERA_HEIGHT - mTRSpacecat.getHeight(), mTRSpacecat, getVertexBufferObjectManager())
	    {
	    	@Override
	    	protected void onManagedUpdate(float pSecondsElapsed) 
	    	{
	    		//System.out.println("steerleft" + mSteerLeftPressed + "steerRight" + mSteerRightPressed);
	    		
	    		if (mSteerLeftPressed && !mSteerRightPressed)
	    		{
	    			mSpaceCat.setPosition(mSpaceCat.getX() - kCATSPEED * pSecondsElapsed, mSpaceCat.getY());	
	    			if (mSpaceCat.getX() <= 0)
	    			{
	    				mSpaceCat.setX(0);
	    			}
	    		}
	    		else if (mSteerRightPressed && !mSteerLeftPressed)
	    		{
	    			mSpaceCat.setPosition(mSpaceCat.getX() + kCATSPEED * pSecondsElapsed, mSpaceCat.getY());
	    			if (mSpaceCat.getX() >= CAMERA_WIDTH - mTRSpacecat.getWidth())
	    			{
	    				mSpaceCat.setX(CAMERA_WIDTH - mTRSpacecat.getWidth());
	    			}
	    		}
	    		// TODO Auto-generated method stub
	    		super.onManagedUpdate(pSecondsElapsed);
	    	}
	    };
	    mSpaceCatLayer.attachChild(mSpaceCat);
	    
	    Rectangle steerButtonLeft = new Rectangle(0, CAMERA_HEIGHT - 350, CAMERA_WIDTH / 2, 350, getVertexBufferObjectManager())
	    {
	    	@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) 
	    	{		
	    		if (pSceneTouchEvent.isActionDown())
	    		{
	    			mSteerLeftPressed = true;
	    		}
	    		else if (pSceneTouchEvent.isActionUp())
	    		{
	    			mSteerLeftPressed = false;
	    		}
	    		
	    		// TODO Auto-generated method stub
	    		return true;
	    	}
	    };
	    
	    Rectangle steerButtonRight = new Rectangle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - 350, CAMERA_WIDTH / 2, 350, getVertexBufferObjectManager())
	    {
	    	@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) 
	    	{
	    		if (pSceneTouchEvent.isActionDown())
	    		{
	    			mSteerRightPressed = true;
	    		}
	    		else if (pSceneTouchEvent.isActionUp())
	    		{
	    			System.out.println("isActionUp");
	    			mSteerRightPressed = false;
	    			
	    		}
	    		
	    		// TODO Auto-generated method stub
	    		return true;
	    	}
	    };
	    
	    
	    scene.registerTouchArea(steerButtonLeft);
	    scene.registerTouchArea(steerButtonRight);
	    scene.setTouchAreaBindingOnActionDownEnabled(true);
	
	    steerButtonLeft.setVisible(false);
	    steerButtonRight.setVisible(false);
	    mSpaceCatLayer.attachChild(steerButtonLeft);
	    mSpaceCatLayer.attachChild(steerButtonRight);
	    
	    return scene;
	}
	
}
