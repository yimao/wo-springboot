package com.mudcode.springboot.loadbalance;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WeightBalance<T> {

    private final List<WeightObj<T>> objs;

    private final ReadWriteLock lock;

    private WeightBalance() {
        this.objs = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public static <T> WeightBalance<T> create() {
        return new WeightBalance<>();
    }

    public void add(T obj, double weight) {
        add(new WeightObj<>(obj, weight));
    }

    /**
     * 增加对象权重
     */
    public void add(WeightObj<T> weightObj) {
        if (null != weightObj) {
            this.objs.add(weightObj);
        }
    }

    /**
     * 下一个随机对象
     */
    public T next() {
        this.lock.readLock().lock();
        try {
            if (this.objs.isEmpty()) {
                return null;
            }

            double totalWeight = this.objs.stream().mapToDouble(WeightObj::getWeight).sum();
            double randomValue = Math.random() * totalWeight;

            List<WeightObj<T>> shuffledObjs = new ArrayList<>(this.objs);
            Collections.shuffle(shuffledObjs);

            for (WeightObj<T> obj : shuffledObjs) {
                randomValue -= obj.getWeight();
                if (randomValue <= 0) {
                    return obj.getObj();
                }
            }

            // This should not happen
            return null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void remove(T obj) {
        this.lock.writeLock().lock();
        try {
            this.objs.removeIf(entry -> Objects.equals(obj, entry.obj));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void updateWeight(T obj, int weight) {
        this.lock.writeLock().lock();
        try {
            this.remove(obj);
            this.add(obj, weight);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * 权重对象个数
     */
    public int size() {
        return this.objs.size();
    }

    @Getter
    public static class WeightObj<T> {

        /**
         * 对象
         */
        private final T obj;

        /**
         * 权重
         */
        private final double weight;

        public WeightObj(T obj, double weight) {
            this.obj = obj;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((obj == null) ? 0 : obj.hashCode());
            long temp;
            temp = Double.doubleToLongBits(weight);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            WeightObj<?> other = (WeightObj<?>) obj;
            if (this.obj == null) {
                if (other.obj != null) {
                    return false;
                }
            } else if (!this.obj.equals(other.obj)) {
                return false;
            }

            return Double.doubleToLongBits(weight) == Double.doubleToLongBits(other.weight);
        }

    }

}
