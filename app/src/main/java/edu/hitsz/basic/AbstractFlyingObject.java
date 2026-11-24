package edu.hitsz.basic;

import android.graphics.Bitmap;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.game.GameConfig;

public abstract class AbstractFlyingObject {

    // 坐标表示图片中心。
    protected int locationX;
    protected int locationY;

    protected int speedX;
    protected int speedY;

    protected Bitmap image;
    protected int width = -1;
    protected int height = -1;

    protected boolean isValid = true;

    public AbstractFlyingObject() {
    }

    public AbstractFlyingObject(int locationX, int locationY,
                                int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void forward() {
        locationX += speedX;
        locationY += speedY;

        if (locationX <= 0 || locationX >= GameConfig.GAME_WIDTH) {
            speedX = -speedX;
        }
    }

    public boolean crash(AbstractFlyingObject flyingObject) {
        // 飞机的纵向碰撞范围按原项目规则缩小。
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int otherFactor = flyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = flyingObject.getLocationX();
        int y = flyingObject.getLocationY();
        int halfWidth = (flyingObject.getWidth() + getWidth()) / 2;
        int halfHeight =
                (flyingObject.getHeight() / otherFactor + getHeight() / factor) / 2;

        return x + halfWidth > locationX
                && x - halfWidth < locationX
                && y + halfHeight > locationY
                && y - halfHeight < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocation(double locationX, double locationY) {
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedX() {
        return speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeed(int speedX, int speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public Bitmap getImage() {
        if (image == null) {
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width == -1) {
            width = getImage().getWidth();
        }
        return width;
    }

    public int getHeight() {
        if (height == -1) {
            height = getImage().getHeight();
        }
        return height;
    }

    public void vanish() {
        isValid = false;
    }

    public boolean notValid() {
        return !isValid;
    }
}