package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.BulletView;
import ru.mipt.bit.platformer.model.GameObjectView;
import ru.mipt.bit.platformer.model.ObservableWorld;
import ru.mipt.bit.platformer.model.ObstacleView;
import ru.mipt.bit.platformer.model.TankView;
import ru.mipt.bit.platformer.model.WorldListener;
import ru.mipt.bit.platformer.model.WorldView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class RenderWorld implements WorldView, WorldListener, Disposable {

    private final ObservableWorld source;
    private final List<GameObjectView> objects = new ArrayList<>();

    public RenderWorld(ObservableWorld source) {
        this.source = source;
        source.addListener(this);
    }

    @Override
    public void objectAdded(GameObjectView object) {
        if (!objects.contains(object)) {
            objects.add(object);
        }
    }

    @Override
    public void objectRemoved(GameObjectView object) {
        objects.remove(object);
    }

    @Override
    public void dispose() {
        source.removeListener(this);
    }

    @Override
    public int getWidth() {
        return source.getWidth();
    }

    @Override
    public int getHeight() {
        return source.getHeight();
    }

    @Override
    public TankView getTank() {
        return source.getTank();
    }

    @Override
    public List<TankView> getTanks() {
        return objectsOfType(TankView.class);
    }

    @Override
    public List<ObstacleView> getObstacles() {
        return objectsOfType(ObstacleView.class);
    }

    @Override
    public List<BulletView> getBullets() {
        return objectsOfType(BulletView.class);
    }

    @Override
    public List<GameObjectView> getObjects() {
        return Collections.unmodifiableList(new ArrayList<>(objects));
    }

    private <T extends GameObjectView> List<T> objectsOfType(Class<T> type) {
        return Collections.unmodifiableList(objects.stream().filter(type::isInstance)
                .map(type::cast).collect(Collectors.toList()));
    }
}
