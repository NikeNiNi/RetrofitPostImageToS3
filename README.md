# RetrofitPostImageToS3
仿微信朋友圈图片浏览器，可以左右滑动，可以双击放大、捏拉放大，并将选择的图片用retrofit POST到Amazon S3

#项目介绍
从这个项目中，学习到：<br>
* Android Studio开发Android的基本配置
* 图片的缓存与压缩 
* 线程轮询与并行
* 反射机制在Android中的使用 
* Handler-线程间通信
* 使用ViewPager和Photoview实现图片左右滑动和放大缩小
* Retrofit框架的使用（GET和POST请求以及gson解析）
* Amazon S3的使用
#效果图

 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-23-07.png)
 <br>
 点击addphoto在弹出的PopupWindows中选择从相册选取，跳转到照片选择<br>
 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-25-24.png)
  <br>
  选择好的照片后在viewpager中显示
  <br>
 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-25-32.png) <br>
 双击或捏拉放大照片<br>
 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-25-45.png)
 <br>照片预览<br>
 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-25-49.png) <br>将选择的照片上传到Amazon S3<br>
 ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-16-25-59.png)
  <br>点击底部标题栏，弹出PopupWindows选择要进入的相册<br>
  ![image](https://raw.githubusercontent.com/ruanqiujing/RetrofitPostImageToS3/master/Images/Screenshot_2016-07-28-17-43-40.png)
