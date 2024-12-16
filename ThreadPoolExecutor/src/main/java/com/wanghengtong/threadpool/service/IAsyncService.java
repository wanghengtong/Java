package com.wanghengtong.threadpool.service;

public interface IAsyncService {

    void executeAsyncTask(Runnable task);

}
