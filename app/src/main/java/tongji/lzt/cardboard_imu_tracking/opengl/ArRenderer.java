package tongji.lzt.cardboard_imu_tracking.opengl;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import glm_.mat4x4.Mat4;
import glm_.quat.Quat;
import tongji.lzt.cardboard_imu_tracking.opengl.renders.TurnRenderer;

public class ArRenderer implements GLSurfaceView.Renderer {

    final static glm_.glm glm = glm_.glm.INSTANCE;

    //    private GLSurfaceView drawView;
//    private Triangle mTriangle;
    private Context context;

    private TurnRenderer turnRenderer;

    protected int SCR_WIDTH = 1280;
    protected int SCR_HEIGHT = 720;

    private Mat4 view;
    private Mat4 projection;

    private float[] defaultViewArray = new float[]{
            0.9997561f, -0.004600341f, -0.021601258f, 0.0f,
            0.0052788518f, 0.9994911f, 0.031459488f, 0.0f,
            0.021445541f, -0.031565845f, 0.99927157f, 0.0f,
            -0.55795634f, -1.5917799f, -6.1064529f, 1.0f
    };

    public ArRenderer(Context context) {
        this.context = context;
        view = new Mat4(defaultViewArray);
        projection = glm.perspective(glm.radians(45.0f), (float) SCR_WIDTH / (float) SCR_HEIGHT, 0.01f, 1000.0f);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0, 0, 0);
        turnRenderer = new TurnRenderer(context, this);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        turnRenderer.draw(5.0f,1);
    }

    public Mat4 getViewMat4() {
        return view;
    }

    public float[] getViewFloatArray() {
        return view.toFloatArray();
    }

    public Mat4 getProjectionMat4() {
        return projection;
    }

    public float[] getProjectionFloatArray() {
        return projection.toFloatArray();
    }

}
