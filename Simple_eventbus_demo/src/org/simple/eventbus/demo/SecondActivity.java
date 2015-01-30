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

package org.simple.eventbus.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.demo.bean.Person;

/**
 * @author mrsimple
 */
public class SecondActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.second_activity);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SecondFragment()).commit();
    }

    /**
     * @author mrsimple
     */
    static class SecondFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.second_fragment, container, false);

            rootView.findViewById(R.id.my_button_2).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            rootView.findViewById(R.id.my_post_button).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new Person("mr.simple-1"));
                }
            });

            rootView.findViewById(R.id.my_post_event_tag_button).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().postWithTag(new Person("mr.simple2, hello"),
                                    "hello");
                        }
                    });

            rootView.findViewById(R.id.my_post_async_event_button).setOnClickListener(
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new Person("mr.simple-3"));
                        }
                    });

            return rootView;
        }
    }
}
