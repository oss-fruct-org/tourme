package org.fruct.oss.tourme;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Image adapter for gallery at main screen
 * @author alexander
 * TODO: autorotate
 */
public class ImageAdapter extends PagerAdapter {
	Context context;
	private int[] GalImages = new int[] { R.drawable.one, R.drawable.two,
			R.drawable.three };

	ImageAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return GalImages.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
		imageView.setPadding(0, 0, 0, 0);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageResource(GalImages[position]);
		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}