package com.qiujing.administrator.fangweixinfriend.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qiujing.administrator.fangweixinfriend.Bimp;
import com.qiujing.administrator.fangweixinfriend.R;

import java.util.ArrayList;
import java.util.List;

import util.ImageLoader;

public class ImageAdapter extends BaseAdapter {
    public static List<String> mSelectImg = new ArrayList<>();
    private String mDirPath;
    private List<String> mImgPaths = new ArrayList<String>();
    private LayoutInflater mInflater;
    private Context context;

    public ImageAdapter(Context context, List<String> mDatas, String dirPath) {
        this.mDirPath = dirPath;
        this.context = context;
        this.mImgPaths = mDatas;
        Log.i("test","00000000000"+mImgPaths.size());
        mInflater = LayoutInflater.from(context);
       setPreviewImgDatas();
    }

    private void setPreviewImgDatas() {
        Bimp.albumDir.clear();
        for (String imgPaths:mImgPaths){
            Bimp.albumDir.add(mDirPath+"/"+imgPaths);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.items_gridview, parent,
                    false);
            holder = new ViewHolder();
            holder.mImg = (ImageView) convertView
                    .findViewById(R.id.iv_item_image);
            holder.mSelect = (ImageButton) convertView
                    .findViewById(R.id.ib_item_select);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // ����״̬
        holder.mImg.setImageResource(R.mipmap.picture_no);
        holder.mSelect.setImageResource(R.mipmap.unselected);
        holder.mImg.setColorFilter(null);

        ImageLoader.getInstance(3,
                ImageLoader.Type.LIFO).loadImage(
                mDirPath + "/" + mImgPaths.get(position), holder.mImg);
        final String filePath = mDirPath + "/" + mImgPaths.get(position);

        holder.mSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mSelectImg.contains(filePath)) {
//                    mSelectImg.remove(filePath);
//                    holder.mImg.setColorFilter(null);
//                    holder.mSelect.setImageResource(R.mipmap.unselected);
//
//                } else {
//
//                    mSelectImg.add(filePath);
//                    holder.mImg.setColorFilter(Color.parseColor("#77000000"));
//                    holder.mSelect.setImageResource(R.mipmap.selected);
//                }
            }
        });
        holder.mImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(context,
//                        ShowPhotoActivity1.class);
//                intent.putExtra("ID", position);
//                context.startActivity(intent);
                if (mSelectImg.contains(filePath)) {
                    mSelectImg.remove(filePath);
                    holder.mImg.setColorFilter(null);
                    holder.mSelect.setImageResource(R.mipmap.unselected);

                } else {

                    mSelectImg.add(filePath);
                    holder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    holder.mSelect.setImageResource(R.mipmap.selected);
                }

            }
        });

        if (mSelectImg.contains(filePath)) {
            holder.mImg.setColorFilter(Color.parseColor("#77000000"));
            holder.mSelect.setImageResource(R.mipmap.selected);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
        ImageButton mSelect;
    }

}