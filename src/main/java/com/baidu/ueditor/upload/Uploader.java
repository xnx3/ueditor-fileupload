package com.baidu.ueditor.upload;

import java.io.File;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.qikemi.packages.baidu.ueditor.upload.SynUploader;
import com.baidu.qikemi.packages.utils.SystemUtil;
import com.xnx3.Log;
import com.xnx3.j2ee.util.SessionUtil;
import cn.zvo.fileupload.framework.springboot.FileUploadUtil;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.fileupload.vo.UploadFileVO;


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
//		if (OSSClientProperties.useAsynUploader) {
//			ConsoleUtil.debug(state.toJSONString());
//			AsynUploaderThreader asynThreader = new AsynUploaderThreader();
//			asynThreader.init(stateJson, this.request);
//			Thread uploadThreader = new Thread(asynThreader);
//			uploadThreader.start();
//		} else {
			Log.debug(state.toJSONString());
			SynUploader synUploader = new SynUploader();
			UploadFileVO uploadFileVO = synUploader.upload(stateJson, this.request);
//		}
//		if (false == OSSClientProperties.useLocalStorager) {
//			String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
//			File uploadFile = new File(uploadFilePath);
//			if (uploadFile.isFile() && uploadFile.exists()) {
//				uploadFile.delete();
//			}
//		}
		if(uploadFileVO.getResult() - UploadFileVO.FAILURE == 0) {
			//上传失败
			state.putInfo("state", uploadFileVO.getInfo());
			Log.error("上传失败:"+uploadFileVO.getInfo()+",  url:"+stateJson.getString("url"));
		}
		state.putInfo("url", stateJson.getString("url"));
		Log.debug("state.url : "+stateJson.getString("url"));
		Log.debug("state.url : "+state.toJSONString());
		return state;
	}
	
	public final State doExec() {
		State state = null;
		//是否登陆
		if(!SessionUtil.isLogin()) {
			return new BaseState(false, "请先登陆");
		}
		//是否有限制某用户上传
		if(!SessionUtil.isAllowUploadForUEditor()){
			return new BaseState(false, "不允许上传文件，可能是您允许上传的存储空间已满");
		}
		
		String filedName = (String) this.conf.get("fieldName");
		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf);
//			Log.debug("doExec--isBase64--"+state.toJSONString());
		} else {
//			Log.debug("doExec--not isBase64");
			state = BinaryUploader.save(this.request, this.conf);
			JSONObject stateJson = new JSONObject(state.toJSONString());
			
			//判断 AttachmentUtil.mode 的模式，根据其不同，上传方式不同。
//			if(AttachmentUtil.isMode(AttachmentUtil.MODE_ALIYUN_OSS)){
//				//上传到阿里云oss
//				SynUploader synUploader = new SynUploader();
//				synUploader.upload(stateJson, this.request);
//				
//				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
//				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
//				File uploadFile = new File(uploadFilePath);
//				if (uploadFile.isFile() && uploadFile.exists()) {
//					uploadFile.delete();
//				}
//				
//				//组合url
//				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
//				if(uploadPath.indexOf("/") == 0){
//					//如果最开始是 / ，那么判断一下 OSSClientProperties.ossEndPoint 中，域名最后是否加 / 了，如果加了，那么uploadPath 最开头的的这个/去掉
//					if(OSSClientProperties.ossEndPoint.lastIndexOf("/")+1 == OSSClientProperties.ossEndPoint.length()){
//						uploadPath = uploadPath.substring(1, uploadPath.length());
//					}
//				}
//				state.putInfo("url", OSSClientProperties.ossEndPoint + uploadPath);
//				ConsoleUtil.debug("doExec--上传到阿里云oss对象存储： "+OSSClientProperties.ossEndPoint + uploadPath);
//			}else if(AttachmentUtil.isMode(AttachmentUtil.MODE_HUAWEIYUN_OBS)){
//				//上传到华为云obs
//				SynUploader synUploader = new SynUploader();
//				synUploader.upload(stateJson, this.request);
//				
//				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
//				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
//				File uploadFile = new File(uploadFilePath);
//				if (uploadFile.isFile() && uploadFile.exists()) {
//					uploadFile.delete();
//				}
//				
//				//组合url
//				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
//				if(uploadPath.indexOf("/") == 0){
//					//如果最开始是 / ，那么判断一下 OSSClientProperties.ossEndPoint 中，域名最后是否加 / 了，如果加了，那么uploadPath 最开头的的这个/去掉
//					if(OSSClientProperties.ossEndPoint.lastIndexOf("/")+1 == OSSClientProperties.ossEndPoint.length()){
//						uploadPath = uploadPath.substring(1, uploadPath.length());
//					}
//				}
//				state.putInfo("url", AttachmentUtil.netUrl() + uploadPath);
//				ConsoleUtil.debug("doExec--上传到华为云obs对象存储： "+AttachmentUtil.netUrl() + uploadPath);
//			}
			
			
			
			if(ConfigManager.getFileUpload().isStorage(LocalStorage.class)){
				//本地存储
				
				//绝对路径，而非原本的相对路径
				String stateStr = stateJson.getString("state");
				if(stateStr.equals("SUCCESS")){
					String filePath = stateJson.getString("url");
					if(filePath != null && filePath.indexOf("/") == 0) {
						filePath = filePath.substring(1, filePath.length());
					}
					state.putInfo("url",  ConfigManager.getFileUpload().getDomain() + SystemUtil.getProjectName() + filePath);
					
				}else{
					//不成功，忽略。自然会在客户端弹出提示
				}
			}else{
				//非本地方式，是先将文件传到本地，再将本地的同步到目标存储上去
				SynUploader synUploader = new SynUploader();
				UploadFileVO uploadFileVO = synUploader.upload(stateJson, this.request);
				
				//判断是否在本地磁盘存在，若存在，那么删除本地磁盘的文件。
				String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
				File uploadFile = new File(uploadFilePath);
				if (uploadFile.isFile() && uploadFile.exists()) {
					uploadFile.delete();
				}
				
				//组合url
				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
				if(uploadPath.indexOf("/") == 0){
					uploadPath = uploadPath.substring(1, uploadPath.length());
				}
				state.putInfo("url", ConfigManager.getFileUpload().getDomain() + uploadPath);
				
				if(uploadFileVO.getResult() - UploadFileVO.FAILURE == 0) {
					//上传失败
					state.putInfo("state", uploadFileVO.getInfo());
					Log.error("上传失败:"+uploadFileVO.getInfo()+",  url:"+stateJson.getString("url"));
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
		return state;
	}
	
	public static void main(String[] args) {
		String filePath = "site/219/news/20221130/1669807247411022177.png";
		if(filePath.indexOf("/") == 0) {
			filePath = filePath.substring(1, filePath.length());
		}
		System.out.println(filePath);
	}
}
