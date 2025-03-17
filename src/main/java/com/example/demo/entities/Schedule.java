package com.example.demo.entities;

import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name=("\"schedules\""))
@Data
public class Schedule {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column
	@NotBlank
	@Size(min=1, max=10)
	private String title;
	
	@Column
	private String memo;
	
	@Column(name="schedule_datetime" ,nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-ddThh:mm")
	@NotNull
	private Timestamp scheduleDatetime;

}
