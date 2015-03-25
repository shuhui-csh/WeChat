package bmob.wechat.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import bmob.wechat.bean.User;
import bmob.wechat.config.BmobConstants;
import bmob.wechat.utils.ImageLoadOptions;
import bmob.wechat.utils.PhotoUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wechat.R;

/**
 * 以下两种情况会调用此类： 1.设置我的个人资料 2.查找好友成功后的显示结果
 * 
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class SetMyInfoActivity extends BaseActivity implements OnClickListener {
	Button btn_back, btn_chat, btn_add;
	RelativeLayout rl_headLayout, rl_nickLayout, rl_accountLayout,
			rl_sexLayout;
	FrameLayout backFrameLayout;
	LinearLayout layout_info;
	// 显示用户账号，标题栏文字
	TextView tv_account, tv_title;
	// 用户昵称，用户性别文本框
	static TextView tv_nick, tv_sex;
	ImageView iv_head, iv_head_arrow;
	String from = "";
	String nickName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		setContentView(R.layout.activity_set_info);

		initView();
	}

	private void initView() {
		// 初始化所有组件
		layout_info = (LinearLayout) findViewById(R.id.layout_Info);
		backFrameLayout = (FrameLayout) findViewById(R.id.back_layout);
		btn_back = (Button) findViewById(R.id.back_btn);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_add = (Button) findViewById(R.id.btn_add);
		rl_headLayout = (RelativeLayout) findViewById(R.id.rl_info_head);
		rl_nickLayout = (RelativeLayout) findViewById(R.id.rl_info_nickname);
		rl_accountLayout = (RelativeLayout) findViewById(R.id.rl_info_account);
		rl_sexLayout = (RelativeLayout) findViewById(R.id.rl_info_sex);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		iv_head_arrow = (ImageView) findViewById(R.id.iv_head_arrow);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_nick = (TextView) findViewById(R.id.tv_nick);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		// 返回按钮设置监听
		btn_back.setOnClickListener(this);
		// 获取传进此Activity的标识me或者是others
		from = getIntent().getStringExtra("from");
		// 如果from携带的值是me,表示的是个人资料
		if (from.equals("me")) {
			// 设置标题栏为个人资料
			tv_title.setText("个人资料");
			// 为头像设置监听
			rl_headLayout.setOnClickListener(this);
			// 为昵称设置监听器
			rl_nickLayout.setOnClickListener(this);
			// 为账户设置监听器
			rl_accountLayout.setOnClickListener(this);
			// 为性别设置监听器
			rl_sexLayout.setOnClickListener(this);
			// 隐藏发起会话的按钮
			btn_chat.setVisibility(View.INVISIBLE);
			// 隐藏加为好友的按钮
			btn_add.setVisibility(View.INVISIBLE);
		} else {
			tv_title.setText("详细资料");
			// 。。。。这里靠队友了。。。。信息栏不需要设置监听，并隐藏信息栏最右边的箭头即可

		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		/**
		 * 设置为竖屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		// onResume在onCreate后面，此时from字符串已经被初始化
		// 如果是标识符为me，就是自己的数据
		if (from.equals("me")) {
			initMyData();
		}
	}

	// 从User表里查找得到的指定的User实例
	User user;

	// 把我的用户资料存储到本地
	// SharedPreferences userPreferences;
	// SharedPreferences.Editor userEditor;

	private void initMyData() {
		// userPreferences = getSharedPreferences("user", MODE_PRIVATE);
		User mySelf = userManager.getCurrentUser(User.class);
		// 初始化我的用户的其他数据
		initElse(mySelf.getUsername());

	}

	/**
	 * 此方法2个用途： 第一个用途：初始化登陆用户的其他数据 第二个用途：查询其他用户的资料直接调用此方法即可
	 */
	private void initElse(String userName) {
		// 查找指定名称的用户
		userManager.queryUser(userName, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "on Error" + arg1,
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSuccess(List<User> arg0) {
				// List内容不为空
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					// 如果是登陆用户（from键的值为me），以下这俩句话是无效的，
					// 因为，这些按钮都被隐藏了
					btn_chat.setEnabled(true);
					btn_add.setEnabled(true);
					// 更新用户资料
					updateInfo(user);
				} else {
					Toast.makeText(getApplicationContext(), "没有此用户",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	// 更新用户数据
	protected void updateInfo(User user) {
		// 更新头像
		refreshAvatar(user.getAvatar());
		// 显示账户名
		tv_account.setText(user.getUsername());
		// 显示昵称
		tv_nick.setText(user.getNick());
		tv_sex.setText(user.getSex() == true ? "男" : "女");
		// 如果是查询的是其他用户还需要进行其他业务操作
		//显示性别
		tv_sex.setText(user.getSex() == true? "男" :"女");
		//如果是查询的是其他用户还需要进行其他业务操作,添加好友操作要用到
		if (!from.equals("me")) {			
		}
	}

	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws ImageLoader.getInstance
	 *             ().displayImage(avatar,)
	 */
	private void refreshAvatar(String avatar) {
		// avatar为头像标识，如果为空的话就加载默认图片
		if (avatar != null && !avatar.equals("")) {
			// 图片加载器实例化，展示图片
			/**
			 * @method displayImage 展示图片
			 * @param String
			 *            用户头像标识
			 * @param ImageView
			 *            用户头像填充的ImageView
			 * @param DisplayImageOptions
			 *            图片加载配置
			 */
			ImageLoader.getInstance().displayImage(avatar, iv_head,
					ImageLoadOptions.getOptions());

		} else {
			// 否则就显示默认图表
			iv_head.setImageResource(R.drawable.default_head);
		}
	}

	/**
	 * 点击事件均会触发这个方法
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 左上角返回按钮
		case R.id.back_btn:
			// 跳回SettingsFragment
			finish();
			break;

		case R.id.rl_info_head:
			// 从底部谈起动画效果
			showAvatarPop();
			break;
		// 点击的是昵称栏
		case R.id.rl_info_nickname:
			Log.i("account", "准备修改昵称");
			Intent intent = new Intent(SetMyInfoActivity.this,
					NickSetActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_info_account:
			Toast.makeText(getApplicationContext(), "账户管理", Toast.LENGTH_SHORT)
					.show();
			break;
		// 点击显示性被选择的Diaglog
		case R.id.rl_info_sex:
			showSelectSexDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 与性别修改有关的(start)，我是华丽的分割线
	 */
	// 弹出性别设置对话框
	private void showSelectSexDialog() {
		// 跳转到设置性别的对话框
		final Intent intent = new Intent(this, SexSetActivity.class);
		startActivity(intent);
	}

	// 刷新性別，在SexSelectedActivity中被调用
	public static void changeSex(boolean sex) {
		tv_sex.setText(sex == true ? "男" : "女");
	}

	/**
	 * 与性别修改有关的（end）,我是华丽的分割线
	 */

	// 刷新昵称，在SexSelectedActivity中被调用
	public static void changeNick(String nickName, User user) {
		// TODO Auto-generated method stub
		tv_nick.setText(nickName);
		// 不知道为什么设置完昵称返回来之后性别就自动变为女
		tv_sex.setText(user.getSex() == true ? "男" : "女");
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	/**
	 * 底部弹起popWindow,里面有两个按钮，拍照上传，本地上传
	 */
	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
				null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		// 点击的是拍照上传
		layout_photo.setOnClickListener(new OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View arg0) {
				// 设置拍照“按钮”被点击的状态
				layout_photo.setBackgroundColor(getResources().getColor(
						R.color.half_trans_blue));
				// 创建一个File对象传入的是我的头像本地目录
				File dir = new File(BmobConstants.MyAvatarDir);
				// 不存在该文件夹，则创建该文件目录
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 在dir路径下创建一个当前时间命名的文件，用来保存待会拍下的照片
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				// 获取相片的保存路径
				filePath = file.getAbsolutePath();
				// 从一个文件中创建一个Uri
				Uri imageUri = Uri.fromFile(file);
				// 调用系统自带的相机
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定该imageUri是用来存储照片的
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				// 启动指定的Activity,传入intent和拍照上传的请求码
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 本地选择图片上传
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.half_trans_blue));
				// ACTION_PICK：从列表中选中某个项，返回所选数据
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				// data：Uri对象：多媒体数据库中的图像库URI
				// type:MIME类型，这里是图片类型
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				// 启动指定的Activity，传入intent和本地相册修改头像的请求码
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		// 实例化popWindow
		/**
		 * 传入的是 LayoutInflater解析出的View 宽度 高度
		 */

		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true;
				}
				return false;
			}
		});

		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_info, Gravity.BOTTOM, 0, 0);
	}

	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	/**
	 * 上传头像
	 */
	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl();
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	/**
	 * 更新头像
	 */
	private void updateUserAvatar(final String url) {
		User user = (User) userManager.getCurrentUser(User.class);
		user.setAvatar(url);
		user.update(this, new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("头像更新成功！");
				// 更新头像
				refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	@SuppressLint("SimpleDateFormat")
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_head.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				path = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

}
