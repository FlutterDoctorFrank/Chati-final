package com.badlogic.gdx.backends.headless.mock.graphics;

import com.badlogic.gdx.graphics.GL20;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MockGL20 implements GL20 {

    @Override
    public void glActiveTexture(int i) {

    }

    @Override
    public void glBindTexture(int i, int i1) {

    }

    @Override
    public void glBlendFunc(int i, int i1) {

    }

    @Override
    public void glClear(int i) {

    }

    @Override
    public void glClearColor(float v, float v1, float v2, float v3) {

    }

    @Override
    public void glClearDepthf(float v) {

    }

    @Override
    public void glClearStencil(int i) {

    }

    @Override
    public void glColorMask(boolean b, boolean b1, boolean b2, boolean b3) {

    }

    @Override
    public void glCompressedTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, Buffer buffer) {

    }

    @Override
    public void glCompressedTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {

    }

    @Override
    public void glCopyTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

    }

    @Override
    public void glCopyTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

    }

    @Override
    public void glCullFace(int i) {

    }

    @Override
    public void glDeleteTextures(int i, IntBuffer intBuffer) {

    }

    @Override
    public void glDeleteTexture(int i) {

    }

    @Override
    public void glDepthFunc(int i) {

    }

    @Override
    public void glDepthMask(boolean b) {

    }

    @Override
    public void glDepthRangef(float v, float v1) {

    }

    @Override
    public void glDisable(int i) {

    }

    @Override
    public void glDrawArrays(int i, int i1, int i2) {

    }

    @Override
    public void glDrawElements(int i, int i1, int i2, Buffer buffer) {

    }

    @Override
    public void glEnable(int i) {

    }

    @Override
    public void glFinish() {

    }

    @Override
    public void glFlush() {

    }

    @Override
    public void glFrontFace(int i) {

    }

    @Override
    public void glGenTextures(int i, IntBuffer intBuffer) {

    }

    @Override
    public int glGenTexture() {
        return 0;
    }

    @Override
    public int glGetError() {
        return 0;
    }

    @Override
    public void glGetIntegerv(int i, IntBuffer intBuffer) {

    }

    @Override
    public String glGetString(int i) {
        return null;
    }

    @Override
    public void glHint(int i, int i1) {

    }

    @Override
    public void glLineWidth(float v) {

    }

    @Override
    public void glPixelStorei(int i, int i1) {

    }

    @Override
    public void glPolygonOffset(float v, float v1) {

    }

    @Override
    public void glReadPixels(int i, int i1, int i2, int i3, int i4, int i5, Buffer buffer) {

    }

    @Override
    public void glScissor(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glStencilFunc(int i, int i1, int i2) {

    }

    @Override
    public void glStencilMask(int i) {

    }

    @Override
    public void glStencilOp(int i, int i1, int i2) {

    }

    @Override
    public void glTexImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {

    }

    @Override
    public void glTexParameterf(int i, int i1, float v) {

    }

    @Override
    public void glTexSubImage2D(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {

    }

    @Override
    public void glViewport(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glAttachShader(int i, int i1) {

    }

    @Override
    public void glBindAttribLocation(int i, int i1, String s) {

    }

    @Override
    public void glBindBuffer(int i, int i1) {

    }

    @Override
    public void glBindFramebuffer(int i, int i1) {

    }

    @Override
    public void glBindRenderbuffer(int i, int i1) {

    }

    @Override
    public void glBlendColor(float v, float v1, float v2, float v3) {

    }

    @Override
    public void glBlendEquation(int i) {

    }

    @Override
    public void glBlendEquationSeparate(int i, int i1) {

    }

    @Override
    public void glBlendFuncSeparate(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glBufferData(int i, int i1, Buffer buffer, int i2) {

    }

    @Override
    public void glBufferSubData(int i, int i1, int i2, Buffer buffer) {

    }

    @Override
    public int glCheckFramebufferStatus(int i) {
        return 0;
    }

    @Override
    public void glCompileShader(int i) {

    }

    @Override
    public int glCreateProgram() {
        return 0;
    }

    @Override
    public int glCreateShader(int i) {
        return 0;
    }

    @Override
    public void glDeleteBuffer(int i) {

    }

    @Override
    public void glDeleteBuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public void glDeleteFramebuffer(int i) {

    }

    @Override
    public void glDeleteFramebuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public void glDeleteProgram(int i) {

    }

    @Override
    public void glDeleteRenderbuffer(int i) {

    }

    @Override
    public void glDeleteRenderbuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public void glDeleteShader(int i) {

    }

    @Override
    public void glDetachShader(int i, int i1) {

    }

    @Override
    public void glDisableVertexAttribArray(int i) {

    }

    @Override
    public void glDrawElements(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glEnableVertexAttribArray(int i) {

    }

    @Override
    public void glFramebufferRenderbuffer(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glFramebufferTexture2D(int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public int glGenBuffer() {
        return 0;
    }

    @Override
    public void glGenBuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public void glGenerateMipmap(int i) {

    }

    @Override
    public int glGenFramebuffer() {
        return 0;
    }

    @Override
    public void glGenFramebuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public int glGenRenderbuffer() {
        return 0;
    }

    @Override
    public void glGenRenderbuffers(int i, IntBuffer intBuffer) {

    }

    @Override
    public String glGetActiveAttrib(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {
        return null;
    }

    @Override
    public String glGetActiveUniform(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {
        return null;
    }

    @Override
    public void glGetAttachedShaders(int i, int i1, Buffer buffer, IntBuffer intBuffer) {

    }

    @Override
    public int glGetAttribLocation(int i, String s) {
        return 0;
    }

    @Override
    public void glGetBooleanv(int i, Buffer buffer) {

    }

    @Override
    public void glGetBufferParameteriv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glGetFloatv(int i, FloatBuffer floatBuffer) {

    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int i, int i1, int i2, IntBuffer intBuffer) {

    }

    @Override
    public void glGetProgramiv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public String glGetProgramInfoLog(int i) {
        return null;
    }

    @Override
    public void glGetRenderbufferParameteriv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glGetShaderiv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public String glGetShaderInfoLog(int i) {
        return null;
    }

    @Override
    public void glGetShaderPrecisionFormat(int i, int i1, IntBuffer intBuffer, IntBuffer intBuffer1) {

    }

    @Override
    public void glGetTexParameterfv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glGetTexParameteriv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glGetUniformfv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glGetUniformiv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public int glGetUniformLocation(int i, String s) {
        return 0;
    }

    @Override
    public void glGetVertexAttribfv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glGetVertexAttribiv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glGetVertexAttribPointerv(int i, int i1, Buffer buffer) {

    }

    @Override
    public boolean glIsBuffer(int i) {
        return false;
    }

    @Override
    public boolean glIsEnabled(int i) {
        return false;
    }

    @Override
    public boolean glIsFramebuffer(int i) {
        return false;
    }

    @Override
    public boolean glIsProgram(int i) {
        return false;
    }

    @Override
    public boolean glIsRenderbuffer(int i) {
        return false;
    }

    @Override
    public boolean glIsShader(int i) {
        return false;
    }

    @Override
    public boolean glIsTexture(int i) {
        return false;
    }

    @Override
    public void glLinkProgram(int i) {

    }

    @Override
    public void glReleaseShaderCompiler() {

    }

    @Override
    public void glRenderbufferStorage(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glSampleCoverage(float v, boolean b) {

    }

    @Override
    public void glShaderBinary(int i, IntBuffer intBuffer, int i1, Buffer buffer, int i2) {

    }

    @Override
    public void glShaderSource(int i, String s) {

    }

    @Override
    public void glStencilFuncSeparate(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glStencilMaskSeparate(int i, int i1) {

    }

    @Override
    public void glStencilOpSeparate(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glTexParameterfv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glTexParameteri(int i, int i1, int i2) {

    }

    @Override
    public void glTexParameteriv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glUniform1f(int i, float v) {

    }

    @Override
    public void glUniform1fv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniform1fv(int i, int i1, float[] floats, int i2) {

    }

    @Override
    public void glUniform1i(int i, int i1) {

    }

    @Override
    public void glUniform1iv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glUniform1iv(int i, int i1, int[] ints, int i2) {

    }

    @Override
    public void glUniform2f(int i, float v, float v1) {

    }

    @Override
    public void glUniform2fv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniform2fv(int i, int i1, float[] floats, int i2) {

    }

    @Override
    public void glUniform2i(int i, int i1, int i2) {

    }

    @Override
    public void glUniform2iv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glUniform2iv(int i, int i1, int[] ints, int i2) {

    }

    @Override
    public void glUniform3f(int i, float v, float v1, float v2) {

    }

    @Override
    public void glUniform3fv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniform3fv(int i, int i1, float[] floats, int i2) {

    }

    @Override
    public void glUniform3i(int i, int i1, int i2, int i3) {

    }

    @Override
    public void glUniform3iv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glUniform3iv(int i, int i1, int[] ints, int i2) {

    }

    @Override
    public void glUniform4f(int i, float v, float v1, float v2, float v3) {

    }

    @Override
    public void glUniform4fv(int i, int i1, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniform4fv(int i, int i1, float[] floats, int i2) {

    }

    @Override
    public void glUniform4i(int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public void glUniform4iv(int i, int i1, IntBuffer intBuffer) {

    }

    @Override
    public void glUniform4iv(int i, int i1, int[] ints, int i2) {

    }

    @Override
    public void glUniformMatrix2fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniformMatrix2fv(int i, int i1, boolean b, float[] floats, int i2) {

    }

    @Override
    public void glUniformMatrix3fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniformMatrix3fv(int i, int i1, boolean b, float[] floats, int i2) {

    }

    @Override
    public void glUniformMatrix4fv(int i, int i1, boolean b, FloatBuffer floatBuffer) {

    }

    @Override
    public void glUniformMatrix4fv(int i, int i1, boolean b, float[] floats, int i2) {

    }

    @Override
    public void glUseProgram(int i) {

    }

    @Override
    public void glValidateProgram(int i) {

    }

    @Override
    public void glVertexAttrib1f(int i, float v) {

    }

    @Override
    public void glVertexAttrib1fv(int i, FloatBuffer floatBuffer) {

    }

    @Override
    public void glVertexAttrib2f(int i, float v, float v1) {

    }

    @Override
    public void glVertexAttrib2fv(int i, FloatBuffer floatBuffer) {

    }

    @Override
    public void glVertexAttrib3f(int i, float v, float v1, float v2) {

    }

    @Override
    public void glVertexAttrib3fv(int i, FloatBuffer floatBuffer) {

    }

    @Override
    public void glVertexAttrib4f(int i, float v, float v1, float v2, float v3) {

    }

    @Override
    public void glVertexAttrib4fv(int i, FloatBuffer floatBuffer) {

    }

    @Override
    public void glVertexAttribPointer(int i, int i1, int i2, boolean b, int i3, Buffer buffer) {

    }

    @Override
    public void glVertexAttribPointer(int i, int i1, int i2, boolean b, int i3, int i4) {

    }
}
