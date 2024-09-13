package com.xnx3.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.upload.Uploader;
import com.xnx3.DateUtil;
import com.xnx3.j2ee.controller.BaseController;
import com.xnx3.j2ee.entity.User;
import com.xnx3.j2ee.service.SqlService;
import com.xnx3.j2ee.util.ApplicationPropertiesUtil;
import com.xnx3.j2ee.util.AttachmentUtil;
import com.xnx3.j2ee.util.ConsoleUtil;
import com.xnx3.j2ee.util.Page;
import com.xnx3.j2ee.util.SessionUtil;
import com.xnx3.j2ee.util.Sql;
import com.xnx3.j2ee.util.SystemUtil;
import com.xnx3.j2ee.vo.BaseVO;
import com.xnx3.j2ee.vo.UploadFileVO;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.framework.springboot.FileUploadUtil;
import cn.zvo.fileupload.storage.sftp.SftpStorage;
import net.sf.json.JSONObject;

/**
 * 用来快速测试的，访问后直接打开ueditor进行测试
 * 打包jar不会包含这个
 * @author 管雷鸣
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseController {
	@Resource
	private SqlService sqlService;

	/**
	 * 访问后直接打开ueditor进行测试
	 */
	@RequestMapping("index.do")
	public String index(HttpServletRequest request,Model model){
		
		
		
		
		Uploader.isdebug = true;
		FileUpload fileupload = new FileUpload();
		fileupload.setDomain("http://localhost:8080/");
		
		
		/**** 定义存储位置，存储到SFTP中 ****/
		Map<String, String> config = new HashMap<String, String>();
		config.put("host", "192.168.31.54");
		config.put("username", "root");
		config.put("password", "jiayouzhan");
		SftpStorage storage = new SftpStorage(config);
		fileupload.setStorage(storage);	//设置使用oss存储
		/*** 结束。 上面sftp这段全部注释掉则是用本地存储 ***/
		
		ConfigManager.setFileUpload(fileupload);
		return "/index";
	}
	
	
}
