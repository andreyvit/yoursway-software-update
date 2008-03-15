package com.yoursway.autoupdate.core.versiondef;

import static com.yoursway.autoupdate.core.path.Pathes.relativePath;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class UrlBasedVersionDefinitionLoader implements
		IVersionDefinitionLoader {

	static class VersionDescriptionFile {
		private static final String TOP_LEVEL_NODE = "version";
		public String nextVersion;
		public String displayName;
		public boolean isLatest;
		public String changesDescription;
		public List<RemoteFile> files;

		public VersionDescriptionFile() {
			files = new ArrayList<RemoteFile>();
			isLatest = false;
		}

		public static VersionDescriptionFile createFromXML(URL repositoryURL,
				InputStream xml) throws IOException {
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
							URL remoteUrl = new URL(repositoryURL, serverPath);
							file.files.add(new RemoteFile(
									relativePath(installationPath),
									md5, remoteUrl));
						}
					}
				} else if (nodeName.equals("changes")) {
					file.changesDescription = node.getTextContent();
				}
			}
			return file;
		}

	}

	private final URL repositoryURL;

	public UrlBasedVersionDefinitionLoader(URL repositoryURL) {
		Assert.isNotNull(repositoryURL);
		this.repositoryURL = repositoryURL;

	}

	protected InputStream contentsFor(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		return connection.getInputStream();
	}

	public VersionDefinition loadDefinition(Version currentVersion) throws VersionDefinitionNotAvailable {
		try {
			VersionDescriptionFile description = getDescription(currentVersion);
			List<RemoteFile> appFiles = description.files;
			RemoteFile[] files = appFiles
					.toArray(new RemoteFile[appFiles.size()]);
			return new VersionDefinition(currentVersion,
					description.displayName, Version
							.fromString(description.nextVersion),
					description.changesDescription, files);
		} catch (IOException e) {
			throw new VersionDefinitionNotAvailable(e);
		}
	}

	private VersionDescriptionFile getDescription(Version currentVersion)
			throws IOException {
		URL updateFileURL;
		try {
			updateFileURL = new URL(repositoryURL, URLEncoder.encode(
					currentVersion.versionString() + ".xml", "UTF-8"));
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
		InputStream stream = contentsFor(updateFileURL);
		
		return VersionDescriptionFile.createFromXML(repositoryURL, stream);
	}

}
