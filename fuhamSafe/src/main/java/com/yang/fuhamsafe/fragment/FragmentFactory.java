package com.yang.fuhamsafe.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

//fragment工厂类
public class FragmentFactory {
	private static Map<Integer, Fragment> fragments = new HashMap<Integer,Fragment>();
	public static Fragment createFragment(int position) {
		Fragment fragment = null;
		fragment = fragments.get(position);
		if (fragment == null) {
			if (position == 0) {
				fragment = new SetStep1Fragment();
			}else if (position == 1) {
				fragment = new SetStep2Fragment();
			}else if (position == 2) {
				fragment = new SetStep3Fragment();
			}else if (position == 3) {
				fragment = new SetStep4Fragment();
			}else if (position == 4) {
				fragment = new SetStep5Fragment();
			}
			fragments.put(position, fragment);
		}
		
		return fragment;
		
	}
}
