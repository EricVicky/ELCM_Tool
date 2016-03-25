package Controller.rest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
	
	@RequestMapping(value = "test/validatescore", method = RequestMethod.GET)
	public String ValidateScore(@PathVariable("score") int score, Model model){
		if(score > 60){
			return "pass";
		}else{
			return "deny";
		}
	}
}
