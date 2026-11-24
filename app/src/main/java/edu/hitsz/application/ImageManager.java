package edu.hitsz.application;


import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.enemy_product.EliteProEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.BossEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.EliteEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.ElitePlusEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.MobEnemy;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.item.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一管理图片的加载与访问，
 * 提供图片的静态访问方法。
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片映射，存储各类型对象对应的图片。<br>
     * 可使用 CLASSNAME_IMAGE_MAP.get(obj.getClass().getName()) 获取对象对应图片。
     */
    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage BACKGROUND_EASY_IMAGE;
    public static BufferedImage BACKGROUND_MEDIUM_IMAGE;
    public static BufferedImage BACKGROUND_HARD_IMAGE;
    public static BufferedImage BACKGROUND_INFI_IMAGE;
    public static BufferedImage HERO_IMAGE;
    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;
    public static BufferedImage MOB_ENEMY_IMAGE;

    // --- 新增：敌机图片 ---
    public static BufferedImage ELITE_ENEMY_IMAGE;
    public static BufferedImage ELITE_PLUS_ENEMY_IMAGE;
    public static BufferedImage ELITE_PRO_ENEMY_IMAGE;
    public static BufferedImage BOSS_ENEMY_IMAGE;

    // --- 新增：道具图片 ---
    public static BufferedImage PROP_BLOOD_IMAGE;
    public static BufferedImage PROP_BOMB_IMAGE;
    public static BufferedImage PROP_BULLET_IMAGE;
    public static BufferedImage PROP_BULLET_PLUS_IMAGE;
    public static BufferedImage PROP_FREEZE_IMAGE;

    static {
        try {

            // 读取背景图片
            // 背景映射要求：简单->bg，中等->bg3，困难->bg5，无敌->bg2
            BACKGROUND_EASY_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
            BACKGROUND_MEDIUM_IMAGE = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));
            BACKGROUND_HARD_IMAGE = ImageIO.read(new FileInputStream("src/images/bg5.jpg"));
            BACKGROUND_INFI_IMAGE = ImageIO.read(new FileInputStream("src/images/bg2.jpg"));
            BACKGROUND_IMAGE = BACKGROUND_MEDIUM_IMAGE;

            // 读取英雄机与英雄子弹图片
            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));

            // 读取敌机与敌机子弹图片
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            ELITE_PLUS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            ELITE_PRO_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePro.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));

            // 读取道具图片
            PROP_BLOOD_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            PROP_BOMB_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            PROP_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            PROP_BULLET_PLUS_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));
            PROP_FREEZE_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_freeze.png"));

            // 英雄机相关映射
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);

            // 敌机相关映射
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteProEnemy.class.getName(), ELITE_PRO_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

            // 道具映射（确保类名与实际道具类一致）
            CLASSNAME_IMAGE_MAP.put(BloodItem.class.getName(), PROP_BLOOD_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombItem.class.getName(), PROP_BOMB_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FireItem.class.getName(), PROP_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FirePlusItem.class.getName(), PROP_BULLET_PLUS_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FreezeItem.class.getName(), PROP_FREEZE_IMAGE);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static BufferedImage get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

    public static void setBackgroundByDifficulty(String difficulty) {
        if ("EASY".equalsIgnoreCase(difficulty)) {
            BACKGROUND_IMAGE = BACKGROUND_EASY_IMAGE;
        } else if ("INFI".equalsIgnoreCase(difficulty)) {
            BACKGROUND_IMAGE = BACKGROUND_INFI_IMAGE;
        } else if ("HARD".equalsIgnoreCase(difficulty)) {
            BACKGROUND_IMAGE = BACKGROUND_HARD_IMAGE;
        } else {
            BACKGROUND_IMAGE = BACKGROUND_MEDIUM_IMAGE;
        }
    }

}
