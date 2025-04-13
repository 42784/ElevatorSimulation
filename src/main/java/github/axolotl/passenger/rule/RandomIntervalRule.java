package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/13 1:51
 */
// 随机时间间隔生成规则
public class RandomIntervalRule implements PassengerGenerationRule {
    private final int minInterval;
    private final int maxInterval;
    private long nextGenTime = 0;

    public RandomIntervalRule(int minInterval, int maxInterval) {
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.nextGenTime = ThreadLocalRandom.current().nextInt(minInterval, maxInterval);
    }

    @Override
    public boolean shouldGenerate(long currentTime) {
        return currentTime >= nextGenTime;
    }

    @Override
    public Passenger generate(Building building) {
        // 重置下次生成时间
        nextGenTime += ThreadLocalRandom.current().nextInt(minInterval, maxInterval);

        int totalFloors = building.getFloors();
        int startFloor = ThreadLocalRandom.current().nextInt(1, totalFloors + 1);
        int destFloor = ThreadLocalRandom.current().nextInt(1, totalFloors + 1);
        if (destFloor >= startFloor)
            destFloor++;//使得不会重复到自己


        return new Passenger(startFloor, destFloor);
    }
}
