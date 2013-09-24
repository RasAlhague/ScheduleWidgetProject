package com.example.schedulewidget;


public interface AsyncTaskCompleteListener<T>
{
    public void OnAsyncTaskComplete(T result);
}