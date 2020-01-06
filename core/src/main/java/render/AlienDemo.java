package render;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import states.GameStateManager;
import states.LoginState;
import states.MenuState;
import states.RegisterState;


public class AlienDemo extends ApplicationAdapter {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    public static final String TITLE = "Alien Demo";
    private GameStateManager gsm;

    private SpriteBatch batch;


    @Override
    public void create () {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        Gdx.gl.glClearColor(1,0,0,1);
        gsm.push(new MenuState(gsm));

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }
}
