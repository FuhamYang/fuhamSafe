package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;

public class SetAddressLocationActivity extends Activity {
	private SharedPreferences sharedPreferences;
	private float startX;
	private float startY;
	private TextView topTextView;
	private TextView bottomTextView;
	private RelativeLayout relativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_address_location);
		topTextView = (TextView) findViewById(R.id.textViewTop);
		bottomTextView = (TextView) findViewById(R.id.textViewBottom);
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

		// 获取默认位置
		int left = sharedPreferences.getInt("address_location_left", 0);
		int top = sharedPreferences.getInt("address_location_top", 0);
		relativeLayout = (RelativeLayout) findViewById(R.id.dialog);

		// 获取屏幕宽度
		WindowManager wm = this.getWindowManager();
		final int width = wm.getDefaultDisplay().getWidth();
		final int height = wm.getDefaultDisplay().getHeight();

		// 获取设置view布局的对象
		final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout
				.getLayoutParams();

		// 如果不是第一次进入，则设置成已设置的位置
		if (left != 0 && top != 0) {
			// 改变view的位置
			layoutParams.leftMargin = left;
			layoutParams.topMargin = top;
			// 重新设置view布局
			relativeLayout.setLayoutParams(layoutParams);

			if (top < height / 2) {

				topTextView.setVisibility(TextView.INVISIBLE);
				bottomTextView.setVisibility(TextView.VISIBLE);

			} else {
				topTextView.setVisibility(TextView.VISIBLE);
				bottomTextView.setVisibility(TextView.INVISIBLE);
			}
		}
		// 设置多击事件
		final long[] hits = new long[2];// 数组长度表示要点击多少次，激活事件
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 从源数组的第二个位置开始拷贝，拷贝到目标数组的第一个位置，拷贝长度为总长度-1
				System.arraycopy(hits, 1, hits, 0, hits.length - 1);
				// 记录第一次点击时间
				hits[hits.length - 1] = SystemClock.uptimeMillis();
				// 计算最后一次与第一次点击的时间差
				if (hits[hits.length - 1] - hits[0] <= 500) {

					layoutParams.leftMargin = width / 2
							- relativeLayout.getWidth() / 2;
					layoutParams.topMargin = height / 2
							- relativeLayout.getHeight() / 2;
					relativeLayout.setLayoutParams(layoutParams);
				}
			}
		});

		relativeLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						startX = event.getRawX();
						startY = event.getRawY();

						break;
					case MotionEvent.ACTION_MOVE:

						float endX = event.getRawX();
						float endY = event.getRawY();

						// 计算位移差
						float dx = endX - startX;
						float dy = endY - startY;

						// 计算移动后的位置
						float left = relativeLayout.getLeft() + dx;
						float top = relativeLayout.getTop() + dy;
						float right = relativeLayout.getRight() + dx;
						float bottom = relativeLayout.getBottom() + dy;

						// 设置移动后的位置
						/*
						 * layoutParams.leftMargin = (int) left;
						 * layoutParams.topMargin = (int) top;
						 */

						// 使用此方法有BUG，移动到边缘时，会缺失部分区域,完善进行边界判断
						// bottom < height 其中bottom并没有加上状态栏的高度，height要减去状态栏的高度
						if (left > 0 && top > 0 && right < width
								&& bottom < height - 38)
							relativeLayout.layout((int) left, (int) top,
									(int) right, (int) bottom);
						// 根据提示框位置，选择要隐藏和显示的textVIEW
						if (top < height / 2) {

							topTextView.setVisibility(TextView.INVISIBLE);
							bottomTextView.setVisibility(TextView.VISIBLE);

						} else {
							topTextView.setVisibility(TextView.VISIBLE);
							bottomTextView.setVisibility(TextView.INVISIBLE);
						}

						// relativeLayout.setLayoutParams(layoutParams);

						// 重新设置开始位置,注意这个是手指的，不要写成控件的left会有误差的
						startX = endX;
						startY = endY;

						break;

					default:
						break;
				}

				return false;
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		saveLocation(relativeLayout);
		super.onDestroy();

	}
	private void saveLocation(RelativeLayout relativeLayout) {
		sharedPreferences.edit()
				.putInt("address_location_left", relativeLayout.getLeft())
				.commit();
		sharedPreferences.edit()
				.putInt("address_location_top", relativeLayout.getTop())
				.commit();

	}

}
