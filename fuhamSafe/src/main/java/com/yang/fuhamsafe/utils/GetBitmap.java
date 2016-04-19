package com.yang.fuhamsafe.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**使用AsyncTask,从网络下载图片
 * Created by fuhamyang on 2016/4/3.
 */
public class GetBitmap {

    private File file;
    private Context context;
    //底层实现就是一个HashMap，分配固定内存给这个HashMap;
    //如果内存使用完，则将最开始进入的对象删除，以便为新的对象腾出空间
    private LruCache<String, Bitmap> memoryCache;

    public GetBitmap(File file, Context context) {
        this.file = file;
        this.context = context;
        //系统默认为每一个应用分配16M的内存大小
        //获取应用的栈内存
        long processMemory = Runtime.getRuntime().maxMemory();
        //将内存的1/8分配给图片缓存
        memoryCache = new LruCache<String, Bitmap>((int)(processMemory/8)){
            //重写sizeof方法，计算对象的大小，默认返回1
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //计算每个图片的大小
                int byteCount = value.getRowBytes() * value.getWidth();
                return byteCount;
            }
        };

    }

    public  void get( ImageView imageView, String url){

        if (getBitmapFromStack(url) != null)
            imageView.setImageBitmap(getBitmapFromStack(url));
        else if (getBitmapFromCache(url) == null)
            imageView.setImageBitmap(getBitmapFromCache(url));
        else
            getBitmapFromNet(imageView,url);

    }
    private void getBitmapFromNet(ImageView imageView,String url){
        System.out.println("##########从网络获取");
        //由于使用listView时会重用对象，因此可能会出现一个imageView对象，指向多张不同的图片
        //为了确定图片与对象一一对应需要将对象与图片的url绑定
        imageView.setTag(url);
        //执行AsyncTask，参数会传入doInBackground方法中
        new BitmapTask().execute(imageView, url);

    }
    //其中Void是泛型，第一个是doInBackground中的参数类型，
    //第二个是onProgressUpdate的参数类型
    //第三个是doInBackground的返回值类型,以及onPostExecute的参数类型
    class BitmapTask extends AsyncTask<Object ,Void,Bitmap>{
        private ImageView imageView;
        private String url;
        //后台耗时方法在此执行，源代码新建了线程池，来运行该方法
        @Override
        protected Bitmap doInBackground(Object... params) {
            //获取传入的参数，数组按照参数传入的先后顺序
            imageView = (ImageView)params[0];
            url = (String)params[1];


            //返回的结果会传递给onPostExecute方法
            return downloade(imageView,url);
        }

        //源码中在Handler中运行该方法更新进度条
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        //耗时方法结束后，执行该方法
        @Override
        protected void onPostExecute(Bitmap result) {
            //将结果进行处理
            if (result != null){
                String bind = (String)imageView.getTag();
                if (bind.equals(url))
                    imageView.setImageBitmap(result);
            }
        }


    }

    private Bitmap downloade(ImageView imageView ,String url){

        try {
            file  = new File(context.getCacheDir(),url);
            //如果该图片不存在，即该图片没有缓存，则进入下载
            if (!file.exists()) {
                //2、将网址封装成一个url对象
                URL url2 = new URL(url);
                //3、获取客户端和服务器的连接对象，此时还没有建立连接
                HttpURLConnection conn = (HttpURLConnection)url2.openConnection();
                //4、对连接对象进行初始化
                //设置请求模式，注意大写
                conn.setRequestMethod("GET");
                //设置连接超时
                conn.setConnectTimeout(5000);
                //设置读取超时
                conn.setReadTimeout(5000);
                //5、发送请求，与服务器建立连接
                conn.connect();
                //如果响应码为200，则响应成功
                if(conn.getResponseCode() == 200){
                    //获取服务器响应头中的流，流里的数据就是客户端请求的数据
                    InputStream inputStream = conn.getInputStream();

                    //获取压缩后的图片
                    Bitmap bitmap = compressBitmap(imageView.getWidth(),imageView.getHeight(),inputStream);
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                    //将图片保存到本地
                    //保存文件，1、指定图片格式；2、指定图片的质量（0——100），100的质量最高；3、输出流
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                    //将图片放入栈内存
                    memoryCache.put(url,bitmap);
                    return bitmap;
                }

            }

        }catch (Exception e){

        }
        return null;
    }

    private Bitmap getBitmapFromStack(String url){
        System.out.println("##########从栈获取");
        return memoryCache.get(url);
    }
    //图片二次采样，压缩
    private Bitmap compressBitmap(int screenWidth, int screenHeight,InputStream inputStream){

        //解析图片时，由于要传递的参数过多，因此将要传递的参数封装到一个Option中
        Options options = new Options();
        //该属性为true，则在解析图片时只获取图片的宽高，不为图片申请内存
        options.inJustDecodeBounds = true;
        //此时只获取图片的宽高，不为图片申请内存
        BitmapFactory.decodeStream(inputStream, null, options);
        //拿到图片的宽高
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        //计算缩放比列
        //初始化缩放比列为1，即不缩放
        int scale = 1;
        //计算宽缩放比列
        int scaleWidth = imageWidth/screenWidth;
        //计算长缩放比列
        int scaleHeight = imageHeight/screenHeight;
        //当缩放比大于1时，取最大的作为最后的缩放比，否则不必进行缩放
        if(scaleWidth >= scaleHeight && scaleWidth > 1)
            scale = scaleWidth;
        else if(scaleWidth < scaleHeight && scaleHeight > 1)
            scale = scaleHeight;
        //不要忘记，将以下属性设置成false
        options.inJustDecodeBounds = false;

        options.inSampleSize = scale;
        //解析图片
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    private Bitmap getBitmapFromCache(String url){
        System.out.println("##########缓存获取");
        //从缓存文件夹中获取图片
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        //将图片放入栈内存
        memoryCache.put(url,bitmap);
        return  bitmap;
    }

}
