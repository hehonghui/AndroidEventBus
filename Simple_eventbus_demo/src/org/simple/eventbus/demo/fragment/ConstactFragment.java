/*
 * Copyright (C) 2015 Mr.Simple <bboyfeiyu@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.simple.eventbus.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.demo.R;
import org.simple.eventbus.demo.bean.User;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mrsimple
 */
public class ConstactFragment extends Fragment {

    BaseAdapter mAdapter;
    List<User> mConstacts = new LinkedList<User>();
    ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ListView) inflater.inflate(R.layout.list_fragment, container, false);

        mockDatas();
        initListView();

        EventBus.getDefault().register(this);
        return mListView;
    }

    private void initListView() {
        mAdapter = new ArrayAdapter<User>(getActivity(),
                android.R.layout.simple_list_item_1, mConstacts);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击用户,发布消息,
                // tag为MenuFragment.CLICK_TAG,接收函数在MenuFragment的updateClickUserName中
                EventBus.getDefault().post(mConstacts.get(position), MenuFragment.CLICK_TAG);
            }
        });
    }

    @Subscriber
    private void primitiveParam(int aInt) {
        Toast.makeText(getActivity(), "int = " + aInt, Toast.LENGTH_SHORT).show();
    }

    @Subscriber
    private void primitiveArrayParam(int[] aInt) {
        Toast.makeText(getActivity(), "int array = " + aInt[0] + ", " + aInt[1], Toast.LENGTH_SHORT)
                .show();
    }

    @Subscriber
    private void primitiveParam(boolean ab) {
        Toast.makeText(getActivity(), "boolean = " + ab, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void mockDatas() {
        for (int i = 0; i < 6; i++) {
            mConstacts.add(new User("User - " + i));
        }
    }

    /**
     * @param person
     */
    @Subscriber
    public void addPerson(User person) {
        mConstacts.add(person);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 含有tag标识的函数
     * 
     * @param person
     */
    @Subscriber(tag = MenuFragment.REMOVE_TAG)
    private void removePersonPrivate(User person) {
        mConstacts.remove(person);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 执行在异步线程的函数
     * 
     * @param event
     */
    @Subscriber(tag = MenuFragment.ASYNC_TAG, mode = ThreadMode.ASYNC)
    private void asyncMethod(final User person) {
        try {
            final String threadName = Thread.currentThread().getName();
            mListView.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getActivity(),
                            R.string.execute_async + threadName + ", User Info  : " + person,
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
