package com.baidu.qikemi.packages.baidu.ueditor.upload;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.xnx3.Log;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 异步上传文件到阿里云OSS
 * 
 * @create date : 2015年08月01日 上午22:13:00
 * @Author XieXianbin<a.b@hotmail.com>
 * @Source Repositories Address:
 *         <https://github.com/qikemi/UEditor-for-aliyun-OSS>
 */
public class AsynUploaderThreader extends Thread {

	private JSONObject stateJson = null;
//	private OSSClient client = null;
	private HttpServletRequest request = null;

	public AsynUploaderThreader() {
	}

//	public void init(JSONObject stateJson, OSSClient client,
//			HttpServletRequest request) {
//		this.stateJson = stateJson;
//		this.client = client;
//		this.request = request;
//	}
	public void init(JSONObject stateJson, HttpServletRequest request) {
		this.stateJson = stateJson;
		this.request = request;
	}

	@Override
	public void run() {
		SynUploader synUploader = new SynUploader();
//		synUploader.upload(stateJson, client, request);
		UploadFileVO vo = synUploader.upload(stateJson, request);
		System.out.println(vo);
		System.out.println("com.baidu.qikemi.packages.baidu.ueditor.uploa.asyn");
		Log.debug("asynchronous upload image to aliyun oss success.");
	}

}
