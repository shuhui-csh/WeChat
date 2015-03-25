package bmob.wechat.ui;

import bmob.wechat.bean.User;
import bmob.wechat.utils.ConnectUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

import com.wechat.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 注册界面
 * 
 * @ClassName: RegisterActivity
 * @Description: TODO
 * @author bright.van
 * @date 2014-11-1 17:00
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private EditText et_account, et_password, et_password_again;
	private Button register, back;
	private Context context = RegisterActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		init();
	}

	// 初始化
	private void init() {
		register = (Button) findViewById(R.id.bt_register);
		et_account = (EditText) findViewById(R.id.et_getaccount);
		et_password = (EditText) findViewById(R.id.et_getpassword);
		et_password_again = (EditText) findViewById(R.id.et_getpassword_again);
		back = (Button) findViewById(R.id.bt_back_register);
		register.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == back) {
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		if (v == register) {
			register();
		}
	}

	// 实现注册方法
	private void register() {
		String account = et_account.getText().toString();
		String password = et_password.getText().toString();
		String password_again = et_password_again.getText().toString();
		if (TextUtils.isEmpty(account)) {
			Toast.makeText(context, "用户名不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if (!password_again.equals(password)) {
			Toast.makeText(context, "输入的两次密码不一致", Toast.LENGTH_LONG).show();
			return;
		}
		boolean isNetConnected = ConnectUtil.isNetworkAvailable(this);
		if (!isNetConnected) {
			Toast.makeText(context, "当前网络不可用,请检查您的网络!", Toast.LENGTH_LONG)
					.show();
		}
		// 设置进度条对话框
		final ProgressDialog progress = new ProgressDialog(
				RegisterActivity.this);
		// 设置显示的信息
		progress.setMessage("正在注册...");
		// 设置ProgressDialog 是否可以按返回键取消
		progress.setCanceledOnTouchOutside(false);
		// 显示ProgressDialog
		progress.show();
		final User bu = new User();
		bu.setUsername(account);
		bu.setPassword(password);
		// 将user和设备id进行绑定
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				progress.dismiss();
				Toast.makeText(context, "注册成功", 1000).show();
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(bu.getUsername());
				// 发广播通知登陆页面退出
				sendBroadcast(new Intent("register.success.finish"));
				// 启动主页
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				Toast.makeText(context, "注册失败", 1000).show();
				progress.dismiss();
			}
		});

	}
}
