package com.yang.fuhamsafe.activity;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.fragment.FragmentFactory;
import com.yang.fuhamsafe.fragment.SetStep2Fragment;
import com.yang.fuhamsafe.fragment.SetStep3Fragment;
import com.yang.fuhamsafe.receiver.AdminReceiver;
import com.yang.fuhamsafe.utils.DensityUtils;
import com.yang.fuhamsafe.utils.ToastUtils;
import com.yang.fuhamsafe.view.MyViewPager;


public class GuideActivity extends FragmentActivity {

	public static final int SET_PHONE_NUMBER = 2;
	public static final int SET_SIM = 1;
	public static final int ACTIVE_ADMIN_DEVICE = 3;
	private MyViewPager myViewPager;
	private Fragment fragment;
	private LinearLayout linearLayoutPoint;
	private int pointWidth;
	private SharedPreferences sharedPreferences;
	private View bluePoint;
	private SetStep3Fragment setStep3Fragment;
	private GestureDetector gestureDetector;
	//private List<View> list;
	private SetStep2Fragment setStep2Fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		startActivity(new Intent(this,HelpActivity.class));
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);


		linearLayoutPoint = (LinearLayout) findViewById(R.id.linearLayout);
		bluePoint = findViewById(R.id.bluePoint);

		//list = new ArrayList<View>();
		//initViews();
		myViewPager = (MyViewPager) findViewById(R.id.viewPager);
		//viewPager.setAdapter(new GuideAdapter());

		//GuideActivity必须继承FragmentActivity才能使用getSupportFragmentManager()
		//FragmentManager类提供了运行时在Activity上添加，移除或者替换Fragment的方法，
		// 从而可以制造出动态的体验。
		GuideAdapter guideAdapter = new GuideAdapter(getSupportFragmentManager());
		myViewPager.setAdapter(guideAdapter);
		//初始化小圆点
		initPoint(guideAdapter.getCount());
		//设置滑动侦听事件
		myViewPager.setOnPageChangeListener(new GuidePageListener());



		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			//e1是手指起点，e2是手指终点
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
/*				if (myViewPager.getCurrentItem() == SET_PHONE_NUMBER) {
					
					
						//判断输入框是否为空
						String phone = setStep3Fragment.getPhone();
						if (!TextUtils.isEmpty(phone)) {
							sharedPreferences.edit()
									.putString("phone_number", phone)
									.commit();
							MyViewPager.isCanScroll = true;
							

						} else {
							Toast.makeText(GuideActivity.this, "请输入号码！",
									Toast.LENGTH_SHORT).show();

						}
					
					return true;
				}
				
				if (myViewPager.getCurrentItem() == SET_SIM) {
				
					
						//判断输入框是否为空
						
						if (setStep2Fragment.getIsCheckBox()) {
							
							MyViewPager.isCanScroll = true;
							

						} else {
							Toast.makeText(GuideActivity.this, "请选择绑定SIM卡！",
									Toast.LENGTH_SHORT).show();
						}
					
					return true;
				}*/
				return true;

			}
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
								   float velocityX, float velocityY) {
				
/*					if (myViewPager.getCurrentItem() == SET_PHONE_NUMBER) {
						// 手指向右划，上一页，设置可滚动，直接跳到下一页
						if (e2.getRawX() - e1.getRawX() > 100) {
							MyViewPager.isCanScroll = true;
							myViewPager.setCurrentItem(
									myViewPager.getCurrentItem() - 1, true);
						}
						// 手指向左划，下一页
						if (e1.getRawX() - e2.getRawX() > 100) {
							//判断输入框是否为空
							String phone = setStep3Fragment.getPhone();
							if (!TextUtils.isEmpty(phone)) {
								sharedPreferences.edit()
										.putString("phone_number", phone)
										.commit();
								MyViewPager.isCanScroll = true;
								
								// 设置viewPager的当前页面
								myViewPager.setCurrentItem(
										myViewPager.getCurrentItem() + 1, true);

							} else {
								Toast.makeText(GuideActivity.this, "请输入号码！",
										Toast.LENGTH_SHORT).show();

							}
						}
						return true;
					}
					
					if (myViewPager.getCurrentItem() == SET_SIM) {
						if (e2.getRawX() - e1.getRawX() > 100) {
							MyViewPager.isCanScroll = true;
							myViewPager.setCurrentItem(
									myViewPager.getCurrentItem() - 1, true);
						}
						// 手指向左划，下一页
						if (e1.getRawX() - e2.getRawX() > 100) {
							//判断输入框是否为空
							
							if (setStep2Fragment.getIsCheckBox()) {
								
								MyViewPager.isCanScroll = true;
								
								// 设置viewPager的当前页面
								myViewPager.setCurrentItem(
										myViewPager.getCurrentItem() + 1, true);

							} else {
								Toast.makeText(GuideActivity.this, "请选择绑定SIM卡！",
										Toast.LENGTH_SHORT).show();
							}
						}
						return true;
					}*/
				return false;


			}
		});

	}
	//初始化圆点
	private void initPoint(int count) {
		// TODO Auto-generated method stub
		for (int i = 0; i < count; i++) {
			//新建一个VIEW	
			View point = new View(this);
			//将view设置成圆点
			point.setBackgroundResource(R.drawable.shape_point_gray);

			//设置圆点的大小

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtils.dpToPx(this, 10),DensityUtils.dpToPx(this, 10));

			//设置圆点间距			
			if(i > 0){
				//从第二个点开始，设置距离左边控件的距离
				params.leftMargin = DensityUtils.dpToPx(this, 10);
			}

			point.setLayoutParams(params);

			//将圆点加到linearLayout中
			linearLayoutPoint.addView(point);
		}
		//在OnCreate方法之后，才会进行View的绘制测量大小（onMeasure），确定位置(Layout)，开始绘制(onDraw)；
		//View绘制是根据视图树从根节点进行绘制
		//在获取宽度时，布局还没绘制出来，拿不到圆点的位置值。
		//获取视图树，对layout结束事件进行侦听
		linearLayoutPoint.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			//当layout执行结束后回调此方法
			@Override
			public void onGlobalLayout() {
				//观察一次之后，将观察监听器移除，防止多次回调该方法
				linearLayoutPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//计算两个点之间的宽度
				pointWidth = linearLayoutPoint.getChildAt(1).getLeft()
						- linearLayoutPoint.getChildAt(0).getLeft();

			}
		});

	}

	//实现viewPager的Fragment适配器，FragmentStatePagerAdapter
	//拥有缓存机制（类似listview），适用于多界面
	class GuideAdapter extends FragmentStatePagerAdapter{

		//实现构造方法
		public GuideAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		//返回一个Fragment的条目
		@Override
		public Fragment getItem(int position) {
			//根据position的不同返回不同的fragment
			fragment = FragmentFactory.createFragment(position);

			//将设置号码页面，保存起来（这一步并没有必要，
			// FragmentFactory中已经存储了创建出来的fragment,
			// 直接FragmentFactory.createFragment(position)就可以获取对应位置的fragment）
			if (position == SET_PHONE_NUMBER) {
				setStep3Fragment = (SetStep3Fragment) fragment;
			}
			if (position == SET_SIM) {
				setStep2Fragment = (SetStep2Fragment) fragment;
			}
			return fragment;
		}

		//返回Fragment的总个数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 5;
		}

	}



	class GuidePageListener implements OnPageChangeListener{

		//滑动事件，1、当前显示的哪个页面；2、该页面已滑动的百分比；3、该页面已滑动的距离
		@Override
		public void onPageScrolled(int position, float positionOffset,
								   int positionOffsetPixels) {
			ToastUtils.cancelToast();
			float len = pointWidth * positionOffset + pointWidth * position;
			// 拿到蓝点的布局参数
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bluePoint
					.getLayoutParams();
			layoutParams.leftMargin = (int) len;// 设置蓝点的左边距
			// 从新给蓝点设置布局参数
			bluePoint.setLayoutParams(layoutParams);
		}

		//哪个页面被选中了
		@Override
		public void onPageSelected(int position) {


		}

		// 正在滑动还是手松开了，监听状态的变化
		@Override
		public void onPageScrollStateChanged(int state) {


			if(state == 1){
				System.out.println("###############我是"+state);
				if (myViewPager.getCurrentItem() == SET_PHONE_NUMBER) {

					// 判断输入框是否为空
					String phone = setStep3Fragment.getPhone();
					if (!TextUtils.isEmpty(phone)) {
						sharedPreferences.edit().putString("phone_number", phone)
								.commit();
						MyViewPager.isCanScroll = true;
					} else {
						MyViewPager.isCanScroll = false;
						ToastUtils.showToast(GuideActivity.this,"请设置安全号码！");
					}
				}

				if (myViewPager.getCurrentItem() == SET_SIM) {

					// 判断输入框是否为空

					if (setStep2Fragment.getIsCheckBox()) {

						MyViewPager.isCanScroll = true;

					} else {
						MyViewPager.isCanScroll = false;
						ToastUtils.showToast(GuideActivity.this,"请绑定sim卡！");
					}

				}
				if (myViewPager.getCurrentItem() == ACTIVE_ADMIN_DEVICE){
					DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
					ComponentName componentName = new ComponentName(GuideActivity.this, AdminReceiver.class);
					if(devicePolicyManager.isAdminActive(componentName)){
						MyViewPager.isCanScroll = true;
					}else {
						MyViewPager.isCanScroll = false;
						ToastUtils.showToast(GuideActivity.this,"请激活设备管理器！");
					}
				}
			}

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		MyViewPager.isCanScroll = true;
		super.onDestroy();
	}

	/*	初始化list容器
	private void initViews(){
		//与findviewbyid的区别是，没有实例化对象动态加载时用inflate，已实例化用findviewbyid
		View v1 = View.inflate(this, R.layout.set_step1, null);
		View v2 = View.inflate(this, R.layout.set_step2, null);
		list.add(v1);
		list.add(v2);
	}
	*/
	
/*	实现viewPager的适配器
	class GuideAdapter extends PagerAdapter{
		//返回条目数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		//将要显示的条目依次放入viewPager
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			container.addView(list.get(position));
			
			return list.get(position);
		}
		//当条目因滑动而消失时，将条目移除
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((View)object);
			
		}
		
	}
	*/


}
