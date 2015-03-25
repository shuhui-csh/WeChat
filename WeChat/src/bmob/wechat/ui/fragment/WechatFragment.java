package bmob.wechat.ui.fragment;

import java.util.List;
import bmob.wechat.adapter.MessageRecentAdapter;
import bmob.wechat.ui.ChatActivity;
import bmob.wechat.ui.view.ClearEditText;
import bmob.wechat.ui.view.DialogTips;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import com.wechat.R;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @ClassName: RecentSessionFragment
 * @Description:
 * @author shuwei-csh
 */
public class WechatFragment extends BaseFragement implements
		OnItemClickListener, OnItemLongClickListener {
	private View parentView;
	private List<String> list;
	private ListView WechatlistView;
	ClearEditText mClearEditText;
	ListView listview;
	MessageRecentAdapter adapter;
	private TextView tv_title;
	private Button btn_title_left,btn_title_right;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.wechat_fragment, container,
				false);// 加载布局文件
		listview = (ListView) parentView.findViewById(R.id.chat_list);
		// 对标题栏的一些设置
		setHeadLayout();
		// BmobDB.create(getActivity().queryRecents()从本地数据库里面拿到最近会话列表，放到adapter里。
		adapter = new MessageRecentAdapter(getActivity(),
				R.layout.item_conversation, BmobDB.create(getActivity())
						.queryRecents());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(WechatFragment.this);
		listview.setOnItemLongClickListener(WechatFragment.this);
		return parentView;
	}

	/**
	 * onActivityCreated() 当activity的onCreated()方法返回后调用此方法。
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// initView();
	}

	// 对标题栏的一些设置
	@SuppressLint("ResourceAsColor")
	private void setHeadLayout() {
		// 对与之相关的Activity带有的标题栏进行设置
		// 标题栏信息框显示标题
		tv_title = (TextView) getActivity().findViewById(R.id.tv_title_main);
		tv_title.setText("最近会话");
		// 左边按钮设置为显示
		btn_title_left = (Button) getActivity().findViewById(
				R.id.title_bar_left_menu);
		btn_title_left.setVisibility(View.VISIBLE);
		btn_title_left.setClickable(true);
		// 右边按钮设置为隐藏
		btn_title_right = (Button) getActivity().findViewById(R.id.add_friends);
		btn_title_right.setVisibility(View.GONE);
		btn_title_right.setClickable(false);

	}

	// private void initView() {
	// initTopBarForOnlyTitle("会话");
	// listview = (ListView) findViewById(R.id.chat_list);
	// listview.setOnItemClickListener(this);
	// listview.setOnItemLongClickListener(this);
	// adapter = new MessageRecentAdapter(getActivity(),
	// R.layout.item_conversation, BmobDB.create(getActivity())
	// .queryRecents());
	// listview.setAdapter(adapter);
	//
	// //mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
	// mClearEditText.addTextChangedListener(new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before,
	// int count) {
	// adapter.getFilter().filter(s);
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	//
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	// }
	// });
	//
	// }

	/**
	 * 删除会话 deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteRecent(BmobRecent recent) {
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(), recent.getUserName(),
				"删除会话", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		// 重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		// 组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(),
							R.layout.item_conversation, BmobDB.create(
									getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

}
