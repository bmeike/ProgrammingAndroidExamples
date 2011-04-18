package pa.ch16;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.maps.MapView;
import pa.ch16.kml.LayoutListener;

public class LayoutControlMapView extends MapView {
    private boolean mLaidOut = true;
    private LayoutListener layoutListener;

    public LayoutControlMapView(Context context, String s) {
        super(context, s);
    }

    public LayoutControlMapView(Context context,
                                AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    public LayoutControlMapView(Context context,
                                AttributeSet attributeSet, int i)
    {
        super(context, attributeSet, i);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int bottom, int right)
    {
        super.onLayout(changed, left, top, bottom, right);

        if (mLaidOut) {
            mLaidOut = false;
            initialLayout();
        }
    }

    private void initialLayout() {
        layoutListener.initialLayout();
    }

    public void setListener(LayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }
}
