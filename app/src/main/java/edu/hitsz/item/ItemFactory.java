package edu.hitsz.item;

import java.util.Random;

/**
 * 道具工厂：简单工厂模式
 */
public class ItemFactory {

    private static final Random RANDOM = new Random();

    /**
     * 随机创建一个道具
     * @param x 产生位置的横坐标
     * @param y 产生位置的纵坐标
     * @return 具体的道具对象
     */
    public static BaseItem createItem(int x, int y) {
        // 生成 0-4 的随机数，对应 5 种道具
        int type = RANDOM.nextInt(5);

        switch (type) {
            case 0:
                return new BloodItem(x, y, 0, 5);      // 加血道具
            case 1:
                return new FireItem(x, y, 0, 5);       // 火力道具
            case 2:
                return new FirePlusItem(x, y, 0, 5);   // 超级火力
            case 3:
                return new BombItem(x, y, 0, 5);       // 炸弹道具
            case 4:
                return new FreezeItem(x, y, 0, 5);     // 冰冻道具
            default:
                return null;
        }
    }
}