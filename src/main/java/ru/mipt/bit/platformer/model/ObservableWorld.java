package ru.mipt.bit.platformer.model;

/**
 * Domain: входящий порт подписки на изменения состава мира.
 */
public interface ObservableWorld extends WorldView {

    void addListener(WorldListener listener);

    void removeListener(WorldListener listener);
}
