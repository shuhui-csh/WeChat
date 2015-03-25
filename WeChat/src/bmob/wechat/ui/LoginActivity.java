package bmob.wechat.ui;

import bmob.wechat.config.Config;
import bmob.wechat.utils.ConnectUtil;
import bmob.wechat.utils.ExitUtil;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;
import com.wechat.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 登陆
 * 
 * @ClassName: LoginActivity
 * @Description: TODO
 * @author bright.van
 * @date 2014-11-1 13:00
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	public String BmobConstants = null;
	private Button login, register, forget;
	private EditText account, password;
	private Context context = LoginActivity.this;
	// 用于双击退出程序的判断时间变量
	private long mExitTime;
	BmobChatUser currentUser;
	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		// 初始化 Bmob SDK
		// 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		Bmob.initialize(this, Config.applicationId);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		init();
		// 注册退出广播
		IntentFilter filter = new IntentFilter();
		// 设置过滤action
		filter.addAction("register.success.finish");
		registerReceiver(receiver, filter);

	}

	// 注册登陆、注册等按钮的点击事件
	@Override
	public void onClick(View v) {
		if (v == register) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
		if (v == login) {
			boolean isNetConnected = ConnectUtil.isNetworkAvailable(this);
			if (!isNetConnected) {
				Toast.makeText(context, "当前网络不可用,请检查您的网络!", Toast.LENGTH_LONG)
						.show();
				return;
			}
			login();
		}
	}

	// 实现登陆方法
	private void login() {
		String accountstr = account.getText().toString();
		String passwordstr = password.getText().toString();
		if (TextUtils.isEmpty(accountstr)) {
			Toast.makeText(context, "用户名不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		if (TextUtils.isEmpty(passwordstr)) {
			Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		// 设置进度条对话框
		final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
		// 设置显示的信息
		progress.setMessage("正在登陆...");
		// 设置ProgressDialog 是否可以按返回键取消
		progress.setCanceledOnTouchOutside(true);
		// 显示ProgressDialog
		progress.show();
		userManager.login(accountstr, passwordstr, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 发送请求
						progress.setMessage("正在获取好友列表...");
					}
				});
				// 更新用户信息
				updateUserInfos();
				// 关闭此对话框，从屏幕上删除它。
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// 关闭此对话框，从屏幕上删除它。
				// 可以安全地从任何线程调用此方法。
				progress.dismiss();
				Toast.makeText(context, "登陆失败！", Toast.LENGTH_LONG).show();
			}
		});

	}

	private void init() {
		// 初始化各个控件
		account = (EditText) findViewById(R.id.et_account);
		password = (EditText) findViewById(R.id.et_password);
		login = (Button) findViewById(R.id.bt_login);
		register = (Button) findViewById(R.id.bt_register);
		forget = (Button) findViewById(R.id.bt_forget);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		// forget.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& "register.success.finish".equals(intent.getAction())) {
				finish();
			}
		}
	}

	// 使用onKeyDown()方法监听菜单键和返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 监听返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 双击退出程序，调用一个退出程序类
			ExitUtil myExit = new ExitUtil(this);
			mExitTime = myExit.exit(mExitTime);
			return false;
		}
		// 为菜单按钮添加点击事件
		// else if (keyCode == KeyEvent.KEYCODE_MENU) {
		// }
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
