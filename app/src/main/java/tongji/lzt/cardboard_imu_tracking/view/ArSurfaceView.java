package tongji.lzt.cardboard_imu_tracking.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import tongji.lzt.cardboard_imu_tracking.opengl.ArRenderer;


public class ArSurfaceView extends GLSurfaceView {

    ArRenderer arRenderer;

    public ArSurfaceView(Context context) {
        super(context);
    }

    public ArSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        arRenderer = new ArRenderer(getContext());
        setRenderer(arRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    public ArRenderer getArRenderer(){
        return arRenderer;
    }
}
