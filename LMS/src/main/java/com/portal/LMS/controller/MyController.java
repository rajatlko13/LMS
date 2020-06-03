package com.portal.LMS.controller;



import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.portal.LMS.entity.AdminDetails;
import com.portal.LMS.entity.StudentDetails;

@Controller
public class MyController {
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	HttpServletResponse response;

	@RequestMapping(value={"/","/main"})
	public String main()
	{
		return "main";
	}
	
	@PostMapping("/adminLogin")
	@Transactional
	public String adminLogin(@ModelAttribute("theAdmin") AdminDetails theAdmin, Model theModel) throws Exception 
	{
		Session currentSession = entityManager.unwrap(Session.class);
		AdminDetails obj = currentSession.get(AdminDetails.class, theAdmin.getUsername());
			
		if(obj!=null)
		{
			EncryptPassword ep=new EncryptPassword();
			if((ep.encrypt(theAdmin.getPassword())).compareTo(obj.getPassword())==0)
			{
				HttpSession session=request.getSession();
				session.setAttribute("adminUsername",obj.getUsername());
				return "redirect:/adminPage";
			}
		}
		
		theModel.addAttribute("adminLoginError", "Invalid Credentials!");
		return "LoginPage";
	}
	
	@RequestMapping("/adminPage")
	public String adminPage()
	{
		return "AdminPage";
	}
	
	@PostMapping("/studentLogin")
	@Transactional
	public String studentLogin(@ModelAttribute("theStudent") StudentDetails theStudent, Model theModel) throws Exception 
	{
		System.out.println(request.getRequestURI());
		System.out.println(request.getRequestURL().toString());
		
		Session currentSession = entityManager.unwrap(Session.class);
		StudentDetails obj=currentSession.get(StudentDetails.class, theStudent.getUsername());
		
		System.out.println("obj= "+obj);
		
		if(obj!=null)
			System.out.println("obj username= "+obj.getUsername());
		
		
		if(obj!=null)
		{
			EncryptPassword ep=new EncryptPassword();
			if((ep.encrypt(theStudent.getPassword())).compareTo(obj.getPassword())==0)
			{
				HttpSession session=request.getSession();
				session.setAttribute("studentUsername",obj.getUsername());
				return "redirect:/studentPage";
			}
		}
		
		theModel.addAttribute("studentLoginError", "Invalid Credentials!");
		return "LoginPage";
	}
	
	@RequestMapping("/studentPage")
	public String studentPage()
	{
		return "StudentPage";
	}
	
	@PostMapping("/adminSignup")
	@Transactional
	public String adminSignup(@ModelAttribute("newadmin") AdminDetails theAdmin, Model theModel) throws Exception 
	{
		String password=theAdmin.getPassword();
		
		EncryptPassword ep=new EncryptPassword();
		theAdmin.setPassword(ep.encrypt(theAdmin.getPassword()));
		
		Session currentSession = entityManager.unwrap(Session.class);
		AdminDetails obj=currentSession.get(AdminDetails.class, theAdmin.getUsername());
		if(obj==null)
			currentSession.save(theAdmin);       //if no same username found then create new user
		else
		{
			theModel.addAttribute("firstname", theAdmin.getFirstname());
			theModel.addAttribute("lastname", theAdmin.getLastname());
			theModel.addAttribute("username", theAdmin.getUsername());
			theModel.addAttribute("password", password);
			theModel.addAttribute("usernameExistsError", "Username exists");
			return "AdminSignupPage";
		}
		
		theModel.addAttribute("success", "New user created!");
		return "AdminSignupPage";	
	}
	
	@PostMapping("/studentSignup")
	@Transactional
	public String studentSignup(@ModelAttribute("newstudent") StudentDetails theStudent, Model theModel) throws Exception 
	{
		String password=theStudent.getPassword();
		
		EncryptPassword ep=new EncryptPassword();
		theStudent.setPassword(ep.encrypt(theStudent.getPassword()));
		
		Session currentSession = entityManager.unwrap(Session.class);
		StudentDetails obj=currentSession.get(StudentDetails.class, theStudent.getUsername());
		if(obj==null)
			currentSession.save(theStudent);       //if no same username found then create new user
		else
		{
			theModel.addAttribute("firstname", theStudent.getFirstname());
			theModel.addAttribute("lastname", theStudent.getLastname());
			theModel.addAttribute("email", theStudent.getEmail());
			theModel.addAttribute("contact", theStudent.getContact());
			theModel.addAttribute("username", theStudent.getUsername());
			theModel.addAttribute("password", password);
			theModel.addAttribute("usernameExistsError", "Username exists");
			return "StudentSignupPage";
		}
		
		theModel.addAttribute("success", "New user created!");
		return "StudentSignupPage";
		
	}
	
	@RequestMapping("/logout")
	public String logout(Model theModel)
	{
		HttpSession session=request.getSession();
		session.removeAttribute("adminUsername");
		session.removeAttribute("studentUsername");
		session.invalidate();
		theModel.addAttribute("logoutDone", "successfully logged out");
		
		return "main";
	}
	
	@PostMapping("/searchForm")
	@Transactional
	public String searchForm()
	{
		String val = request.getParameter("searchItem");
		System.out.println("Requested Site : "+val);
		if(val.isEmpty())
			return "main";
		Session currentSession = entityManager.unwrap(Session.class);
		@SuppressWarnings("rawtypes")
		NativeQuery query = currentSession.createSQLQuery("select address from search_bar where link=:val");
		query.setParameter("val", val);
		String address = (String) query.uniqueResult();
		if(address.isEmpty())
			return "main";
		else
			return address;
	}

}
