package com.tubes.lightnovel.Interface;

import com.tubes.lightnovel.Model.Novel;

import java.util.List;

public interface INovelLoadDone {
    void onNovelLoadDoneListener(List<Novel> novel);
}
