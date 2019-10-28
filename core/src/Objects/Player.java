package Objects;

import Bullet.CollisionRect;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class Player {

    private static final int GRAVITY = -10;

    Healthbar healthbar;

    private Vector3 position;
    private Vector3 velocity;

    private CollisionRect ship;

    private int health;
    public boolean onship = false;
    public boolean hit = false;

    CollisionRect rect;

    Texture PlayerTexture;
    int PlayerNumber;



    public Player(String File, float x, float y, int number) {
        PlayerTexture = new Texture(File);
        position = new Vector3(x, y, 0);
        PlayerNumber = number;
        velocity = new Vector3(0, 0, 0);
        health = 100;
        if(number == 0)
        {
            rect = new CollisionRect(x - 60, y, 60, 60);
            healthbar = new Healthbar((int)position.x - 160, (int)(position.y + 100), health, number);
        }
        else
        {
            rect = new CollisionRect(x,y, 60, 60);
            healthbar = new Healthbar((int)position.x, (int)(position.y + 100), health, number);
        }
        ship = new CollisionRect(x - 60, y - 20, 100, 10);
    }

    public Texture getTexture() {
        return PlayerTexture;
    }

    public void update(float deltaTime)
    {
        if(rect.collidesWith(ship))
        {
            onship = true;
            velocity = new Vector3(velocity.x, 0, velocity.z);
        }
        if(hit)
        {
            if (PlayerNumber == 0) {
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
        if(position.y > 0 && !onship){
            velocity.add(0, GRAVITY,0);}

        velocity.scl(deltaTime);
        position.add(velocity.x, velocity.y, 0);
        position.add(0, velocity.y, 0);

        velocity.scl(1/deltaTime);


        rect.move(position.x, position.y);

        healthbar.update(deltaTime);

        onship = false;
        healthbar.updateposition(position);
    }

    public float getXPosition() { return position.x; }
    public float getYPosition() { return position.y; }
    public int getPlayerNumber() { return PlayerNumber; }
    public void updateHP(int damage) {
        health = health - damage;
        healthbar.damage(health);
    }
    public int getHealth() { return health; }
    public void updateVelocity(Vector3 update) { velocity = update; }
    public Vector3 getPosition() { return position; }
    public CollisionRect getCollisionRect () { return rect; }
    public Healthbar getHealthbar() { return  healthbar; }
}
