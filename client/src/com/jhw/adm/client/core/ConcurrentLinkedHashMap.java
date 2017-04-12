package com.jhw.adm.client.core;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 支持并发的LinkedHashMap
 */
public class ConcurrentLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	public ConcurrentLinkedHashMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	public ConcurrentLinkedHashMap(int maxCapacity) {
		super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
	}

	@Override
	public boolean containsKey(Object key) {
		try {
			readLock.lock();
			return super.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public V get(Object key) {
		try {
			readLock.lock();
			return super.get(key);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public V put(K key, V value) {
		try {
			writeLock.lock();
			return super.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public int size() {
		try {
			readLock.lock();
			return super.size();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void clear() {
		try {
			writeLock.lock();
			super.clear();
		} finally {
			writeLock.unlock();
		}
	}

	private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
	private final Lock readLock = rwlock.readLock();
	private final Lock writeLock = rwlock.writeLock();
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private static final long serialVersionUID = -7762717693140401105L;
}