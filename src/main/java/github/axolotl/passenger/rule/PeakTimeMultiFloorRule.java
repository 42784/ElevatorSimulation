package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 高峰期多楼层规则
 * 适用于模拟多个楼层的乘客前往一个特定楼层的情况
 * 例如，上下班高峰期的公寓楼多个楼层居民前往一楼
 *
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/16
 */
public class PeakTimeMultiFloorRule implements PassengerGenerationRule {
    private final long peakStartTime; // 高峰期开始时间
    private final long peakEndTime;   // 高峰期结束时间
    private final int minInterval;    // 一般时段最小生成间隔
    private final int maxInterval;    // 一般时段最大生成间隔
    private final int peakMinInterval; // 高峰期最小生成间隔
    private final int peakMaxInterval; // 高峰期最大生成间隔
    private final int commonTargetFloor; // 共同目标楼层
    private final int excludedSourceFloor; // 排除的起始楼层（避免与目标楼层相同）
    private long nextGenTime = 0;

    public PeakTimeMultiFloorRule(long peakStartTime, long peakEndTime, 
                              int minInterval, int maxInterval, 
                              int peakMinInterval, int peakMaxInterval,
                              int commonTargetFloor) {
        this.peakStartTime = peakStartTime;
        this.peakEndTime = peakEndTime;
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.peakMinInterval = peakMinInterval;
        this.peakMaxInterval = peakMaxInterval;
        this.commonTargetFloor = commonTargetFloor;
        this.excludedSourceFloor = commonTargetFloor;
        this.nextGenTime = ThreadLocalRandom.current().nextInt(minInterval, maxInterval);
    }

    @Override
    public boolean shouldGenerate(long currentTime) {
        return currentTime >= nextGenTime;
    }

    @Override
    public Passenger generate(Building building) {
        long currentTime = building.getCurrentTime();
        boolean isPeakTime = currentTime >= peakStartTime && currentTime <= peakEndTime;
        
        // 设置下一次生成时间间隔
        if (isPeakTime) {
            nextGenTime += ThreadLocalRandom.current().nextInt(peakMinInterval, peakMaxInterval);
        } else {
            nextGenTime += ThreadLocalRandom.current().nextInt(minInterval, maxInterval);
        }
        
        int totalFloors = building.getFloors();
        int startFloor;
        
        // 生成非目标楼层的随机起始楼层
        do {
            startFloor = ThreadLocalRandom.current().nextInt(1, totalFloors + 1);
        } while (startFloor == excludedSourceFloor);
        
        // 所有乘客的目标都是指定的楼层
        return new Passenger(startFloor, commonTargetFloor);
    }
} 