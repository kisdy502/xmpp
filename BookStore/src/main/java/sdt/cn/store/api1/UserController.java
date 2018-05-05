package sdt.cn.store.api1;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sdt.cn.store.bean.Notification;
import sdt.cn.store.service.NotificationService;
import sdt.cn.store.xmpp.ServiceLocator;
@Controller  
public class UserController {

	public UserController() {
		super();
		notificationService=ServiceLocator.getNotificationService();
		System.out.println("is null:"+(notificationService==null));
	}

	private NotificationService notificationService;

	@RequestMapping("/index")  
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {  

		List<Notification> list=notificationService.findNotificationsByUsername("admin");
		if(list!=null){
			System.out.println("size:"+list.size());
		}else{
			System.out.println("no notification!");
		}

		ModelAndView mav = new ModelAndView("index");  
		return mav;  
	}  
}
