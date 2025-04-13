package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/13 1:52
 */
// 限定楼层生成规则
public class LimitedFloorRule implements PassengerGenerationRule {
    private final int minInterval;
    private final int maxInterval;
    private final int minFloor;
    private final int maxFloor;
    private long nextGenTime = 0;

    public LimitedFloorRule(int minInterval, int maxInterval, int minFloor, int maxFloor) {
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.nextGenTime = ThreadLocalRandom.current().nextInt(minInterval, maxInterval);
    }

    @Override
    public boolean shouldGenerate(long currentTime) {
        return currentTime >= nextGenTime;
    }

    @Override
    public Passenger generate(Building building) {
        nextGenTime += ThreadLocalRandom.current().nextInt(minInterval, maxInterval);

        int startFloor = ThreadLocalRandom.current().nextInt(minFloor, maxFloor + 1);
        int destFloor = ThreadLocalRandom.current().nextInt(minFloor, maxFloor );
        if (destFloor >= startFloor)
            destFloor++;//使得不会重复到自己


        return new Passenger(startFloor, destFloor);
    }
}