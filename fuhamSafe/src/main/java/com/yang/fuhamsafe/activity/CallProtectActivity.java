package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.bean.BlackNumber;
import com.yang.fuhamsafe.dao.BlackNumberDao;
import com.yang.fuhamsafe.utils.ToastUtils;

import java.util.List;

/**
 * Created by fuhamyang on 2015/12/13.
 */
public class CallProtectActivity extends Activity {

    private List<BlackNumber> list;
    private ProgressBar progressBar;
    //当前页数初试化
    private int currentPageNumber = 0;
    private int totalPage;
    //设置每页记录数
    private int pageSize = 20;

    private boolean flag  =  true;

    private ListView listView;
    private BlackNumberDao blackNumberDao;
    private MyAdapter myAdapter;
    private View footerView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                progressBar.setVisibility(View.GONE);
                listView.setAdapter(myAdapter);
            }else {
                //移除圆形进度条
                listView.removeFooterView(footerView);
                //进度条移除，滑动到最后的时候，页面最后一条实际数据，就是可见的最后一条数据
                //符合查找数据的条件
                //已经取完数据，下次可以进入取数据
                flag = true;
            }

        }
    };
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_protect);

        ImageView imageViewAdd = (ImageView) findViewById(R.id.imageView_add);
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBlackNumberDialog();
            }
        });
        setListView();

        getBlackNumber();
    }

    private void setListView() {
        myAdapter = new MyAdapter();

        footerView = View.inflate(this,R.layout.black_number_footer,null);
        progressBar  = (ProgressBar) findViewById(R.id.progress_bar_black_number);
        blackNumberDao = new BlackNumberDao(CallProtectActivity.this);

        listView = (ListView) findViewById(R.id.lv_black_number);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //停止滑动
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    //当滑动到最后一条时
                    if (view.getLastVisiblePosition() == view.getCount() - 1 && flag ==true){
                        //flag标记，为了防止当添加底部圆心进度的时候，此时底部圆形进度成为最后一条，
                        //如果没有flag标记，依然符合进入该语句块的条件，此时currentPageNumber++；依然会执行
                        //会导致页面显示数据混乱
                        //此时页面最后一条实际数据不是在listview页面的最后一条，也不是可见的最后一条（它们都是底部圆形进度条）
                        //因此不符合进入条件
                        //flag为false，表明已经进入了读取新数据阶段，不能再进入
                        flag = false;
                        //当前页码+1
                        currentPageNumber++;
                        //当前页码不大于总页码时，由于页面是从0开始，所以条件是<
                        if (currentPageNumber < totalPage ){
                            //底部添加圆形进度
                            listView.addFooterView(footerView);
                            //加载剩余数据
                            getBlackNumber();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void getBlackNumber(){
        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(2000);
                //如果第一次进入
                if (list == null) {
                    //通过总数据条数，计算总页数，页数从0开始
                    int totalSize = blackNumberDao.getTotalSize();
                    //用于判断数据是否还有数据，判断终止查询的条件
                    totalPage = totalSize/pageSize;
                    //如果总数据条数，除以每页条目数有余数的话，那么页数+1；
                    if((totalSize%pageSize) >= 1)
                        totalPage++;
                    //currentPageNumber，即要查询的页数
                    list = blackNumberDao.selectAllBlackNumebr(currentPageNumber, pageSize);
                    handler.sendEmptyMessage(0);
                } else{
                    //第二次进入
                    //查询新的数据，并将数据加到list中
                    //currentPageNumber，即要查询的页数
                    list.addAll(blackNumberDao.selectAllBlackNumebr(currentPageNumber, pageSize));
                    //查询数据完成后，发送消息更新ui
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();

    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = android.view.View.inflate(CallProtectActivity.this, R.layout.black_number_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_black_number);
                viewHolder.tvType = (TextView)convertView.findViewById(R.id.tv_type);
                viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.imageViewDelete);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BlackNumber temp = list.get(position);
            viewHolder.tvNumber.setText(temp.getNumber());
            String type = temp.getType();
            if (type.equals("1")){
                viewHolder.tvType.setText("短信拦截");
            }else if (type.equals("2")){
                viewHolder.tvType.setText("电话拦截");
            }else if (type.equals("3")){
                viewHolder.tvType.setText("短信+电话拦截");
            }
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result = blackNumberDao.deleteBlackNumber(temp.getNumber());
                    if(result){
                        list.remove(temp);
                        //更新listView的显示
                        myAdapter.notifyDataSetChanged();
                        ToastUtils.showToast(CallProtectActivity.this,"删除成功！");
                    } else {
                        ToastUtils.showToast(CallProtectActivity.this,"删除失败！");
                    }

                }
            });
            return convertView;

        }
    }

    //该类用于存储，TextView
    private static class ViewHolder{
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }
    //设置添加黑名单弹窗
    protected void showAddBlackNumberDialog() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.add_black_number_dialog, null);
        //将布局放到dialog上
        dialog.setView(view);
        dialog.show();

        phoneEditText = (EditText) view.findViewById(R.id.et_number);

        Button selectContact = (Button)view.findViewById(R.id.select_button_black);
        Button ok = (Button) view.findViewById(R.id.bt_black_ok);
        Button cancel = (Button) view.findViewById(R.id.bt_black_cancel);
        final CheckBox checkNumber = (CheckBox) view.findViewById(R.id.cb_number_restrict);
        final CheckBox checkSms = (CheckBox)view.findViewById(R.id.cb_sms_restrict);
        selectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 2);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String number = phoneEditText.getText().toString();
                boolean isRestrictNumber =  checkNumber.isChecked();
                boolean isRestrictSms = checkSms.isChecked();
                if (!TextUtils.isEmpty(number) && (isRestrictNumber || isRestrictSms)) {
                    BlackNumber blackNumber = new BlackNumber();
                    blackNumber.setNumber(number);
                    if(isRestrictNumber && isRestrictSms){
                        blackNumber.setType("3");
                    }else if (isRestrictNumber && !isRestrictSms) {
                        blackNumber.setType("2");
                    }else{
                        blackNumber.setType("1");
                    }

                    if(blackNumberDao.insertBlackNumber(blackNumber)) {
                        Toast.makeText(CallProtectActivity.this, "添加成功!", Toast.LENGTH_SHORT).show();
                        list.add(0, blackNumber);
                        myAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    else
                        Toast.makeText(CallProtectActivity.this, "添加失败!", Toast.LENGTH_SHORT).show();


                } else if(TextUtils.isEmpty(number)){
                    //为输入框添加动画效果，2、添加插补器，设置动画的方式
                    Animation shake = AnimationUtils.loadAnimation(CallProtectActivity.this, R.anim.shake);
                    //自定义插补器
                    /*shake.setInterpolator(new Interpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return 0;
                        }
                    });*/
                    phoneEditText.startAnimation(shake);
                    //设置手机震动
                    //获取振动器
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    //震动1秒
                    vibrator.vibrate(500);
                    //1、先等待1秒，再震动2秒，再等待1秒，再震动3秒；
                    //2、-1表示只执行一次，0表示从第0个位置开始循环，1表示从第1个位置开始循环
                    //vibrator.vibrate(new long[]{1000,2000,1000,3000},-1);
                    //取消震动
                    //vibrator.cancel();
                    Toast.makeText(CallProtectActivity.this, "电话号码不能为空!", Toast.LENGTH_SHORT).show();

                }else if(!isRestrictSms && !isRestrictNumber){
                    Toast.makeText(CallProtectActivity.this, "请选择拦截方式!", Toast.LENGTH_SHORT).show();
                }
            }


        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

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
        String usernumber;
        //String username;
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            ContentResolver reContentResolverol = getContentResolver();
            //获取返回的内容提供者的URI
            Uri contactData = data.getData();
            // 通过URI进行查找，取得电话本中开始一项的光标
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
                phoneEditText.setText(usernumber.replace("-","").replace(" ",""));
            }


        }
    }
}
