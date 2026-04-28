package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.os.Handler;

public class Actions {

    Runnable runnable;
    Object arg;

    public Actions setArg(Object arg){
        this.arg = arg;
        return this;
    }

    public Object getArg(){
        return arg;
    }

    public Actions build(Runnable runnable){
        this.runnable = runnable;
        return this;
    }

    public void execute(){
        new Handler().post(runnable);
    }

    public void executeDelayed(int delay){
        new Handler().postDelayed(runnable, delay);
    }
}
