package tongji.lzt.cardboard_imu_tracking.opengl;

import android.content.Context;

public class ARBaseRenderer {

    private ArRenderer arRenderer;

    protected int SCR_WIDTH = 1520;
    protected int SCR_HEIGHT = 720;

    protected int lanePointsNum=10;

    protected ARBaseRenderer(Context context, ArRenderer arRenderer){
        this.arRenderer=arRenderer;
    }

    private float[] defaultViewArray = new float[]{
            0.9997561f, -0.004600341f, -0.021601258f, 0.0f,
            0.0052788518f, 0.9994911f, 0.031459488f, 0.0f,
            0.021445541f, -0.031565845f, 0.99927157f, 0.0f,
            -0.55795634f, -1.5917799f, -6.1064529f, 1.0f
    };

    protected float[] getViewArray() {
        return this.arRenderer.getViewFloatArray();
    }

    protected float[] getDefaultViewArray() {
        return defaultViewArray;
    }

    protected float[] getProjectionArray() {
        return  this.arRenderer.getProjectionFloatArray();
    }

}
