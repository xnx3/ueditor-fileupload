package com.baidu.qikemi.packages.baidu.ueditor.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.baidu.qikemi.packages.utils.SystemUtil;
import com.xnx3.Log;

import cn.zvo.fileupload.framework.springboot.FileUploadUtil;


/**
 * 同步上传文件到阿里云OSS<br>
 * 
 * @create date : 2014年10月28日 22:11:00
 * @Author XieXianbin<a.b@hotmail.com>
 * @Source Repositories Address:
 *         <https://github.com/qikemi/UEditor-for-aliyun-OSS>
 */
public class SynUploader extends Thread {


//	public boolean upload(JSONObject stateJson, OSSClient client,
//			HttpServletRequest request) {
////		Bucket bucket = BucketService.create(client,
////				OSSClientProperties.bucketName);
//		// get the key, which the upload file path
//		String key = stateJson.getString("url").replaceFirst("/", "");
//		try {
//			FileInputStream fileInputStream = new FileInputStream(new File(
//					SystemUtil.getProjectRootPath() + key));
//			PutObjectResult result = ObjectService.putObject(client,
//					OSSClientProperties.bucketName, key, fileInputStream);
//			logger.debug("upload file to aliyun OSS object server success. ETag: "
//					+ result.getETag());
//			return true;
//		} catch (FileNotFoundException e) {
//			logger.error("upload file to aliyun OSS object server occur FileNotFoundException.");
//		} catch (NumberFormatException e) {
//			logger.error("upload file to aliyun OSS object server occur NumberFormatException.");
//		} catch (IOException e) {
//			logger.error("upload file to aliyun OSS object server occur IOException.");
//		}
//		return false;
//	}

	public boolean upload(JSONObject stateJson, HttpServletRequest request) {
		String key = stateJson.getString("url").replaceFirst("/", "");
		try {
			Log.debug("upload--fileInputStream file path: "+SystemUtil.getProjectRootPath() + key);			
//			FileInputStream fileInputStream = new FileInputStream(new File(
//					SystemUtil.getProjectRootPath() + key));
//			ObjectService.putObject(key, fileInputStream);
			FileUploadUtil.upload(key, SystemUtil.getProjectRootPath() + key);
			
			return true;
		} catch (NumberFormatException e) {
			Log.error("upload file to fileupload server occur NumberFormatException.");
		}
		return false;
	}

	
	public static void main(String[] args) {
		String key = "12{userid}345";
		key = key.replaceAll("\\{userid\\}", "-");
		System.out.println(key);
	}
}
