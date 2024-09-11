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
	public static boolean isdebug = false; //debug模式将不登陆就可以上传，用于本身直接运行 fileupload-framework-ueditor 进行调试用的
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
		
		if(isdebug) {
			
		}else {
			//是否登陆
			if(!SessionUtil.isLogin()) {
				return new BaseState(false, "请先登陆");
			}
			//是否有限制某用户上传
			if(!SessionUtil.isAllowUploadForUEditor()){
				return new BaseState(false, "不允许上传文件，可能是您允许上传的存储空间已满");
			}
		}
		
		
		String filedName = (String) this.conf.get("fieldName");
		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf);
//			Log.debug("doExec--isBase64--"+state.toJSONString());
		} else {
//			Log.debug("doExec--not isBase64");
			state = BinaryUploader.save(this.request, this.conf);
			
//			JSONObject stateJson = new JSONObject(state.toJSONString());
//			
//			if(ConfigManager.getFileUpload().isStorage(LocalStorage.class)){
//				//本地存储
//				
//				//绝对路径，而非原本的相对路径
//				String stateStr = stateJson.getString("state");
//				if(stateStr.equals("SUCCESS")){
//					String filePath = stateJson.getString("url");
//					if(filePath != null && filePath.indexOf("/") == 0) {
//						filePath = filePath.substring(1, filePath.length());
//					}
//					String before = ConfigManager.getFileUpload().getDomain() + SystemUtil.getProjectName();
//					state.putInfo("url",  before + filePath);
//					
//				}else{
//					//不成功，忽略。自然会在客户端弹出提示
//				}
//			}else{
//				//非本地方式，是先将文件传到本地，再将本地的同步到目标存储上去
////				SynUploader synUploader = new SynUploader();
////				UploadFileVO uploadFileVO = synUploader.upload(stateJson, this.request);
//				
//				String key = stateJson.getString("url").replaceFirst("/", "");
//				UploadFileVO uploadFileVO = null;
//				try {
//					Log.debug("upload--fileInputStream file path: "+SystemUtil.getProjectRootPath() + key);			
//					uploadFileVO = ConfigManager.getFileUpload().upload(key, SystemUtil.getProjectRootPath() + key);
//				} catch (NumberFormatException e) {
//					Log.error("upload file to fileupload server occur NumberFormatException.");
////					UploadFileVO vo = new UploadFileVO();
////					vo.setBaseVO(UploadFileVO.FAILURE, e.getMessage());
//					return new BaseState(false, "上传时异常:"+e.getMessage());
//				}
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
////				if(uploadPath.indexOf("/") == 0){
////					uploadPath = uploadPath.substring(1, uploadPath.length());
////				}
////				state.putInfo("url", ConfigManager.getFileUpload().getDomain() + uploadPath);
//				state.putInfo("url", ConfigManager.getFileUpload().getDomain() + uploadFileVO.getPath());
//				
//				if(uploadFileVO.getResult() - UploadFileVO.FAILURE == 0) {
//					//上传失败
//					state.putInfo("state", uploadFileVO.getInfo());
//					Log.error("上传失败:"+uploadFileVO.getInfo()+",  url:"+stateJson.getString("url"));
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
