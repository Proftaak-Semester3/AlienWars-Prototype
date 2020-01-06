package states;

import bullet.Bullets;
import bullet.TurnHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import gameChecks.CameraUpdate;
import gameChecks.CollisionChecks;
import gameChecks.PlayerBool;
import objects.Player;
import render.AlienDemo;
import websockets.messageCreator.iJsonCreator;
import websockets.messageSender.MessageBroadcaster;

import java.util.ArrayList;

public class PlayState extends State {

    Bullets currentBullet;
    private TurnHandler turnHandler;
    private Texture bg;
    private Texture textureship;
    private CollisionChecks collisionChecks;
    private CameraUpdate camUpdate;
    private OrthographicCamera cam;
    private iJsonCreator messageCreator;
    private PlayerBool playerBool;
    private boolean serverconfirm;
    private boolean yourTurn;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int playerSize = 60;
    private int playernumber;
    private ArrayList<Bullets> bullets;


    protected PlayState(GameStateManager gsm, boolean firstToFire, iJsonCreator messageCreator) {
        super(gsm);
        this.camUpdate = new CameraUpdate();
        this.messageCreator = messageCreator;
        this.gsm = gsm;
        this.playerBool = new PlayerBool();
        serverconfirm = false;
        turnHandler = new TurnHandler();
        this.collisionChecks = new CollisionChecks();
        cam = new OrthographicCamera(AlienDemo.WIDTH / 1.5F, AlienDemo.HEIGHT / 1.5F);
        cam.update();
        bg = new Texture("melkweg.jpg");
        textureship = new Texture("Platform.png");
        bullets = new ArrayList<>();
        if (firstToFire) {
            playernumber = 0;
            yourTurn = true;
        } else {
            playernumber = 1;
            yourTurn = false;
        }
        x1 = (int) turnHandler.getPlayer1().getXPosition();
        y1 = (int) turnHandler.getPlayer1().getYPosition();
        x2 = (int) turnHandler.getPlayer2().getXPosition();
        y2 = (int) turnHandler.getPlayer2().getYPosition();
        MessageBroadcaster.broadcast(messageCreator.startPositionMessage(x1,y1,x2,y2));
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                Player currentPlayer = turnHandler.getCurrentPlayer();
                Vector3 convertedInputPosition = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                if (bullets.isEmpty() && turnHandler.player1turn(playernumber)) {
                    MessageBroadcaster.broadcast(messageCreator.bulletMessage(currentPlayer.getXPosition(), currentPlayer.getYPosition(), (int) (convertedInputPosition.x - currentPlayer.getXPosition()), (int) (convertedInputPosition.y - currentPlayer.getYPosition()), yourTurn));
                    try{
                        Thread.sleep(50);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    turnHandler.switchTurn();
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void update(float dt) {
        if (serverconfirm) {
            handleInput();
            cam.update();
            for (Bullets bullet : bullets) {
                collisionChecks.checkCollision(bullet, turnHandler);
                bullet.update(dt);
            }
            turnHandler.getPlayer1().update(dt);
            turnHandler.getPlayer2().update(dt);
            if (turnHandler.getPlayer1().isDead() || turnHandler.getPlayer2().isDead()) {
                cam = new OrthographicCamera(AlienDemo.WIDTH, AlienDemo.HEIGHT);
                cam.update();
                gsm.set(new MenuState(gsm));
                dispose();
            }
            if (currentBullet != null) {
                bullets.add(currentBullet);
            }

            removeBullets(dt);
        }
    }

    private void removeBullets(float dt) {
        ArrayList<Bullets> bulletsToRemove = new ArrayList<>();

        for (Bullets bullet : this.bullets) {
            if (bullet.isRemove()) {
                if(!bullet.isHit())
                {
                    turnHandler.switchTurn();
                    System.out.println("switched turns");
                }
                bulletsToRemove.add(bullet);
            }
        }

        bullets.removeAll(bulletsToRemove);
    }

    @Override
    public void render(SpriteBatch sb) {
        camUpdate.cameraUpdate(cam, turnHandler, bullets);
        cam.update();
        sb.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, AlienDemo.WIDTH, AlienDemo.HEIGHT);
        sb.draw(turnHandler.getPlayer1().getTexture(), turnHandler.getPlayer1().getXPosition(), turnHandler.getPlayer1().getYPosition(), -playerSize, playerSize * 1.8f);
        sb.draw(turnHandler.getPlayer2().getTexture(), turnHandler.getPlayer2().getXPosition(), turnHandler.getPlayer2().getYPosition(), playerSize, playerSize * 1.8f);
        turnHandler.getPlayer1().getHealthbar().render(sb);
        turnHandler.getPlayer2().getHealthbar().render(sb);
        for (Bullets bullet : this.bullets) {
            bullet.setTexture();
            bullet.render(sb);
        }
        turnHandler.getPlayer1().draw(sb);
        turnHandler.getPlayer2().draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
    }

    public void enemyMove(float x, float y, int horizontal, int vertical, boolean player1Turn) {
        bullets.add(new Bullets(x, y, horizontal, vertical, player1Turn));
        if(playerBool.playerbool(playernumber) != player1Turn)
        {
            turnHandler.switchTurn();
        }
        System.out.println("bullet made");
    }

    public void confirmServer(boolean confirm) {
        if (!confirm) {
            System.out.println("NotConfirmed");
            gsm.push(new MenuState(gsm));
        } else {
            serverconfirm = true;
            System.out.println("Confirmed");
        }
    }
}
