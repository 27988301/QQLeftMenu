##QQLeftMenu
  QQ旧版的侧滑栏,增加了复杂情况下的事件分发处理

  主要判断了如果在多级view,比如内嵌ViewPager情况下的事件分发处理,这只是一种边界触摸事件

  ViewHelp有个bug,就是如果界面正在ViewHelp的操控下滑动时,

  如果这时界面的view发生改变,比如textview更改文字,广告viewpager自动轮播等等,ViewHelp所操控的滑动状态立刻消失,界面布局立刻回到原处,

  有时还有发生main界面缩小的百分之80,并处于屏幕中间(这是我在界面滑动中广告轮播图自动轮播出现的状况),
  
  ![image](https://github.com/Zhaoss/QQLeftMenu/blob/master/image/1.jpg?raw=true)

  我尝试很多方法解决,都没有办法,所以只要让广告轮播图在界面滑动时暂停轮播

  如果界面上来就处于leftmenu打开状态,那么这是调用viewDragHelper.processTouchEvent(event);

  就会出现空指针异常,这是由于viewDragHelper内部初始化不成功导致的,并且因为viewDragHelper内部代码初始化方法是私有的,所以手动调用方法初始化不行,只能抓取一下异常

  ps:代码只提供思路

![image](https://github.com/Zhaoss/QQLeftMenu/blob/master/image/2.jpg?raw=true)
