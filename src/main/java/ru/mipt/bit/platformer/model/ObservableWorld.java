package ru.mipt.bit.platformer.model;

public interface ObservableWorld extends WorldView {

    void addListener(WorldListener listener);

    void removeListener(WorldListener listener);
}
