package edu.hitsz.shoot;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 弹道策略接口
 */
public interface ShootStrategy {
    List<BaseBullet> shoot(AbstractAircraft aircraft, int power, int direction);
}
