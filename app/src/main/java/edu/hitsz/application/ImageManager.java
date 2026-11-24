package edu.hitsz.application;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.enemy_product.BossEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.EliteEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.ElitePlusEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.EliteProEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.MobEnemy;
import edu.hitsz.aircraftwar2026.R;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.item.BloodItem;
import edu.hitsz.item.BombItem;
import edu.hitsz.item.FireItem;
import edu.hitsz.item.FirePlusItem;
import edu.hitsz.item.FreezeItem;

public final class ImageManager {

    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();
    private static boolean initialized;

    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;

    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ELITE_PLUS_ENEMY_IMAGE;
    public static Bitmap ELITE_PRO_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;

    public static Bitmap PROP_BLOOD_IMAGE;
    public static Bitmap PROP_BOMB_IMAGE;
    public static Bitmap PROP_BULLET_IMAGE;
    public static Bitmap PROP_BULLET_PLUS_IMAGE;
    public static Bitmap PROP_FREEZE_IMAGE;

    private ImageManager() {
    }

    public static synchronized void init(Resources resources) {
        if (initialized) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // 实验二默认进入简单模式，使用 bg.jpg。
        BACKGROUND_IMAGE = load(resources, R.drawable.bg, options);

        HERO_IMAGE = load(resources, R.drawable.hero, options);
        HERO_BULLET_IMAGE = load(resources, R.drawable.bullet_hero, options);
        ENEMY_BULLET_IMAGE = load(resources, R.drawable.bullet_enemy, options);

        MOB_ENEMY_IMAGE = load(resources, R.drawable.mob, options);
        ELITE_ENEMY_IMAGE = load(resources, R.drawable.elite, options);
        ELITE_PLUS_ENEMY_IMAGE = load(resources, R.drawable.elite_plus, options);
        ELITE_PRO_ENEMY_IMAGE = load(resources, R.drawable.elite_pro, options);
        BOSS_ENEMY_IMAGE = load(resources, R.drawable.boss, options);

        PROP_BLOOD_IMAGE = load(resources, R.drawable.prop_blood, options);
        PROP_BOMB_IMAGE = load(resources, R.drawable.prop_bomb, options);
        PROP_BULLET_IMAGE = load(resources, R.drawable.prop_bullet, options);
        PROP_BULLET_PLUS_IMAGE =
                load(resources, R.drawable.prop_bullet_plus, options);
        PROP_FREEZE_IMAGE = load(resources, R.drawable.prop_freeze, options);

        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteProEnemy.class.getName(), ELITE_PRO_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

        CLASSNAME_IMAGE_MAP.put(BloodItem.class.getName(), PROP_BLOOD_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BombItem.class.getName(), PROP_BOMB_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FireItem.class.getName(), PROP_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FirePlusItem.class.getName(), PROP_BULLET_PLUS_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FreezeItem.class.getName(), PROP_FREEZE_IMAGE);

        initialized = true;
    }

    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object object) {
        return object == null ? null : get(object.getClass().getName());
    }

    private static Bitmap load(Resources resources, int resourceId,
                               BitmapFactory.Options options) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId, options);
        if (bitmap == null) {
            throw new IllegalStateException(
                    "无法加载图片资源 " + resources.getResourceName(resourceId));
        }
        return bitmap;
    }
}