package tongji.lzt.cardboard_imu_tracking.opengl;

import android.content.Context;
import android.opengl.GLES30;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class OpenGLUtil {
    private static final String TAG = "OpenGLUtil";

    public static String loadShaderFromAssetsFile(Context context, String filename) {
        String result = null;
        try {
            InputStream in = context.getResources().getAssets().open(filename);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int loadGlslProgram(String vertexSrc, String fragSrc) {
        //创建顶点着色器
        int vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        //加载着色器程序
        GLES30.glShaderSource(vertexShader, vertexSrc);
        //编译配置
        GLES30.glCompileShader(vertexShader);
        //检查配置是否成功,检查着色器程序编写错误,
        int[] status = new int[1];
        GLES30.glGetShaderiv(vertexShader, GLES30.GL_COMPILE_STATUS, status, 0);
//        if (status[0] != GLES30.GL_TRUE) {
//            throw new IllegalStateException("load vertex shader :" +
//                    GLES30.glGetShaderInfoLog(vertexShader));
//        }

        //创建片元着色器,
        int fragShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        //加载着色器代码,
        GLES30.glShaderSource(fragShader, fragSrc);
        //编译配置,
        GLES30.glCompileShader(fragShader);
        //查看配置是否成功,检查着色器程序编写错误,
        GLES30.glGetShaderiv(fragShader, GLES30.GL_COMPILE_STATUS, status, 0);
//        if (status[0] != GLES30.GL_TRUE) {
//            throw new IllegalStateException("load fragment shader :" +
//                    GLES30.glGetShaderInfoLog(vertexShader));
//        }
        //创建着色器程序
        int program = GLES30.glCreateProgram();
        //绑定顶点和片元
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragShader);
        //链接着色器程序
        GLES30.glLinkProgram(program);

        //检查链接状态
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
//        if (status[0] != GLES30.GL_TRUE) {
//            throw new IllegalStateException("link program :" +
//                    GLES30.glGetShaderInfoLog(vertexShader));
//        }

        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragShader);
        return program;
    }

    public static float[] imgPoints2glPoints(int[] p, int width, int height) {
        float[] points = new float[3 * p.length / 2];
        for (int i = 0; i<p.length / 2; i++) {
            points[3 * i] = 2f * p[2 * i] / (float) width - 1f;
            points[3 * i + 1] = 1f - 2f * p[2 * i + 1] / (float) height;
            points[3 * i + 2] = 0.0f;
        }
        return points;
    }

}
