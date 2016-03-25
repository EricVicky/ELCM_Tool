package Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class DataController {
	
	@RequestMapping(value = "/test/validatescore/70", method = RequestMethod.GET)
	public String ValidateScore(@PathVariable("score") int score, Model model){
		if(score > 60){
			return "pass";
		}else{
			return "deny";
		}
	}
}
