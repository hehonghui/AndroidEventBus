/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.simple.eventbus.demo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subcriber;
import org.simple.eventbus.ThreadMode;
import org.simple.eventbus.demo.R;
import org.simple.eventbus.demo.bean.Person;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mrsimple
 */
public class ConstactFragment extends Fragment {

    BaseAdapter mAdapter;
    List<Person> mConstacts = new LinkedList<Person>();
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
        mAdapter = new ArrayAdapter<Person>(getActivity(),
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

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void mockDatas() {
        for (int i = 0; i < 6; i++) {
            mConstacts.add(new Person("User - " + i));
        }
    }

    /**
     * @param person
     */
    @Subcriber
    public void addPerson(Person person) {
        mConstacts.add(person);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 含有tag标识的函数
     * 
     * @param person
     */
    @Subcriber(tag = "remove")
    private void addPersonPrivate(Person person) {
        mConstacts.remove(person);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 执行在异步线程的函数
     * 
     * @param event
     */
    @Subcriber(tag = "async", mode = ThreadMode.ASYNC)
    private void asyncMethod(final Person person) {
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
