# AndroidEventBus

This is an EventBus library for Android. It simplifies the communication between Activities, Fragments, Threads, Services, etc. and lowers the coupling among them to a great extent, thus making simplifier codes, lower coupling possible and improving code quality.             
  
[//]: # (   ****中文版 [README.md]&#40;README-ch.md&#41;.【该项目不再维护】****    )

## new feature

1. support sticky event;

## AndroidEventBus is adopted in the following app
* [Accupass - Events around you](https://play.google.com/store/apps/details?id=com.accuvally.android.accupass)     
* [考拉FM](http://www.wandoujia.com/apps/com.itings.myradio)
* [羞羞](http://www.wandoujia.com/apps/com.yelong.wesex)
* [大题小作](http://www.pkdati.com/)
* [易方达移动OA](http://www.wandoujia.com/apps/com.efunds.trade)
* [Novu - Your Health Rewarded](https://play.google.com/store/apps/details?id=com.novu.novu)
* [魅族手机中的日历应用]()
  
## Basic Architecture
 ![arch](http://img.blog.csdn.net/20150426223040789)         
  
AndroidEventBus is like the Observer Pattern. It will have the objects which need to subscribe events registered into the EventBus through Function “register” and store such subscription methods and subscription objects in Map. When a user posts an event somewhere, the EventBus will find corresponding subscription object in accordance with the parameter type and tag of the Event and then execute the method in subscription object. These subscription methods will be executed in the Thread Mode designated by the user. For example, mode=ThreadMode. ASNYC means the subscription method is executed in the sub-thread. Please refer to the following instructions for more details.      	


## Code Example  
 You can use AndroidEventBus according to the following steps.    
         
* 1. Event-receiving Object      

```
   
public class YourActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // register the receiver object
        EventBus.getDefault().register(this);
    }
    
   @Override
    protected void onDestroy() {
        // Don’t forget to unregister !!
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
   
```      

* 2. Mark the receiving method of the Event-receiving Object with Subscriber annotation.           

```
public class YourActivity extends Activity {
 
    // code ......
    
    // A receiving method with a default tag will execute on UI thread.
    @Subscriber
    private void updateUser(User user) {
        Log.e("", "### update user name = " + user.name);
    }

	// When there is a “my_tag”, only events designated with “my_tag” can 
	// trigger the function and execute on UI thread when a user posts an event.
    @Subscriber(tag = "my_tag")
    private void updateUserWithTag(User user) {
        Log.e("", "### update user with my_tag, name = " + user.name);
    }
    
	// When there is a “my_tag”, only events designated with “my_tag” can trigger the function.
	// The function will execute on the same thread as the one post function is executed on.   
    @Subscriber(tag = "my_tag", mode=ThreadMode.POST)
    private void updateUserWithMode(User user) {
        Log.e("", "### update user with my_tag, name = " + user.name);
    }

	// When there is a “my_tag”, only events designated with “my_tag” can trigger  
	// the function and execute on an independent thread when a user posts an event.
    @Subscriber(tag = "my_tag", mode = ThreadMode.ASYNC)
    private void updateUserAsync(User user) {
        Log.e("", "### update user async , name = " + user.name + ", thread name = " + Thread.currentThread().getName());
    }
}

```           

   User class is approximately as follows :    
   
``` 
    public class User  {
        String name ;
        public User(String aName) {
            name = aName ;
        }
    }
```        

The receiving function will use “tag” to mark receivable types of events, just like designating “action” in BroadcastReceiver, which can deliver messages precisely. Mode can designate which thread the object function will be executed on but defaultly it will be executed on UI thread for the purpose of convenient UI update for users. When the object method executes long-running operations, the “mode” can be set as ASYNC so as to be executed on sub-thread.           
         
  
* 3. To post an event in other components such as Activities, Fragments or Services.           

```
    
    EventBus.getDefault().post(new User("android"));
    
    // post a event with tag, the tag is like broadcast's action
    EventBus.getDefault().post(new User("mr.simple"), "my_tag");
    
    // post sticky event
    EventBus.getDefault().postSticky(new User("sticky"));

```         

  After posting the event, the object registered with the event type will receive responsive event.           


## Usage 
### integrate with jar
It will be enough to add the jar file into the “quote” part of the Project, AndroidEventBus.[AndroidEventBus.jar](lib/androideventbus-1.0.5.1.jar?raw=true "download")      


### Gradle

* Add dependency in build.gradle of the Module .

```
dependencies {

    // add AndroidEventBus dependency
    compile 'org.simple:androideventbus:1.0.5.1'
}
```    
               
## Differing from the EventBus of greenrobot
   1. <a href="https://github.com/greenrobot/EventBus" target="_blank">EventBus</a> of greenrobot is a popular open source library but its user experience is not as friendly. For example, its subscription function is required to start with onEvent, and if a function’s execution thread needs to be designated, it is necessary to add the mode name of execution thread in the name of the function according to certain rules. This may be difficult to understand. Let’s say, if I want the receiving function of some event to be executed on the main thread, I am required to name it as onEventMainThread. What if two of my subscribers share the same parameter name and both are executed on the receiving function of the main thread? It seems impossible to deal with it in such case. And a set-in-stone function name can’t properly reflect the function of the Function, i.e., the self-documentation of the function. AndroidEventBus uses annotation to mark receiving function, by which the function name is not limited. For example, I can name the receiving function as updateUserInfo(Person info). It’s more flexible.
   2. Another difference lies in that AndroidEventBus adds an extra tag to mark the tag of receivable event of every receiving function, just like the action in Broadcast. For instance, one Broadcast corresponds to one or more actions, and you need to designate the action of a broadcast before you can publish one and the broadcast receiver can receive. EventBus of greenrobot marks whether a function can receive a certain event only by the parameter type of the function. In this way, the function can receive all the events of the same parameter type, resulting in a limited delivery principle. Let’s say, if there are two events: one is about adding user and the other is about deleting user. Their parameter types are both User. Then the EventBus of greenrobot would be lke:         
   
```

private void onEventMainThread(User aUser) {
	// code 
}
```        

If there are two receiving functions of the same parameter type and both are executed on the main thread, how to name them distinctively? Supposing that there are two functions meeting the requirements and the event is adding user, but because the EventBus judges receiving function only by parameter type of the event, both function will be executed. The strategy of AndroidEventBus is to add a “tag” for each event, and use parameter type and “tag” to jointly mark the uniqueness of the vent so as to ensure precise delivery.      

These are the differences between AndroidEventBus and EventBus of greenrobot. But it is understandable for greenrobot’s approach considering performance. And what I try to express is that there are very limited quantity of events posted in an App and the performance difference is negligible while user experience is well sensible. What I need to point out is that I know little about the ins and outs of EventsBus of greenrobot and there could be errors among what I’ve mentioned. If that happens, you are more than welcome to correct me.        


### Comparison Of Characteristics

|         library     | Whether the subscription function can be executed on other thread |         features          |
|---------------------|-----------------------|------------------|
| [greenrobot's EventBus](https://github.com/greenrobot/EventBus)  |  yes  | It adopts name pattern which is efficient but inconvenient to use. |
| [square's otto](https://github.com/square/otto)    |  no  | It is convenient to use annotation but it’s not as efficient as EventBus|   
| [AndroidEventBus]()  |  yes  | It is convenient to use annotation but it’s not as efficient as EventBus. The subscription supports tag (like the Action in Broadcast Receiver) which can make event delivery more accurate and applicable to more usage scenarios.  |   

## Proguard

```
-keep class org.simple.** { *; }
-keep interface org.simple.** { *; }
-keepclassmembers class * {
    @org.simple.eventbus.Subscriber <methods>;
}
-keepattributes *Annotation*
```

## Thanks Note         
I really appreciate E-pal “淡蓝色的星期三” for his proposing of bugs and feedback and I hope more and more friends will join our team of AndroidEventBus Development.    
   
 
## Release Note

### V1.0.5 ( 2015.6.20 )
1. fix bugs.

### V1.0.4 ( 2015.5.23 )
1. support Sticky Events and use WeakReference to hold the Subscriber.


### V1.0.2 ( 2015.2.28 )
Solved the problem of failing to receive an event when the parameter of the subscription method is a basic type (int, Boolean, etc.)

### V1.0.1 ( 2015.2.13 )
1. Solved the problem that the subscription method can’t receive an event because the subscription method is delivered as sub-type when posting an event while it was originally of basic type.     


### v1.0 ( 2015.2.9 )
1.	Release an EventBus library; use @Subscriber annotation to mark subscription method
2.	The subscription method supports “tag” mark, which makes event delivery more precise.
    

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
