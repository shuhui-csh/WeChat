package bmob.wechat.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import bmob.wechat.bean.User;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;

import com.wechat.R;
/**
 * 对话框形式的Activity
 * @author Administrator
 *
 */
@SuppressLint("ShowToast")
public class SexSetActivity extends Activity implements OnClickListener {
	Button change_btn,cancel_btn;
	RadioGroup rg;
	RadioButton rg_male,rg_female;
	//用户性别
	boolean sex = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sex_select);
		initView();
	}
	private void initView() 
	{	//依次为RadioGroup，RadioButton,Button的初始化
		rg = (RadioGroup) findViewById(R.id.rg);
		rg_female = (RadioButton) findViewById(R.id.male);
		rg_male = (RadioButton) findViewById(R.id.female);
		change_btn = (Button) findViewById(R.id.btn_change);
		cancel_btn = (Button) findViewById(R.id.btn_cancel);
		//设置监听事件
		change_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		
		
		//单选按钮的监听事件
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			//从这个被启动的Activity返回一个布尔值，true为男，false为女
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
					//判断点击的是那一个RadioButton
					if (checkedId == R.id.male) {
						//true为男
						sex = true;
					}
					if (checkedId == R.id.female) {
						//false为女
						sex = false;
					}
			}
		});	
		
	}
	
	//两个按钮被点击的时候被触发
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			
			case R.id.btn_change:
				//点击确定的时候将sex提交到Bmob的User表
				changeUserSex();
				SexSetActivity.this.finish();
				break;
			case R.id.btn_cancel:
				SexSetActivity.this.finish();
			break;

			default:
				break;
		}
	}
	/**
	 * 改变User表中当前用户的性别
	 */
	@SuppressLint("ShowToast")
	private void changeUserSex() 
	{
		//用户管理类：所有和用户有关的操作均使用此类
		final BmobUserManager userManager = BmobUserManager.getInstance(this);
		//获取当前用户
		final User user = userManager.getCurrentUser(User.class);
		//设置User性别
		user.setSex(sex);
		user.update(getApplicationContext(), new UpdateListener(){
			@Override
			public void onSuccess(){
				//再次取得当前用户
				final User u =	userManager.getCurrentUser(User.class);
				//修改成功后提示
				Toast.makeText(getApplicationContext(), "已修改为："+(u.getSex() == true?"男":"女")
										, Toast.LENGTH_SHORT).show();
				//調用SetMyInfoActiviy中刷新性別的方法
				SetMyInfoActivity.changeSex(u.getSex());
			}
			@Override
			public void onFailure(int arg0, String arg1){
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "修改失败",
								Toast.LENGTH_SHORT).show();
			}
	  });
	}
	
}