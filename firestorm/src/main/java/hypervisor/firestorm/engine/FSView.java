package hypervisor.firestorm.engine;

import android.opengl.GLES32;
import android.opengl.GLU;
import android.opengl.Matrix;

import hypervisor.vanguard.array.VLArrayFloat;
import hypervisor.vanguard.array.VLArrayInt;
import hypervisor.vanguard.utils.VLCopyable;

public class FSView implements VLCopyable<FSView>{

    protected VLArrayFloat matview;
    protected VLArrayFloat matperspective;
    protected VLArrayFloat matorthographic;
    protected VLArrayFloat matprojection;
    protected VLArrayFloat matviewprojection;

    protected VLArrayInt settingsviewport;
    protected VLArrayFloat settingsview;
    protected VLArrayFloat settingsperspective;
    protected VLArrayFloat settingsorthographic;

    public FSView(boolean perspectivemode){
        matprojection = null;

        matview = new VLArrayFloat(new float[16]);
        matperspective = new VLArrayFloat(new float[16]);
        matorthographic = new VLArrayFloat(new float[16]);
        matviewprojection = new VLArrayFloat(new float[16]);

        settingsviewport = new VLArrayInt(new int[4]);
        settingsview = new VLArrayFloat(new float[9]);
        settingsperspective = new VLArrayFloat(new float[4]);
        settingsorthographic = new VLArrayFloat(new float[6]);

        if(perspectivemode){
            setPerspectiveMode();

        }else{
            setOrthographicMode();
        }
    }

    public FSView(FSView src, long flags){
        copy(src, flags);
    }

    protected FSView(){

    }

    public void setPerspectiveMode(){
        matprojection = matperspective;
    }

    public void setOrthographicMode(){
        matprojection = matorthographic;
    }

    public VLArrayFloat matrixPerspective(){
        return matperspective;
    }

    public VLArrayFloat matrixOrthographic(){
        return matorthographic;
    }

    public VLArrayFloat matrixView(){
        return matview;
    }

    public VLArrayFloat matrixViewProjection(){
        return matviewprojection;
    }

    public VLArrayInt settingsViewport(){
        return settingsviewport;
    }

    public VLArrayFloat settingsView(){
        return settingsview;
    }

    public VLArrayFloat settingsPerspective(){
        return settingsperspective;
    }

    public VLArrayFloat settingsOrthographic(){
        return settingsorthographic;
    }

    public void viewPort(int x, int y, int width, int height){
        settingsViewPort(x, y, width, height);
        applyViewPort();
    }

    public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        settingsLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        applyLookAt();
    }

    public void perspective(float fovy, float aspect, float znear, float zfar){
        settingsPerspective(fovy, aspect, znear, zfar);
        applyPerspective();
    }

    public void orthographic(float left, float right, float bottom, float top, float znear, float zfar){
        settingsOrthographic(left, right, bottom, top, znear, zfar);
        applyOrthographic();
    }

    public void settingsViewPort(int x, int y, int width, int height){
        int[] viewport = this.settingsviewport.array;

        viewport[0] = x;
        viewport[1] = y;
        viewport[2] = width;
        viewport[3] = height;
    }

    public void settingsLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        float[] settings = settingsview.array;

        settings[0] = eyeX;
        settings[1] = eyeY;
        settings[2] = eyeZ;
        settings[3] = centerX;
        settings[4] = centerY;
        settings[5] = centerZ;
        settings[6] = upX;
        settings[7] = upY;
        settings[8] = upZ;
    }

    public void settingsPerspective(float fovy, float aspect, float znear, float zfar){
        float[] settings = settingsperspective.array;

        settings[0] = fovy;
        settings[1] = aspect;
        settings[2] = znear;
        settings[3] = zfar;
    }

    public void settingsOrthographic(float left, float right, float bottom, float top, float znear, float zfar){
        float[] settings = settingsorthographic.array;

        settings[0] = left;
        settings[1] = right;
        settings[2] = bottom;
        settings[3] = top;
        settings[4] = znear;
        settings[5] = zfar;
    }

    public void applyLookAt(){
        float[] settings = settingsview.array;
        Matrix.setLookAtM(matview.array, 0, settings[0], settings[1], settings[2], settings[3], settings[4], settings[5], settings[6], settings[7], settings[8]);
    }

    public void applyOrthographic(){
        float[] settings = settingsorthographic.array;
        Matrix.orthoM(matorthographic.array, 0, settings[0], settings[1], settings[2], settings[3], settings[4], settings[5]);
    }

    public void applyPerspective(){
        float[] settings = settingsperspective.array;
        Matrix.perspectiveM(matperspective.array, 0, settings[0], settings[1], settings[2], settings[3]);
    }

    public void applyViewPort(){
        int[] viewport = this.settingsviewport.array;
        GLES32.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    public void applyViewProjection(){
        Matrix.multiplyMM(matviewprojection.array, 0, matprojection.array, 0, matview.array, 0);
    }

    public void multiplyViewPerspective(float[] results, int offset, float[] point, int offset2){
        Matrix.multiplyMV(results, offset, matviewprojection.array, 0, point, offset2);

        float w = results[offset + 3];
        results[offset] /= w;
        results[offset + 1] /= w;
        results[offset + 2] /= w;
    }

    public void convertToMVP(float[] results, int offset, float[] model){
        Matrix.multiplyMM(results, offset, matviewprojection.array, 0, model, 0);
    }

    public void unProject2DPoint(float x, float y, float[] resultsnear, int offset1, float[] resultsfar, int offset2){
        y = settingsviewport.get(3) - y;

        GLU.gluUnProject(x, y, 0F, matview.array, 0, matprojection.array, 0, settingsviewport.array, 0, resultsnear, offset1);
        GLU.gluUnProject(x, y, 1F, matview.array, 0, matprojection.array, 0, settingsviewport.array, 0, resultsfar, offset2);

        y = resultsnear[offset1 + 3];

        resultsnear[offset1] /= y;
        resultsnear[offset1 + 1] /= y;
        resultsnear[offset1 + 2] /= y;

        y = resultsfar[offset2 + 3];

        resultsfar[offset2] /= y;
        resultsfar[offset2 + 1] /= y;
        resultsfar[offset2 + 2] /= y;
    }

    @Override
    public void copy(FSView src, long flags){
        if((flags & FLAG_REFERENCE) == FLAG_REFERENCE){
            matview = src.matview;
            matperspective = src.matperspective;
            matorthographic = src.matorthographic;
            matviewprojection = src.matviewprojection;
            settingsviewport = src.settingsviewport;
            settingsview = src.settingsview;
            settingsperspective = src.settingsperspective;
            settingsorthographic = src.settingsorthographic;

        }else if((flags & FLAG_DUPLICATE) == FLAG_DUPLICATE){
            matview = src.matview.duplicate(FLAG_DUPLICATE);
            matperspective = src.matperspective.duplicate(FLAG_DUPLICATE);
            matorthographic = src.matorthographic.duplicate(FLAG_DUPLICATE);
            matviewprojection = src.matviewprojection.duplicate(FLAG_DUPLICATE);
            settingsviewport = src.settingsviewport.duplicate(FLAG_DUPLICATE);
            settingsview = src.settingsview.duplicate(FLAG_DUPLICATE);
            settingsperspective = src.settingsperspective.duplicate(FLAG_DUPLICATE);
            settingsorthographic = src.settingsorthographic.duplicate(FLAG_DUPLICATE);

        }else{
            Helper.throwMissingDefaultFlags();
        }

        matprojection = src.matprojection;
    }

    @Override
    public FSView duplicate(long flags){
        return new FSView(this, flags);
    }
}