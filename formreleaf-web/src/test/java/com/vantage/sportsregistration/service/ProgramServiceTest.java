package com.vantage.sportsregistration.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.vantage.sportsregistration.Application;
import com.formreleaf.domain.Program;
import com.formreleaf.repository.ProgramRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ProgramServiceTest {

	@Autowired
	ProgramService programService;
	
	@Autowired
	ProgramRepository programRepository;
	
	
	@Test
	public void createProgramTest() {
		
		String data = "{\"test\":\"test result\"}";//"{'guid':'9c36adc1-7fb5-4d5b-83b4-90356a46061a','name':'Angela Barton','is_active':true,'company':'Magnafone','address':'178 Howard Place, Gulf, Washington, 702','registered':'2009-11-07T08:53:22 +08:00','latitude':19.793713,'longitude':86.513373,'tags':['enim','aliquip','qui']}";
		

		Program program = new Program();
		program.setName("Test");
		Date startDate = new Date();
		
		program.setDeleted(false);
		//program.setFormTemplate(data);
		
		//programRepository.saveAndFlush(program);
		programRepository.save(program);
		
		program = programRepository.findAll().get(0);
		//program = programRepository.findByName("Test5 Program");
		System.out.println(program.getName());
		System.out.println(programRepository.count());
		
		
		

		
	}
}
