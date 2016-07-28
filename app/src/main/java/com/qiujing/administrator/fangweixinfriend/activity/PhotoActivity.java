package com.qiujing.administrator.fangweixinfriend.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiujing.administrator.fangweixinfriend.bean.FolderBean;
import com.qiujing.administrator.fangweixinfriend.adapter.ImageAdapter;
import com.qiujing.administrator.fangweixinfriend.ListImageDirPopupWindow;
import com.qiujing.administrator.fangweixinfriend.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhotoActivity extends AppCompatActivity {

	protected static final int DATA_LOADGER = 0x110;
	private GridView mGridView;
	private List<String> mImgs;

	private RelativeLayout mBottomLy;
	private TextView mDirName;
	private TextView mDirCount;

	private File mCurrentDir;
	private int mMaxCount;
	private List<FolderBean> mFolderBeans = new ArrayList<FolderBean>();
	private ListImageDirPopupWindow mDirPopupWindow;
	public static Bitmap bimap;

	/**
	 * 扫描图片的进度条
	 */
	private ProgressDialog mProgressDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == DATA_LOADGER) {
				mProgressDialog.dismiss();
				// 绑定数据到View中
				data2View();
				initDirPopupWindow();

			}

		};
	};
	private ImageAdapter adapter;
	private TextView complete;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		getSupportActionBar().hide();
		initView();
		initDatas();
		initEvent();
		initData();
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		// /**
		// * 这里，我们假设已经从网络或者本地解析好了数据，所以直接在这里模拟了10个实体类，直接装进列表中
		// */
		// dataList = new ArrayList<Entity>();
		// for(int i=-0;i<10;i++){
		// Entity entity = new Entity(R.drawable.picture, false);
		// dataList.add(entity);
		// }
//		dataList = helper.getImagesBucketList(false);
//		bimap= BitmapFactory.decodeResource(
//				getResources(),
//				R.mipmap.icon_addpic_unfocused);
	}
	protected void initDirPopupWindow() {
		mDirPopupWindow = new ListImageDirPopupWindow(this, mFolderBeans);
		mDirPopupWindow.setOutsideTouchable(true);
		mDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				lightOn();
			}
		});
		mDirPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {

			@Override
			public void onSelect(FolderBean folderBean) {
				mCurrentDir = new File(folderBean.getDir());
				mImgs = Arrays.asList(mCurrentDir.list(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".jpg")
								|| filename.endsWith(".jpeg")
								|| filename.endsWith(".png")) {
							return true;
						}

						return false;
					}
				}));

				adapter = new ImageAdapter(PhotoActivity.this, mImgs,
						mCurrentDir.getAbsolutePath());
				mGridView.setAdapter(adapter);
				mDirName.setText(folderBean.getName());
				mDirCount.setText(mImgs.size() + "张");
				mDirPopupWindow.dismiss();
			}
		});

	}

	/**
	 * popupWindow消失后，内容区域变亮
	 */
	protected void lightOn() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}

	/**
	 * popupWindow出现后，内容区域变暗
	 */
	protected void lightOff() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.3f;
		getWindow().setAttributes(lp);
	}

	protected void data2View() {
		if (mCurrentDir == null) {
			Toast.makeText(PhotoActivity.this, "未扫描到任何图片", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mImgs = Arrays.asList(mCurrentDir.list());
		adapter = new ImageAdapter(PhotoActivity.this, mImgs,
				mCurrentDir.getAbsolutePath());
		mGridView.setAdapter(adapter);
		mDirCount.setText(mMaxCount + "张");
		mDirName.setText(mCurrentDir.getName());

	}

	private void initEvent() {
		mBottomLy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设置动画
				mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);

				mDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				lightOff();

			}
		});
		complete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(PhotoActivity.this,MainActivity.class));
				finish();
			}
		});
	}

	/**
	 * 利用ContentProvider扫描手机中的所有图片
	 */
	private void initDatas() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "储存卡不可用", Toast.LENGTH_SHORT).show();
			return;
		}
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread() {
			public void run() {
				Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver resolver = PhotoActivity.this
						.getContentResolver();
				Cursor cursor = resolver.query(mImgUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				// 防止重复遍历，Set最大的特性就是不允许在其中存放的元素是重复的。根据这个特点，我们就可以使用Set
				// 这个接口来实现前面提到的关于商品种类的存储需求。Set
				// 可以被用来过滤在其他集合中存放的元素，从而得到一个没有包含重复新的集合。
				Set<String> mDirPaths = new HashSet<String>();
				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					File parentFile = new File(path).getParentFile();
					if (parentFile == null) {
						continue;
					}
					String dirPath = parentFile.getAbsolutePath();
					FolderBean folderBean = null;
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						folderBean = new FolderBean();
						folderBean.setDir(dirPath);
						folderBean.setFirstImgPath(path);
						if (parentFile.list() == null) {
							continue;
						}
						int picSize = parentFile.list(new FilenameFilter() {

							@Override
							public boolean accept(File dir, String filename) {
								if (filename.endsWith(".jpg")
										|| filename.endsWith(".jpeg")
										|| filename.endsWith(".png")) {
									return true;
								}

								return false;
							}
						}).length;
						folderBean.setCount(picSize);
						mFolderBeans.add(folderBean);
						if (mMaxCount < picSize) {
							mMaxCount = picSize;
							mCurrentDir = parentFile;
						}
					}
				}
				cursor.close();
				/**
				 * 通知Handler图片扫描完成
				 */
				mHandler.sendEmptyMessage(DATA_LOADGER);
			};
		}.start();

	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.gv_main);
		mBottomLy = (RelativeLayout) findViewById(R.id.rl_bottom);
		mDirName = (TextView) findViewById(R.id.dir_name);
		mDirCount = (TextView) findViewById(R.id.dir_count);
		complete = (TextView) findViewById(R.id.tv_complete_photo);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			startActivity(new Intent(PhotoActivity.this,MainActivity.class));
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
