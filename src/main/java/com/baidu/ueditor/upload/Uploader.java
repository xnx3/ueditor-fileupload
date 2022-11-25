package com.baidu.ueditor.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.qikemi.packages.alibaba.aliyun.oss.properties.OSSClientProperties;
import com.qikemi.packages.baidu.ueditor.upload.AsynUploaderThreader;
import com.qikemi.packages.baidu.ueditor.upload.SynUploader;
import com.qikemi.packages.utils.SystemUtil;
import com.xnx3.DateUtil;
import com.xnx3.UrlUtil;
import com.xnx3.j2ee.util.AttachmentUtil;
import com.xnx3.j2ee.util.ConsoleUtil;
import com.xnx3.j2ee.util.SessionUtil;
import com.xnx3.j2ee.vo.UploadFileVO;


/**
 * 同步上传文件到阿里云OSS<br>
 * 
 * @create date : 2014年10月28日 上午22:15:00
 * @Author XieXianbin<a.b@hotmail.com>
 * @Source Repositories Address:
 *         <https://github.com/qikemi/UEditor-for-aliyun-OSS>
 */
public class Uploader {
	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;

	public Uploader(HttpServletRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
	}

	public State doExecss(){
		State state = null;
		state = BinaryUploader.save(this.request, this.conf);
		JSONObject stateJson = new JSONObject(state.toJSONString());
//		String bucketName = OSSClientProperties.bucketName;
//		OSSClient client = OSSClientFactory.createOSSClient();
		if (OSSClientProperties.useAsynUploader) {
			ConsoleUtil.debug(state.toJSONString());
			AsynUploaderThreader asynThreader = new AsynUploaderThreader();
			asynThreader.init(stateJson, this.request);
			Thread uploadThreader = new Thread(asynThreader);
			uploadThreader.start();
		} else {
			ConsoleUtil.debug(state.toJSONString());
			SynUploader synUploader = new SynUploader();
			synUploader.upload(stateJson, this.request);
		}
		if (false == OSSClientProperties.useLocalStorager) {
			String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
			File uploadFile = new File(uploadFilePath);
			if (uploadFile.isFile() && uploadFile.exists()) {
				uploadFile.delete();
			}
		}
		state.putInfo("url", OSSClientProperties.ossEndPoint + stateJson.getString("url"));
		ConsoleUtil.debug("state.url : "+OSSClientProperties.ossEndPoint + stateJson.getString("url"));
		ConsoleUtil.debug("state.url : "+state.toJSONString());
		return state;
	}
	
	public final State doExec() {
		ConsoleUtil.debug("doExec--");
		State state = null;
		//是否有限制某用户上传
		if(OSSClientProperties.astrictUpload){
			if(!SessionUtil.isAllowUploadForUEditor()){
				return new BaseState(false, OSSClientProperties.astrictUploadMessage);
			}
		}
		
		String filedName = (String) this.conf.get("fieldName");
		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf);
			ConsoleUtil.debug("doExec--isBase64--"+state.toJSONString());
		} else {
			ConsoleUtil.debug("doExec--not isBase64");
			state = BinaryUploader.save(this.request, this.conf);
			JSONObject stateJson = new JSONObject(state.toJSONString());
			
			ConsoleUtil.debug("doExec--OSSClientProperties.useStatus: "+OSSClientProperties.useStatus);
			
			//判断 AttachmentUtil.mode 的模式，根据其不同，上传方式不同。
			if(AttachmentUtil.isMode(AttachmentUtil.MODE_ALIYUN_OSS)){
				//上传到阿里云oss
				SynUploader synUploader = new SynUploader();
				synUploader.upload(stateJson, this.request);
				
				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
				File uploadFile = new File(uploadFilePath);
				if (uploadFile.isFile() && uploadFile.exists()) {
					uploadFile.delete();
				}
				
				//组合url
				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
				if(uploadPath.indexOf("/") == 0){
					//如果最开始是 / ，那么判断一下 OSSClientProperties.ossEndPoint 中，域名最后是否加 / 了，如果加了，那么uploadPath 最开头的的这个/去掉
					if(OSSClientProperties.ossEndPoint.lastIndexOf("/")+1 == OSSClientProperties.ossEndPoint.length()){
						uploadPath = uploadPath.substring(1, uploadPath.length());
					}
				}
				state.putInfo("url", OSSClientProperties.ossEndPoint + uploadPath);
				ConsoleUtil.debug("doExec--上传到阿里云oss对象存储： "+OSSClientProperties.ossEndPoint + uploadPath);
			}else if(AttachmentUtil.isMode(AttachmentUtil.MODE_HUAWEIYUN_OBS)){
				//上传到华为云obs
				SynUploader synUploader = new SynUploader();
				synUploader.upload(stateJson, this.request);
				
				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
				File uploadFile = new File(uploadFilePath);
				if (uploadFile.isFile() && uploadFile.exists()) {
					uploadFile.delete();
				}
				
				//组合url
				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
				if(uploadPath.indexOf("/") == 0){
					//如果最开始是 / ，那么判断一下 OSSClientProperties.ossEndPoint 中，域名最后是否加 / 了，如果加了，那么uploadPath 最开头的的这个/去掉
					if(OSSClientProperties.ossEndPoint.lastIndexOf("/")+1 == OSSClientProperties.ossEndPoint.length()){
						uploadPath = uploadPath.substring(1, uploadPath.length());
					}
				}
				state.putInfo("url", AttachmentUtil.netUrl() + uploadPath);
				ConsoleUtil.debug("doExec--上传到华为云obs对象存储： "+AttachmentUtil.netUrl() + uploadPath);
			}else if(AttachmentUtil.isMode(AttachmentUtil.MODE_KUOZHAN)){
				//自定义扩展
				
				//获取到本地磁盘保存的文件的名字
				String fileName = UrlUtil.getFileName((String)stateJson.get("url"));
				String key = stateJson.getString("url").replaceFirst("/", "");
				UploadFileVO uploadFileVO = null;
				try {
					FileInputStream fileInputStream = new FileInputStream(new File(
							SystemUtil.getProjectRootPath() + key));
					uploadFileVO = AttachmentUtil.uploadFile("site/"+SessionUtil.getUeUploadParam1()+"/ue/"+DateUtil.currentDate("yyyyMMdd")+"/"+fileName, fileInputStream);
				} catch (FileNotFoundException e) {
					ConsoleUtil.error("upload file to AttachmentUtil.mode: kuozhan, FileNotFoundException.");
				} catch (NumberFormatException e) {
					ConsoleUtil.error("upload file to AttachmentUtil.mode: kuozhan, NumberFormatException.");
				} catch (IOException e) {
					ConsoleUtil.error("upload file to AttachmentUtil.mode: kuozhan, IOException."+e.getMessage());
				}
				if(uploadFileVO != null && uploadFileVO.getResult() - UploadFileVO.SUCCESS == 0){
					state.putInfo("url", uploadFileVO.getUrl());
					ConsoleUtil.debug("上传到自定义AttachmentUtil的存储  "+uploadFileVO.getPath());
				}else{
					//上传失败
				}
				
				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
				File uploadFile = new File(uploadFilePath);
				if (uploadFile.isFile() && uploadFile.exists()) {
					uploadFile.delete();
				}
			}else if(AttachmentUtil.isMode(AttachmentUtil.MODE_LOCAL_FILE)){
				//本地存储
				//绝对路径，而非原本的相对路径
				String stateStr = stateJson.getString("state");
				if(stateStr.equals("SUCCESS")){
					state.putInfo("url",  AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
					ConsoleUtil.debug("doExec--上传服务器磁盘： "+AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
				}else{
					//不成功，忽略。自然会在客户端弹出提示
				}
			}else{
				//未发现存储方式，那么默认使用服务器本身存储
				ConsoleUtil.error("AttachmentUtil.mode 未发现存储方式。当前AttachmentUtil.mode:"+AttachmentUtil.mode);
				
				String stateStr = stateJson.getString("state");
				if(stateStr.equals("SUCCESS")){
					state.putInfo("url",  AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
					ConsoleUtil.debug("doExec--上传服务器磁盘： "+AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
				}else{
					//不成功，忽略。自然会在客户端弹出提示
				}
			}
			
			// 判别云同步方式
//			if (OSSClientProperties.useStatus) {
//				// upload type
//				ConsoleUtil.debug("doExec--OSSClientProperties.useAsynUploader: "+OSSClientProperties.useAsynUploader);
//				if (OSSClientProperties.useAsynUploader) {
//					AsynUploaderThreader asynThreader = new AsynUploaderThreader();
//					asynThreader.init(stateJson, this.request);
//					Thread uploadThreader = new Thread(asynThreader);
//					uploadThreader.start();
//				} else {
//					SynUploader synUploader = new SynUploader();
//					synUploader.upload(stateJson, this.request);
//				}
//
//				// storage type
//				if (false == OSSClientProperties.useLocalStorager) {
//					String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
//					File uploadFile = new File(uploadFilePath);
//					if (uploadFile.isFile() && uploadFile.exists()) {
//						uploadFile.delete();
//					}
//				}
//				
//				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
//				if(uploadPath.indexOf("/") == 0){
//					//如果最开始是 / ，那么判断一下 OSSClientProperties.ossEndPoint 中，域名最后是否加 / 了，如果加了，那么uploadPath 最开头的的这个/去掉
//					if(OSSClientProperties.ossEndPoint.lastIndexOf("/")+1 == OSSClientProperties.ossEndPoint.length()){
//						uploadPath = uploadPath.substring(1, uploadPath.length());
//					}
//				}
//				state.putInfo("url", OSSClientProperties.ossEndPoint + uploadPath);
//				ConsoleUtil.debug("doExec--上传到对象存储： "+OSSClientProperties.ossEndPoint + uploadPath);
//			} else {
//				//绝对路径，而非原本的相对路径
//				String stateStr = stateJson.getString("state");
//				if(stateStr.equals("SUCCESS")){
//					state.putInfo("url",  AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
//					ConsoleUtil.debug("doExec--上传服务器磁盘： "+AttachmentUtil.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
//				}else{
//					//不成功，忽略。自然会在客户端弹出提示
//				}
//			}
		}
		/*
		 * { "state": "SUCCESS", "title": "1415236747300087471.jpg", "original":
		 * "a.jpg", "type": ".jpg", "url":
		 * "/upload/image/20141106/1415236747300087471.jpg", "size": "18827" }
		 */
		ConsoleUtil.debug("doExec--最后返回："+state.toJSONString());
		return state;
	}
}
