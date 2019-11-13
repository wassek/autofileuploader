package org.jahia.modules.autofileuploader.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.jahia.api.Constants;
import org.jahia.modules.autofileuploader.services.AutoFileUploadService;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.core.collection.LocalParameterMap;

public class AutoFileUploaderSettingsFlow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(AutoFileUploaderSettingsFlow.class);

	public static Map<String, String> getAndSave(LocalParameterMap requestParameters) {
		final HashMap<String, String> result = new HashMap<String, String>();
		try {
			AutoFileUploadService service = (AutoFileUploadService) SpringContextSingleton
					.getBeanInModulesContext("AutoFileUploadService");
			if (requestParameters != null && requestParameters.get("save") != null) {
				JCRTemplate.getInstance().doExecuteWithSystemSessionAsUser(
						ServicesRegistry.getInstance().getJahiaUserManagerService().lookupRootUser().getJahiaUser(),
						Constants.EDIT_WORKSPACE, Locale.ENGLISH, new JCRCallback<Object>() {
							@Override
							public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
								JCRNodeWrapper settingsNode = session.getNode("/settings/autofileuploadersettings");
								settingsNode.setProperty("intervall", requestParameters.get("intervall"));
								settingsNode.setProperty("autopublish", (requestParameters.get("autopublish") != null));
								settingsNode.setProperty("serverPath", requestParameters.get("serverPath"));
								settingsNode.saveSession();
								result.putAll(convertNodeToMap(settingsNode));
								// TODO save & reinitialize the service
								return null;
							}
						});
			} else {
				JCRTemplate.getInstance().doExecuteWithSystemSessionAsUser(
						ServicesRegistry.getInstance().getJahiaUserManagerService().lookupRootUser().getJahiaUser(),
						Constants.EDIT_WORKSPACE, Locale.ENGLISH, new JCRCallback<Object>() {
							@Override
							public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
								JCRNodeWrapper settingsNode = null;
								if (!session.nodeExists("/settings/autofileuploadersettings")) {
									// create default
									settingsNode = session.getNode("/settings").addNode("autofileuploadersettings",
											"jnt:autofileuploader");
									settingsNode.setProperty("intervall", service.getScanInterval());
									settingsNode.setProperty("autopublish", service.isAutoPublish());
									settingsNode.setProperty("serverPath", service.getImportdiskpath());
									settingsNode.saveSession();

								} else {
									settingsNode = session.getNode("/settings/autofileuploadersettings");
								}
								result.putAll(convertNodeToMap(settingsNode));
								return null;
							}
						});
			}

		} catch (RepositoryException ex) {
			logger.error("Initialization of Auto File Upload Settings failed!", ex);
		}

		return result;
	}

	private static HashMap<String, String> convertNodeToMap(JCRNodeWrapper node) throws RepositoryException {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("intervall", node.getProperty("intervall").getString());
		result.put("serverPath", node.getProperty("serverPath").getString());
		result.put("autopublish", node.getProperty("autopublish").getString());

		return result;
	}
}
