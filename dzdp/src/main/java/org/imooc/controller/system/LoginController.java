package org.imooc.controller.system;

import javax.servlet.http.HttpSession;

import org.imooc.constant.PageCodeEnum;
import org.imooc.constant.SessionKeyConst;
import org.imooc.dto.GroupDto;
import org.imooc.dto.UserDto;
import org.imooc.service.GroupService;
import org.imooc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 登录相关
 */
@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private HttpSession session;

	/**
	 * 登录页面
	 */
	@RequestMapping
	public String index() {
		return "/system/login";
	}

	/**
	 * session超时
	 */
	@RequestMapping("/sessionTimeout")
	public String sessionTimeout(Model model) {
		model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.SESSION_TIMEOUT);
		return "/system/error";//直接回去
	}
	
	/**
	 * 没有权限访问
	 */
	@RequestMapping("/noAuth")
	public String noAuth(Model model) {
		model.addAttribute(PageCodeEnum.KEY, PageCodeEnum.NO_AUTH);
//		session.invalidate()是销毁跟用户关联session,例如有的用户强制关闭浏览器,而跟踪用户的信息的session还存在,可是用户已经离开了。
//		虽然session 生命周期浏览默认时间30分,但是在30分钟内别的用户还可以访问到前一个用户的页面,需销毁用户的session。
//		session.removeAttribute()移除session中的某项属性。
//		在spring例子中宠物商店的注销登录的代码：
//		request.getSession().removeAttribute("userSession");
//// 注销用户，使session失效。
//		request.getSession().invalidate();
		session.invalidate();//
		return "/system/error";//就是转发
	}

	/**
	 * 验证用户名/密码是否正确 验证通过跳转至后台管理首页，验证失败，返回至登录页。
	 * RedirectAttributes门用于重定向之后还能带参数跳转的的工具类
	 * redirectAttributes.addFlashAttributie(“prama”,value); 这种方法是隐藏了参数
	 * controller中用(@RequestPrama(value = “prama”)String prama)注解，采用传参的方式
	 */
	@RequestMapping("/validate")
	public String validate(UserDto userDto, RedirectAttributes attr) {
		if (userService.validate(userDto)) {
			//session  spring直接可以拿
			//把session的key可以定义成  常量
			//session默认30分钟失效  web.xml可以配置session
			session.setAttribute(SessionKeyConst.USER_INFO, userDto);
			GroupDto groupDto = groupService.getByIdWithMenuAction(userDto.getGroupId());
			session.setAttribute(SessionKeyConst.MENU_INFO,groupDto.getMenuDtoList());
			session.setAttribute(SessionKeyConst.ACTION_INFO, groupDto.getActionDtoList());
			return "redirect:/index";//重定向到/index控制器
		}
		attr.addFlashAttribute(PageCodeEnum.KEY, PageCodeEnum.LOGIN_FAIL);
		return "redirect:/login";
	}
}