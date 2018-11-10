package com.facewarrant.fw.global;

import org.greenrobot.eventbus.EventBus;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class EventBusUtils {
    private EventBusUtils() {
    }

    /**
     * 注册EventBus
     */
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber))
            EventBus.getDefault().register(subscriber);
    }


    /**
     * 取消注册EventBus
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 发布订阅事件
     */
    public static void post(Object subscriber) {
        EventBus.getDefault().post(subscriber);
    }

    /**
     * 发布订阅事件
     */
    public static void post(final Object subscriber, final long time) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(time * 1000);//休眠3秒
                    // PalLog.d(TAG, " post delay "+subscriber);
                    EventBus.getDefault().post(subscriber);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /**
                 * 要执行的操作
                 */
            }
        }.start();


    }


    /**
     * 发布粘性订阅事件
     */
    public static void postSticky(Object subscriber) {
        EventBus.getDefault().postSticky(subscriber);
    }

    /**
     * 移除指定的粘性订阅事件
     *
     * @param eventType class的字节码，例如：String.class
     */
    public static <T> void removeStickyEvent(Class<T> eventType) {
        T stickyEvent = EventBus.getDefault().getStickyEvent(eventType);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent((T) stickyEvent);
        }
    }

    /**
     * 移除所有的粘性订阅事件
     */
    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }
}
