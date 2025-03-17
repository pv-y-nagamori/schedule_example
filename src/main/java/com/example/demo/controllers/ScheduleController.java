package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.YearMonth;
import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entities.Schedule;
import com.example.demo.services.ScheduleService;

@Controller
public class ScheduleController {
	
	@Autowired
	ScheduleService service;
	
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);
	
    // イミュータブルにデータ保持を行うレコードクラス
    record DisplayDay (String day , List<Schedule> schedules) {}
    
	@GetMapping()
	public ModelAndView index (@RequestParam(required = false) String target) {
		
		ModelAndView mav = new ModelAndView();
		
		// 現在の年月を取得
		YearMonth ym = YearMonth.now();
		
		// クエリパラメータがある場合
		if (target != null) {
			try {
				ym = YearMonth.parse(target, DateTimeFormatter.ofPattern("yyyy-MM"));
			} catch (Exception e){
				log.error("不正な日付形式です。");
				log.error(e.toString());
				mav.setViewName("error/404");
	            return mav;
			}
		}
		
		//　月初
		LocalDate startOfMonth = ym.atDay(1);
		//　月末
		LocalDate endOfMonth = ym.atEndOfMonth();
		
		ArrayList<DisplayDay> displayDays = new ArrayList<DisplayDay>();
		
		// 月初が日曜日以外の時		
		if (startOfMonth.getDayOfWeek().getValue() != 7) {
			for (int i = 1; i <= startOfMonth.getDayOfWeek().getValue(); i++) {
				displayDays.add(null);
			}
		}
	
		for (int i = startOfMonth.getDayOfMonth(); i <= endOfMonth.getDayOfMonth(); i++) {
			DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			
			List<Schedule> schedules = service.findByScheduleDate(dateformat.format(ym.atDay(i)));
			
			DisplayDay dDay = new DisplayDay(
					Integer.toString(i),
					schedules
					);
			
			displayDays.add(dDay);
		}
		
		DateTimeFormatter displayDateformat = DateTimeFormatter.ofPattern("yyyy年MM月");
		String displayYearMonth = displayDateformat.format(ym);
		
		mav.addObject("schedule", new Schedule());
		mav.addObject("displayDays", displayDays);
		mav.addObject("displayYearMonth", displayYearMonth);
		mav.addObject("previousMonth", ym.minusMonths(1));
		mav.addObject("nextMonth", ym.plusMonths(1));
		mav.setViewName("index");
		
		return mav;
	
	}
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null && !text.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);
                    setValue(Timestamp.valueOf(localDateTime));
                }
            }
        });
    }
	
    @GetMapping({"/edit/{id}", "/create"})
    public ModelAndView editOrCreate(@PathVariable(required = false) Integer id) {

        ModelAndView mav = new ModelAndView();
        
        // idが存在すれば編集用、存在しなければ新規作成用のScheduleを設定
        Schedule schedule = new Schedule();
        if (id != null) {
        	Optional<Schedule> scheduleEntity = service.findById(id);
        	if (!scheduleEntity.isPresent()) {
				log.error("スケジュールが存在しません。");
				mav.setViewName("error/404");
	            return mav;
        	}
        	schedule = scheduleEntity.get();
        }
        
        // isEditを設定。idがnullでなければ編集、そうでなければ作成。
        mav.addObject("schedule", schedule);
        mav.addObject("isEdit", id != null);
        mav.setViewName("form");
        
        return mav;
    }
	
	@PostMapping("/store")
	public String store (@Validated Schedule schedule, BindingResult result) {
		if (result.hasErrors()) {
			return "form";
		}
		
		service.save(schedule);
		
		return "redirect:/";
	}
	
	@PostMapping("/delete/{id}")
	public String store (@PathVariable int id) {
		
		service.deleteById(id);
		
		return "redirect:/";
	}

}
