package github.axolotl.elevator;

import github.axolotl.algorithm.ElevatorSchedulingAlgorithm;
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
    private final int floors;
    private final double floorHeight; // 层高(m)
    private final List<Elevator> elevators = new ArrayList<>();
    private int elevatorsCount = 0;//计数电梯数量并且编号
    private long currentTime = 0;//当前的模拟时间
    private long duration;//模拟时长
    private List<Passenger> waitingPassengers = new ArrayList<>();//在外等待的请求
    private long interval;//每tick模拟的时长
    private List<PassengerGenerator> passengerGenerators = new ArrayList<>();//生成器


    public Building(long duration, int floors, double floorHeight, long interval) {
        this.duration = duration;
        this.floors = floors;
        this.floorHeight = floorHeight;
        this.interval = interval;
    }


    /**
     * 开始模拟
     */
    public void startSimulation() {
        while (currentTime <= duration) {
            passengerGenerators.forEach(passengerGenerators -> passengerGenerators.tick(this));

            elevators.forEach(elevator -> elevator.schedulingAlgorithm.doLogic(this, elevator));//逻辑判断

            for (int i = 0; i < 2; i++) {
                elevators.forEach(Elevator::doTick);
                currentTime += interval;//运行

                waitingPassengers.forEach(waitingPassengers -> waitingPassengers.addWaitingTime(interval));//等待时间
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
        private ElevatorSchedulingAlgorithm schedulingAlgorithm;
        private int waitTime = 0;//等待乘客的时间

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
                    ", joinList=" + joinList +
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
                    currentFloor += speed / 1000.0 * interval;
                    if (currentFloor >= (floors - 1) * floorHeight) {
                        currentFloor = (floors - 1) * floorHeight;//不超楼高
                    }
                }
                case DOWN -> {
                    currentFloor -= speed / 1000.0 * interval;
                    if (currentFloor <= 0) {
                        currentFloor = 0;//不要太低（-1楼等 映射为0层开始）
                    }
                }
                case WAIT -> {
                    waitTime -= (int) interval;
                    if (waitTime <= 0)//乘客进入完毕
                        direction = Direction.IDLE;
                }
            }

            waitingPassengers.forEach(passenger -> {
                if (Math.abs(passenger.getOriginFloorHeight(Building.this) - getFloorNumber()) < 0.05) {//楼距小于0.1 认为接待乘客成功
                    joinList.add(passenger);
                }
            });

            //乘客进入电梯
            waitTime += 3000;//开关门
            waitTime += joinList.size() * 500;//每一个人进门

            joinList.forEach(passenger -> {
                passengers.add(passenger);
                passenger.finish();//结束等待
            });
            joinList.clear();
        }

        /**
         * 获取当前在第几层
         */
        public double getFloorNumber() {
            return currentFloor / floorHeight - 1;
        }


    }
}
