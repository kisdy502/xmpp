package sdt.cn.store.api1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sdt.cn.store.util.Config;
import sdt.cn.store.xmpp.push.NotificationManager;

@Controller  
public class NotificationController {

	private NotificationManager notificationManager;

	public NotificationController() {
		notificationManager = new NotificationManager();
	}

	@RequestMapping("/notification")  
	public ModelAndView form(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("/notification/form");
		return mav;
	}

	@RequestMapping("/sendNotification")  
	public ModelAndView send(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String broadcast = ServletRequestUtils.getStringParameter(request,"broadcast", "0");
		String username = ServletRequestUtils.getStringParameter(request,"username");
		String alias = ServletRequestUtils.getStringParameter(request,"alias");
		String title = ServletRequestUtils.getStringParameter(request, "title");
		String message = ServletRequestUtils.getStringParameter(request,"message");
		String uri = ServletRequestUtils.getStringParameter(request, "uri");
		String apiKey = Config.getString("apiKey", "");
		System.out.println("broadcast:"+broadcast);
		System.out.println("username:"+username);
		System.out.println("alias:"+alias);
		System.out.println("title:"+title);
		System.out.println("message:"+message);
		System.out.println("uri:"+uri);
		
		if (broadcast.equalsIgnoreCase("0")) {
			notificationManager.sendBroadcast(apiKey, title, message, uri);
		} else if(broadcast.equalsIgnoreCase("1")) {
			notificationManager.sendNotifcationToUser(apiKey, username, title,message, uri,true);
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:notification");
		return mav;
	}
}
