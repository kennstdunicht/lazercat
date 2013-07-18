package com.mandi.spacecat;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.algorithm.Spiral;

public class MainActivity extends SimpleBaseGameActivity
{
	
	private Camera camera;
	private BitmapTextureAtlas mTexSpaceCat;
	private BitmapTextureAtlas mTexOtherStuff;
	private BitmapTextureAtlas mTexBackground;
	
	private ITextureRegion mTRSpacecat;
	private ITextureRegion mTREmpty;
	private ITextureRegion mTRBackground;
	private ITextureRegion mTRStarfield;
	
	private static final int CAMERA_WIDTH = 768;
	private static final int CAMERA_HEIGHT = 1280;

	private Sprite mSpaceCat;

    Entity mSpaceCatLayer = new Entity();
    Entity mBackgroundLayer = new Entity();
	
	@Override
	public EngineOptions onCreateEngineOptions() 
	{
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		new FillResolutionPolicy(), camera);
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
		mTexOtherStuff = new BitmapTextureAtlas(getTextureManager(), 512, 512, TextureOptions.DEFAULT);
		mTexBackground = new BitmapTextureAtlas(getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		
		mTRSpacecat = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexSpaceCat, this, "space_cat.png", 0, 0);
		mTREmpty = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexOtherStuff, this, "empty.png", 0, 0);
		
		mTRBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexBackground, this, "background.png", 0, 0);
		mTRStarfield = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexBackground, this, "starfield.png", 769, 0);
		
		mTexSpaceCat.load(); 
		mTexOtherStuff.load();
		mTexBackground.load();
	}

	private final int LAYER0 = 0;
	
	private final float kSPRITESCALEW = 2f;
	private final float kSPRITESCALEH = 3f;
	
	private final float kCATSPEED = 500f;
	private final float kBGSPEED  = 80f;
	
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
	    
	    // cat
	    
	    mSpaceCat = new Sprite((CAMERA_WIDTH - mTRSpacecat.getWidth()) / 2, 
	    		CAMERA_HEIGHT - mTRSpacecat.getHeight(), mTRSpacecat, getVertexBufferObjectManager());
	    scene.getChildByIndex(LAYER0).attachChild(mSpaceCat);
	    
	    
	    Sprite emptyLeft = new Sprite(
	    		0, 
	    		CAMERA_HEIGHT-mTREmpty.getHeight() * kSPRITESCALEH, 
	    		mTREmpty.getWidth() * kSPRITESCALEW, 
	    		mTREmpty.getHeight() * kSPRITESCALEH, 
	    		mTREmpty, 
	    		getVertexBufferObjectManager())
	    {
	    	boolean steerLeft;
	    	
	    	@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) 
	    	{		
	    		if (pSceneTouchEvent.isActionDown())
	    		{
	    			steerLeft = true;
	    		}
	    		else if (pSceneTouchEvent.isActionUp())
	    		{
	    			steerLeft = false;
	    		}
	    		
	    		// TODO Auto-generated method stub
	    		return true;
	    	}
	    	
	    	@Override
	    	protected void onManagedUpdate(float pSecondsElapsed) 
	    	{
	    		if (steerLeft)
	    		{
	    			mSpaceCat.setPosition(mSpaceCat.getX() - kCATSPEED * pSecondsElapsed, mSpaceCat.getY());	
	    			if (mSpaceCat.getX() <= 0)
	    			{
	    				mSpaceCat.setX(0);
	    			}
	    		}
	    		
	    		// TODO Auto-generated method stub
	    		super.onManagedUpdate(pSecondsElapsed);
	    	}
	    };
	    
	    Sprite emptyRight = new Sprite(
	    		CAMERA_WIDTH - mTREmpty.getWidth() * kSPRITESCALEW, 
	    		CAMERA_HEIGHT-mTREmpty.getHeight() * kSPRITESCALEH, 
	    		mTREmpty.getWidth() * kSPRITESCALEW, 
	    		mTREmpty.getHeight() * kSPRITESCALEH, 
	    		mTREmpty, 
	    		getVertexBufferObjectManager())
	    {
	    	boolean steerRight;
	    	
	    	@Override
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
	    			float pTouchAreaLocalX, float pTouchAreaLocalY) 
	    	{
	    		if (pSceneTouchEvent.isActionDown())
	    		{
	    			System.out.println("isActionDown");
	    			steerRight = true;
	    		}
	    		else if (pSceneTouchEvent.isActionUp())
	    		{
	    			System.out.println("isActionUp");
	    			steerRight = false;
	    			
	    		}
	    		
	    		// TODO Auto-generated method stub
	    		return true;
	    	}
	    	
	    	@Override
	    	protected void onManagedUpdate(float pSecondsElapsed) 
	    	{
	    		// TODO Auto-generated method stub
	    		super.onManagedUpdate(pSecondsElapsed);
	    		
	    		if (steerRight)
	    		{
	    			mSpaceCat.setPosition(mSpaceCat.getX() + kCATSPEED * pSecondsElapsed, mSpaceCat.getY());
	    			
	    			if (mSpaceCat.getX() >= CAMERA_WIDTH - mTRSpacecat.getWidth())
	    			{
	    				mSpaceCat.setX(CAMERA_WIDTH - mTRSpacecat.getWidth());
	    			}
	    		}
	    	}
	    };
	    
	    
	    scene.registerTouchArea(emptyLeft);
	    scene.registerTouchArea(emptyRight);
	    scene.setTouchAreaBindingOnActionDownEnabled(true);
	    
	    mSpaceCatLayer.attachChild(emptyLeft);
	    mSpaceCatLayer.attachChild(emptyRight);
	    
	    return scene;
	}
	
}
