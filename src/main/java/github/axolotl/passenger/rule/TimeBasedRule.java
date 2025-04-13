package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于时间的乘客生成规则
 * 可以根据模拟时间的不同阶段生成不同模式的乘客流量
 *
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/16
 */
public class TimeBasedRule implements PassengerGenerationRule {
    private final long peakStartTime; // 高峰期开始时间
    private final long peakEndTime;   // 高峰期结束时间
    private final int minInterval;    // 最小生成间隔
    private final int maxInterval;    // 最大生成间隔
    private final int peakMinInterval; // 高峰期最小生成间隔
    private final int peakMaxInterval; // 高峰期最大生成间隔
    private final int mainSourceFloor; // 主要出发楼层
    private final int mainTargetFloor; // 主要目标楼层
    private final double reverseDirectionProbability; // 反向行驶的概率
    private long nextGenTime = 0;

    public TimeBasedRule(long peakStartTime, long peakEndTime, 
                         int minInterval, int maxInterval, 
                         int peakMinInterval, int peakMaxInterval,
                         int mainSourceFloor, int mainTargetFloor,
                         double reverseDirectionProbability) {
        this.peakStartTime = peakStartTime;
        this.peakEndTime = peakEndTime;
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.peakMinInterval = peakMinInterval;
        this.peakMaxInterval = peakMaxInterval;
        this.mainSourceFloor = mainSourceFloor;
        this.mainTargetFloor = mainTargetFloor;
        this.reverseDirectionProbability = reverseDirectionProbability;
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
        
        // 设置下一次生成时间
        if (isPeakTime) {
            nextGenTime += ThreadLocalRandom.current().nextInt(peakMinInterval, peakMaxInterval);
        } else {
            nextGenTime += ThreadLocalRandom.current().nextInt(minInterval, maxInterval);
        }
        
        int totalFloors = building.getFloors();
        int startFloor, destFloor;
        
        // 根据是否为高峰期以及随机概率决定乘客的起始楼层和目标楼层
        if (ThreadLocalRandom.current().nextDouble() < reverseDirectionProbability) {
            // 反向的少数乘客
            startFloor = this.mainTargetFloor;
            // 避免目标和起始楼层相同
            do {
                destFloor = ThreadLocalRandom.current().nextInt(1, totalFloors + 1);
            } while (destFloor == startFloor);
        } else {
            // 主流向的乘客
            startFloor = this.mainSourceFloor;
            destFloor = this.mainTargetFloor;
        }
        
        return new Passenger(startFloor, destFloor);
    }
} 