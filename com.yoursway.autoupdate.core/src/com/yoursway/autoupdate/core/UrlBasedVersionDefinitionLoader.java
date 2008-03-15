package com.yoursway.autoupdate.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class UrlBasedVersionDefinitionLoader implements IVersionDefinitionLoader {

	static class VersionDescriptionFile {
		private static final String TOP_LEVEL_NODE = "version";
		public String nextVersion;
		public String displayName;
		public boolean isLatest;
		public String changesDescription;
		public List<ApplicationFile> files;

		public VersionDescriptionFile() {
			files = new ArrayList<ApplicationFile>();
			isLatest = false;
		}

		public static VersionDescriptionFile createFromXML(
				String repositoryURL, InputStream xml) throws IOException {
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
					file.nextVersion = node.getTextContent();
				} else if (nodeName.equals("display-name")) {
					file.displayName = node.getTextContent();
				} else if (nodeName.equals("latest")) {
					file.isLatest = (node.getTextContent().equals("true"));
				} else if (nodeName.equals("files")) {
					NodeList filesNodes = node.getChildNodes();
					int filesCount = filesNodes.getLength();
					for (int j = 0; j < filesCount; j++) {
						Node f = filesNodes.item(j);
						if (f.getNodeName().equals("file")) {
							NamedNodeMap attrs = f.getAttributes();
							String serverPath = attrs.getNamedItem("path")
									.getNodeName();
							String md5 = attrs.getNamedItem("md5")
									.getNodeValue();
							String installationPath = attrs.getNamedItem(
									"installPath").getNodeValue();
							if (new Path(serverPath).getDevice() == null)
								serverPath = repositoryURL + serverPath;
							URL remoteUrl = new URL(serverPath);
							file.files.add(new ApplicationFile(md5,
									installationPath, remoteUrl));
						}
					}
				} else if (nodeName.equals("changes")) {
					file.changesDescription = node.getTextContent();
				}
			}
			return file;
		}

	}

	private final String repositoryURL;

	public UrlBasedVersionDefinitionLoader(String repositoryURL) {
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
	public VersionDefinition latestUpdateFor(Version currentVersion)
			throws UpdateLoopException {
		Set<Version> visited = new HashSet<Version>();
		visited.add(currentVersion);
		VersionDefinition currentUpdate = null;
		while (true) {
			VersionDefinition lastUpdate = currentUpdate;
			currentUpdate = nextVersionFor(currentVersion);
			if (currentUpdate == null)
				return lastUpdate;
			if (visited.contains(currentUpdate.version())) {
				throw new UpdateLoopException();
			}
			currentVersion = currentUpdate.version();
			visited.add(currentVersion);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoursway.autoupdate.core.IApplicationUpdater#availableVersions()
	 */
	public Version[] availableVersions(
			Version currentVersion) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoursway.autoupdate.core.IApplicationUpdater#updateToVersion(com.yoursway.autoupdate.core.ApplicationVersion,
	 *      com.yoursway.autoupdate.core.ApplicationVersion)
	 */
	public VersionDefinition updateToVersion(Version current,
			Version target) {
		return null;
	}

	public boolean newerVersionExists(Version currentVersion) {
		return nextVersionFor(currentVersion) != null;
	}

	public VersionDefinition nextVersionFor(Version currentVersion) {
		try {
			VersionDescriptionFile description = getDescription(currentVersion);
			Version freshVersion = new Version(
					description.nextVersion);
			if (description.isLatest)
				return null;
			description = getDescription(freshVersion);
			List<ApplicationFile> appFiles = description.files;
			ApplicationFile[] files = appFiles
					.toArray(new ApplicationFile[appFiles.size()]);
			return new VersionDefinition(freshVersion, description.displayName,
					description.changesDescription, files);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private VersionDescriptionFile getDescription(
			Version currentVersion) throws MalformedURLException,
			IOException {
		URL updateFileURL = new URL(repositoryURL
				+ currentVersion.versionString() + ".xml");
		InputStream stream = contentsFor(updateFileURL);
		VersionDescriptionFile description = VersionDescriptionFile
				.createFromXML(repositoryURL, stream);
		return description;
	}

}
