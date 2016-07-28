package util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SuppressLint("NewApi")
public class ImageLoader {
	private static ImageLoader mInstance;
	/**
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEFAULT_THREAD_COUNT = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 *
	 * @author Administrator
	 *
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 *
	 * @author Administrator
	 *
	 */

	private Thread mPoolThread;
	private Handler mPoolThreadHandler;

	/**
	 * UI线程中的Handler
	 *
	 * @author Administrator
	 *
	 */
	private Handler mUIHandler;
	/**
	 * 信号量
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	public enum Type {
		FIFO, LIFO;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	private void init(int threadCount, Type type) {
		// 后台轮询线程
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// 线程池取出一个执行任务进行
						mThreadPool.execute(getTask());
						try {
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				};
				// 初始化完毕，释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			};
		};
		mPoolThread.start();
		// 获取我们应用的最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int maxCache = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(maxCache) {
			@Override
			protected int sizeOf(String key, Bitmap value) {

				return value.getRowBytes() * value.getHeight();
			}
		};
		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);

	}

	/**
	 * 从任务队列取出一个方法
	 *
	 * @return
	 */
	private Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTaskQueue.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTaskQueue.removeLast();
		}
		return null;
	}

	public static ImageLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(DEFAULT_THREAD_COUNT, Type.FIFO);
				}
			}
		}
		return mInstance;
	}

	public static ImageLoader getInstance(int threadCount, Type type) {
		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据path为imageview设置图片
	 *
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		imageView.setTag(path);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					// 将path与getTag里的存储的路径进行比较
					if (imageView.getTag().toString().equals(path)) {
						imageView.setImageBitmap(bm);
					}
				}
			};
		}
		// 根据path在缓存中获取图片
		final Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null) {
			refreshBitmap(path, imageView, bm);
		} else {
			addTask(new Runnable() {

				@Override
				public void run() {
					// 加载图片
					// 图片的压缩
					// 1、获取图片需要显示的大小
					ImageSize imageSize = getImageViewSize(imageView);
					// 2、压缩图片
					Bitmap bm = decodeSampleBitmapFromPath(path,
							imageSize.width, imageSize.height);
					// 3、将图片加载到缓存中去
					addBitmapToLruCache(path, bm);
					refreshBitmap(path, imageView, bm);
					mSemaphoreThreadPool.release();
				}
			});
		}
	}

	private void refreshBitmap(final String path, final ImageView imageView,
							   final Bitmap bm) {
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.path = path;
		holder.imageView = imageView;
		holder.bitmap = bm;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	protected void addBitmapToLruCache(String path, Bitmap bm) {

		if (getBitmapFromLruCache(path) == null) {
			if (bm != null) {
				mLruCache.put(path, bm);
			}
		}

	}

	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 *
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampleBitmapFromPath(String path, int width,
												int height) {
		// 获得图片的宽和高，并不把图片加载到内存中去
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = caculateInSampleSize(options, width, height);
		// 使用获得的inSimpleSize再次解析图片，并加载到内存
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SimpleSize
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int caculateInSampleSize(Options options, int reqWidth,
									 int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSimpleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);// 四舍五入
			int heightRadio = Math.round(height * 1.0f / reqHeight);
			inSimpleSize = Math.max(widthRadio, heightRadio);
		}
		return inSimpleSize;
	}

	protected ImageSize getImageViewSize(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();
		android.view.ViewGroup.LayoutParams lp = imageView.getLayoutParams();
		// int width = imageView.getWidth();// 获取imageview的实际宽度
		int width = getImageViewFieldValue(imageView, "mMaxWidth");
		if (width <= 0) {
			width = lp.width;// 获取imageView在layout中声明的宽度
		}
		if (width <= 0) {
			imageView.getMaxWidth();// 检查最大值
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;// 屏幕的宽度
		}
		// int height = imageView.getHeight();// 获取imageview的实际高度
		int height = getImageViewFieldValue(imageView, "mMaxHeight");
		if (height <= 0) {
			height = lp.height;// 获取imageView在layout中声明的高度
		}
		if (height <= 0) {
			imageView.getMaxHeight();// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;// 屏幕的高度
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	private static int getImageViewFieldValue(Object object, String fieldName) {

		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		try {
			// 确保mPoolThreadHandler不等于null，线程问题
			if (mPoolThreadHandler == null) {
				// 请求一个信号量
				mSemaphorePoolThreadHandler.acquire();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 根据path在缓存中获取图片
	 *
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key) {

		return mLruCache.get(key);
	}

	private class ImageSize {
		int width;
		int height;
	}

	private class ImgBeanHolder {
		ImageView imageView;
		String path;
		Bitmap bitmap;
	}
}
