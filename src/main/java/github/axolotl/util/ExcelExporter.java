package github.axolotl.util;

import github.axolotl.event.AbstractEvent;
import github.axolotl.event.EventRecoder;
import github.axolotl.passenger.Passenger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.desktop.AboutEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Excel数据导出类
 *
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/15
 */
public class ExcelExporter {

    /**
     * 导出乘客数据到Excel
     *
     * @param passengerList 乘客列表
     * @param filePath      文件路径
     */
    public static void exportPassengerData(List<Passenger> passengerList, String filePath) {
        System.out.println("处理乘客数量: " + passengerList.size());
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("乘客数据");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("起始楼层");
            headerRow.createCell(1).setCellValue("目标楼层");
            headerRow.createCell(2).setCellValue("方向");
            headerRow.createCell(3).setCellValue("等待时间(s)");
            headerRow.createCell(4).setCellValue("乘梯时间(s)");
            headerRow.createCell(5).setCellValue("总时间(s)");

            // 填充数据
            int rowNum = 1;
            for (Passenger passenger : passengerList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(passenger.getOriginFloor());
                row.createCell(1).setCellValue(passenger.getTargetFloor());
                row.createCell(2).setCellValue(passenger.getDirection().toString());
                row.createCell(3).setCellValue(passenger.getWaitingTime() / 1000.0);
                row.createCell(4).setCellValue(passenger.getArriveTime() / 1000.0);
                row.createCell(5).setCellValue((passenger.getWaitingTime() + passenger.getArriveTime()) / 1000.0);
            }

//            // 自动调整列宽
//            for (int i = 0; i < 6; i++) {
//                sheet.autoSizeColumn(i);
//            }

            // 创建统计表
            Sheet statsSheet = workbook.createSheet("统计数据");
            Row statsHeader = statsSheet.createRow(0);
            statsHeader.createCell(0).setCellValue("指标");
            statsHeader.createCell(1).setCellValue("值");

            Row avgWaitRow = statsSheet.createRow(1);
            avgWaitRow.createCell(0).setCellValue("平均等待时间(s)");
            avgWaitRow.createCell(1).setCellValue(calculateAvgWaitingTime(passengerList));

            Row avgRideRow = statsSheet.createRow(2);
            avgRideRow.createCell(0).setCellValue("平均乘梯时间(s)");
            avgRideRow.createCell(1).setCellValue(calculateAvgRideTime(passengerList));

            Row avgTotalRow = statsSheet.createRow(3);
            avgTotalRow.createCell(0).setCellValue("平均总时间(s)");
            avgTotalRow.createCell(1).setCellValue(calculateAvgTotalTime(passengerList));

            // 自动调整列宽
            for (int i = 0; i < 2; i++) {
                statsSheet.autoSizeColumn(i);
            }

            // 创建目录（如果不存在）
            Files.createDirectories(Paths.get(filePath).getParent());

            // 写入文件
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("乘客数据已成功导出到: " + filePath);
        } catch (IOException e) {
            System.err.println("导出Excel文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 导出事件记录到文本文件
     *
     * @param eventRecoder 事件记录器
     * @param filePath     文件路径
     */
    public static void exportEventsToTxt(EventRecoder eventRecoder, String filePath) {
        try {
            // 创建目录（如果不存在）
            Files.createDirectories(Paths.get(filePath).getParent());

            // 写入文件
            StringBuilder content = new StringBuilder();
            content.append("事件记录\n");
            content.append("====================\n\n");

            for (AbstractEvent event : eventRecoder.getEvents()) {
                content.append(event.toString()).append("\n");
            }

            Files.writeString(Paths.get(filePath), content.toString());
            System.out.println("事件记录已成功导出到: " + filePath);
        } catch (IOException e) {
            System.err.println("导出事件记录时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 计算平均等待时间
     */
    private static double calculateAvgWaitingTime(List<Passenger> passengerList) {
        if (passengerList.isEmpty()) return 0;

        double totalWaitingTime = 0;
        for (Passenger passenger : passengerList) {
            totalWaitingTime += passenger.getWaitingTime() / 1000.0;
        }

        return totalWaitingTime / passengerList.size();
    }

    /**
     * 计算平均乘梯时间
     */
    private static double calculateAvgRideTime(List<Passenger> passengerList) {
        if (passengerList.isEmpty()) return 0;

        double totalRideTime = 0;
        for (Passenger passenger : passengerList) {
            totalRideTime += passenger.getArriveTime() / 1000.0;
        }

        return totalRideTime / passengerList.size();
    }

    /**
     * 计算平均总时间
     */
    private static double calculateAvgTotalTime(List<Passenger> passengerList) {
        if (passengerList.isEmpty()) return 0;

        double totalTime = 0;
        for (Passenger passenger : passengerList) {
            totalTime += (passenger.getWaitingTime() + passenger.getArriveTime()) / 1000.0;
        }

        return totalTime / passengerList.size();
    }
} 