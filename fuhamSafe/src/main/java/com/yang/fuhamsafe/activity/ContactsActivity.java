package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yang.fuhamsafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsActivity extends Activity {
	private ListView listView;
	private List<HashMap<String, String>> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		list = new ArrayList<HashMap<String,String>>();
		readContacts();
		listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(new SimpleAdapter(this, list, R.layout.contact_list_item,
				new String[]{"name","phone"}, new int[]{R.id.name,R.id.number}));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				//获取点击条目的电话
				String phone = list.get(position).get("phone");
				//将数据封装到intent中
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				//返回结果码以区分是哪个view的返回值，与数据
				//直接点击返回，默认结果码是Activity.RESULT_CANCEL；正常返回则是OK
				//如果不设置结果码，那么当直接按返回键时，
				//由于没有数据传输到请求结果的activity,那么在那个activity中就会出现空指针错误
				setResult(Activity.RESULT_OK, intent);
				finish();

			}
		});
	}

	public void readContacts(){

		Cursor cursorID = getContentResolver().query(
				Uri.parse("content://com.android.contacts/raw_contacts"),
				new String[]{"contact_id"},
				null, null, null);
		Cursor cursor = null;
		while(cursorID.moveToNext()){

			cursor = getContentResolver().query(Uri.parse("content://com.android.contacts/data"),
					new String[]{"data1","mimetype"},
					"raw_contact_id = ?",
					new String[]{cursorID.getString(0)}, null);
			HashMap<String, String> message = new HashMap<String, String>();
			while(cursor.moveToNext()){
				String data = cursor.getString(0);
				String type = cursor.getString(1);

				if("vnd.android.cursor.item/name".equals(type)){
					message.put("name", data);
				}
				if("vnd.android.cursor.item/phone_v2".equals(type)){
					message.put("phone", data);
				}

			}
			list.add(message);
		}
		cursor.close();
		cursorID.close();
	}

}
