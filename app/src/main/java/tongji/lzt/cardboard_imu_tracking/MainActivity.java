package tongji.lzt.cardboard_imu_tracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private boolean isStop=false;
    private boolean isIMUInit=true;
    private int initConut=0;

    private static float[] viewMatrix=new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f};
    private float[][] initViewMatrix=new float[][]{
            {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f},
            {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f},
            {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f},
            {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f}};

    static {
        System.loadLibrary("native-lib");
    }

    private static long nativeApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersiveSticky();
        setContentView(R.layout.activity_main);
        nativeApp = createNativeApp();
        viewMatrix=new float[]{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,1.0f};
        initConut=0;
        // Example of a call to a native method
        new Thread() {
            @Override
            public void run() {
                super.run();
                //定时器
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!isStop){
                                    float[] trans = getTransMatrix(nativeApp);
                                    if(!isIMUInit){
                                        if(trans[0]!=initViewMatrix[0][0] && trans[0]!=initViewMatrix[1][0] && trans[0]!=initViewMatrix[2][0] && trans[0]!=initViewMatrix[3][0]){
                                            viewMatrix = trans;
                                        }
                                    }else {
                                        initViewMatrix[initConut]=trans;
//                                    viewMatrix = trans;
                                        initConut=initConut+1;
                                        if(initConut==3){
                                            isIMUInit=false;
                                        }
                                    }
//                                    System.out.println(Arrays.toString(viewMatrix));
                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 1000, 20);//3秒后每隔一秒刷新一次
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "====Activity onPause.");
            nativePause(nativeApp);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "====Activity onResume.");
        nativeResume(nativeApp);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "====Activity onDestroy");
        super.onDestroy();

    }

    private void setImmersiveSticky() {
        getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Prevents screen from dimming/locking.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native long createNativeApp();
    public native void nativePause(long nativePtr);
    public native void nativeResume(long nativePtr);
    public native void nativeStop(long nativePtr);
    public native float[] getValue(long nativePtr);
    public static native float[] getTransMatrix(long nativePtr);

    public void start(View view) {
        nativeResume(nativeApp);
        isStop=false;
    }

    public void stop(View view) {
        nativePause(nativeApp);
        isStop=true;
    }

    public static float[] getViewMatrix(){
        return viewMatrix;
    }
}