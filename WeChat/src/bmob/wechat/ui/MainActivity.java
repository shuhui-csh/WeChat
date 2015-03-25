package bmob.wechat.ui;

import bmob.wechat.residememu.ResideMenu;
import bmob.wechat.residememu.ResideMenuItem;
import bmob.wechat.ui.fragment.AddressListFragment;
import bmob.wechat.ui.fragment.SettingsFragment;
import bmob.wechat.ui.fragment.WechatFragment;
import bmob.wechat.utils.ExitUtil;
import cn.bmob.im.BmobChat;

import com.wechat.R;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * 主界面
 * 
 * @ClassName: MainActivity
 * @Description: TODO
 * @author bright.van
 * @date 2014-10-31 20:00
 */
public class MainActivity extends FragmentActivity implements
		View.OnClickListener {
	private TextView title;
	private Button WeChat, AddressList, Settings, AddFriends, Me;
	private ResideMenu resideMenu;
	private MainActivity mContext;
	private Context context = MainActivity.this;
	private ResideMenuItem itemNick, itemSex, itemAccount, itemAbatar;
	// 用于双击退出程序的判断时间变量
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		BmobChat.getInstance(this).startPollService(1);
		init();
		setUpMenu();
		changeFragment(new AddressListFragment());
	}

	// 初始化
	private void init() {
		WeChat = (Button) findViewById(R.id.btn_wechat);
		AddressList = (Button) findViewById(R.id.btn_address_list);
		Settings = (Button) findViewById(R.id.btn_settings);
		AddFriends = (Button) findViewById(R.id.add_friends);
		Me = (Button) findViewById(R.id.title_bar_left_menu);
		Me.setOnClickListener(this);
		AddressList.setSelected(true);
		WeChat.setOnClickListener(this);
		AddressList.setOnClickListener(this);
		Settings.setOnClickListener(this);
		AddFriends.setOnClickListener(this);
	}

	private void setUpMenu() {
		// 将侧滑菜单添加到当前的activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		// resideMenu.setMenuListener(menuListener);
		// 设置缩放比例值.
		resideMenu.setScaleValue(0.6f);
		// 创建菜单项;
		itemAccount = new ResideMenuItem(this, R.drawable.ic_account_set_icon,
				"账户");
		itemAbatar = new ResideMenuItem(this, R.drawable.ic_abatar_set_icon,
				"头像");
		itemNick = new ResideMenuItem(this, R.drawable.ic_nick_set_icon, "昵称");
		itemSex = new ResideMenuItem(this, R.drawable.ic_sex_set_icon, "性别");
		// 为每个菜单项创建点击事件
		itemAbatar.setOnClickListener(this);
		itemAccount.setOnClickListener(this);
		itemNick.setOnClickListener(this);
		itemSex.setOnClickListener(this);
		// 设置侧滑菜单是在左侧还是在右侧
		resideMenu.addMenuItem(itemAbatar, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemAccount, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemNick, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemSex, ResideMenu.DIRECTION_LEFT);
		// 通过设置可以禁用方向 ->
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	// 设置点击事件逻辑
	@Override
	public void onClick(View view) {
		// 为主界面按钮注册点击事件，通过changeFragment()方法替换fragment
		if (view == WeChat) {
			changeFragment(new WechatFragment());
			WeChat.setSelected(true);
			Settings.setSelected(false);
			AddressList.setSelected(false);
		} else if (view == AddressList) {
			changeFragment(new AddressListFragment());
			WeChat.setSelected(false);
			Settings.setSelected(false);
			AddressList.setSelected(true);
		} else if (view == Settings) {
			changeFragment(new SettingsFragment());
			WeChat.setSelected(false);
			Settings.setSelected(true);
			AddressList.setSelected(false);
		}
		// 为添加好友按钮注册点击事件
		if (view == AddFriends) {
			// 跳转到AddfriendsActivity
			Intent intent = new Intent();
			intent.setClass(context, AddfriendsActivity.class);
			startActivity(intent);
		}
		if (view == itemAbatar) {
			Intent intent = new Intent();
			intent.setClass(context, AbatarSetActivity.class);
			startActivity(intent);
		}
		if (view == itemNick) {
			Intent intent = new Intent();
			intent.setClass(context, NickSetActivity.class);
			startActivity(intent);
		}
		if (view == itemSex) {
			Intent intent = new Intent();
			intent.setClass(context, SexSetActivity.class);
			startActivity(intent);
		}
		if (view == Me) {
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		}
		// 关闭菜单
		// resideMenu.closeMenu();
	}

	private void changeFragment(Fragment targetFragment) {
		// 清除忽略视图列表
		resideMenu.clearIgnoredViewList();
		// 通过beginTransaction()方法即可开启并返回FragmentTransaction对象
		getSupportFragmentManager().beginTransaction()
				// replace()方法可以替换Fragment,从id-->targetFragment
				.replace(R.id.home_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
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
		else if (keyCode == KeyEvent.KEYCODE_MENU) {
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BmobChat.getInstance(this).stopPollService();
		super.onDestroy();
	}

}
