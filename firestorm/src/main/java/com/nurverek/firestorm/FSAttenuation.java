package com.nurverek.firestorm;

import vanguard.VLCopyable;
import vanguard.VLFloat;

public abstract class FSAttenuation implements VLCopyable<FSAttenuation>{

    public FSAttenuation(){

    }

    public static class Distance extends FSAttenuation{

        protected VLFloat constant;
        protected VLFloat linear;
        protected VLFloat quadratic;

        public Distance(VLFloat constant, VLFloat linear, VLFloat quadratic){
            this.constant = constant;
            this.linear = linear;
            this.quadratic = quadratic;
        }

        public Distance(Distance src, long flags){
            copy(src, flags);
        }

        @Override
        public void copy(FSAttenuation src, long flags){
            Distance target = (Distance)src;

            if((flags & FLAG_REFERENCE) == FLAG_REFERENCE){
                constant = target.constant;
                linear = target.linear;
                quadratic = target.quadratic;

            }else if((flags & FLAG_DUPLICATE) == FLAG_DUPLICATE){
                constant = target.constant.duplicate(FLAG_DUPLICATE);
                linear = target.linear.duplicate(FLAG_DUPLICATE);
                quadratic = target.quadratic.duplicate(FLAG_DUPLICATE);

            }else{
                Helper.throwMissingDefaultFlags();
            }
        }

        @Override
        public Distance duplicate(long flags){
            return new Distance(this, flags);
        }

        public VLFloat constant(){
            return constant;
        }

        public VLFloat linear(){
            return linear;
        }

        public VLFloat quadratic(){
            return quadratic;
        }
    }

    public static class Radius extends FSAttenuation{

        protected VLFloat radius;

        public Radius(VLFloat radius){
            this.radius = radius;
        }

        public Radius(Radius src, long flags){
            copy(src, flags);
        }

        @Override
        public void copy(FSAttenuation src, long flags){
            Radius target = (Radius)src;

            if((flags & FLAG_REFERENCE) == FLAG_REFERENCE){
                radius = target.radius;

            }else if((flags & FLAG_DUPLICATE) == FLAG_DUPLICATE){
                radius = target.radius.duplicate(FLAG_DUPLICATE);

            }else{
                Helper.throwMissingDefaultFlags();
            }
        }

        @Override
        public Radius duplicate(long flags){
            return new Radius(this, flags);
        }

        public VLFloat radius(){
            return radius;
        }
    }
}
