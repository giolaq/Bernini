package com.laquysoft.bernini.rendering

import android.content.Context
import android.opengl.GLES20
import android.util.Log

import java.io.IOException

/**
 * Shader helper functions.
 */
object ShaderUtil {
    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    fun loadGLShader(tag: String, context: Context, type: Int, resId: Int): Int {
        val code = readRawTextFile(context, resId)
        var shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(tag, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
            GLES20.glDeleteShader(shader)
            shader = 0
        }

        if (shader == 0) {
            throw RuntimeException("Error creating shader.")
        }

        return shader
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     * @throws RuntimeException If an OpenGL error is detected.
     */
    fun checkGLError(tag: String, label: String) {
        var lastError = GLES20.GL_NO_ERROR
        // Drain the queue of all errors.
        var error = GLES20.glGetError()
        while ( error != GLES20.GL_NO_ERROR) {
            Log.e(tag, label + ": glError " + error)
            lastError = error
            error = GLES20.glGetError()
        }
        if (lastError != GLES20.GL_NO_ERROR) {
            throw RuntimeException(label + ": glError " + lastError)
        }
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private fun readRawTextFile(context: Context, resId: Int): String? {
        val inputStream = context.resources.openRawResource(resId)
        try {

            val bufferedReader = inputStream.bufferedReader()
            val lineList = mutableListOf<String>()

            bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
            lineList.forEach { println(">  " + it) }

            return lineList.joinToString("\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
