package com.laquysoft.bernini.rendering

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView

import com.google.ar.core.Frame
import com.google.ar.core.Session
import com.laquysoft.bernini.R

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * This class renders the AR background from camera feed. It creates and hosts the texture
 * given to ARCore to be filled with the camera image.
 */
class BackgroundRenderer {

    private var mQuadVertices: FloatBuffer? = null
    private var mQuadTexCoord: FloatBuffer? = null
    private var mQuadTexCoordTransformed: FloatBuffer? = null

    private var mQuadProgram: Int = 0

    private var mQuadPositionParam: Int = 0
    private var mQuadTexCoordParam: Int = 0
    var textureId = -1
        private set

    /**
     * Allocates and initializes OpenGL resources needed by the background renderer.  Must be
     * called on the OpenGL thread, typically in
     * [GLSurfaceView.Renderer.onSurfaceCreated].
     *
     * @param context Needed to access shader source.
     */
    fun createOnGlThread(context: Context) {
        // Generate the background texture.
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]
        val textureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES
        GLES20.glBindTexture(textureTarget, textureId)
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        val numVertices = 4
        if (numVertices != QUAD_COORDS.size / COORDS_PER_VERTEX) {
            throw RuntimeException("Unexpected number of vertices in BackgroundRenderer.")
        }

        val bbVertices = ByteBuffer.allocateDirect(QUAD_COORDS.size * FLOAT_SIZE)
        bbVertices.order(ByteOrder.nativeOrder())
        mQuadVertices = bbVertices.asFloatBuffer()
        mQuadVertices!!.put(QUAD_COORDS)
        mQuadVertices!!.position(0)

        val bbTexCoords = ByteBuffer.allocateDirect(
                numVertices * TEXCOORDS_PER_VERTEX * FLOAT_SIZE)
        bbTexCoords.order(ByteOrder.nativeOrder())
        mQuadTexCoord = bbTexCoords.asFloatBuffer()
        mQuadTexCoord!!.put(QUAD_TEXCOORDS)
        mQuadTexCoord!!.position(0)

        val bbTexCoordsTransformed = ByteBuffer.allocateDirect(
                numVertices * TEXCOORDS_PER_VERTEX * FLOAT_SIZE)
        bbTexCoordsTransformed.order(ByteOrder.nativeOrder())
        mQuadTexCoordTransformed = bbTexCoordsTransformed.asFloatBuffer()

        val vertexShader = ShaderUtil.loadGLShader(TAG, context,
                GLES20.GL_VERTEX_SHADER, R.raw.screenquad_vertex)
        val fragmentShader = ShaderUtil.loadGLShader(TAG, context,
                GLES20.GL_FRAGMENT_SHADER, R.raw.screenquad_fragment_oes)

        mQuadProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mQuadProgram, vertexShader)
        GLES20.glAttachShader(mQuadProgram, fragmentShader)
        GLES20.glLinkProgram(mQuadProgram)
        GLES20.glUseProgram(mQuadProgram)

        ShaderUtil.checkGLError(TAG, "Program creation")

        mQuadPositionParam = GLES20.glGetAttribLocation(mQuadProgram, "a_Position")
        mQuadTexCoordParam = GLES20.glGetAttribLocation(mQuadProgram, "a_TexCoord")

        ShaderUtil.checkGLError(TAG, "Program parameters")
    }

    /**
     * Draws the AR background image.  The image will be drawn such that virtual content rendered
     * with the matrices provided by [com.google.ar.core.Camera.getViewMatrix]
     * and [com.google.ar.core.Camera.getProjectionMatrix] will
     * accurately follow static physical objects.
     * This must be called **before** drawing virtual content.
     *
     * @param frame The last `Frame` returned by [Session.update].
     */
    fun draw(frame: Frame) {
        // If display rotation changed (also includes view size change), we need to re-query the uv
        // coordinates for the screen rect, as they may have changed as well.
        if (frame.hasDisplayGeometryChanged()) {
            frame.transformDisplayUvCoords(mQuadTexCoord!!, mQuadTexCoordTransformed!!)
        }

        // No need to test or write depth, the screen quad has arbitrary depth, and is expected
        // to be drawn first.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthMask(false)

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

        GLES20.glUseProgram(mQuadProgram)

        // Set the vertex positions.
        GLES20.glVertexAttribPointer(
                mQuadPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mQuadVertices)

        // Set the texture coordinates.
        GLES20.glVertexAttribPointer(mQuadTexCoordParam, TEXCOORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mQuadTexCoordTransformed)

        // Enable vertex arrays
        GLES20.glEnableVertexAttribArray(mQuadPositionParam)
        GLES20.glEnableVertexAttribArray(mQuadTexCoordParam)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(mQuadPositionParam)
        GLES20.glDisableVertexAttribArray(mQuadTexCoordParam)

        // Restore the depth state for further drawing.
        GLES20.glDepthMask(true)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        ShaderUtil.checkGLError(TAG, "Draw")
    }

    companion object {
        private val TAG = BackgroundRenderer::class.java.simpleName

        private val COORDS_PER_VERTEX = 3
        private val TEXCOORDS_PER_VERTEX = 2
        private val FLOAT_SIZE = 4

        private val QUAD_COORDS = floatArrayOf(-1.0f, -1.0f, 0.0f, -1.0f, +1.0f, 0.0f, +1.0f, -1.0f, 0.0f, +1.0f, +1.0f, 0.0f)

        private val QUAD_TEXCOORDS = floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f)
    }
}
