package com.nurverek.firestorm;

import vanguard.VLCopyable;
import vanguard.VLFloat;

public class FSGamma implements VLCopyable<FSGamma>{

    private VLFloat level;

    public FSGamma(VLFloat level){
        this.level = level;
    }

    public FSGamma(FSGamma src, long flags){
        copy(src, flags);
    }

    public void level(VLFloat level){
        this.level = level;
    }

    public VLFloat level(){
        return level;
    }

    @Override
    public void copy(FSGamma src, long flags){
        if((flags & FLAG_REFERENCE) == FLAG_REFERENCE){
            level = src.level;

        }else if((flags & FLAG_DUPLICATE) == FLAG_DUPLICATE){
            level = src.level.duplicate(FLAG_DUPLICATE);

        }else{
            throw new RuntimeException("Invalid flags : " + flags);
        }
    }

    @Override
    public FSGamma duplicate(long flags){
        return new FSGamma(this, flags);
    }
}

