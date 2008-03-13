package com.yoursway.autoupdate.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Path;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HTTPBasedApplicationUpdater implements IApplicationUpdater {

	static class VersionDescriptionFile {
		private static final String TOP_LEVEL_NODE = "version";
		public String nextVersion;
		public String displayName;
		public boolean isLatest;
		public String changesDescription;
		public List<ApplicationFile> files;

		public static VersionDescriptionFile createFromXML(String repositoryURL, InputStream xml) throws IOException  {
			VersionDescriptionFile file = new VersionDescriptionFile();

			InputStream stream = new BufferedInputStream(xml);
			Element config = null;
			try {
				DocumentBuilder parser = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				parser.setErrorHandler(new DefaultHandler());
				config = parser.parse(new InputSource(stream))
						.getDocumentElement();
			} catch (SAXException e) {
				throw new RuntimeException(e);
			} catch (ParserConfigurationException e) {
				stream.close();
				throw new RuntimeException(e);
			} finally {
				stream.close();
			}

			if (!config.getNodeName().equalsIgnoreCase(TOP_LEVEL_NODE)) {
				throw new IOException();
			}

			NodeList list = config.getChildNodes();
			int length = list.getLength();
			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				String nodeName = node.getNodeName();
				if (nodeName.equals("next-version")) {
					file.nextVersion = node.getNodeValue();
				} else if (nodeName.equals("display-name")) {
					file.displayName = node.getNodeValue();
				} else if (nodeName.equals("latest")) {
					file.isLatest = (node.getNodeValue().equals("true"));
				} else if (nodeName.equals("files")) {
					file.files = new ArrayList<ApplicationFile>();
					NodeList filesNodes = node.getChildNodes();
					int filesCount = filesNodes.getLength();
					for (int j = 0; j < filesCount; j++) {
						Node f = filesNodes.item(j);						
						if (f.getNodeName().equals("file")) {
							NamedNodeMap attrs = f.getAttributes();
							String serverPath = attrs.getNamedItem("path").getNodeValue();
							String md5 = attrs.getNamedItem("md5").getNodeValue();
							String installationPath = attrs.getNamedItem("installPath").getNodeValue();
							if (new Path(serverPath).getDevice() == null)
								serverPath = repositoryURL + serverPath;
							URL remoteUrl = new URL(serverPath);
							file.files.add(new ApplicationFile(md5, installationPath, remoteUrl));
						}
					}
				} else if (nodeName.equals("changes")) {
					file.changesDescription = node.getNodeValue();
				}
			}
			return file;
		}

	}

	private final String repositoryURL;

	public HTTPBasedApplicationUpdater(String repositoryURL) {
		if (!repositoryURL.endsWith("/"))
			repositoryURL += "/";
		this.repositoryURL = repositoryURL;
		
	}

	protected InputStream contentsFor(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		return connection.getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoursway.autoupdate.core.IApplicationUpdater#latestUpdateFor(com.yoursway.autoupdate.core.ApplicationVersion)
	 */
	public ApplicationUpdate latestUpdateFor(ApplicationVersion currentVersion) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoursway.autoupdate.core.IApplicationUpdater#availableVersions()
	 */
	public ApplicationVersion[] availableVersions(
			ApplicationVersion currentVersion) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoursway.autoupdate.core.IApplicationUpdater#updateToVersion(com.yoursway.autoupdate.core.ApplicationVersion,
	 *      com.yoursway.autoupdate.core.ApplicationVersion)
	 */
	public ApplicationUpdate updateToVersion(ApplicationVersion current,
			ApplicationVersion target) {
		return null;
	}

	public boolean freshUpdatesAvailable(ApplicationVersion currentVersion) {
		// TODO Auto-generated method stub
		return false;
	}

	public ApplicationUpdate nextUpdateFor(ApplicationVersion currentVersion) {
		try {
			VersionDescriptionFile description = getDescription(currentVersion);
			if (description.isLatest)
				return null;
			List<ApplicationFile> appFiles = description.files;
			ApplicationFile[] files = appFiles.toArray(new ApplicationFile[appFiles.size()]);
			ApplicationVersion version = new ApplicationVersion(description.nextVersion, description.displayName);
			return new ApplicationUpdate(version, description.changesDescription, files);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private VersionDescriptionFile getDescription(
			ApplicationVersion currentVersion) throws MalformedURLException,
			IOException {
		URL updateFileURL = new URL(repositoryURL + currentVersion.versionString()+ ".xml");
		InputStream stream = contentsFor(updateFileURL);
		VersionDescriptionFile description = VersionDescriptionFile.createFromXML(repositoryURL, stream);
		return description;
	}

}
