package Controller.rest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
	
	@RequestMapping(value = "/test/validatescore", method = RequestMethod.GET)
	public String ValidateScore(@ModelAttribute("score") String score){
		if(score.equals("70")){
			return "pass";
		}else{
			return "deny";
		}
	}
}
