package org.jahia.modules.autofileuploader.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.jcr.RepositoryException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.JahiaAfterInitializationService;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoFileUploadService implements JahiaAfterInitializationService {

	private static Logger logger = LoggerFactory.getLogger(AutoFileUploadService.class);

	private String importdiskpath;
	private String webappRootPath;
	private int scanInterval;
	private List<String> ignoreFiles;

	private Timer watchdog;

	public void destroy() throws Exception {
		if (watchdog != null) {
			watchdog.cancel();
		}
	}

	public void checkImport(final String path) throws RepositoryException {
		synchronized (this) {
			File lookupFolder = new File(path);
			final List<File> files = new LinkedList<>(FileUtils.listFiles(lookupFolder,
					new NotFileFilter(new SuffixFileFilter(new String[] { "readMe" })), TrueFileFilter.INSTANCE));
			if (!files.isEmpty()) {
				JahiaUser user = JahiaUserManagerService.getInstance().lookupRootUser().getJahiaUser();

				JCRTemplate.getInstance().doExecuteWithSystemSessionAsUser(user, "default", null,
						new JCRCallback<Object>() {

							@Override
							public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
								for (File file : files) {
									logger.info("File:" + file.getPath());

									String uploadPath = file.getPath().substring(path.length());
									String fileSeperator = (String) System.getProperty("file.separator");
									logger.info("File" + file.getName() + " will be uploaded to " + fileSeperator
											+ "sites" + uploadPath);
									StringTokenizer tokenizer = new StringTokenizer(uploadPath, fileSeperator);
									// create folders
									JCRNodeWrapper folderNode = session.getNode("/sites");
									int counter = 0;
									String nodeName = null;
									boolean toSave = false;
									boolean nextFile = false;
									while (tokenizer.hasMoreElements()) {
										nodeName = tokenizer.nextToken();
										if (folderNode.hasNode(nodeName)) {
											folderNode = folderNode.getNode(nodeName);
											counter++;
										} else {
											if (counter < 2) {
												logger.error("Site and files folder path wrong for file "
														+ file.getPath() + " it is not imported!!!!");
												nextFile = true;
												file.delete();
												break;

											} else {
												// create folder
												if (tokenizer.hasMoreTokens()) { // check if last
													folderNode = folderNode.addNode(nodeName, "jnt:folder");
													counter++;
													toSave = true;
												}
											}
										}
									}
									if (toSave) {
										session.save(); // create all folders
									}
									if (!nextFile) {
										// upload file in folderNode
										String mime = JCRContentUtils.getMimeType(nodeName);
										try {
											InputStream is = new FileInputStream(file);
											JCRNodeWrapper uploadedfile = folderNode.uploadFile(nodeName, is, mime);
											uploadedfile.saveSession();
											is.close();
										} catch (FileNotFoundException ex) {
											logger.error("FileNotFound " + uploadPath, ex);
										} catch (IOException ex) {
											logger.error("I/O Error on " + uploadPath, ex);
										}

									}

								}
								return null;
							}
						});

			}
		}
	}
	
	private void createDirIfNotExists(File directory) {
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	private void checkSiteFolders(String path) {
		
		File lookupFolder = new File(path);
		List<String> siteNames = ServicesRegistry.getInstance().getJahiaSitesService().getSitesNames();
        createDirIfNotExists(lookupFolder);
        for (String siteName: siteNames) {
        	createDirIfNotExists(new File(path + "/" + siteName));
        	createDirIfNotExists(new File(path + "/" + siteName + "/files"));
        }
	}

	public void initAfterAllServicesAreStarted() throws JahiaInitializationException {
		checkSiteFolders(webappRootPath + importdiskpath);
		// start watchdog for monitoring
		watchdog = new Timer(true);
		watchdog.schedule(new TimerTask() {
			@Override
			public void run() {
				perform();
			}
		}, 0, scanInterval);
	}

	private void perform() {
		if (logger.isTraceEnabled()) {
			logger.trace("Perform import check on " + webappRootPath + importdiskpath);
		}
		try {
			checkImport(webappRootPath + importdiskpath);
		} catch (RepositoryException ex) {
			logger.error("A file couldn't import!", ex);
		}
	}

	public String getImportdiskpath() {
		return importdiskpath;
	}

	public void setImportdiskpath(String importdiskpath) {
		this.importdiskpath = importdiskpath;
	}

	public String getWebappRootPath() {
		return webappRootPath;
	}

	public void setWebappRootPath(String webappRootPath) {
		this.webappRootPath = webappRootPath;
	}

	public int getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}

	public List<String> getIgnoreFiles() {
		return ignoreFiles;
	}

	public void setIgnoreFiles(List<String> ignoreFiles) {
		this.ignoreFiles = ignoreFiles;
	}

}
