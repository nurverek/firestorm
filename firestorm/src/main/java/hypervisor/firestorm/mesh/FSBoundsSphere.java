package hypervisor.firestorm.mesh;

import hypervisor.firestorm.engine.FSCache;
import hypervisor.vanguard.math.VLMath;

public class FSBoundsSphere extends FSBounds{

    protected float radius;

    public FSBoundsSphere(FSSchematics schematics, float xoffset, float yoffset, float zoffset, CalculationMethod xmode, CalculationMethod ymode, CalculationMethod zmode, float radius, CalculationMethod radiusmode){
        super(schematics);

        initialize(new Point(xmode, ymode, zmode, xoffset, yoffset, zoffset), 1);
        add(new Point(radiusmode, MODE_DIRECT_VALUE, MODE_DIRECT_VALUE, radius, 0, 0));
    }

    public FSBoundsSphere(FSBoundsSphere src, long flags){
        super(null);
        copy(src, flags);
    }

    protected FSBoundsSphere(){

    }

    @Override
    public void copy(FSBounds src, long flags){
        super.copy(src, flags);
        radius = ((FSBoundsSphere)src).radius;
    }

    @Override
    public FSBoundsSphere duplicate(long flags){
        return new FSBoundsSphere(this, flags);
    }

    @Override
    protected void notifyBasePointsUpdated(){
        radius = VLMath.euclideanDistance(point(1).coordinates, 0, offset().coordinates, 0, 3);
    }

    @Override
    public void check(Collision results, FSBoundsSphere bounds){
        super.check(results, bounds);

        results.distance = VLMath.euclideanDistance(point(1).coordinates, 0, offset().coordinates, 0, 3) - radius - bounds.radius;
        results.collided = results.distance <= 0;
    }

    @Override
    public void check(Collision results, FSBoundsCuboid bounds){
        super.check(results, bounds);

        bounds.check(results, this);
    }

    @Override
    public void checkPoint(Collision results, float[] point){
        super.checkPoint(results, point);

        results.distance = VLMath.euclideanDistance(point, 0, offset().coordinates, 0, 3) - radius;
        results.collided = results.distance <= 0;
    }

    @Override
    public void checkInput(Collision results, float[] near, float[] far){
        super.checkInput(results, near, far);

        VLMath.closestPointOfRay(near, 0, far, 0, offset().coordinates, 0, FSCache.FLOAT4_2, 0);
        checkPoint(results, FSCache.FLOAT4_2);
    }
}
