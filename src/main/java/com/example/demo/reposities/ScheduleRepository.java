package com.example.demo.reposities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entities.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	
	@Query(value = "SELECT * FROM \"schedules\" WHERE CAST(schedule_datetime AS DATE) = :date ORDER BY schedule_datetime ASC", nativeQuery = true)
	List<Schedule> findByScheduleDate(String date);
}