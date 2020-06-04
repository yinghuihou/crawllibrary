### CrawlMainHandler 使用文档

设备信息抓取库可以抓取手机的基本设备信息，网络信息，电量信息等等，但是不包括联系人信息、通话记录和通讯录等。
#### 1、环境配置
首先将crawllibrary.aar加入moudle中的libs文件夹，然后在moudle中的build.gradle中配置

```
repositories {
        flatDir {
            dirs 'libs'
        }
    }
    
dependencies{
	implementation (name: 'crawllibrary', ext: 'aar')；
}
```
#### 2、权限说明
此抓取库会依赖某些权限，我们已在aar中声明过了。在用到的地方会检查是否有权限，具体需要用到的权限如下：

- android.permission.ACCESS_NETWORK_STATE
- android.permission.ACCESS_WIFI_STATE
- android.permission.READ_PHONE_STATE

#### 3、第一步需要在自己的Application中初始化
```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrawlMainHandler.init(this);
    }
}
```

#### 4、在需要使用的地方获取抓取结果
```
try {
   String result = CrawlMainHandler.getDeviceInfo();
} catch (Exception e) {
   e.printStackTrace();
 }
```
#### 5、关于一些自定义字段
##### IMEI
我们默认会尝试获取手机的IMEI号码，但不保证一定能获取到，如果获取不到，可以自定义IMEI号码，像这样：CrawlMainHandler.setImeIValue("12345");
##### Location
我们不会获取位置信息，默认情况下location字段为空，如果需要可以传入自定义location信息，像这样：CrawlMainHandler.setLocationInfo("xxx");
##### GAID(google advertisingId)
我们通过手机内置的Google Play Service去获取GAID，如果不想采用这种方式，也可以自行通过firebase去获取，然后传入自定义的GAID，像这样：CrawlMainHandler.setGAID("xxx");