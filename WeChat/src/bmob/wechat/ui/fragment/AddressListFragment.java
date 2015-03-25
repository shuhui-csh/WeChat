package bmob.wechat.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bmob.wechat.adapter.UserFriendAdapter;
import bmob.wechat.bean.User;
import bmob.wechat.ui.FriendDtail;
import bmob.wechat.ui.view.ClearEditText;
import bmob.wechat.ui.view.HeaderLayout;
import bmob.wechat.ui.view.MyLetterView;
import bmob.wechat.ui.view.MyLetterView.OnTouchingLetterChangedListener;
import bmob.wechat.utils.CharacterParser;
import bmob.wechat.utils.PinyinComparator;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.wechat.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 通讯录界面
 * 
 * @ClassName: AddressListFragment
 * @Description: TODO
 * @author bright.van,huan.huang
 * @date 2014-10-31 20:00
 */
public class AddressListFragment extends Fragment {
	private BmobUserManager userManager;
	private ListView lv;
	private List<BmobChatUser> bmobChatUserList = new ArrayList<BmobChatUser>();
	private List<User> friends = new ArrayList<User>();
	private MyLetterView myLetterView;
	private View parentView;
	TextView dialog;
	// 与Fragment相关的Activity的标题栏布局，标题栏信息框，右边的按钮
	TextView tv_title;
	Button btn_title_right, btn_title_left;
	UserFriendAdapter userFriendAdapter;
	ClearEditText mClearEditText;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private HeaderLayout headerLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.address_list_fragment,
				container, false);// 加载布局文件

		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initRightLetterView();
		initEditText();
		lv = (ListView) parentView.findViewById(R.id.lv);
		setHeadLayout();
		userManager = BmobUserManager.getInstance(getActivity());
		// 查询所有的联系人
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				Toast.makeText(getActivity(), "失败", 1000).show();
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					Log.i("AddressListFragment", "失败" + arg1);
				} else {
					Log.i("AddressListFragment", "查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {		
				// 成功，得到联系人列表，将其设置到ListView中去
				bmobChatUserList = arg0;
				setAdapter();
			}
		});
		/*
		 * 点击进去查看好友详细信息
		 */
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), FriendDtail.class);
				User user = friends.get(position);
				String avatar = user.getAvatar();
				String nick = user.getNick();
				String userName = user.getUsername();
				Boolean sex = user.getSex();
				intent.putExtra("sex", sex);
				intent.putExtra("avatar", avatar);
				intent.putExtra("nick", nick);
				intent.putExtra("userName", userName);
				getActivity().startActivity(intent);
			}
		});
		/*
		 * 设置监听，长按删除
		 */
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("删除！");
				builder.setMessage("您想将好友“"
						+ friends.get(position).getUsername() + "”删除？");
				builder.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						userManager.deleteContact(friends.get(position)
								.getObjectId(), new UpdateListener() {

							@Override
							public void onSuccess() {
								Toast.makeText(getActivity(), "删除成功", 2000)
										.show();
								refresh();

							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(getActivity(), "删除失败", 2000)
										.show();
							}
						});
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
		return parentView;
	}

	public void queryMyfriends() {
		// 查询所有的联系人
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				Toast.makeText(getActivity(), "失败", 1000).show();
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					Log.i("AddressListFragment", "失败" + arg1);
				} else {
					Log.i("AddressListFragment", "查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(getActivity(), arg0.size() + "", 1000).show();
				// 成功，得到联系人列表，将其设置到ListView中去
				bmobChatUserList = arg0;
				setAdapter();
			}
		});
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setHeadLayout();
		refresh();
	}

	// 对标题栏的一些设置
	@SuppressLint("ResourceAsColor")
	private void setHeadLayout() {
		// 对与之相关的Activity带有的标题栏进行设置
		// 标题栏信息框显示标题
		tv_title = (TextView) getActivity().findViewById(R.id.tv_title_main);
		tv_title.setText("通讯录");
		// 左边按钮设置为隐藏
		btn_title_left = (Button) getActivity().findViewById(
				R.id.title_bar_left_menu);
		btn_title_left.setVisibility(View.GONE);
		btn_title_left.setClickable(false);
		// 右边按钮设置为显示
		btn_title_right = (Button) getActivity().findViewById(R.id.add_friends);
		btn_title_right.setVisibility(View.VISIBLE);
		btn_title_right.setClickable(true);
	}

	/*
	 * 将List<BmobChatUser>转化为List<User>
	 */
	private List<User> filledData(List<BmobChatUser> datas) {
		// 清除原有list
		Log.i("MainActivity", datas.size() + "-----data");
		List<User> friends = new ArrayList<User>();
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());// 昵称
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getUsername();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);// 添加到盆友的数组中去
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
		return friends;
	}

	/*
	 * 设置ListView
	 */
	public void setAdapter() {
		Log.i("MainActivity", bmobChatUserList.size() + "===");
		friends = filledData(bmobChatUserList);
		Log.i("MainActivity", friends.size() + "---->friends");
		userFriendAdapter = new UserFriendAdapter(getActivity(), friends);
		lv.setAdapter(userFriendAdapter);
	}

	/*
	 * 右边字母顺序
	 */
	private void initRightLetterView() {
		myLetterView = (MyLetterView) parentView
				.findViewById(R.id.right_letter);
		dialog = (TextView) parentView.findViewById(R.id.dialog);
		myLetterView.setTextView(dialog);
		myLetterView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = userFriendAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				lv.setSelection(position);
			}
		}
	}

	private void initEditText() {
		mClearEditText = (ClearEditText) parentView
				.findViewById(R.id.et_msg_search);
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<User> filterDateList = new ArrayList<User>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = friends;
		} else {
			filterDateList.clear();
			for (User sortModel : friends) {
				String name = sortModel.getUsername();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		userFriendAdapter.updateListView(filterDateList);
	}

}
