package sdt.cn.store.api1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import sdt.cn.store.bean.User;
import sdt.cn.store.service.NotificationService;
import sdt.cn.store.service.UserService;
import sdt.cn.store.xmpp.ServiceLocator;
import sdt.cn.store.xmpp.presence.PresenceManager;
@Controller  
public class UserController {

	private NotificationService notificationService;
	private UserService userService;
	final PresenceManager presenceManager;

	public UserController() {
		super();
		notificationService=ServiceLocator.getNotificationService();
		userService=ServiceLocator.getUserService();
		presenceManager=PresenceManager.getInstance();
	}

	@RequestMapping("/index")  
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {  		
		ModelAndView mav = new ModelAndView("index");  
		return mav;  
	}  

	@RequestMapping("/user") 
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<User> userList = userService.getUsers();
		for (User user : userList) {
			if(presenceManager.isAvailable(user)) {
				user.setOnline(true);
			}
		}
		if(userList!=null) {
			System.out.println("size:"+userList.size());
		}else {
			System.out.println("no user!");
		}
		ModelAndView mav = new ModelAndView("/user/list");
		mav.addObject("userList", userList);
		return mav;
	}
}
