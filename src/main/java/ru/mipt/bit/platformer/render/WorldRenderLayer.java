package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.WorldView;

public interface WorldRenderLayer extends Disposable {

    void render(Batch batch, WorldView world);
}
