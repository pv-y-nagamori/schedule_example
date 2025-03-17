package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Schedule;
import com.example.demo.reposities.ScheduleRepository;

@Service
public class ScheduleService {
	@Autowired 
	ScheduleRepository repository;
	
	public Schedule save(Schedule schedule) {
		return repository.save(schedule);
	}
	
	public Optional<Schedule> findById(int id) {
		return repository.findById(id);
	}
	
	public List<Schedule> findByScheduleDate(String Date) {
		return repository.findByScheduleDate(Date);
	}
	
	public void deleteById(int id) {
		repository.deleteById(id);
		return;
	}

}
