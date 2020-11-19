package com.nurverek.firestorm;

import com.nurverek.vanguard.VLArray;
import com.nurverek.vanguard.VLBufferManagerBase;

public abstract class FSEntryTypeVertexBuffer<TYPE extends VLArray> extends VLBufferManagerBase.EntryType<TYPE> {

    protected FSVertexBuffer vertexbuffer;

    protected FSEntryTypeVertexBuffer(FSVertexBuffer buffer){
        super(buffer.provider());
        this.vertexbuffer = buffer;
    }

    @Override
    protected void initialize(){
        super.initialize();
        vertexbuffer.initialize();
    }

    public FSVertexBuffer vertexBuffer(){
        return vertexbuffer;
    }
}
