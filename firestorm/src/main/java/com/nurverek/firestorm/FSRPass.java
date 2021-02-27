package com.nurverek.firestorm;

import android.opengl.GLES32;

import java.util.ArrayList;

public final class FSRPass{

    private ArrayList<Entry> entries;
    
    private boolean advanceprocessors;
    private boolean runtasks;
    private boolean clearcolor;
    private boolean cleardepth;
    private boolean clearstencil;
    private boolean update;
    private boolean draw;
    
    private long id;

    protected final ArrayList<Order> orders = new ArrayList<>(15);
    protected int debug;
    
    public FSRPass(int debug){
        entries = new ArrayList<>();

        advanceprocessors = true;
        runtasks = true;
        clearcolor = true;
        cleardepth = true;
        clearstencil = true;
        update = true;
        draw = true;
        
        id = FSRControl.getNextID();

        this.debug = debug;
    }




    public void add(Entry e){
        entries.add(e);
        FSRControl.signalFrameRender(true);
    }

    public void add(int index, Entry e){
        entries.add(index, e);
        FSRControl.signalFrameRender(true);
    }

    public Entry get(int index){
        return entries.get(index);
    }

    public Entry getWithID(int id){
        Entry e;

        for(int i = 0; i < entries.size(); i++){
            e = entries.get(i);
            FSG c = e.c;

            if(c.id() == id){
                return e;
            }
        }

        return null;
    }

    public int size(){
        return entries.size();
    }

    public void remove(FSG c){
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).c.id() == c.id()){
                entries.remove(i);
            }
        }
    }

    public Entry remove(int index){
        return entries.remove(index);
    }



    public FSRPass setAdvanceProcessors(boolean enabled){
        advanceprocessors = enabled;
        return this;
    }

    public FSRPass setRunTasks(boolean enabled){
        runtasks = enabled;
        return this;
    }

    public FSRPass setClearColor(boolean enabled){
        clearcolor = enabled;
        return this;
    }

    public FSRPass setClearDepth(boolean enabled){
        cleardepth = enabled;
        return this;
    }

    public FSRPass setClearStencil(boolean enabled){
        clearstencil = enabled;
        return this;
    }

    public FSRPass setUpdateMeshes(boolean enabled){
        update = enabled;
        return this;
    }

    public FSRPass setDrawMeshes(boolean enabled){
        draw = enabled;
        return this;
    }

    public boolean getAdvanceProcessors(){
        return advanceprocessors;
    }

    public boolean getRunTasks(){
        return runtasks;
    }

    public boolean getClearColor(){
        return clearcolor;
    }

    public boolean getClearDepth(){
        return cleardepth;
    }

    public boolean getClearStencil(){
        return clearstencil;
    }

    public boolean getUpdateMeshes(){
        return update;
    }

    public boolean getDrawMeshes(){
        return draw;
    }

    public long id(){
        return id;
    }

    public FSRPass build(){
        orders.add(new Order(){

            @Override
            public void execute(int orderindex, int passindex){
                FSRControl.timeFrameStarted();
            }
        });
        
        if(clearcolor || cleardepth || clearstencil){
            int clearbit = 0;

            if(clearcolor){
                clearbit |= GLES32.GL_COLOR_BUFFER_BIT;
            }
            if(cleardepth){
                clearbit |= GLES32.GL_DEPTH_BUFFER_BIT;
            }
            if(clearstencil){
                clearbit |= GLES32.GL_STENCIL_BUFFER_BIT;
            }
            
            if(clearbit != 0){
                final int clearbitf = clearbit;

                orders.add(new Order(){

                    @Override
                    public void execute(int orderindex, int passindex){
                        FSR.clear(clearbitf);

                        if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                            try{
                                FSTools.checkGLError();

                            }catch(Exception ex){
                                throw new RuntimeException("Error running glClear() for bits[" + clearbitf + "] renderPass[" + passindex + "]", ex);
                            }
                        }
                    }
                });
            }

            if(clearcolor){
                orders.add(new Order(){

                    @Override
                    public void execute(int orderindex, int passindex){
                        FSR.clearColor();

                        if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                            try{
                                FSTools.checkGLError();

                            }catch(Exception ex){
                                throw new RuntimeException("Error clearing color with glClear() on renderPass[" + passindex + "]", ex);
                            }
                        }
                    }
                });
            }
        }
        if(runtasks){
            orders.add(new Order(){
                
                @Override
                public void execute(int orderindex, int passindex){
                    FSR.runTasks();

                    if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                        try{
                            FSTools.checkGLError();

                        }catch(Exception ex){
                            throw new RuntimeException("Error running tasks on renderPass[" + passindex + "]", ex);
                        }
                    }
                }
            });
        }
        if(update){
            orders.add(new Order(){

                @Override
                public void execute(int orderindex, int passindex){
                    update();
                }
            });

        }
        if(draw){
            orders.add(new Order(){

                @Override
                public void execute(int orderindex, int passindex){
                    draw();
                }
            });
        }
        if(advanceprocessors){
            orders.add(new Order(){

                @Override
                public void execute(int orderindex, int passindex){
                    FSR.advanceRunners();
                }
            });
        }
        
        return this;
    }
    
    protected void execute(){
        int size = orders.size();

        for(int i = 0; i < size; i++){
            orders.get(i).execute(i, FSR.CURRENT_RENDER_PASS_INDEX);
        }
    }

    public int advanceRunners(){
        int changes = 0;

        for(int i = 0; i < entries.size(); i++){
            changes += entries.get(i).c.next();
        }

        return changes;
    }

    public void update(){
        FSEvents events = FSControl.getSurface().events();

        events.GLPreDraw();

        Entry e;

        for(int index = 0; index < entries.size(); index++){
            e = entries.get(index);

            FSR.CURRENT_FSG_INDEX = index;
            FSR.CURRENT_PROGRAM_SET_INDEX = e.programsetindex;

            entries.get(index).c.update(FSR.CURRENT_RENDER_PASS_INDEX, e.programsetindex);

            if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                try{
                    FSTools.checkGLError();

                }catch(Exception ex){
                    throw new RuntimeException("Error running update() for FSG[" + index
                            + "] programSet[" + e.programsetindex + "] renderPass[" + FSR.CURRENT_RENDER_PASS_INDEX + "]", ex);
                }
            }
        }

        events.GLPostDraw();
    }

    public void draw(){
        FSEvents events = FSControl.getSurface().events();

        events.GLPreDraw();

        Entry e;

        for(int index = 0; index < entries.size(); index++){
            e = entries.get(index);

            FSR.CURRENT_FSG_INDEX = index;
            FSR.CURRENT_PROGRAM_SET_INDEX = e.programsetindex;

            e.c.draw(FSR.CURRENT_RENDER_PASS_INDEX, e.programsetindex);

            if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                try{
                    FSTools.checkGLError();

                }catch(Exception ex){
                    throw new RuntimeException("Error running draw() for FSG[" + index
                            + "] programSet[" + e.programsetindex + "] renderPass[" + FSR.CURRENT_RENDER_PASS_INDEX + "]", ex);
                }
            }
        }

        events.GLPostDraw();
    }

    protected void noitifyPostFrameSwap(){
        for(int i = 0; i < entries.size(); i++){
            entries.get(i).c.postFramSwap(FSR.CURRENT_RENDER_PASS_INDEX);

            if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                try{
                    FSTools.checkGLError();

                }catch(Exception ex){
                    throw new RuntimeException("Error running postFrameSwap() for entry[" + i + "] FSG[" + i
                            + "] programSet[" + entries.get(i).programsetindex + "] renderPass[" + FSR.CURRENT_RENDER_PASS_INDEX + "]", ex);
                }
            }
        }
    }

    public FSRPass copySettings(FSRPass src){
        advanceprocessors = src.advanceprocessors;
        runtasks = src.runtasks;
        clearcolor = src.clearcolor;
        cleardepth = src.cleardepth;
        clearstencil = src.clearstencil;
        update = src.update;
        draw = src.draw;
        
        id = FSRControl.getNextID();

        return this;
    }

    public void destroy(){
        for(int i = 0; i < entries.size(); i++){
            entries.get(i).c.destroy();

            if(FSControl.DEBUG_GLOBALLY && debug >= FSControl.DEBUG_NORMAL){
                try{
                    FSTools.checkGLError();

                }catch(Exception ex){
                    throw new RuntimeException("Error running destroy() for FSG[" + i
                            + "] programSet[" + entries.get(i).programsetindex + "] renderPass[" + FSR.CURRENT_RENDER_PASS_INDEX + "]", ex);
                }
            }
        }

        entries = null;
    }
    
    protected static interface Order{
        
        void execute(int orderindex, int passindex);
    }

    public static final class Entry{

        protected FSG c;
        protected int programsetindex;

        public Entry(FSG c, int programsetindex){
            this.c = c;
            this.programsetindex = programsetindex;
        }

        public FSG constructor(){
            return c;
        }

        public int programSetIndex(){
            return programsetindex;
        }
    }
}