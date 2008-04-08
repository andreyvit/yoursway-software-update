/**
 * 
 */
package com.yoursway.autoupdate.core.versions.definitions;

import static com.yoursway.utils.relativepath.Pathes.relativePath;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.yoursway.autoupdate.core.actions.RemoteSource;
import com.yoursway.autoupdate.core.versions.Version;
import com.yoursway.utils.filespec.ConcreteFilesSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class VersionDefinitionParser {
    
    static class VersionInfo {
        public String nextVersion;
        public String displayName;
        public boolean isLatest = false;
        public String changesDescription;
        public List<RemoteFile> files = new ArrayList<RemoteFile>();
        public List<RelativePath> updaterFiles = new ArrayList<RelativePath>();
        public RelativePath updaterMainFile;
        public Date date;
        
        public VersionDefinition build(Version currentVersion) throws InvalidVersionDefinitionException {
            if (!isLatest && nextVersion == null)
                throw new InvalidVersionDefinitionException("Missing nextVersion");
            if (!isLatest && changesDescription == null)
                throw new InvalidVersionDefinitionException("Missing changesDescription");
            if (date == null)
                throw new InvalidVersionDefinitionException("Missing date");
            if (updaterMainFile == null)
                throw new InvalidVersionDefinitionException("Missing updaterMainFile");
            return new VersionDefinition(currentVersion, displayName, Version.fromString(nextVersion),
                    changesDescription, files, date, new UpdaterInfo(new ConcreteFilesSpec(updaterFiles),
                            updaterMainFile));
        }
    }
    
    static class FileInfo {
        
        public String md5;
        
        public String installationPath;
        
        public UpdaterAffiliation updaterAffiliation = UpdaterAffiliation.NONE;
        
        public RemoteSource source;
        
        private RemoteFile build() {
            return new RemoteFile(relativePath(installationPath), md5, source);
        }
        
        public void addTo(VersionInfo info) {
            RemoteFile rf = build();
            info.files.add(rf);
            RelativePath path = rf.relativePath();
            if (updaterAffiliation.isUpdater())
                info.updaterFiles.add(path);
            if (updaterAffiliation == UpdaterAffiliation.UPDATER_MAIN)
                info.updaterMainFile = path;
        }
        
    }
    
    private static final String TOP_LEVEL_NODE = "version";
    
    private VersionInfo createFromXML(URL repositoryURL, InputStream xml) throws IOException,
            InvalidVersionDefinitionException {
        VersionInfo file = new VersionInfo();
        
        InputStream stream = new BufferedInputStream(xml);
        Element config = null;
        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            parser.setErrorHandler(new DefaultHandler());
            config = parser.parse(new InputSource(stream)).getDocumentElement();
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
            } else if (nodeName.equals("date")) {
                try {
                    file.date = parseDate(node.getTextContent());
                } catch (ParseException e) {
                    throw new InvalidVersionDefinitionException(e);
                }
            } else if (nodeName.equals("latest")) {
                file.isLatest = (node.getTextContent().equals("true"));
            } else if (nodeName.equals("files")) {
                NodeList filesNodes = node.getChildNodes();
                int filesCount = filesNodes.getLength();
                for (int j = 0; j < filesCount; j++) {
                    Node f = filesNodes.item(j);
                    if (f.getNodeName().equals("file")) {
                        NamedNodeMap attrs = f.getAttributes();
                        FileInfo fi = new FileInfo();
                        fi.md5 = attrs.getNamedItem("md5").getNodeValue();
                        String role = get(attrs, "role");
                        if (role != null)
                            parseRole(role, fi);
                        fi.installationPath = attrs.getNamedItem("installPath").getNodeValue();
                        String serverPath = attrs.getNamedItem("path").getNodeName();
                        URL remoteUrl = new URL(repositoryURL, serverPath);
                        fi.source = new RemoteSource(remoteUrl);
                        fi.addTo(file);
                    }
                }
            } else if (nodeName.equals("changes")) {
                file.changesDescription = node.getTextContent();
            }
        }
        return file;
    }

    private String get(NamedNodeMap attrs, String n) {
        Node item = attrs.getNamedItem(n);
        if (item == null)
            return null;
        return item.getNodeValue();
    }
    
    private static void parseRole(String role, FileInfo fi) {
        UpdaterAffiliation updaterAffiliation = UpdaterAffiliation.NONE;
        for (String item : role.split(" "))
            if ("updater".equals(item))
                updaterAffiliation = UpdaterAffiliation.UPDATER;
            else if ("updater-main".equals(item))
                updaterAffiliation = UpdaterAffiliation.UPDATER_MAIN;
        fi.updaterAffiliation = updaterAffiliation;
    }
    
    /**
     * Public for usage in tests.
     */
    public static Date parseDate(String textContent) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm Z").parse(textContent);
    }
    
    public VersionDefinition parse(URL repositoryURL, Version currentVersion, InputStream stream)
            throws IOException, InvalidVersionDefinitionException {
        VersionInfo description = createFromXML(repositoryURL, stream);
        return description.build(currentVersion);
    }
    
}
