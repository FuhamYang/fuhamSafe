package com.yang.fuhamsafe.fragment;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.activity.TheftproofActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetStep5Fragment extends Fragment {
	private Button button;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_set_step5, null);

		button = (Button) view.findViewById(R.id.enterTheftproof);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),TheftproofActivity.class));
				getActivity().finish();
			}
		});
		return view;
	}
}
