package com.qiujing.administrator.fangweixinfriend.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiujing.administrator.fangweixinfriend.Bimp;
import com.qiujing.administrator.fangweixinfriend.adapter.ImageAdapter;
import com.qiujing.administrator.fangweixinfriend.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView addPhoto;
    private ViewPager pager;
    private PopupWindows popupWindows;
    private MyPageAdapter adapter;
    public static List<Bitmap> bmp = new ArrayList<Bitmap>();
    private int count;
    private boolean isShowPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initBmp();
        addPhoto = (TextView) findViewById(R.id.tv_add_photo_main);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOnPageChangeListener(pageChangeListener);
        if (adapter == null) {
            // 构造adapter
            adapter = new MyPageAdapter();
            pager.setAdapter(adapter);// 设置适配器
        } else {
            adapter.notifyDataSetChanged();
        }


    }

    private void initBmp() {

        if (ImageAdapter.mSelectImg.size() != 0) {
            bmp.clear();
            for (int i = 0; i < ImageAdapter.mSelectImg.size(); i++) {
                try {
                    bmp.add(Bimp.revisionImageSize(ImageAdapter.mSelectImg.get(i)));
                    isShowPhoto = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            bmp.clear();
            isShowPhoto = false;
        }
    }

    public void addPhoto(View view) {
        showPopupWindows();

    }

    private void showPopupWindows() {
        popupWindows = new PopupWindows(MainActivity.this, addPhoto);
        popupWindows.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000FF")));
        popupWindows.setOutsideTouchable(true);
        lightOff();
        popupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
    }

    /**
     * popupWindow出现后，内容区域变暗
     */
    protected void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    /**
     * popupWindow消失后，内容区域变亮
     */
    protected void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);

    }

    public void back(View view) {
        finish();
    }

    public class PopupWindows extends PopupWindow {
        public PopupWindows(Context mContext, View parent) {
            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            // 设置动画
            setAnimationStyle(R.style.dir_popupwindow_anim);
            setFocusable(true);
            setTouchable(true);
            setOutsideTouchable(true);
            setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();
            final RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                }
            });
            Button takePhoto = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button selectFromAlbum = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button cancel = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            takePhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    takePhoto();
                    dismiss();

                }
            });
            selectFromAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, PhotoActivity.class));
                    dismiss();
                    finish();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindows != null) {
                        popupWindows.dismiss();
                    }
                }
            });
        }

    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path;

    protected void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/myimage/" + String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
//                    ImageAdapter.mSelectImg.add(path);
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Bitmap newBitmap = Bimp.compressImage(bitmap);
                    Log.i("test","4444444444.."+bitmap);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    if (bitmap != null){
                        bitmap.recycle();
                        bmp.add(bitmap);
                    }
                    adapter.notifyDataSetChanged();

//                    startActivity(new Intent(MainActivity.this, ShowPhotoActivity1.class));
                        Log.i("test","333333333333333"+path);
                    break;
            }
        }
    }


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {// 页面选择响应函数
            count = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。
        }

        public void onPageScrollStateChanged(int arg0) {// 滑动状态改变
            if (arg0 == 2) {
            }
        }
    };

    class MyPageAdapter extends PagerAdapter {


        public MyPageAdapter() {// 构造函数

        }

        public int getCount() {// 返回数量
            if (bmp.size() == 0){
               return 1;
            }
            return bmp.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Object instantiateItem(ViewGroup container, final int position) {// 返回view对象
            if (isShowPhoto) {
                ImageView content = new ImageView(container.getContext());
                try {
                    content.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                   Log.i("test",",,,,,,,,,,,"+position);
                    content.setImageBitmap(bmp.get(position));
                    container.addView(content);
                    content.setClickable(true);
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MainActivity.this, ShowPhotoActivity1.class);
                            intent.putExtra("position", position);
                            startActivity(intent);
                            finish();
                        }
                    });

                } catch (Exception e) {
                    Log.e("crash", "fail", e);
                }
                return content;
            } else {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.view_add_photo, null);
                container.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupWindows();
                    }
                });
                return view;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
