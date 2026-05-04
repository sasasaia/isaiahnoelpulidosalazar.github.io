package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.os.Handler;

/**
 * A utility class for building and executing {@link Runnable} actions on the main thread,
 * with optional support for passing arguments and delayed execution.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Actions action = new Actions()
 *     .setArg("Hello")
 *     .build(() -> {
 *         String msg = (String) action.getArg();
 *         Log.d("TAG", msg);
 *     });
 * action.execute();
 * }</pre>
 */
public class Actions {

    Runnable runnable;
    Object arg;

    /**
     * Sets an argument that can be retrieved inside the action's {@link Runnable}.
     *
     * @param arg the argument to store; can be any {@link Object}
     * @return this {@code Actions} instance, for chaining
     */
    public Actions setArg(Object arg){
        this.arg = arg;
        return this;
    }

    /**
     * Returns the argument previously set via {@link #setArg(Object)}.
     *
     * @return the stored argument, or {@code null} if none was set
     */
    public Object getArg(){
        return arg;
    }

    /**
     * Assigns the {@link Runnable} that will be executed when {@link #execute()} or
     * {@link #executeDelayed(int)} is called.
     *
     * @param runnable the runnable action to build; must not be {@code null}
     * @return this {@code Actions} instance, for chaining
     */
    public Actions build(Runnable runnable){
        this.runnable = runnable;
        return this;
    }

    /**
     * Posts the built {@link Runnable} to the main thread's message queue for immediate execution.
     *
     * <p>The runnable must have been set via {@link #build(Runnable)} before calling this method.</p>
     */
    public void execute(){
        new Handler().post(runnable);
    }

    /**
     * Posts the built {@link Runnable} to the main thread's message queue with the specified delay.
     *
     * <p>The runnable must have been set via {@link #build(Runnable)} before calling this method.</p>
     *
     * @param delay the delay in milliseconds before the runnable is executed
     */
    public void executeDelayed(int delay){
        new Handler().postDelayed(runnable, delay);
    }
}