<%@page import="java.nio.file.Path"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page import="java.util.*, java.text.*" %>
<%@page import = "org.json.simple.JSONObject" %>
<%@page import = "org.json.simple.JSONArray" %>
<%@page import = "java.io.FileOutputStream" %>
<%@page import = "java.io.IOException" %>
<%@page import = "java.io.OutputStream" %>

<%
request.setCharacterEncoding("UTF-8");
%>

<%
	System.out.println(request.getParameterNames());
	JSONObject result_object = new JSONObject();
	
	
	//Http request state log을 남김
	try {
      	
		//File file = new File("requestLog.txt");
		OutputStream output = new FileOutputStream("");
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			  String headerName = headerNames.nextElement();
			  String headerValue = request.getHeader(headerName);
			  
			  String result = headerName + " : " + headerValue + "\n";
			  byte[] bytes = result.getBytes();
			  output.write(bytes);
		}
		output.write("\n".getBytes());
		Enumeration<String> params = request.getParameterNames(); 
		while(params.hasMoreElements()){
			 String paramName = params.nextElement();
			 String result = "" + paramName + " : " + request.getParameter(paramName);
			 byte[] bytes = result.getBytes();
			 output.write(bytes);
		}
		
		
	} catch(Exception e) {
		
	}
	
	String input = request.getParameter("name");
	
	if(input.equals("parkhs")) {
		
		result_object.put("name", "parkhs");
		result_object.put("age", 23);
		result_object.put("dept", "software");
	}
	else if(input.equals("parkhs2")) {
		
		result_object.put("name", "parkhs2");
		result_object.put("age", 23);
		result_object.put("dept", "software");
	}
	
	out.clear();
	out.println(result_object);
	out.flush();
%>
