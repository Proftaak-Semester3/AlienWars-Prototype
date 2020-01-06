package objects;

import bullet.CollisionRect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Player {

    private static final int GRAVITY = -10;

    private Healthbar healthbar;

    private Vector3 position;
    private Vector3 velocity;
    private boolean isDead;

    private Platform ship;
    private float shipScale = 2f;

    private int health;
    private boolean onship = false;
    private boolean hit = false;

    private CollisionRect rect;

    private Texture playerTexture;
    private int playerNumber;

    public Player(float x, float y, int number) {
        isDead = false;
        position = new Vector3(x, y, 0);
        playerNumber = number;
        velocity = new Vector3(0, 0, 0);
        health = 100;
        if(number == 0)
        {
            rect = new CollisionRect(x - 60, y, 60, 60);
            healthbar = new Healthbar((int)(position.x - 160), (int)(position.y + 100), health, number);
        }
        else
        {
            rect = new CollisionRect(x, y, 60, 60);
            healthbar = new Healthbar((int)position.x, (int)(position.y + 100), health, number);
        }
        ship = new Platform(rect.getCenter().x - (1.6f * shipScale) * (rect.getWidth() / 2), y - 62 * shipScale, (int)(100 * shipScale), (int)(81 * shipScale), "Platform.png");
    }

    public Texture getTexture() {
        return playerTexture;
    }

    public void update(float deltaTime)
    {
        checkDead();
        if(rect.collidesWith(ship.getCollider()))
        {
            onship = true;
            velocity = new Vector3(velocity.x, 0, velocity.z);
        }
        if(hit)
        {
            bulletHit();
        }
        if(position.y > 0 && !onship){ velocity.add(0, GRAVITY,0); }

        velocity.scl(deltaTime);
        position.add(velocity.x, velocity.y, 0);
        position.add(0, velocity.y, 0);

        velocity.scl(1/deltaTime);

        if(playerNumber == 0) { rect.move((position.x - 60), position.y); }
        else { rect.move((position.x), position.y); }

        healthbar.update();

        onship = false;
        healthbar.updatePosition(position);
    }

    public void draw(SpriteBatch sb){
        sb.draw(ship.getTexture(), ship.getXPosition(), ship.getYPosition(), 100 * shipScale, 81.4f * shipScale);
    }

    public float getXPosition() { return position.x; }
    public float getYPosition() { return position.y; }
    public int getPlayerNumber() { return playerNumber; }
    public void updateHP(int damage) { health = health - damage;  healthbar.damage(health); }
    public int getHealth() { return health; }
    public void updateVelocity(Vector3 update) { velocity = update; }
    public Vector3 getPosition() { return position; }
    public CollisionRect getCollisionRect () { return rect; }
    public Healthbar getHealthbar() { return  healthbar; }
    public void setHit(boolean hit) {  this.hit = hit;  }
    public boolean isDead() { return isDead; }
    public Platform GetShip(){ return ship; }
    public void setTexture()
    {
        if(playerTexture == null) {
            playerTexture = new Texture("alien.png");
        }
    }

    private void checkDead()
    {
        if (position.x > Gdx.graphics.getWidth() || position.y < 0 || position.x < 0 || health <= 0) { isDead = true; }
    }
    private void bulletHit()
    {
        if (playerNumber == 0) {
            if (velocity.x >= 0) {
                hit = false;
                velocity.x = 0;
            } else {
                velocity.x = velocity.x + 1;
            }
        }
        else
        {
            if (velocity.x <= 0) {
                hit = false;
                velocity.x = 0;
            } else {
                velocity.x = velocity.x - 1;
            }
        }
    }
}
