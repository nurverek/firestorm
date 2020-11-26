package com.nurverek.firestorm;

import com.nurverek.vanguard.VLListType;
import com.nurverek.vanguard.VLV;
import com.nurverek.vanguard.VLVMatrix;

public class FSModelMatrix extends VLVMatrix{

    public FSModelMatrix(int initialcapacity, int resizercount){
        super(initialcapacity, resizercount);
    }


    public void addRowTranslation(VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(4, 0);

        row.add(FSModelArray.TRANSLATE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);

        matrix.add(row);
    }

    public void addRowTranslation(int rowindex, VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(4, 0);

        row.add(FSModelArray.TRANSLATE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);

        matrix.add(rowindex, row);
    }

    public void addRowScale(VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(4, 0);

        row.add(FSModelArray.SCALE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);

        matrix.add(row);
    }

    public void addRowScale(int rowindex, VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(4, 0);

        row.add(FSModelArray.SCALE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);

        matrix.add(rowindex, row);
    }

    public void addRowRotate(VLV a, VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(5, 0);

        row.add(FSModelArray.ROTATE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);
        row.add(a);

        matrix.add(row);
    }

    public void addRowRotate(int rowindex, VLV a, VLV x, VLV y, VLV z){
        VLListType<VLV> row = new VLListType<>(5, 0);

        row.add(FSModelArray.ROTATE_FLAG);
        row.add(x);
        row.add(y);
        row.add(z);
        row.add(a);

        matrix.add(rowindex, row);
    }

    public void setTranslateType(int rowindex){
        matrix.get(rowindex).set(0, FSModelArray.TRANSLATE_FLAG);
    }

    public void setScaleType(int rowindex){
        matrix.get(rowindex).set(0, FSModelArray.SCALE_FLAG);
    }

    public void setRotateType(int rowindex){
        matrix.get(rowindex).set(0, FSModelArray.ROTATE_FLAG);
    }

    public void setX(int rowindex, VLV x){
        matrix.get(rowindex).set(1, x);
    }

    public void setY(int rowindex, VLV y){
        matrix.get(rowindex).set(2, y);
    }

    public void setZ(int rowindex, VLV z){
        matrix.get(rowindex).set(3, z);
    }

    public void setAngle(int rowindex, VLV a){
        matrix.get(rowindex).set(4, a);
    }

    public VLV getX(int rowindex){
        return matrix.get(rowindex).get(1);
    }

    public VLV getY(int rowindex){
        return matrix.get(rowindex).get(2);
    }

    public VLV getZ(int rowindex){
        return matrix.get(rowindex).get(3);
    }

    public VLV getAngle(int rowindex){
        return matrix.get(rowindex).get(4);
    }

    public int getTransformType(int rowindex){
        return (int) matrix.get(rowindex).get(0).get();
    }
}
