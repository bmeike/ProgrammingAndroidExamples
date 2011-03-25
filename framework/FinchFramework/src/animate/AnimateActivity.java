package animate;

import com.finchframework.finch.R;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class AnimateActivity extends Activity implements Animation.AnimationListener {
	private View theAnimatedView;
	private Animation flip;
	private Animation moveToMiddle;
	private Animation slideAway;
	private int middleX;
	private int middleY;
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.animate_activity);

	}
		
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	public void onWindowFocusChanged (boolean hasFocus) {
		if (false == hasFocus) { return; }
		theAnimatedView = this.findViewById(R.id.animate_me);
		flip = AnimationUtils.loadAnimation(this, R.anim.flip);
		View parentView = this.findViewById(R.id.animate_container);
		int h = parentView.getHeight();
		middleX = (parentView.getWidth() / 2) - (theAnimatedView.getWidth() / 2);
		middleY = (parentView.getHeight() / 2) - (theAnimatedView.getHeight() / 2);
		moveToMiddle = new TranslateAnimation(
				Animation.ABSOLUTE, theAnimatedView.getLeft(),
				Animation.ABSOLUTE, middleX,
				Animation.ABSOLUTE, theAnimatedView.getTop(),
				Animation.ABSOLUTE, middleY);
		moveToMiddle.setDuration(2000);
		slideAway = new TranslateAnimation(
				Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, parentView.getHeight());
		slideAway.setDuration(2000);
		moveToMiddle.setAnimationListener(this);
		theAnimatedView.startAnimation(moveToMiddle);
	}

	public void onAnimationEnd(Animation animation) {
		if (null != moveToMiddle && animation == moveToMiddle) {
			theAnimatedView.offsetLeftAndRight(middleX);
			theAnimatedView.offsetTopAndBottom(middleY);
			flip.setAnimationListener(this);
			theAnimatedView.startAnimation(flip);
		} else if (null != flip && animation == flip) {
			invertImage((ImageView) theAnimatedView);
			theAnimatedView.startAnimation(slideAway);
		}
		
	}
	
	private void invertImage(ImageView v) {
		Matrix m = v.getImageMatrix();
		m.setRotate(180, v.getWidth() / 2, v.getHeight() / 2);
	}

	public void onAnimationRepeat(Animation animation) {
		
	}

	public void onAnimationStart(Animation animation) {
		
	}
	
	TranslateAnimation makeMove(View view) {

		return new TranslateAnimation(Animation.ABSOLUTE, view.getLeft(),
				Animation.ABSOLUTE, middleX,
				Animation.ABSOLUTE, view.getTop(),
				Animation.ABSOLUTE, middleY);		
	}

}
