# ![AndroidEventBus Logo](http://img.blog.csdn.net/20150203120217873)  AndroidEventBus

  这是一个Android平台的事件总线框架, 它简化了Activity、Fragment、Service等组件之间的交互，很大程度上降低了它们之间的耦合，使得我们的代码更加简洁，耦合性更低，提升我们的代码质量。          
  
  更多详情请参考[AndroidEventBus 框架发布](http://blog.csdn.net/bboyfeiyu/article/details/43450553);        
  
   ****A english readme is here [README-en.md](README-en.md).****    

## 最新特性

1. 支持 sticky event;
2. 使用弱引用持有订阅对象。

## 使用了AndroidEventBus的已知App
* [Accupass - Events around you](https://play.google.com/store/apps/details?id=com.accuvally.android.accupass)     
* [考拉FM](http://www.wandoujia.com/apps/com.itings.myradio)
* [羞羞](http://www.wandoujia.com/apps/com.yelong.wesex)
* [大题小作](http://www.pkdati.com/)
* [易泊商户](http://www.myebox.cn/)
* [易方达移动OA](http://www.wandoujia.com/apps/com.efunds.trade)
* [功夫泡](http://gongfupao.com)
* * [魅族手机中的日历应用]()

`欢迎大家给我反馈使用情况`
  
## 基本结构
 ![结构图](http://img.blog.csdn.net/20150426223040789)      
 AndroidEventBus类似于观察者模式,通过register函数将需要订阅事件的对象注册到事件总线中,然后根据@Subscriber注解来查找对象中的订阅方法,并且将这些订阅方法和订阅对象存储在map中。当用户在某个地方发布一个事件时,事件总线根据事件的参数类型和tag找到对应的订阅者对象,最后执行订阅者对象中的方法。这些订阅方法会执行在用户指定的线程模型中,比如mode=ThreadMode.ASYNC则表示该订阅方法执行在子线程中,更多细节请看下面的说明。        	

## 使用AndroidEventBus 
 你可以按照下面几个步骤来使用AndroidEventBus.     
         
*  1. 注册事件接收对象      

```
public class YourActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // 注册对象
        EventBus.getDefault().register(this);
    }
   
      @Override
    protected void onDestroy() {
        // 注销
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
   
```      

*    2. 通过Subscriber注解来标识事件接收对象中的接收方法        

```

public class YourActivity extends Activity {
 
    // code ......
    
    // 接收方法,默认的tag,执行在UI线程
    @Subscriber
    private void updateUser(User user) {
        Log.e("", "### update user name = " + user.name);
    }

	// 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,执行在UI线程
    @Subscriber(tag = "my_tag")
    private void updateUserWithTag(User user) {
        Log.e("", "### update user with my_tag, name = " + user.name);
    }
    
    // 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,
    // post函数在哪个线程执行,该函数就执行在哪个线程    
    @Subscriber(tag = "my_tag", mode=ThreadMode.POST)
    private void updateUserWithMode(User user) {
        Log.e("", "### update user with my_tag, name = " + user.name);
    }

	// 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,执行在一个独立的线程
    @Subscriber(tag = "my_tag", mode = ThreadMode.ASYNC)
    private void updateUserAsync(User user) {
        Log.e("", "### update user async , name = " + user.name + ", thread name = " + Thread.currentThread().getName());
    }
}

```           

   User类大致如下 : 
``` 
    public class User  {
        String name ;
        public User(String aName) {
            name = aName ;
        }
    }
```        


  接收函数使用tag来标识可接收的事件类型，与BroadcastReceiver中指定action是一样的,这样可以精准的投递消息。mode可以指定目标函数执行在哪个线程,默认会执行在UI线程,方便用户更新UI。目标方法执行耗时操作时,可以设置mode为ASYNC,使之执行在子线程中。      
         
  
*    3. 在其他组件,例如Activity, Fragment,Service中发布事件       

```
    
    EventBus.getDefault().post(new User("android"));
    
    // post a event with tag, the tag is like broadcast's action
    EventBus.getDefault().post(new User("mr.simple"), "my_tag");

```         

  发布事件之后,注册了该事件类型的对象就会接收到响应的事件.         


## 集成
### jar文件集成
将jar文件添加到工程中的引用中即可,[AndroidEventBus.jar下载](lib/androideventbus-1.0.5.1.jar?raw=true "点击下载到本地")      

### Android Studio集成

*   在Module的build.gradle添加依赖

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    
    // 添加依赖
	compile 'org.simple:androideventbus:1.0.5.1'
	
}
```
         

## 与greenrobot的EventBus的不同
   1. greenrobot的<a href="https://github.com/greenrobot/EventBus" target="_blank">EventBus</a>是一个非常流行的开源库,但是它在使用体验上并不友好,例如它的订阅函数必须以onEvent开头,并且如果需要指定该函数运行的线程则又要根据规则将函数名加上执行线程的模式名,这么说很难理解,比如我要将某个事件的接收函数执行在主线程,那么函数名必须为onEventMainThread。那如果我一个订阅者中有两个参数名相同,且都执行在主线程的接收函数呢? 这个时候似乎它就没法处理了。而且规定死了函数命名,那就不能很好的体现该函数的功能,也就是函数的自文档性。AndroidEventBus使用注解来标识接收函数,这样函数名不受限制,比如我可以把接收函数名写成updateUserInfo(Person info),这样就灵活得多了。    
   2. 另一个不同就是AndroidEventBus增加了一个额外的tag来标识每个接收函数可接收的事件的tag,这类似于Broadcast中的action，比如每个Broadcast对应一个或者多个action,当你发广播时你得指定这个广播的action,然后对应的广播接收器才能收到.greenrobot的EventBus只是根据函数参数类型来标识这个函数是否可以接收某个事件,这样导致只要是参数类型相同,任何的事件它都可以接收到,这样的投递原则就很局限了。比如我有两个事件,一个添加用户的事件, 一个删除用户的事件,他们的参数类型都为User,那么greenrobot的EventBus大概是这样的:    
   
```

private void onEventMainThread(User aUser) {
	// code 
}
```        
   如果你有两个同参数类型的接收函数，并且都要执行在主线程,那如何命名呢 ？  即使你有两个符合要求的函数吧,那么我实际上是添加用户的事件,但是由于EventBus只根据事件参数类型来判断接收函数,因此会导致两个函数都会被执行。AndroidEventBus的策略是为每个事件添加一个tag,参数类型和tag共同标识一个事件的唯一性,这样就确保了事件的精确投递。       
   
   这就是AndroidEventBus和greenrobot的EventBus的不同,当然greenrobot出于性能的考虑这么处理也可以理解，但是我们在应用中发布的事件数量是很有限的，性能差异可以忽略，但使用体验上却是很直接的。另外由于本人对greenrobot的EventBus前世今生并不是很了解,很可能上述我所说的有误,如果是那样,欢迎您指出。                

### 与EventBus、otto的特性对比

|         名称         | 订阅函数是否可执行在其他线程 |         特点          |
|---------------------|-----------------------|---------------------------------|
| [greenrobot的EventBus](https://github.com/greenrobot/EventBus)  |  是  | 使用name pattern模式，效率高，但使用不方便。|
| [square的otto](https://github.com/square/otto)    | 否  | 使用注解，使用方便，但效率比不了EventBus。   |
| [AndroidEventBus]()  |  是  | 使用注解，使用方便，但效率比不上EventBus。订阅函数支持tag(类似广播接收器的Action)使得事件的投递更加准确，能适应更多使用场景。 | 


## 混淆配置

```
-keep class org.simple.** { *; }
-keep interface org.simple.** { *; }
-keepclassmembers class * {
    @org.simple.eventbus.Subscriber <methods>;
}
-keepattributes *Annotation*
```

## 感谢
   在此非常感谢网友“淡蓝色的星期三”提出的bug以及反馈,也希望更多的朋友能够加入到Android EventBus的开发中来。  
   
      
## 发布历史

### V1.0.4   ( 2015.5.23 )
1. 支持Sticky事件;
2. 弱引用持有订阅对象。


### V1.0.2   ( 2015.2.28 )
1. 修复订阅方法的参数是基本类型( int, boolean等 )不能接收事件的问题。

### 1.0.1    ( 2015.2.13 )
1. 修复订阅方法是基类,而发布事件时传递的是子类型导致订阅方法无法接收到事件的问题。


### v1.0     ( 2015.2.9 )
1. 事件总线框架发布，使用@Subscriber注解标识订阅方法；
2. 订阅方法支持tag标识，使得事件投递更加精准。      


## License
```
Copyright (C) 2015 Mr.Simple <bboyfeiyu@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```      
