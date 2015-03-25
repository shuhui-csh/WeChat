package bmob.wechat.ui;

import bmob.wechat.utils.ImageLoadOptions;

import cn.bmob.im.bean.BmobChatUser;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wechat.R;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendDtail extends Activity {
	private Button back = null;
	private ImageView avatar = null;
	private TextView nick = null;
	private TextView userName = null;
	private TextView sex = null;
	private Button chat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend_dtail);
		init();
		set();

		chat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BmobChatUser user = new BmobChatUser();
				user.setUsername("huan");
				user.setObjectId("b9a0fde58d");
				Intent intent = new Intent(FriendDtail.this, ChatActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
				// Toast.makeText(FriendDtail.this, "功能正在完善中", 1000).show();
			}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	void init() {
		back = (Button) findViewById(R.id.back_btn);
		avatar = (ImageView) findViewById(R.id.iv_head);
		nick = (TextView) findViewById(R.id.tv_nick);
		userName = (TextView) findViewById(R.id.tv_account);
		sex = (TextView) findViewById(R.id.tv_sex);
		chat = (Button) findViewById(R.id.btn_chat);
	}

	void set() {
		Intent intent = getIntent();
		String avatar_ = intent.getStringExtra("avatar");
		String nick_ = intent.getStringExtra("nick");
		String userName_ = intent.getStringExtra("userName");
		Boolean sex_ = intent.getBooleanExtra("sex", false);
		nick.setText(nick_);
		userName.setText(userName_);
		if (sex_) {
			sex.setText("男");
		} else {
			sex.setText("女");
		}
		refreshAvatar(avatar_);
	}
	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar_) {
		if (avatar_ != null && !avatar_.equals("")) {
			ImageLoader.getInstance().displayImage(avatar_, avatar,
					ImageLoadOptions.getOptions());
		} else {
			avatar.setImageResource(R.drawable.head);
		}
	}
}
