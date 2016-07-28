package com.qiujing.administrator.fangweixinfriend.json;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class RetrofitAdapter extends BaseAdapter{
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
//
//    private Context context;
//    private List<Cook> list;
//
//    public RetrofitAdapter(Context context, List<Cook> list){
//        this.context = context;
//        this.list = list;
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return list.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        ViewHolder holder = null;
//        if (view == null){
//            view = LayoutInflater.from(context).inflate(R.layout.item_lv_tngou_main,viewGroup,false);
//            holder = new ViewHolder(view);
//            view.setTag(holder);
//        }else {
//            holder = (ViewHolder) view.getTag();
//        }
//        Cook cook = list.get(i);
//        holder.tv_title.setText(cook.getName());
//        holder.tv_info.setText(cook.getDescription());
//        //使用同样开发团队的Picasso支持包进行图片加载，
//        // 由于接口中返回的img路径不是全的，所以需要加上网站前缀
//        Picasso.with(context).load("http://tnfs.tngou.net/img"+
//                cook.getImg()).into(holder.imageView);
//
//        return view;
//    }
//
//    public void setList(List<Cook> cooks) {
//        this.list = cooks;
//    }
//
//    public List<Cook> getList() {
//        return this.list;
//    }
//
//    public static class ViewHolder{
//
//        private final ImageView imageView;
//        private final TextView tv_title;
//        private final TextView tv_info;
//
//        public ViewHolder(View item){
//            imageView = (ImageView) item.findViewById(R.id.iv_item);
//            tv_title = (TextView) item.findViewById(R.id.tv_item_title);
//            tv_info = (TextView) item.findViewById(R.id.tv_item_info);
//        }
//    }
}
