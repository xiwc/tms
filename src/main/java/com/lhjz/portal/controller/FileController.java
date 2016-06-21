/**
 * 版权所有 (TMS)
 */
package com.lhjz.portal.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lhjz.portal.base.BaseController;
import com.lhjz.portal.model.RespBody;
import com.lhjz.portal.pojo.Enum.Action;
import com.lhjz.portal.pojo.Enum.Status;
import com.lhjz.portal.pojo.Enum.Target;
import com.lhjz.portal.pojo.FileForm;
import com.lhjz.portal.repository.FileRepository;
import com.lhjz.portal.util.FileUtil;
import com.lhjz.portal.util.ImageUtil;
import com.lhjz.portal.util.StringUtil;
import com.lhjz.portal.util.WebUtil;

/**
 * 
 * @author xi
 * 
 * @date 2015年3月28日 下午1:19:05
 * 
 */
@Controller
@RequestMapping("admin/file")
public class FileController extends BaseController {

	static Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	FileRepository fileRepository;

	@RequestMapping(value = "list", method = RequestMethod.POST)
	@ResponseBody
	public RespBody list(HttpServletRequest request,
			HttpServletResponse response, ModelMap model, Locale locale) {

		String storePath = env.getProperty("lhjz.upload.img.store.path");
		int sizeLarge = env.getProperty("lhjz.upload.img.scale.size.large",
				Integer.class);
		int sizeHuge = env.getProperty("lhjz.upload.img.scale.size.huge",
				Integer.class);
		int sizeOriginal = env.getProperty(
				"lhjz.upload.img.scale.size.original", Integer.class);

		// img relative path (eg:'upload/img/' & 640 & '/' )
		model.addAttribute("path", storePath + sizeOriginal + "/");
		model.addAttribute("pathLarge", storePath + sizeLarge + "/");
		model.addAttribute("pathHuge", storePath + sizeHuge + "/");
		// list all files
		model.addAttribute("imgs", fileRepository.findAll());

		return RespBody.succeed(model);
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public RespBody update(HttpServletRequest request,
			HttpServletResponse response, Model model, Locale locale,
			@Valid FileForm fileForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return RespBody.failed(bindingResult.getAllErrors().stream()
					.map(err -> err.getDefaultMessage())
					.collect(Collectors.joining("<br/>")));
		}

		com.lhjz.portal.entity.File file = fileRepository.findOne(fileForm
				.getId());
		if (file.getStatus() == Status.Bultin) {
			return RespBody.failed("内置文件，不能修改！");
		}

		String oldName = file.getName();

		file.setName(fileForm.getName() + FileUtil.getType(file.getName()));

		logWithProperties(Action.Update, Target.File, "name", file.getName(),
				oldName);

		return RespBody.succeed(fileRepository.save(file));
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public RespBody delete(HttpServletRequest request,
			HttpServletResponse response, Model model, Locale locale,
			@RequestParam(value = "id", required = true) Long id) {

		com.lhjz.portal.entity.File file = fileRepository.findOne(id);
		if (file.getStatus() == Status.Bultin) {
			return RespBody.failed("内置文件，不能删除！");
		}

		fileRepository.delete(id);

		log(Action.Delete, Target.File, id);

		return RespBody.succeed();
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public RespBody upload(HttpServletRequest request,
			HttpServletResponse response, Model model, Locale locale,
			@RequestParam("file") MultipartFile[] files) {

		logger.debug("upload file start...");

		String realPath = WebUtil.getRealPath(request);

		String storePath = env.getProperty("lhjz.upload.img.store.path");
		int sizeOriginal = env.getProperty(
				"lhjz.upload.img.scale.size.original", Integer.class);
		int sizeLarge = env.getProperty("lhjz.upload.img.scale.size.large",
				Integer.class);
		int sizeHuge = env.getProperty("lhjz.upload.img.scale.size.huge",
				Integer.class);

		try {
			// make upload dir if not exists
			FileUtils.forceMkdir(new File(realPath + storePath + sizeOriginal));
			FileUtils.forceMkdir(new File(realPath + storePath + sizeLarge));
			FileUtils.forceMkdir(new File(realPath + storePath + sizeHuge));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return RespBody.failed(e.getMessage());
		}

		List<com.lhjz.portal.entity.File> saveFiles = new ArrayList<com.lhjz.portal.entity.File>();

		RespBody respBody = RespBody.succeed();

		for (MultipartFile file : files) {

			String originalFileName = file.getOriginalFilename();

			if (!ImageUtil.isImage(originalFileName)) {
				logger.error("上传文件不是图片！ 文件名: {}", originalFileName);
				respBody.status(false).addMsg(
						String.format("上传文件不是图片！ 文件名: %s", originalFileName));
				continue;
			}

			String type = originalFileName.substring(originalFileName
					.lastIndexOf("."));

			String uuid = UUID.randomUUID().toString();

			String uuidName = StringUtil.replace("{?1}{?2}", uuid, type);

			// relative file path
			String path = storePath + sizeOriginal + "/" + uuidName;// 原始图片存放
			String pathLarge = storePath + sizeLarge + "/" + uuidName;// 缩放图片存放
			String pathHuge = storePath + sizeHuge + "/" + uuidName;// 缩放图片存放

			// absolute file path
			String filePath = realPath + path;

			try {

				// store into webapp dir
				file.transferTo(new File(filePath));

				// scale image size as thumbnail
				// 图片缩放处理.120*120
				ImageUtil.scale2(filePath, realPath + pathLarge, sizeLarge,
						sizeLarge, true);
				// 图片缩放处理.640*640
				ImageUtil.scale2(filePath, realPath + pathHuge, sizeHuge,
						sizeHuge, true);

				// 保存记录到数据库
				com.lhjz.portal.entity.File file2 = new com.lhjz.portal.entity.File();
				file2.setCreateDate(new Date());
				file2.setName(originalFileName);
				file2.setUsername(WebUtil.getUsername());
				file2.setUuidName(uuidName);
				file2.setPath(storePath + sizeOriginal + "/");
				saveFiles.add(fileRepository.save(file2));

				log(Action.Upload, Target.File, file2);

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
				return RespBody.failed(e.getMessage());
			}
		}

		// back relative file path
		return respBody.data(saveFiles);
	}

	@RequestMapping(value = "base64", method = RequestMethod.POST)
	@ResponseBody
	public RespBody base64(HttpServletRequest request,
			HttpServletResponse response, Model model, Locale locale,
			@RequestParam("dataURL") String dataURL,
			@RequestParam("type") String type) {

		logger.debug("upload base64 start...");

		try {

			String realPath = WebUtil.getRealPath(request);

			String storePath = env.getProperty("lhjz.upload.img.store.path");
			int sizeOriginal = env.getProperty(
					"lhjz.upload.img.scale.size.original", Integer.class);
			int sizeLarge = env.getProperty("lhjz.upload.img.scale.size.large",
					Integer.class);
			int sizeHuge = env.getProperty("lhjz.upload.img.scale.size.huge",
					Integer.class);

			// make upload dir if not exists
			FileUtils.forceMkdir(new File(realPath + storePath + sizeOriginal));
			FileUtils.forceMkdir(new File(realPath + storePath + sizeLarge));
			FileUtils.forceMkdir(new File(realPath + storePath + sizeHuge));

			String uuid = UUID.randomUUID().toString();

			// data:image/gif;base64,base64编码的gif图片数据
			// data:image/png;base64,base64编码的png图片数据
			// data:image/jpeg;base64,base64编码的jpeg图片数据
			// data:image/x-icon;base64,base64编码的icon图片数据

			String suffix = type.contains("png") ? ".png" : ".jpg";

			String uuidName = StringUtil.replace("{?1}{?2}", uuid, suffix);

			// relative file path
			String path = storePath + sizeOriginal + "/" + uuidName;// 原始图片存放
			String pathLarge = storePath + sizeLarge + "/" + uuidName;// 缩放图片存放
			String pathHuge = storePath + sizeHuge + "/" + uuidName;// 缩放图片存放

			// absolute file path
			String filePath = realPath + path;

			// 原始图保存
			ImageUtil.GenerateImage(dataURL, filePath);
			// 缩放图
			// scale image size as thumbnail
			// 图片缩放处理.120*120
			ImageUtil.scale2(filePath, realPath + pathLarge, sizeLarge,
					sizeLarge, true);
			// 图片缩放处理.640*640
			ImageUtil.scale2(filePath, realPath + pathHuge, sizeHuge, sizeHuge,
					true);

			// 保存记录到数据库
			com.lhjz.portal.entity.File file2 = new com.lhjz.portal.entity.File();
			file2.setCreateDate(new Date());
			file2.setName(uuidName);
			file2.setUsername(WebUtil.getUsername());
			file2.setUuidName(uuidName);
			file2.setPath(storePath + sizeOriginal + "/");
			com.lhjz.portal.entity.File file = fileRepository.save(file2);

			log(Action.Upload, Target.File, file2);

			return RespBody.succeed(file);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return RespBody.failed(e.getMessage());
		}

	}
}
