package com.zhaoss.qqslidebar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MainRelativeLayout mainLayout;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private String[] datas = {"中文", "英语", "韩语", "日语", "法语", "德语", "俄语", "西班牙", "阿拉伯"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initData();
    }

    private void initData() {

        viewPager.setAdapter(new ViewPagerAdapter(this));
        recyclerView.setAdapter(new MyAdapter(this, datas));

        mainLayout.setOnMenuChangeStateListener(new MainRelativeLayout.OnMenuChangeStateListener() {
            @Override
            public void onOpen() {
                System.out.print("leftMenu被打开");
            }
            @Override
            public void onClose() {
                System.out.print("leftMenu被关闭");
            }
            @Override
            public void onStartSlide() {
                System.out.print("leftMenu开始滑动");
            }
            @Override
            public void onSlide(float pctSlide) {
                System.out.print("leftMenu滑动距离: "+pctSlide);
            }
        });
    }

    private void initUI() {

        mainLayout = (MainRelativeLayout) findViewById(R.id.mainLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        RelativeLayout rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        Button button = (Button) findViewById(R.id.button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rl_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.slideMenu();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "响应点击事件!", Toast.LENGTH_LONG).show();
            }
        });
    }
}












