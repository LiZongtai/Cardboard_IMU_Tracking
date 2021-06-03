package tongji.lzt.cardboard_imu_tracking.opengl.renders;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import glm_.glm;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;
import tongji.lzt.cardboard_imu_tracking.opengl.ARBaseRenderer;
import tongji.lzt.cardboard_imu_tracking.opengl.ArRenderer;
import tongji.lzt.cardboard_imu_tracking.opengl.OpenGLUtil;

public class TurnRenderer extends ARBaseRenderer {

    final static glm_.glm glm = glm_.glm.INSTANCE;

    private final int mProgram;
    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indicesBuffer;
    private static final int VERTEX_POSITION_SIZE = 3;

    private Mat4 model;
    private Mat4 rotation;

    private float turnXpos = 0.0f;
    private float turnRightXpos = 1.0f;
    private float turnLeftXpos = -1.0f;
    boolean turnAnimationEnd = false;

    private float[] vertexPoints = {
            // positions
            0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            1.f, -0.5f, 0.0f,
            -1.f, -0.5f, 0.0f,
    };
    private short[] indices = {
            0, 1, 3,
            0, 1, 2,
    };

    public TurnRenderer(Context context, ArRenderer arRenderer) {
        super(context, arRenderer);
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        indicesBuffer = ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的数据
        indicesBuffer.put(indices);
        indicesBuffer.position(0);

        String vertexSharder = OpenGLUtil.loadShaderFromAssetsFile(context, "shaders/turn_vert.glsl");
        String fragSharder = OpenGLUtil.loadShaderFromAssetsFile(context, "shaders/turn_frag.glsl");
        mProgram = OpenGLUtil.loadGlslProgram(vertexSharder, fragSharder);
    }

    public void draw(float dist, int type) {
        GLES30.glUseProgram(mProgram);
        // position attribute
        GLES30.glVertexAttribPointer(0, VERTEX_POSITION_SIZE, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        //启用位置顶点属性
        GLES30.glEnableVertexAttribArray(0);

        float turnDirection = 0.0f;
        if (type == 1) {
            turnXpos = turnLeftXpos;
            turnDirection = 90.0f;
        } else if (type == 2) {
            turnXpos = turnRightXpos;
            turnDirection = -90.0f;
        }
        for (int j = 0; j < 5; j++) {
            Vec3 transPos = new Vec3(0.0f + turnXpos * j,
                    3.0f,
                    -dist - 5.f * j);
            model = new Mat4(1.0f); // make sure to initialize matrix to identity matrix first
            //model = glm::scale(model, glm::vec3(2.f , 1.f, 1.f));
            model = model.translate(transPos);
            model = model.rotate(glm.radians(turnDirection), new Vec3(0.0f, 0.0f, 1.0f));
            Mat4 rotation=new Mat4(1.0f);
//            rotation=new Mat4(getRotationArray());
//            rotation=rotation.rotate(glm.radians(getTurnAngle()),new  Vec3(0.0f, 1.0f, 0.0f));
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(mProgram, "model"), 1, false, model.toFloatArray(), 0);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(mProgram, "rotation"), 1, false, rotation.toFloatArray(), 0);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(mProgram, "view"), 1, false, getViewArray(), 0);
            GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(mProgram, "projection"), 1, false, getProjectionArray(), 0);
            GLES30.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);

        }

    }
}
