package github.axolotl.elevator;

import github.axolotl.algorithm.ElevatorSchedulingAlgorithm;
import github.axolotl.event.ElevatorEvent;
import github.axolotl.event.EventRecoder;
import github.axolotl.event.EventType;
import github.axolotl.event.PassengerEvent;
import github.axolotl.passenger.Passenger;
import github.axolotl.passenger.PassengerGenerator;
import lombok.Data;

import java.util.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/12 20:41
 */
@Data
public class Building {
    private long duration;//模拟时长
    private final int floors;
    private final double floorHeight; // 层高(m)
    private final List<Elevator> elevators = new ArrayList<>();
    private int elevatorsCount = 0;//计数电梯数量并且编号
    private long currentTime = 0;//当前的模拟时间
    private List<Passenger> waitingPassengers = new ArrayList<>();//在外等待的请求
    private long interval;//每tick模拟的时长
    private List<PassengerGenerator> passengerGenerators = new ArrayList<>();//生成器
    private EventRecoder eventRecoder;


    public Building(long duration, int floors, double floorHeight, long interval, EventRecoder eventRecoder) {
        this.duration = duration;
        this.floors = floors;
        this.floorHeight = floorHeight;
        this.interval = interval;
        this.eventRecoder = eventRecoder;
    }

    /**
     * 开始模拟
     */
    public void startSimulation() {
        while (currentTime <= duration) {
            passengerGenerators.forEach(passengerGenerators -> passengerGenerators.tick(this));

            elevators.forEach(elevator -> elevator.schedulingAlgorithm.doLogic(this, elevator));//逻辑判断

            for (int i = 0; i < 1; i++) {
                elevators.forEach(Elevator::doTick);
                currentTime += interval;//运行

                elevators.forEach(elevator -> elevator.passengers.forEach(passenger -> passenger.addArriveTime(interval)));//乘坐时间
                waitingPassengers.forEach(waitingPassengers -> waitingPassengers.addWaitingTime(interval));//等待电梯时间
            }

        }

    }

    /**
     * 添加一台电梯
     *
     * @param elevator 电梯
     */
    public void addElevator(Elevator elevator) {
        elevator.setId("#" + ++elevatorsCount);
        elevators.add(elevator);
    }

    public void addPassengerGenerators(PassengerGenerator passengerGenerator) {
        passengerGenerators.add(passengerGenerator);
    }

    /**
     * 乘客在外面点击了请求按钮
     *
     * @param passenger 外部乘客
     */
    public void passengerCall(Passenger passenger) {
        eventRecoder.addEvent(new PassengerEvent(EventType.PassengerCall, passenger));
        waitingPassengers.add(passenger);
        elevators.forEach(elevator -> elevator.schedulingAlgorithm.passengerCall(this, elevator, passenger));
    }

    @Data
    public class Elevator {
        private String id;
        private final double speed; // 速度 m/s
        private double currentFloor;//当前位置
        private Direction direction;//现在运行方向
        private List<Passenger> passengers = new ArrayList<>();//电梯内的乘客
        private List<Passenger> joinList = new ArrayList<>();//存放有多少乘客进入电梯
        private List<Passenger> outList = new ArrayList<>();//存放有多少乘客出电梯
        private ElevatorSchedulingAlgorithm schedulingAlgorithm;
        private int openDoorTime = 0;//开关门时长
        private boolean isOpenDoor = false;//这一次tick是否开门

        public Elevator(double speed) {
            this.speed = speed;
            this.currentFloor = 0;
            this.direction = Direction.IDLE;
        }

        @Override
        public String toString() {
            return "Elevator{" +
                    "id='" + id + '\'' +
                    ", currentFloor=" + currentFloor +
                    ", passengers=" + passengers +
                    ", direction=" + direction +
                    '}';
        }

        /**
         * 运行一次tick
         */
        public void doTick() {
//            System.out.println(this);
            switch (direction) {
                case IDLE -> {
                    //do nothing
                }
                case UP -> {
                    // 优化速度计算，确保计算顺序正确
                    currentFloor += speed * (interval / 1000.0);
                    if (currentFloor >= (floors - 1) * floorHeight) {
                        currentFloor = (floors - 1) * floorHeight;//不超楼高
                    }
                }
                case DOWN -> {
                    // 优化速度计算，确保计算顺序正确
                    currentFloor -= speed * (interval / 1000.0);
                    if (currentFloor <= 0) {
                        currentFloor = 0;//不要太低（-1楼等 映射为0层开始）
                    }
                }
                case WAIT -> {
                    openDoorTime -= (int) interval;
                    if (openDoorTime <= 0)//乘客进入完毕
                        direction = Direction.IDLE;
                }
            }

            // 检查乘客进出电梯 - 放宽判断条件，使用0.3作为判断阈值
            checkPassengersToExit();
            checkPassengersToEnter();

            // 处理开门和乘客进出逻辑
            handleDoorOperations();
        }

        /**
         * 检查并处理需要出电梯的乘客
         */
        private void checkPassengersToExit() {
            List<Passenger> passengersToExit = new ArrayList<>();
            
            for (Passenger passenger : passengers) {
                // 放宽判断条件，使用0.3作为判断阈值
                if (Math.abs(passenger.getTargetFloorHeight(Building.this) - currentFloor) < 0.3) {
                    passengersToExit.add(passenger);
                    isOpenDoor = true;
                }
            }
            
            outList.addAll(passengersToExit);
        }

        /**
         * 检查并处理需要进入电梯的乘客
         */
        private void checkPassengersToEnter() {
            List<Passenger> passengersToEnter = new ArrayList<>();
            List<Passenger> passengersToRemove = new ArrayList<>();
            
            for (Passenger passenger : waitingPassengers) {
                // 放宽判断条件，使用0.3作为判断阈值
                if (Math.abs(passenger.getOriginFloorHeight(Building.this) - currentFloor) < 0.3) {
                    passengersToEnter.add(passenger);
                    passengersToRemove.add(passenger);
                    isOpenDoor = true;
                }
            }
            
            joinList.addAll(passengersToEnter);
            waitingPassengers.removeAll(passengersToRemove);
        }

        /**
         * 处理开门和乘客进出逻辑
         */
        private void handleDoorOperations() {
            if (isOpenDoor) {
                // 减少基础开门时间
                openDoorTime += 1000;
                isOpenDoor = false;
                // 设置为等待状态
                direction = Direction.WAIT;
            }
            
            // 处理乘客出电梯
            if (!outList.isEmpty()) {
                // 优化每个乘客出门时间
                openDoorTime += outList.size() * 300;
                
                for (Passenger passenger : outList) {
                    eventRecoder.addEvent(new ElevatorEvent(EventType.PassengerFinishElevator, Building.this, this, passenger));
                    passenger.finishElevator();
                    passengers.remove(passenger);
                }
                
                outList.clear();
            }
            
            // 处理乘客进入电梯
            if (!joinList.isEmpty()) {
                // 优化每个乘客进门时间
                openDoorTime += joinList.size() * 300;
                
                for (Passenger passenger : joinList) {
                    eventRecoder.addEvent(new ElevatorEvent(EventType.PassengerFinishWait, Building.this, this, passenger));
                    passengers.add(passenger);
                    passenger.finishWait();
                }
                
                joinList.clear();
            }
        }

        /**
         * 获取当前在第几层
         */
        public double getFloorNumber() {
            return currentFloor / floorHeight;
        }
    }
}
