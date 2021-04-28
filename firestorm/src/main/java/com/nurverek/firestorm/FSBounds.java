package com.nurverek.firestorm;

import vanguard.VLListType;
import vanguard.VLUpdater;

public abstract class FSBounds{

    protected static final float[] CACHE1 = new float[4];
    protected static final float[] CACHE2 = new float[4];

    public static final Mode MODE_X_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return coefficient * schematics.modelWidth() / 100f;
        }
    };
    public static final Mode MODE_Y_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return coefficient * schematics.modelHeight() / 100f;
        }
    };
    public static final Mode MODE_Z_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return coefficient * schematics.modelDepth() / 100f;
        }
    };
    public static final Mode MODE_X_RELATIVE = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelLeft() + coefficient;
        }
    };
    public static final Mode MODE_Y_RELATIVE = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelBottom() + coefficient;
        }
    };
    public static final Mode MODE_Z_RELATIVE = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelFront() + coefficient;
        }
    };
    public static final Mode MODE_X_RELATIVE_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelLeft() + coefficient * schematics.modelWidth() / 100f;
        }
    };
    public static final Mode MODE_Y_RELATIVE_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelBottom() + coefficient * schematics.modelHeight() / 100f;
        }
    };
    public static final Mode MODE_Z_RELATIVE_VOLUMETRIC = new Mode(){

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return schematics.modelFront() + coefficient * schematics.modelDepth() / 100f;
        }
    };
    public static final Mode MODE_DIRECT_VALUE = new DirectMode();

    private static final VLUpdater<FSBounds> UPDATE = new VLUpdater<FSBounds>(){
        @Override
        public void update(FSBounds s){
            s.recalculate();
            s.updater = UPDATE_NOTHING;
        }
    };

    protected FSSchematics schematics;
    private VLUpdater<FSBounds> updater;

    protected Point offset;
    protected VLListType<Point> points;

    protected FSBounds(FSSchematics schematics){
        this.schematics = schematics;
    }

    protected final void initialize(Point offset, int pointscapacity){
        this.offset = offset;
        this.points = new VLListType<>(pointscapacity, pointscapacity);

        markForUpdate();
    }

    public void markForUpdate(){
        updater = UPDATE;
    }

    public void add(Point point){
        points.add(point);
    }

    public Point offset(){
        return offset;
    }

    public Point point(int index){
        return points.get(index);
    }

    public VLListType<Point> points(){
        return points;
    }

    public int size(){
        return points.size();
    }

    public final void checkForUpdates(){
        updater.update(this);
    }

    protected final void recalculate(){
        offset.calculate(schematics);
        float[] offsetcoords = offset.coordinates;

        int size = points.size();

        for(int i = 0; i < size; i++){
            Point point = points.get(i);
            point.calculate(schematics);
            point.offset(offsetcoords);
        }

        notifyBasePointsUpdated();
    }

    protected abstract void notifyBasePointsUpdated();

    protected void check(Collision results, FSBounds bounds){
        if(bounds instanceof FSBoundsSphere){
            check(results, (FSBoundsSphere)bounds);

        }else if(bounds instanceof FSBoundsCuboid){
            check(results, (FSBoundsCuboid)bounds);

        }else{
            throw new RuntimeException("Invalid bound type[" + bounds.getClass().getSimpleName() + "]");
        }
    }

    protected void check(Collision results, FSBoundsSphere bounds){
        checkForUpdates();
    }

    protected void check(Collision results, FSBoundsCuboid bounds){
        checkForUpdates();
    }

    public void checkPoint(Collision results, float[] point){
        checkForUpdates();
    }

    public void checkInput(Collision results, float[] near, float[] far){
        checkForUpdates();
    }

    public static final class Point{

        protected Mode[] modes;
        protected float[] coefficients;
        protected float[] coordinates;

        public Point(Mode modeX, Mode modeY, Mode modeZ, float coefficientX, float coefficientY, float coefficientZ){
            this.modes = new Mode[]{
                    modeX, modeY, modeZ
            };
            this.coefficients = new float[]{
                    coefficientX, coefficientY, coefficientZ
            };

            coordinates = new float[3];
        }

        public void calculate(FSSchematics schematics){
            coordinates[0] = modes[0].calculate(schematics, coefficients[0]);
            coordinates[1] = modes[1].calculate(schematics, coefficients[1]);
            coordinates[2] = modes[2].calculate(schematics, coefficients[2]);
        }

        public void offset(float[] offset){
            coordinates[0] += offset[0];
            coordinates[1] += offset[1];
            coordinates[2] += offset[2];
        }

        public Mode[] modes(){
            return modes;
        }

        public float[] coefficients(){
            return coefficients;
        }

        public float[] coordinates(){
            return coordinates;
        }
    }

    public static interface Mode{

        float calculate(FSSchematics schematics, float coefficient);
    }

    protected static final class DirectMode implements Mode{

        @Override
        public float calculate(FSSchematics schematics, float coefficient){
            return coefficient;
        }
    }

    public static final class Collision{

        public boolean collided;
        public int boundsindex;
        public float distance;

        public Collision(boolean collided, int boundsindex, float distance){
            this.collided = collided;
            this.boundsindex = boundsindex;
            this.distance = distance;
        }

        public Collision(Collision src){
            this.collided = src.collided;
            this.boundsindex = src.boundsindex;
            this.distance = src.distance;
        }

        public Collision(){

        }
    }
}