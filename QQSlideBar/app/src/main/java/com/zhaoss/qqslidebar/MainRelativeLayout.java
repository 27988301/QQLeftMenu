package com.zhaoss.qqslidebar;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Zhaoss on 2016/1/21.
 * 主页面根布局
 */
public class MainRelativeLayout extends RelativeLayout {

    private FloatEvaluator floatEvaluator;
    private View menu;
    private View main;
    private int menuWidth;
    private int menuHeight;
    private int mainWidth;
    private int mainHeight;
    /** 一共要移动的距离 */
    private int disWidth;
    public static boolean is_open_menu;
    private OnMenuChangeStateListener listener;
    private float dimen100 = 100;
    private ViewDragHelper viewDragHelper;

    public MainRelativeLayout(Context context) {
        super(context);
        init();
    }

    public MainRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        viewDragHelper = ViewDragHelper.create(this, new MyCallback());
        floatEvaluator = new FloatEvaluator();
    }

    /**
     * 设置触摸事件由viewHelper消费
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
            //抓取异常是因为这个地方可能出现空指针,这是viewDragHelper内部没有初始化的原因
            viewDragHelper.processTouchEvent(event);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    float downX = 0;

    /**
     * 设置是否拦截触摸事件,这个地方是事件分发的重点
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean flag = false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                float downY = ev.getY();
                //如果滑动区域是边界
                if(downX<dimen100 && downY>dimen100){
                    if(listener != null){
                        listener.onStartSlide();
                        flag = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                //如果此时menu栏是开启,并且手势是从右向左滑动
                if(is_open_menu && moveX-downX < -10) {
                    flag = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                float upX = ev.getX();
                //判断是否为单机事件
                if(Math.abs(upX-downX) < 10){
                    flag = false;
                    if(!is_open_menu && listener != null){
                        listener.onClose();
                    }
                }
                break;
        }
        return flag;
    }

    /**
     * 滑动menu
     */
    public void slideMenu(){

        if(is_open_menu){
            close();
            listener.onClose();
        }else{
            open();
            listener.onOpen();
        }
        is_open_menu = !is_open_menu;
    }

    class MyCallback extends ViewDragHelper.Callback{
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menu || child == main;
        }
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if(child == main){
                if(left < 0){
                    left = 0;
                }else if(left > disWidth){
                    left = disWidth;
                }
            }
            return left;
        }
        @Override
        public int getViewHorizontalDragRange(View child) {
            return disWidth;
        }
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if(changedView == menu){
                //固定menu在左边位置
                menu.layout(0, 0, menuWidth, menuHeight);
                //让main跟随移动
                int slideLeft = main.getLeft()+dx;
                if(slideLeft < 0){
                    slideLeft = 0;
                }else if(slideLeft > disWidth){
                    slideLeft = disWidth;
                }
                main.layout(slideLeft, main.getTop(), slideLeft+mainWidth, main.getBottom());
            }
            //计算出滑动的百分比
            float pctSlide = Float.valueOf(main.getLeft())/disWidth;
            dispatchDragAnim(pctSlide);
            //执行接口方法回调
            if(pctSlide==0 && is_open_menu){
                is_open_menu = false;
                if(listener!=null){
                    listener.onClose();
                }
            }else if (pctSlide==1 && !is_open_menu) {
                is_open_menu = true;
                if(listener!=null){
                    listener.onOpen();
                }
            }else if (pctSlide>0 && pctSlide<1) {
                if(listener!=null){
                    listener.onSlide(pctSlide);
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if(xvel>500 && !is_open_menu){//应该打开
                open();
            }else if(xvel<-500 && is_open_menu){//应该关闭
                close();
            }else if(main.getLeft()>disWidth/2){//在右半边
                open();
            }else if(main.getLeft()<disWidth/2){//在左半边
                close();
            }
        }
    }

    public void setOnMenuChangeStateListener(OnMenuChangeStateListener listener){
        this.listener = listener;
    }

    public interface OnMenuChangeStateListener{
        void onOpen();
        void onClose();
        void onStartSlide();
        void onSlide(float pctSlide);
    }

    /**
     * 打开菜单
     */
    private void open(){
        if(listener != null) listener.onOpen();
        viewDragHelper.smoothSlideViewTo(main, disWidth, main.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 关闭菜单
     */
    private void close(){
        if(listener != null) listener.onClose();
        viewDragHelper.smoothSlideViewTo(main, 0, main.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 分发拖拽动画
     * @param dragFraction
     */
    private void dispatchDragAnim(float dragFraction){

        //1.给mainView增加缩放动画
        ViewHelper.setScaleY(main, floatEvaluator.evaluate(dragFraction, 1f, 0.8f));
        ViewHelper.setScaleX(main, floatEvaluator.evaluate(dragFraction, 1f, 0.8f));
        //2.给menuView增加放大动画
        ViewHelper.setScaleY(menu, floatEvaluator.evaluate(dragFraction, 0.5f, 1f));
        ViewHelper.setScaleX(menu, floatEvaluator.evaluate(dragFraction, 0.5f, 1f));
        //3.给menuView增加移动动画
        ViewHelper.setTranslationX(menu, floatEvaluator.evaluate(dragFraction, -menuWidth/2, 0));
        //4.给menuView增加透明动画
        ViewHelper.setAlpha(menu, floatEvaluator.evaluate(dragFraction, 0.3f, 1f));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        menu = getChildAt(0);
        main = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        menuWidth = menu.getMeasuredWidth();
        menuHeight = menu.getMeasuredHeight();
        mainWidth = main.getMeasuredWidth();
        mainHeight = main.getMeasuredHeight();

        disWidth = (int)(getMeasuredWidth()*0.6);

        //1.一开始将menuView缩小
        ViewHelper.setScaleX(menu, 0.5f);
        ViewHelper.setScaleY(menu, 0.5f);
        //2.一开始让menuView往左移动
        ViewHelper.setTranslationX(menu, -menuWidth/2);
        //3.一开始改变menuView的alpha值
        ViewHelper.setAlpha(menu, 0.3f);
    }
}
