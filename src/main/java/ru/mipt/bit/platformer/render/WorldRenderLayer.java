package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.model.WorldView;

/**
 * Infrastructure: внутренний контракт графического адаптера, зависящий от libGDX; не порт ядра.
 */
public interface WorldRenderLayer extends Disposable {

    void render(Batch batch, WorldView world);
}
