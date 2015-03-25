package bmob.wechat.ui;

import java.util.ArrayList;
import java.util.List;

import bmob.wechat.adapter.AddFriendAdapter;
import bmob.wechat.utils.CollectionUtils;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.wechat.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 添加好友界面
 * 
 * @ClassName: AddFriendsActivity
 * @Description: TODO
 * @author bright.van
 * @date 2014-10-31 20:00
 */
public class AddfriendsActivity extends Activity {
	EditText addFeiends;
	Button searchfriends;
	ProgressDialog progress;
	BmobUserManager userManager;
	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	AddFriendAdapter adapter;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addfriends);
		//initView();
	}

	public void initView() {
		addFeiends = (EditText) findViewById(R.id.et_searchfriends);
		searchfriends = (Button) findViewById(R.id.add_friends);
		lv = (ListView) findViewById(R.id.lv_addfriends);
		searchfriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

}
