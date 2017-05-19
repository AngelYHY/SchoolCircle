package com.myschool.schoolcircle.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Mr.R on 2016/8/3.
 */
public class ImageLoader {
    /**
     * 图片缓存的核心对象
     */
    private static LruCache<String,Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    //默认的线程数量
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.FIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    /**
     * UI线程中的Handler
     */
    private Handler mUIHandler;

    /**
     * 声明一个信号量
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;

    public enum Type {
        FIFO,LIFO;
    }

    private static ImageLoader mInstance;

    private ImageLoader(int threadCount, Type type) {
        init(threadCount,type);
    }

    /**
     * 初始化
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //初始化轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                //Looper准备
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务去执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                //Looper轮询
                Looper.loop();
            }
        };
        mPoolThread.start();

        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        //初始化线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        //初始化任务队列
        mTaskQueue = new LinkedList<>();
        //初始化加载策略
        mType = type;
        //初始化信号量
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 默认的获取实例方法
     * @return
     */
    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据传入的线程数，图片的加载策略来获取实例
     * @param threadCount
     * @param type
     * @return
     */
    public static ImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount,type);
                }
            }
        }
        return mInstance;
    }

    //加载图片
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取到图片，为imageView设置图片
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView iv = holder.imageView;
                    String p = holder.path;

                    if (iv.getTag().toString().equals(p)) {
                        iv.setImageBitmap(bm);
                    }
                }
            };
        }

        Bitmap bitmap = getBitmapFromLruCache(path);

        if (bitmap != null) {
            refreshBitmap(bitmap,imageView,path);
        } else {
            addTask(new Runnable(){
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //获取图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path,imageSize.width,imageSize.height);
                    //将图片加入到缓存中
                    addBitmap2LruCache(path,bm);

                    refreshBitmap(bm,imageView,path);
                    //加载完图片后，释放信号量
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * 刷新图片
     * @param bitmap
     * @param imageView
     * @param path
     */
    private void refreshBitmap(Bitmap bitmap,ImageView imageView,String path) {
        Message message = Message.obtain();
        ImageBeanHolder holder = new ImageBeanHolder(bitmap,imageView,path);
        message.obj = holder;
        mUIHandler.sendMessage(message);
    }

    /**
     * 将图片加入到LruCache缓存中
     * @param path
     * @param bitmap
     */
    private void addBitmap2LruCache(String path, Bitmap bitmap) {
        if (getBitmapFromLruCache(path) == null) {
            if (bitmap != null) {
                mLruCache.put(path,bitmap);
            }
        }
    }

    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //获取图片的款和高，但并不把图片加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        options.inSampleSize = calculateInSampleSize(options,width,height);

        //使用获取到的inSampleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {

        int w = options.outWidth;
        int h = options.outHeight;

        int inSampleSize = 1;

        if (w > width || h > height) {
            int widthRadio = Math.round(w*1.0f/width);
            int heightRadio = Math.round(h*1.0f/height);

            inSampleSize = Math.max(widthRadio,heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 添加一个新的任务
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler == null) {
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 从任务队列取出一个方法
     * @return
     */
    private Runnable getTask() {

        if (mType == Type.FIFO) {
            //先进先出
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            //后进先出
            return mTaskQueue.removeLast();
        }
        return null;
    }

    /**
     * 根据路径在缓存中获取Bitmap
     * @param path
     * @return
     */
    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    //image对象
    private class ImageBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;

        public ImageBeanHolder(Bitmap bitmap,ImageView imageView,String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }
    }

    private class ImageSize {
        int width;
        int height;
    }

    /**
     * 根据ImageView获取适当压缩的宽和高
     * @param imageView
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        //获取屏幕尺寸
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();

        //获取imageView的实际宽度
        int width = imageView.getWidth();
        if (width <= 0) {
            //获取在layout中声明的宽度
            width = params.width;
        }
        if (width <= 0) {
            //检查最大值
            width = getImageViewFileValue(imageView,"mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        //获取imageView的实际高度
        int height = imageView.getHeight();
        if (height <= 0) {
            //获取在layout中声明的高度
            height = params.height;
        }
        if (height <= 0) {
            //检查最大值
            height = getImageViewFileValue(imageView,"mMaxHeight");
        }
        if (height <= 0) {
            height = displayMetrics.widthPixels;
        }

        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    /**
     * 通过反射获取ImageView的某个属性值
     * @param obj
     * @param fileName
     * @return
     */
    private static int getImageViewFileValue(Object obj,String fileName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fileName);
            field.setAccessible(true);
            int fileValue = field.getInt(obj);
            if (fileValue > 0 && fileValue < Integer.MAX_VALUE) {
                value = fileValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}
