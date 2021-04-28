package com.nurverek.firestorm;

import vanguard.VLListType;
import vanguard.VLMath;

public class FSBoundsCuboid extends FSBounds {

    protected float halfwidth;
    protected float halfheight;
    protected float halfdepth;
    protected float halfdiameter;

    public FSBoundsCuboid(FSSchematics schematics, float xoffset, float yoffset, float zoffset, Mode xmode, Mode ymode, Mode zmode,
                          float halfwidth, float halfheight, float halfdepth, Mode wmode, Mode hmode, Mode dmode){
        super(schematics);

        initialize(new Point(xmode, ymode, zmode, xoffset, yoffset, zoffset), 1);
        add(new Point(wmode, hmode, dmode, halfwidth, halfheight, halfdepth));
    }


    public float getHalfWidth(){
        return halfwidth;
    }

    public float getHalfHeight(){
        return halfheight;
    }

    public float getHalfDepth(){
        return halfdepth;
    }

    public float getHalfDiameter(){
        return halfdiameter;
    }

    @Override
    protected void notifyBasePointsUpdated(){
        float[] offsetcoords = offset.coordinates;
        float[] point1 = point(0).coordinates;

        halfwidth = (point1[0] - offsetcoords[0]);
        halfheight = (point1[1] - offsetcoords[1]);
        halfdepth = (point1[2] - offsetcoords[2]);

        CACHE1[0] = halfwidth;
        CACHE1[1] = halfheight;
        CACHE1[2] = halfheight;

        halfdiameter = VLMath.euclideanDistance(CACHE1, 0, offsetcoords, 0, 3);
    }

    @Override
    public void check(Collision results, FSBoundsSphere bounds){
        super.check(results, bounds);

        float[] coords = offset.coordinates;
        float[] targetcoords = bounds.offset.coordinates;

        VLMath.difference(coords, 0, targetcoords, 0, CACHE1, 0, 3);
        float origindistance = VLMath.length(CACHE1, 0, 3);

        CACHE1[0] = VLMath.clamp(CACHE1[0], -halfwidth, halfwidth);
        CACHE1[1] = VLMath.clamp(CACHE1[1], -halfheight, halfheight);
        CACHE1[2] = VLMath.clamp(CACHE1[2], -halfdepth, halfdepth);

        results.distance = origindistance - VLMath.length(CACHE1, 0, 3) - bounds.radius;
        results.collided = results.distance <= 0;
    }

    @Override
    public void check(Collision results, FSBoundsCuboid bounds){
        super.check(results, bounds);

        float[] coords = offset.coordinates;
        float[] targetcoords = bounds.offset.coordinates;

        CACHE1[0] = Math.abs(coords[0] - targetcoords[0]) - halfwidth - bounds.halfwidth;
        CACHE1[1] = Math.abs(coords[1] - targetcoords[1]) - halfheight - bounds.halfheight;
        CACHE1[2] = Math.abs(coords[2] - targetcoords[2]) - halfdepth - bounds.halfdepth;

        results.distance = VLMath.length(CACHE1, 0, 3);
        results.collided = CACHE1[0] <= 0 && CACHE2[1] <= 0 && CACHE2[2] <= 0;
    }

    @Override
    public void checkPoint(Collision results, float[] point){
        super.checkPoint(results, point);

        VLMath.difference(offset.coordinates, 0, point, 0, CACHE1, 0, 3);
        float origindistance = VLMath.length(CACHE1, 0, 3);

        CACHE1[0] = VLMath.clamp(CACHE1[0], -halfwidth, halfwidth);
        CACHE1[1] = VLMath.clamp(CACHE1[1], -halfheight, halfheight);
        CACHE1[2] = VLMath.clamp(CACHE1[2], -halfdepth, halfdepth);

        results.distance = origindistance - VLMath.length(CACHE1, 0, 3);
        results.collided = results.distance <= 0;
    }

    @Override
    public void checkInput(Collision results, float[] near, float[] far){
        super.checkInput(results, near, far);

        VLMath.closestPointOfRay(near, 0, far, 0, offset.coordinates, 0, CACHE2, 0);
        checkPoint(results, CACHE2);
    }
}