package states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import render.AlienDemo;
import websockets.messageCreator.iJsonCreator;
import websockets.websocket.WebsocketClient;

public class QueueState extends State {
    private BitmapFont font;
    private iJsonCreator messageCreator;
    private boolean matchFound = false;
    private boolean firstToFire;
    private Texture background = new Texture("Loadscreen.png");

    protected QueueState(GameStateManager gsm) {
        super(gsm);
        font = new BitmapFont();
        WebsocketClient client = new WebsocketClient();
        messageCreator = client.getMessageCreator();
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            Gdx.app.exit();
        }
    }

    @Override
    public void update(float dt) {
        if(matchFound)
        {
            gsm.push(new PlayState(gsm, firstToFire, messageCreator));
        }
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0 , 0, AlienDemo.WIDTH, AlienDemo.HEIGHT);
        sb.end();
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public void setMatchFound(boolean matchFound) {
        this.matchFound = matchFound;
    }
    public void setFirstToFire(boolean firstToFire)
    {
        this.firstToFire = firstToFire;
    }
}
