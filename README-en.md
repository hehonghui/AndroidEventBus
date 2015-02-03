# ![AndroidEventBus Logo](http://img.blog.csdn.net/20150203120217873)  AndroidEventBus
  A eventbus library for android, simplifies communication between Activities, Fragments, Threads, Services, etc. 
  
## Architecture Overview
	



## Usage 
 It's easy to use AndroidEventBus, here is the steps.        
1. register the subcriber object
```java
   
public class YourActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // register this as a subscrober
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy() {
        // don't forget to unregister
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
   
```      

3. use Subscriber annotation to mark a receiver method
```java
public class YourActivity extends Activity {
    // code ......
    
    @Subcriber
    private void updateTime(String time) {
        Log.e("", "### update time = " + time);
    }

    @Subcriber(tag = "my_tag")
    private void updateTimeWithTag(String time) {
        Log.e("", "### update time with my_tag, time = " + time);
    }

    @Subcriber(tag = "my_tag", mode = ThreadMode.ASYNC)
    private void updateTimeAsync(String time) {
        Log.e("", "### update time async , time = " + time + ", thread name = " + Thread.currentThread().getName());
    }
}
```       
  when you set tag field , the method will only receive the event with a corresponding tag.     
  
4. in other activity, service, fragment , etc. you can post a event to subcribers.
```java
    EventBus.getDefault().post("what's the time now ?");
    // post a event with tag, the tag is like broadcast's action
    EventBus.getDefault().post(new Date().toLocaleString(), "my_tag");
```      


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
