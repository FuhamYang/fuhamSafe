package com.yang.fuhamsafe.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.yang.fuhamsafe.R;

public class SetStep3Fragment extends Fragment {
	
	private EditText phoneEditText;
	private Button button;
	private SharedPreferences sharedPreferences;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_set_step3, null);
		phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
		sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		String currentPhoneNumber = sharedPreferences.getString("phone_number", "");
		phoneEditText.setText(currentPhoneNumber);
		button = (Button) view.findViewById(R.id.select_button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//在fragment中使用startActivityForResult
				//必须将第二个参数的请求码设置成Activity.RESULT_FIRST_USER(即第一个用户调用，就给第一个用户)
				//否则不能使Fragment中的onActivityResult起作用，而是跳转回GuideActivity中
				//startActivityForResult(new Intent(getActivity(), ContactsActivity.class), Activity.RESULT_FIRST_USER);
				//调用系统的通讯录
				startActivityForResult(new Intent(
		                Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI), Activity.RESULT_FIRST_USER);
				
			}
		});
		
		return view;
	}
	
	public String getPhone() {
		//trim()过滤空格
		return phoneEditText.getText().toString().trim().replaceAll("-","");
		
	}
	
	public void setEditText(String phoneNumber) {
		
		phoneEditText.setText(phoneNumber);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		/*if (resultCode == Activity.RESULT_OK) {
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");
			phoneEditText.setText(phone);
		}
		
		System.out.println("***************");
	*/	

		//String username;
		super.onActivityResult(requestCode, resultCode, data);
		String usernumber;
		if (resultCode == Activity.RESULT_OK) {
			//获取内容解决器
			ContentResolver reContentResolverol = getActivity().getContentResolver();
			//获取Uri
			Uri contactData = data.getData();
			
			// 通过内容解决器，执行Uri,查询数据，并获得结果集光标
			Cursor cursor = reContentResolverol.query(contactData, null, null,
					null, null);
			//去到第一行
			cursor.moveToFirst();
			//取得姓名
			/*username = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));*/
			//取得id
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			//通过id获得电话
			Cursor phone = reContentResolverol.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				usernumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneEditText.setText(usernumber.replaceAll("-",""));
			}
		}
	}
}
