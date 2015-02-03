# ![AndroidEventBus Logo](http://img.blog.csdn.net/20150203120217873)  AndroidEventBus

  这是一个Android平台的事件总线框架, 它简化了Activity、Fragment、Service等组件之间的交互，很大程度上降低了它们之间的耦合，使得我们的代码更加简洁，耦合性更低，提升我们的代码质量。      

  在往下看之前,你可以考虑这么一个场景,两个Fragment之间的通信你会怎么实现？
  按照Android官方给的建议的解决方法如下: [Communicating with the Activity](http://developer.android.com/intl/zh-cn/guide/components/fragments.html#CommunicatingWithActivity),思路就是Activity实现某个接口,然后在Fragment-A关联上Activity之后将Activity强转为接口类型,然后在某个时刻Fragment中回调这个接口,然后再从Activity中调用Fragment-B中方法。这个过程是不是有点复杂呢？ 如果你也这么觉得,那也就是你继续看下去的理由了。       
        
   ****A english readme is here [README-en.md](README-en.md).****    
  
## 基本结构
 ![结构图](http://img.blog.csdn.net/20150203125508110)      
 AndroidEventBus类似于观察者模式,通过register函数将需要订阅事件的对象注册到事件总线中,然后根据@Subcriber注解来查找对象中的订阅方法,并且将这些订阅方法和订阅对象存储在map中。当用户在某个地方发布一个事件时,事件总线根据事件的参数类型和tag找到对应的订阅者对象,最后执行订阅者对象中的方法。这些订阅方法会执行在用户指定的线程模型中,比如mode=ThreadMode.ASYNC则表示该订阅方法执行在子线程中,更多细节请看下面的说明。        	
 
## 与greenrobot的EventBus的不同
   1. greenrobot的<a href="https://github.com/greenrobot/EventBus" target="_blank">EventBus</a>是一个非常流行的开源库,但是它在使用体验上并不友好,例如它的订阅函数必须以onEvent开头,并且如果需要指定该函数运行的线程则又要根据规则将函数名加上执行线程的模式名,这么说很难理解,比如我要将某个事件的接收函数执行在主线程,那么函数名必须为onEventMainThread。那如果我一个订阅者中有两个参数名相同,且都执行在主线程的接收函数呢? 这个时候似乎它就没法处理了。而且规定死了函数命名,那就不能很好的体现该函数的功能,也就是函数的自文档性。AndroidEventBus使用注解来标识接收函数,这样函数名不受限制,比如我可以把接收函数名写成updateUserInfo(Person info),这样就灵活得多了。    
   2. 另一个不同就是AndroidEventBus增加了一个额外的tag来标识每个接收函数可接收的事件的tag,这类似于Broadcast中的action，比如每个Broadcast对应一个或者多个action,当你发广播时你得指定这个广播的action,然后对应的广播接收器才能收到.greenrobot的EventBus只是根据函数参数类型来标识这个函数是否可以接收某个事件,这样导致只要是参数类型相同,任何的事件它都可以接收到,这样的投递原则就很局限了。比如我有两个事件,一个添加用户的事件, 一个删除用户的事件,他们的参数类型都为User,那么greenrobot的EventBus大概是这样的:    
   
```java

private void onEventMainThread(User aUser) {
	// code 
}
```        
   如果你有两个同参数类型的接收函数，并且都要执行在主线程,那如何命名呢 ？  即使你有两个符合要求的函数吧,那么我实际上是添加用户的事件,但是由于EventBus只根据事件参数类型来判断接收函数,因此会导致两个函数都会被执行。AndroidEventBus的策略是为每个事件添加一个tag,参数类型和tag共同标识一个事件的唯一性,这样就确保了事件的精确投递。
   这就是AndroidEventBus和greenrobot的EventBus的不同,但是由于本人对greenrobot的EventBus并不是很了解,很可能上述我所说的有误,如果是那样,欢迎您指出。         
   
   AndroidEventBus起初只是为了学习,但是在学习了EventBus的实现之后,发现它在使用上有些不便之处,我想既然我有这些感觉,应该也是有同感之人,在开发群里交流之后,发现确实有这样的情况。因此才将正式地AndroidEventBus以开源库的形式推出来,希望能够帮助到一些需要的人。当然,这个库的成长需要大家的支持与测试,欢迎大家发 pull request。如果你需要的是一个相对稳定的库,greenrobot的EventBus和square的otto都是非常好的选择。       
   

## 使用AndroidEventBus 
 你可以按照下面几个步骤来使用AndroidEventBus.     
         
1. 注册事件接收对象
```java
   
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
        // 不要忘记注销！！！！
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
   
```      

3. 通过Subscriber注解来标识事件接收对象中的接收方法        
```java
public class YourActivity extends Activity {
    // code ......
    
    // 接收方法,默认的tag,执行在UI线程
    @Subcriber
    private void updateTime(String time) {
        Log.e("", "### update time = " + time);
    }

	// 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,执行在UI线程
    @Subcriber(tag = "my_tag")
    private void updateTimeWithTag(String time) {
        Log.e("", "### update time with my_tag, time = " + time);
    }
    
    // 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,
    // post函数在哪个线程执行,该函数就执行在哪个线程    
    @Subcriber(tag = "my_tag", mode=ThreadMode.POST)
    private void updateTimeWithMode(String time) {
        Log.e("", "### update time with my_tag, time = " + time);
    }

	// 含有my_tag,当用户post事件时,只有指定了"my_tag"的事件才会触发该函数,执行在一个独立的线程
    @Subcriber(tag = "my_tag", mode = ThreadMode.ASYNC)
    private void updateTimeAsync(String time) {
        Log.e("", "### update time async , time = " + time + ", thread name = " + Thread.currentThread().getName());
    }
}
```       

  接收函数使用tag来标识可接收的事件类型，与BroadcastReceiver中指定action是一样的,这样可以精准的投递消息。mode可以指定目标函数执行在哪个线程,默认会执行在UI线程,方便用户更新UI。目标方法执行耗时操作时,可以设置mode为ASYNC,使之执行在子线程中。           
  
4. 在其他组件,例如Activity, Fragment,Service中发布事件      
```java
    EventBus.getDefault().post("what's the time now ?");
    
    // post a event with tag, the tag is like broadcast's action
    EventBus.getDefault().post(new Date().toLocaleString(), "my_tag");
```       
   发布事件之后,注册了该事件类型的对象就会接收到响应的事件.



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
