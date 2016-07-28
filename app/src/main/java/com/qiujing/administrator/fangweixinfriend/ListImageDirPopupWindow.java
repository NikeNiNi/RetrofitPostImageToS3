package com.qiujing.administrator.fangweixinfriend;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qiujing.administrator.fangweixinfriend.bean.FolderBean;

import java.util.List;

import util.ImageLoader;


public class ListImageDirPopupWindow extends PopupWindow {

	private int mWidth;
	private int mHeight;
	private View mConvertView;
	private ListView listView;
	private List<FolderBean> mDatas;

	public interface OnDirSelectedListener {
		void onSelect(FolderBean folderBean);
	}

	public OnDirSelectedListener mListener;

	public void setOnDirSelectedListener(OnDirSelectedListener mListener) {
		this.mListener = mListener;
	}

	public ListImageDirPopupWindow(Context context, List<FolderBean> datas) {
		calWidthAndHeight(context);
		mConvertView = LayoutInflater.from(context).inflate(
				R.layout.popup_photo, null);
		mDatas = datas;
		setContentView(mConvertView);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(mWidth);
		setHeight(mHeight);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		initView(context);
		initEvent();
	}

	private void initEvent() {

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (mListener != null) {
					mListener.onSelect(mDatas.get(position));
				}

			}
		});
	}

	private void initView(Context context) {
		listView = (ListView) mConvertView.findViewById(R.id.lv_popup_main);
		PopupAdapter adapter = new PopupAdapter(context, mDatas);
		listView.setAdapter(adapter);
	}

	/**
	 * 计算popupwindow的宽和高
	 *
	 * @param context
	 */
	private void calWidthAndHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mWidth = outMetrics.widthPixels;
		mHeight = (int) (outMetrics.heightPixels * 0.7);
	}

	private class PopupAdapter extends ArrayAdapter<FolderBean> {
		private LayoutInflater inflater;

		public PopupAdapter(Context context, List<FolderBean> objects) {
			super(context, 0, objects);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_popup_photo,
						parent, false);
				holder = new ViewHolder();
				holder.mTvName = (TextView) convertView
						.findViewById(R.id.tv_item_popup_name);
				holder.mTvCount = (TextView) convertView
						.findViewById(R.id.tv_item_popup_count);
				holder.mImg = (ImageView) convertView
						.findViewById(R.id.iv_item_popup_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			FolderBean bean = getItem(position);
			Log.i("test", bean.getName());
			// 重置
			holder.mImg.setImageResource(R.mipmap.picture_no);

			ImageLoader.getInstance().loadImage(bean.getFirstImgPath(),
					holder.mImg);
			holder.mTvName.setText(bean.getName());
			holder.mTvCount.setText(bean.getCount() + "张");
			return convertView;
		}

	}

	private class ViewHolder {
		private ImageView mImg;
		private TextView mTvName;
		private TextView mTvCount;
	}
}
