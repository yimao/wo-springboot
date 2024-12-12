package com.mudcode.springboot.test;

public class SingleModelDemo {

    private static volatile SingleModelDemo instance = null;

    private long timestamp;

    private SingleModelDemo() {
    }

    public static SingleModelDemo getInstance() {
        if (instance == null) {
            synchronized (SingleModelDemo.class) {
                if (instance == null) {
                    instance = new SingleModelDemo();
                    instance.init();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        SingleModelDemo.getInstance().sayHi();
    }

    private void init() {
        this.timestamp = System.currentTimeMillis();
    }

    public void sayHi() {
        System.out.println(this.timestamp);
    }

}
