package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.utils.ParameterStringBuilder;

@RestController
public class HelloController {

	@RequestMapping(value="/")
	public String hello() {
		return "hello world";
	}
	
	@RequestMapping(value="/arcgis/rest/**/MapServer/**")
	public void Hello(HttpServletRequest request, HttpServletResponse response) {
		// test url : http://localhost:8080/arcgis/rest/services/ChinaOnlineCommunity/MapServer
		System.out.println(request.getRequestURI());
		System.out.println(request.getMethod());
		System.out.println(request.getRemoteHost());
		System.out.println(request.getServerName());
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String)headerNames.nextElement();
			String value = request.getHeader(name);
			System.out.println(name + "=" + value);
		}
		
		System.out.println("============================");
		String destUrl = "https://map.geoq.cn" + request.getRequestURI();
		
		try {
			URL url = new URL(destUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			
			conn.setRequestProperty("accept", "*/*");
			//conn.setRequestProperty("connection", "keep-alive");
			//conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
			//conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
			
			Map<String, String> parameters = new HashMap<>();
			Enumeration<String> paramNames = request.getParameterNames();
			
			while (paramNames.hasMoreElements()) {
				String name = (String)paramNames.nextElement();
				String value = request.getParameter(name);
				parameters.put(name, value);
			}
			
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
			
			conn.connect();
			
			if (conn.getResponseCode() == 200) {
				
				for (Map.Entry<String, List<String>> entries: conn.getHeaderFields().entrySet()) {
					String values = "";
					for (String value : entries.getValue()) {
						values += value + "";
					}
					System.out.println(entries.getKey() + "=" + values);
				}
				
				
				 
				 String contentType = conn.getHeaderField("Content-Type");
				 if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					 
					 InputStream is = conn.getInputStream();
					 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					 StringBuffer sbf = new StringBuffer();
					 String temp = null;
					 while ((temp = br.readLine()) != null) {
						 sbf.append(temp);
		             }
					 System.out.println(sbf.toString());
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 //response.setHeader("Cache-Control", "max-age=86400");
					 response.setHeader("Content-Type", "application/json");
					 response.setHeader("Connection", "keep-alive");
					 //response.setHeader("Content-Encoding", "gzip");
					 //response.setHeader("Server", "Apache");
					 //response.setHeader("Vary", "Accept-Encoding");
					 //response.setHeader("X-Robots-Tag", "noindex");
					 response.setCharacterEncoding("UTF-8");
					 response.setHeader("Content-Length", conn.getHeaderField("Content-Length"));
					 
					 response.getWriter().write(sbf.toString());
					 response.getWriter().flush();
				 } else {
					 InputStream is = conn.getInputStream();
					 byte[] buffer = new byte[4096];
					 int n = 0;
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 response.setHeader("Content-Type", "image/jpeg");
					 response.setHeader("Connection", "keep-alive");
					 
					 OutputStream outResponse = response.getOutputStream();
					 
					 while ((n = is.read(buffer)) != -1) {
						 outResponse.write(buffer, 0, n);
					 }
					 outResponse.close();
				 }
			}
			
			conn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	@RequestMapping(value= {"/arcgis/rest/**/FeatureServer/{layerId}", "/arcgis/rest/**/MapServer/{layerId}"})
	public void World(HttpServletRequest request, HttpServletResponse response, @PathVariable("layerId") int layerId) {
		// test url: http://localhost:8080/arcgis/rest/services/Military/FeatureServer/2
		System.out.println(request.getRequestURI());
		System.out.println(request.getMethod());
		System.out.println(request.getRemoteHost());
		System.out.println(request.getServerName());
		System.out.println(layerId);
		
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String)headerNames.nextElement();
			String value = request.getHeader(name);
			System.out.println(name + "=" + value);
		}
		
		System.out.println("============================");
		String destUrl = "https://sampleserver6.arcgisonline.com" + request.getRequestURI();
		
		try {
			URL url = new URL(destUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			
			conn.setRequestProperty("accept", "*/*");
			//conn.setRequestProperty("connection", "keep-alive");
			//conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
			//conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
			
			Map<String, String> parameters = new HashMap<>();
			Enumeration<String> paramNames = request.getParameterNames();
			
			while (paramNames.hasMoreElements()) {
				String name = (String)paramNames.nextElement();
				String value = request.getParameter(name);
				parameters.put(name, value);
			}
			
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
			
			conn.connect();
			
			if (conn.getResponseCode() == 200) {
				
				for (Map.Entry<String, List<String>> entries: conn.getHeaderFields().entrySet()) {
					String values = "";
					for (String value : entries.getValue()) {
						values += value + "";
					}
					System.out.println(entries.getKey() + "=" + values);
				}
				
				
				 
				 String contentType = conn.getHeaderField("Content-Type");
				 if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					 
					 InputStream is = conn.getInputStream();
					 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					 StringBuffer sbf = new StringBuffer();
					 String temp = null;
					 while ((temp = br.readLine()) != null) {
						 sbf.append(temp);
		             }
					 System.out.println(sbf.toString());
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 //response.setHeader("Cache-Control", "max-age=86400");
					 response.setHeader("Content-Type", "application/json");
					 response.setHeader("Connection", "keep-alive");
					 //response.setHeader("Content-Encoding", "gzip");
					 //response.setHeader("Server", "Apache");
					 //response.setHeader("Vary", "Accept-Encoding");
					 //response.setHeader("X-Robots-Tag", "noindex");
					 response.setCharacterEncoding("UTF-8");
					 response.setHeader("Content-Length", conn.getHeaderField("Content-Length"));
					 
					 response.getWriter().write(sbf.toString());
					 response.getWriter().flush();
				 } else {
					 InputStream is = conn.getInputStream();
					 byte[] buffer = new byte[4096];
					 int n = 0;
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 response.setHeader("Content-Type", "image/jpeg");
					 response.setHeader("Connection", "keep-alive");
					 
					 OutputStream outResponse = response.getOutputStream();
					 
					 while ((n = is.read(buffer)) != -1) {
						 outResponse.write(buffer, 0, n);
					 }
					 outResponse.close();
				 }
			}
			
			conn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	@RequestMapping(value= {"/arcgis/rest/**/FeatureServer/{layerId}/query/**", "/arcgis/rest/**/MapServer/{layerId}/query/**"})
	public void hello(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getRequestURI());
		System.out.println(request.getMethod());
		System.out.println(request.getRemoteHost());
		System.out.println(request.getServerName());
		
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String)headerNames.nextElement();
			String value = request.getHeader(name);
			System.out.println(name + "=" + value);
		}
		
		System.out.println("============================");
		String destUrl = "https://sampleserver6.arcgisonline.com" + request.getRequestURI();
		
		try {
			URL url = new URL(destUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			
			conn.setRequestProperty("accept", "*/*");
			//conn.setRequestProperty("connection", "keep-alive");
			//conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
			//conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
			
			Map<String, String> parameters = new HashMap<>();
			Enumeration<String> paramNames = request.getParameterNames();
			
			while (paramNames.hasMoreElements()) {
				String name = (String)paramNames.nextElement();
				String value = request.getParameter(name);
				parameters.put(name, value);
			}
			
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
			out.flush();
			out.close();
			
			conn.connect();
			
			if (conn.getResponseCode() == 200) {
				
				for (Map.Entry<String, List<String>> entries: conn.getHeaderFields().entrySet()) {
					String values = "";
					for (String value : entries.getValue()) {
						values += value + "";
					}
					if (entries.getKey() != null) {
						System.out.println(entries.getKey() + "=" + values);
					} else {
						System.out.println(values);
					}
				}
				
				
				 
				 String contentType = conn.getHeaderField("Content-Type");
				 if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
					 
					 InputStream is = conn.getInputStream();
					 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					 StringBuffer sbf = new StringBuffer();
					 String temp = null;
					 while ((temp = br.readLine()) != null) {
						 sbf.append(temp);
		             }
					 System.out.println(sbf.toString());
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 //response.setHeader("Cache-Control", "max-age=86400");
					 response.setHeader("Content-Type", "application/json");
					 response.setHeader("Connection", "keep-alive");
					 //response.setHeader("Content-Encoding", "gzip");
					 //response.setHeader("Server", "Apache");
					 //response.setHeader("Vary", "Accept-Encoding");
					 //response.setHeader("X-Robots-Tag", "noindex");
					 response.setCharacterEncoding("UTF-8");
					 response.setHeader("Content-Length", conn.getHeaderField("Content-Length"));
					 
					 response.getWriter().write(sbf.toString());
					 response.getWriter().flush();
				 } else {
					 InputStream is = conn.getInputStream();
					 byte[] buffer = new byte[4096];
					 int n = 0;
					 
					 response.setHeader("Access-Control-Allow-Origin", "*");
					 response.setHeader("Content-Type", "image/jpeg");
					 response.setHeader("Connection", "keep-alive");
					 
					 OutputStream outResponse = response.getOutputStream();
					 
					 while ((n = is.read(buffer)) != -1) {
						 outResponse.write(buffer, 0, n);
					 }
					 outResponse.close();
				 }
			}
			
			conn.disconnect();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	public class NullHostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
	}
	
	public void trustSSL() {
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType){}
				}
		};
		
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
