package com.samuelberrien.odyspace.main.infos;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.Purchases;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 12/10/17.
 */

public class Item3DView extends GLSurfaceView implements GLSurfaceView.Renderer {

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private float[] mItemModelMatrix = new float[16];
	private float angle;

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	private String objFileName;
	private String mtlFileName;
	private Context context;

	private ObjModelMtlVBO objModelMtlVBO;

	private boolean willCreateObj;

	public Item3DView(Context context, Purchases purchases, String name) {
		super(context);
		this.context = context;
		angle = (float) (Math.random() * 360d);

		changeObj(purchases, name);

		setEGLContextClientVersion(2);
		setRenderer(this);
	}

	public void changeObj(Purchases purchases, String name) {
		//TODO faire truc propre string.xml avec list des noms de fichier obj et mtl (biens indéxés)
		switch (purchases) {
			case SHIP:
				if (name.equals(context.getString(R.string.ship_simple))) {
					objFileName = "ship_3.obj";
					mtlFileName = "ship_3.mtl";
				} else if (name.equals(context.getString(R.string.ship_bird))) {
					objFileName = "ship_bird.obj";
					mtlFileName = "ship_bird.mtl";
				} else if (name.equals(context.getString(R.string.ship_supreme))) {
					objFileName = "ship_supreme.obj";
					mtlFileName = "ship_supreme.mtl";
				} else {
					objFileName = "ship_3.obj";
					mtlFileName = "ship_3.mtl";
				}
				break;
			case FIRE:
				if (name.equals(context.getString(R.string.fire_1))) {
					objFileName = "rocket.obj";
					mtlFileName = "rocket.mtl";
				} else if (name.equals(context.getString(R.string.fire_2))) {
					objFileName = "quint_fire.obj";
					mtlFileName = "quint_fire.mtl";
				} else if (name.equals(context.getString(R.string.fire_3))) {
					objFileName = "bomb.obj";
					mtlFileName = "bomb.mtl";
				} else if (name.equals(context.getString(R.string.fire_4))) {
					objFileName = "triple_fire.obj";
					mtlFileName = "triple_fire.mtl";
				} else if (name.equals(context.getString(R.string.fire_5))) {
					objFileName = "laser_item_menu.obj";
					mtlFileName = "laser_item_menu.mtl";
				} else if (name.equals(context.getString(R.string.fire_6))) {
					objFileName = "torus.obj";
					mtlFileName = "torus.mtl";
				} else {
					objFileName = "rocket.obj";
					mtlFileName = "rocket.mtl";
				}
				break;
			case BONUS:
				//TODO item bonus en 3D
				if (name.equals(context.getString(R.string.bonus_1))) {
					objFileName = "arrow_speed.obj";
					mtlFileName = "arrow_speed.mtl";
				} else if (name.equals(context.getString(R.string.bought_duration))) {
					objFileName = "clock.obj";
					mtlFileName = "clock.mtl";
				} else if (name.equals(context.getString(R.string.bonus_2))) {
					objFileName = "arrow_speed.obj";
					mtlFileName = "arrow_speed.mtl";
				} else {
					objFileName = "arrow_speed.obj";
					mtlFileName = "arrow_speed.mtl";
				}
				break;
			default:
				objFileName = "ship_3.obj";
				mtlFileName = "ship_3.mtl";
				break;
		}
		willCreateObj = true;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

		if (willCreateObj) {
			objModelMtlVBO = new ObjModelMtlVBO(context, objFileName, mtlFileName, 1, 0, false);
			willCreateObj = false;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 50f);
		Matrix.perspectiveM(mProjectionMatrix, 0, 50f, ratio, 1, 50f);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		if (willCreateObj) {
			objModelMtlVBO = new ObjModelMtlVBO(context, objFileName, mtlFileName, 1, 0, false);
			willCreateObj = false;
		}

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -4f, 0f, 0f, 0f, 0f, 1f, 0f);

		Matrix.setRotateM(mItemModelMatrix, 0, angle++, 0f, 1f, 0f);

		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mItemModelMatrix, 0);
		float[] mvpMatrix = new float[16];
		Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

		updateLight(new float[]{0f, 0f, -4f});

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		objModelMtlVBO.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, new float[]{0f, 0f, -4f});
	}

	private void updateLight(float[] pos) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, pos[0], pos[1], pos[2]);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	/*public static Item3DWindow makeFireView(Context context, String currFireType) {
		if (currFireType.equals(context.getString(R.string.fire_1))) {
			return new Item3DWindow(context, "rocket.obj", "rocket.mtl");
		} else if (currFireType.equals(context.getString(R.string.fire_2))) {
			return new Item3DWindow(context, "quint_fire.obj", "quint_fire.mtl");
		} else if (currFireType.equals(context.getString(R.string.fire_3))) {
			return new Item3DWindow(context, "bomb.obj", "bomb.mtl");
		} else if (currFireType.equals(context.getString(R.string.fire_4))) {
			return new Item3DWindow(context, "triple_fire.obj", "triple_fire.mtl");
		} else if (currFireType.equals(context.getString(R.string.fire_5))) {
			return new Item3DWindow(context, "laser.obj", "laser.mtl");
		} else if (currFireType.equals(context.getString(R.string.fire_6))) {
			return new Item3DWindow(context, "torus.obj", "torus.mtl");
		} else {
			return new Item3DWindow(context, "rocket.obj", "rocket.mtl");
		}
	}*/

	/*public static Item3DWindow makeShipView(Context context, String shipUsed) {
		if (shipUsed.equals(context.getString(R.string.ship_simple))) {
			return new Item3DWindow(context, "ship_3.obj", "ship_3.mtl");
		} else if (shipUsed.equals(context.getString(R.string.ship_bird))) {
			return new Item3DWindow(context, "ship_bird.obj", "ship_bird.mtl");
		} else if (shipUsed.equals(context.getString(R.string.ship_supreme))) {
			return new Item3DWindow(context, "ship_supreme.obj", "ship_supreme.mtl");
		} else {
			return new Item3DWindow(context, "ship_3.obj", "ship_3.mtl");
		}
	}*/
}
