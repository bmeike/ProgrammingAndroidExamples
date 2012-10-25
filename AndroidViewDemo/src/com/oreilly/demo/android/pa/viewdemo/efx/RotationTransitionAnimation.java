/* $Id: $
 *
 */
package com.oreilly.demo.android.pa.viewdemo.efx;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * RotationTransitionAnimation
 */
public class RotationTransitionAnimation
    extends Animation
    implements Animation.AnimationListener
{
    private static final float Z_MAX = 10000.0f;

    /** the root view */
    final View root;
    /** the currently visible view */
    final View curView;
    /** the transition target */
    final View nextView;

    final float xCenter;
    final float yCenter;

    private final int dir;
    private final Camera camera = new Camera();

    /**
     * @param d
     * @param cont
     * @param cur
     * @param nxt
     */
    public RotationTransitionAnimation(
        int d,
        View cont,
        View cur,
        View nxt)
    {
        if (1 != Math.abs(d)) {
            throw new IllegalArgumentException(
                "direction must be 1 or -1");
        }

        dir = d;
        root = cont;
        curView = cur;
        nextView = nxt;

        xCenter = cur.getWidth() / 2.0f;
        yCenter = cur.getHeight() / 2.0f;
    }

    RotationTransitionAnimation(
        View r,
        View cur,
        float xc,
        float yc)
    {
        dir = -1;
        root = r;
        curView = cur;
        nextView = null;
        xCenter = xc;
        yCenter = yc;
    }

    /**
     * Start the transition animation
     */
    public void runAnimation() {
        animateOnce(new AccelerateInterpolator(), this);
    }

    /**
     * @see android.view.animation.Animation#applyTransformation(
     *      float,
     *      android.view.animation.Transformation)
     */
    @Override
    protected void applyTransformation(float t, Transformation xf) {
        Matrix xform = xf.getMatrix();

        float z = ((dir > 0) ? 0.0f : -Z_MAX) - (dir * t * Z_MAX);

        camera.save();
        camera.rotateZ(t * 360);
        camera.translate(0.0F, 0.0F, z);
        camera.getMatrix(xform);
        camera.restore();

        xform.preTranslate(-xCenter, -yCenter);
        xform.postTranslate(xCenter, yCenter);
    }

    /**
     * @see android.view.animation.Animation.AnimationListener
     *      #onAnimationEnd(android.view.animation.Animation)
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        root.post(new Runnable() {
            @Override public void run() {
                curView.setVisibility(View.GONE);
                nextView.setVisibility(View.VISIBLE);
                nextView.requestFocus();
                new RotationTransitionAnimation(root, nextView, xCenter, yCenter)
                   .animateOnce(new DecelerateInterpolator(), null);
            } });
    }

    /**
     * @see android.view.animation.Animation.AnimationListener
     *      #onAnimationRepeat(android.view.animation.Animation)
     */
    @Override public void onAnimationRepeat(Animation animation) { }

    /**
     * @see android.view.animation.Animation.AnimationListener
     *      #onAnimationStart(android.view.animation.Animation)
     */
    @Override public void onAnimationStart(Animation animation) { }

    /**
     * Run the animation one time.
     *
     * @param interpolator the animation interpolator
     * @param listener animation completion listener
     */
    void animateOnce(
        Interpolator interpolator,
        Animation.AnimationListener listener)
    {
        setDuration(2000);
        setInterpolator(interpolator);
        setAnimationListener(listener);
        root.startAnimation(this);
    }
}
