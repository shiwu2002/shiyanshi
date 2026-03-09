package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发预约测试
 * 用于验证分布式锁和数据库唯一索引的防超卖机制
 */
@Slf4j
@SpringBootTest
public class ConcurrentReservationTest {

    @Autowired
    private ReservationService reservationService;

    /**
     * 测试场景：模拟 10 个用户同时预约同一个实验室的同一个时间段
     * 预期结果：只有 1 个预约成功，其余 9 个失败
     */
    @Test
    public void testConcurrentReservation() throws InterruptedException {
        log.info("========== 开始并发预约测试 ==========");
        
        // 测试参数
        final int threadCount = 10;  // 并发线程数
        final Long labId = 1L;       // 实验室 ID
        final LocalDate reserveDate = LocalDate.now().plusDays(1);  // 明天
        final String timeSlot = "08:00-10:00";  // 时间段
        final Long baseUserId = 1L;  // 基础用户 ID（每个线程使用不同的用户 ID）
        
        // 计数器
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        // 同步辅助工具
        CountDownLatch startLatch = new CountDownLatch(1);  // 控制同时开始
        CountDownLatch endLatch = new CountDownLatch(threadCount);  // 等待所有线程完成
        
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        log.info("测试参数：threadCount={}, labId={}, date={}, timeSlot={}", 
            threadCount, labId, reserveDate, timeSlot);
        
        // 提交任务
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final Long userId = baseUserId + threadIndex;  // 每个线程使用不同的用户 ID
            
            executor.submit(() -> {
                try {
                    // 等待开始信号
                    startLatch.await();
                    
                    log.info("[线程-{}] 开始预约，userId={}, labId={}, date={}, timeSlot={}", 
                        threadIndex, userId, labId, reserveDate, timeSlot);
                    
                    // 创建预约对象
                    Reservation reservation = new Reservation();
                    reservation.setUserId(userId);
                    reservation.setLabId(labId);
                    reservation.setReserveDate(reserveDate);
                    reservation.setTimeSlot(timeSlot);
                    reservation.setPeopleNum(5);
                    reservation.setPurpose("并发测试 - 线程" + threadIndex);
                    
                    try {
                        // 执行预约
                        Reservation result = reservationService.createReservation(reservation);
                        successCount.incrementAndGet();
                        log.info("[线程-{}] 预约成功！reservationId={}", threadIndex, result.getId());
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.warn("[线程-{}] 预约失败：{}", threadIndex, e.getMessage());
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("[线程-{}] 被中断", threadIndex, e);
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // 所有线程准备就绪后，同时释放开始信号
        log.info("所有线程准备就绪，3 秒后开始...");
        Thread.sleep(3000);
        startLatch.countDown();
        log.info("===== 开始并发测试 =====");
        
        // 等待所有线程完成（最多等待 60 秒）
        endLatch.await();
        
        // 关闭线程池
        executor.shutdown();
        
        // 输出测试结果
        log.info("========== 测试完成 ==========");
        log.info("总线程数：{}", threadCount);
        log.info("成功数量：{}", successCount.get());
        log.info("失败数量：{}", failCount.get());
        log.info("==============================");
        
        // 验证结果
        if (successCount.get() == 1) {
            log.info("✅ 测试通过！只有 1 个预约成功，符合预期");
        } else {
            log.error("❌ 测试失败！成功的预约数量为 {}，预期应该是 1", successCount.get());
        }
    }

    /**
     * 测试场景：模拟不同时间段的预约（应该都成功）
     * 预期结果：所有预约都成功
     */
    @Test
    public void testDifferentTimeSlots() throws InterruptedException {
        log.info("========== 开始不同时间段预约测试 ==========");
        
        final int threadCount = 5;
        final Long labId = 1L;
        final LocalDate reserveDate = LocalDate.now().plusDays(2);  // 后天
        final Long baseUserId = 100L;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // 不同的时间段
        String[] timeSlots = {
            "08:00-10:00",
            "10:00-12:00",
            "14:00-16:00",
            "16:00-18:00",
            "19:00-21:00"
        };
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final Long userId = baseUserId + threadIndex;
            final String timeSlot = timeSlots[i];
            
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Reservation reservation = new Reservation();
                    reservation.setUserId(userId);
                    reservation.setLabId(labId);
                    reservation.setReserveDate(reserveDate);
                    reservation.setTimeSlot(timeSlot);
                    reservation.setPeopleNum(3);
                    reservation.setPurpose("不同时间段测试 - 线程" + threadIndex);
                    
                    try {
                        reservationService.createReservation(reservation);
                        successCount.incrementAndGet();
                        log.info("[线程-{}] 预约成功：timeSlot={}", threadIndex, timeSlot);
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.warn("[线程-{}] 预约失败：{} - {}", threadIndex, timeSlot, e.getMessage());
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        Thread.sleep(2000);
        startLatch.countDown();
        endLatch.await();
        executor.shutdown();
        
        log.info("========== 测试完成 ==========");
        log.info("成功：{}, 失败：{}", successCount.get(), failCount.get());
        
        if (successCount.get() == threadCount) {
            log.info("✅ 测试通过！不同时间段的预约都成功了");
        } else {
            log.error("❌ 测试失败！预期全部成功");
        }
    }

    /**
     * 压力测试：模拟 100 人同时抢 1 个实验室
     */
    @Test
    public void testHighConcurrency() throws InterruptedException {
        log.info("========== 开始高并发压力测试 ==========");
        
        final int threadCount = 100;
        final Long labId = 1L;
        final LocalDate reserveDate = LocalDate.now().plusDays(7);  // 一周后
        final String timeSlot = "14:00-16:00";
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        ExecutorService executor = Executors.newFixedThreadPool(50);  // 限制线程池大小
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final Long userId = 1000L + threadIndex;
            
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Reservation reservation = new Reservation();
                    reservation.setUserId(userId);
                    reservation.setLabId(labId);
                    reservation.setReserveDate(reserveDate);
                    reservation.setTimeSlot(timeSlot);
                    reservation.setPeopleNum(1);
                    reservation.setPurpose("压力测试 - 线程" + threadIndex);
                    
                    try {
                        reservationService.createReservation(reservation);
                        successCount.incrementAndGet();
                        log.info("[线程-{}] 成功", threadIndex);
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        log.info("准备启动 100 线程并发测试...");
        Thread.sleep(3000);
        startLatch.countDown();
        endLatch.await();
        executor.shutdown();
        
        log.info("========== 压力测试完成 ==========");
        log.info("总请求数：{}", threadCount);
        log.info("成功数量：{}", successCount.get());
        log.info("失败数量：{}", failCount.get());
        log.info("==================================");
        
        if (successCount.get() == 1) {
            log.info("✅ 压力测试通过！100 人抢 1 个位置，只有 1 人成功");
        } else {
            log.error("❌ 压力测试失败！成功数量：{}", successCount.get());
        }
    }
}
