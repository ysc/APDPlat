/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.platform.spring;

import org.apdplat.module.system.service.PropertyHolder;
import org.apdplat.platform.log.APDPlatLogger;
import org.apdplat.platform.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.util.ResourceUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.util.xml.SimpleSaxErrorHandler;

import java.lang.instrument.ClassFileTransformer;
import java.net.URL;
import java.security.ProtectionDomain;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.spi.ClassTransformer;
import org.apdplat.platform.log.APDPlatLoggerFactory;

import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
/**
 * Internal helper class for reading <code>persistence.xml</code> files.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 2.0
 */
class APDPlatPersistenceUnitReader {

	private static final String PERSISTENCE_VERSION = "version";

	private static final String PERSISTENCE_UNIT = "persistence-unit";

	private static final String UNIT_NAME = "name";

	private static final String MAPPING_FILE_NAME = "mapping-file";

	private static final String JAR_FILE_URL = "jar-file";

	private static final String MANAGED_CLASS_NAME = "class";

	private static final String PROPERTIES = "properties";

	private static final String PROVIDER = "provider";

	private static final String TRANSACTION_TYPE = "transaction-type";

	private static final String JTA_DATA_SOURCE = "jta-data-source";

	private static final String NON_JTA_DATA_SOURCE = "non-jta-data-source";

	private static final String EXCLUDE_UNLISTED_CLASSES = "exclude-unlisted-classes";

	private static final String SHARED_CACHE_MODE = "shared-cache-mode";

	private static final String VALIDATION_MODE = "validation-mode";

	private static final String META_INF = "META-INF";


        protected final APDPlatLogger logger = APDPlatLoggerFactory.getAPDPlatLogger(getClass());

	private final ResourcePatternResolver resourcePatternResolver;

	private final DataSourceLookup dataSourceLookup;


	/**
	 * Create a new APDPlatPersistenceUnitReader.
	 * @param resourcePatternResolver the ResourcePatternResolver to use for loading resources
	 * @param dataSourceLookup the DataSourceLookup to resolve DataSource names in
	 * <code>persistence.xml</code> files against
	 */
	public APDPlatPersistenceUnitReader(ResourcePatternResolver resourcePatternResolver, DataSourceLookup dataSourceLookup) {
		Assert.notNull(resourcePatternResolver, "ResourceLoader must not be null");
		Assert.notNull(dataSourceLookup, "DataSourceLookup must not be null");
		this.resourcePatternResolver = resourcePatternResolver;
		this.dataSourceLookup = dataSourceLookup;
	}


	/**
	 * Parse and build all persistence unit infos defined in the specified XML file(s).
	 * @param persistenceXmlLocation the resource location (can be a pattern)
	 * @return the resulting PersistenceUnitInfo instances
	 */
	public SpringPersistenceUnitInfo[] readPersistenceUnitInfos(String persistenceXmlLocation) {
		return readPersistenceUnitInfos(new String[] {persistenceXmlLocation});
	}

	/**
	 * Parse and build all persistence unit infos defined in the given XML files.
	 * @param persistenceXmlLocations the resource locations (can be patterns)
	 * @return the resulting PersistenceUnitInfo instances
	 */
	public SpringPersistenceUnitInfo[] readPersistenceUnitInfos(String[] persistenceXmlLocations) {
		ErrorHandler handler = new SimpleSaxErrorHandler(LogFactory.getLog(getClass()));
		List<SpringPersistenceUnitInfo> infos = new LinkedList<SpringPersistenceUnitInfo>();
		String resourceLocation = null;
		try {
			for (String location : persistenceXmlLocations) {
				Resource[] resources = this.resourcePatternResolver.getResources(location);
				for (Resource resource : resources) {
					resourceLocation = resource.toString();
					InputStream stream = resource.getInputStream();
					try {
						Document document = buildDocument(handler, stream);
						parseDocument(resource, document, infos);
					}
					finally {
						stream.close();
					}
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Cannot parse persistence unit from " + resourceLocation, ex);
		}
		catch (SAXException ex) {
			throw new IllegalArgumentException("Invalid XML in persistence unit from " + resourceLocation, ex);
		}
		catch (ParserConfigurationException ex) {
			throw new IllegalArgumentException("Internal error parsing persistence unit from " + resourceLocation);
		}

		return infos.toArray(new SpringPersistenceUnitInfo[infos.size()]);
	}


	/**
	 * Validate the given stream and return a valid DOM document for parsing.
	 */
	protected Document buildDocument(ErrorHandler handler, InputStream stream)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder parser = dbf.newDocumentBuilder();
		parser.setErrorHandler(handler);
		return parser.parse(stream);
	}


	/**
	 * Parse the validated document and add entries to the given unit info list.
	 */
	protected List<SpringPersistenceUnitInfo> parseDocument(
			Resource resource, Document document, List<SpringPersistenceUnitInfo> infos) throws IOException {

		Element persistence = document.getDocumentElement();
		String version = persistence.getAttribute(PERSISTENCE_VERSION);
		URL unitRootURL = determinePersistenceUnitRootUrl(resource);
		List<Element> units = DomUtils.getChildElementsByTagName(persistence, PERSISTENCE_UNIT);
		for (Element unit : units) {
			SpringPersistenceUnitInfo info = parsePersistenceUnitInfo(unit, version);
			info.setPersistenceUnitRootUrl(unitRootURL);
			infos.add(info);
		}

		return infos;
	}

	/**
	 * Determine the persistence unit root URL based on the given resource
	 * (which points to the <code>persistence.xml</code> file we're reading).
	 * @param resource the resource to check
	 * @return the corresponding persistence unit root URL
	 * @throws IOException if the checking failed
	 */
	protected URL determinePersistenceUnitRootUrl(Resource resource) throws IOException {
		URL originalURL = resource.getURL();
		String urlToString = originalURL.toExternalForm();

		// If we get an archive, simply return the jar URL (section 6.2 from the JPA spec)
		if (ResourceUtils.isJarURL(originalURL)) {
			return ResourceUtils.extractJarFileURL(originalURL);
		}

		else {
			// check META-INF folder
			if (!urlToString.contains(META_INF)) {
				if (logger.isInfoEnabled()) {
					logger.info(resource.getFilename() +
							" should be located inside META-INF directory; cannot determine persistence unit root URL for " +
							resource);
				}
				return null;
			}
			if (urlToString.lastIndexOf(META_INF) == urlToString.lastIndexOf('/') - (1 + META_INF.length())) {
				if (logger.isInfoEnabled()) {
					logger.info(resource.getFilename() +
							" is not located in the root of META-INF directory; cannot determine persistence unit root URL for " +
							resource);
				}
				return null;
			}

			String persistenceUnitRoot = urlToString.substring(0, urlToString.lastIndexOf(META_INF));
			return new URL(persistenceUnitRoot);
		}
	}

	/**
	 * Parse the unit info DOM element.
	 */
	protected SpringPersistenceUnitInfo parsePersistenceUnitInfo(Element persistenceUnit, String version) throws IOException { // JC: Changed
		SpringPersistenceUnitInfo unitInfo = new SpringPersistenceUnitInfo();

		// set JPA version (1.0 or 2.0)
		unitInfo.setPersistenceXMLSchemaVersion(version);

		// set unit name
                logger.info("开始执行apdplat对spring jpa的定制修改1(1. Start to execute custom modifications  of APDPlat for Spring JPA )");
                String unitName=persistenceUnit.getAttribute(UNIT_NAME).trim();
                logger.info("占位符的内容为(Content of placeholder is): "+unitName);
                //去掉${和}，从配置文件读取真正内容
                unitName=PropertyHolder.getProperty(unitName.substring(2,unitName.length()-1));
                logger.info("占位符对应的配置文件的内容为(Content of config file related to placeholder is): "+unitName);
		unitInfo.setPersistenceUnitName(unitName);

		// set transaction type
		String txType = persistenceUnit.getAttribute(TRANSACTION_TYPE).trim();
		if (StringUtils.hasText(txType)) {
			unitInfo.setTransactionType(PersistenceUnitTransactionType.valueOf(txType));
		}

		// data-source
		String jtaDataSource = DomUtils.getChildElementValueByTagName(persistenceUnit, JTA_DATA_SOURCE);
		if (StringUtils.hasText(jtaDataSource)) {
			unitInfo.setJtaDataSource(this.dataSourceLookup.getDataSource(jtaDataSource.trim()));
		}

		String nonJtaDataSource = DomUtils.getChildElementValueByTagName(persistenceUnit, NON_JTA_DATA_SOURCE);
		if (StringUtils.hasText(nonJtaDataSource)) {
			unitInfo.setNonJtaDataSource(this.dataSourceLookup.getDataSource(nonJtaDataSource.trim()));
		}

		// provider
		String provider = DomUtils.getChildElementValueByTagName(persistenceUnit, PROVIDER);
		if (StringUtils.hasText(provider)) {
			unitInfo.setPersistenceProviderClassName(provider.trim());
		}

		// exclude unlisted classes
		Element excludeUnlistedClasses = DomUtils.getChildElementByTagName(persistenceUnit, EXCLUDE_UNLISTED_CLASSES);
		if (excludeUnlistedClasses != null) {
			unitInfo.setExcludeUnlistedClasses(true);
		}

		// set JPA 2.0 shared cache mode
		String cacheMode = DomUtils.getChildElementValueByTagName(persistenceUnit, SHARED_CACHE_MODE);
		if (StringUtils.hasText(cacheMode)) {
			unitInfo.setSharedCacheModeName(cacheMode);
		}

		// set JPA 2.0 validation mode
		String validationMode = DomUtils.getChildElementValueByTagName(persistenceUnit, VALIDATION_MODE);
		if (StringUtils.hasText(validationMode)) {
			unitInfo.setValidationModeName(validationMode);
		}

		parseMappingFiles(persistenceUnit, unitInfo);
		parseJarFiles(persistenceUnit, unitInfo);
		parseClass(persistenceUnit, unitInfo);
		parseProperty(persistenceUnit, unitInfo);

		return unitInfo;
	}

	/**
	 * Parse the <code>property</code> XML elements.
	 */
	@SuppressWarnings("unchecked")
	protected void parseProperty(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
		Element propRoot = DomUtils.getChildElementByTagName(persistenceUnit, PROPERTIES);
		if (propRoot == null) {
			return;
		}
		List<Element> properties = DomUtils.getChildElementsByTagName(propRoot, "property");
		for (Element property : properties) {
			String name = property.getAttribute("name");
			String value = property.getAttribute("value");
			unitInfo.addProperty(name, value);
		}
	}

	/**
	 * Parse the <code>class</code> XML elements.
	 */
	@SuppressWarnings("unchecked")
	protected void parseClass(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
		List<Element> classes = DomUtils.getChildElementsByTagName(persistenceUnit, MANAGED_CLASS_NAME);
		for (Element element : classes) {
			String value = DomUtils.getTextValue(element).trim();
			if (StringUtils.hasText(value))
				unitInfo.addManagedClassName(value);
		}
	}

	/**
	 * Parse the <code>jar-file</code> XML elements.
	 */
	@SuppressWarnings("unchecked")
	protected void parseJarFiles(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) throws IOException {
		List<Element> jars = DomUtils.getChildElementsByTagName(persistenceUnit, JAR_FILE_URL);
		for (Element element : jars) {
                        logger.info("开始执行apdplat对spring jpa的定制修改2(2. Start to execute custom modifications  of APDPlat for Spring JPA )");
                        String jarHolder=DomUtils.getTextValue(element).trim();
                        if(jarHolder==null || "".equals(jarHolder.trim())){
                            continue;
                        }
                        logger.info("占位符的内容为(Content of placeholder is): "+jarHolder);
                        //去掉${和}，从配置文件读取真正内容
                        String realJars=PropertyHolder.getProperty(jarHolder.substring(2,jarHolder.length()-1));
                        logger.info("占位符对应的配置文件的内容为(Content of config file related to placeholder is): "+realJars);
                        String[] jarArray=realJars.split(",");
                        for(String jar : jarArray){
                            if (StringUtils.hasText(jar)) {
                                FileSystemResource resource=new FileSystemResource(FileUtils.getAbsolutePath(jar));
				unitInfo.addJarFileUrl(resource.getURL());
                            }
                        }
		}
	}

	/**
	 * Parse the <code>mapping-file</code> XML elements.
	 */
	@SuppressWarnings("unchecked")
	protected void parseMappingFiles(Element persistenceUnit, SpringPersistenceUnitInfo unitInfo) {
		List<Element> files = DomUtils.getChildElementsByTagName(persistenceUnit, MAPPING_FILE_NAME);
		for (Element element : files) {
			String value = DomUtils.getTextValue(element).trim();
			if (StringUtils.hasText(value)) {
				unitInfo.addMappingFileName(value);
			}
		}
	}

}
/**
 * Subclass of {@link MutablePersistenceUnitInfo} that adds instrumentation hooks based on
 * Spring's {@link org.springframework.instrument.classloading.LoadTimeWeaver} abstraction.
 *
 * <p>This class is restricted to package visibility, in contrast to its superclass.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Costin Leau
 * @since 2.0
 * @see PersistenceUnitManager
 */
class SpringPersistenceUnitInfo extends MutablePersistenceUnitInfo {

	private static final String DEFAULT_SHARED_CACHE_MODE_NAME = "UNSPECIFIED";

	private static final String DEFAULT_VALIDATION_MODE_NAME = "AUTO";


	private String sharedCacheModeName = DEFAULT_SHARED_CACHE_MODE_NAME;

	private String validationModeName = DEFAULT_VALIDATION_MODE_NAME;

	private LoadTimeWeaver loadTimeWeaver;

	private ClassLoader classLoader;


	public void setSharedCacheModeName(String sharedCacheModeName) {
		this.sharedCacheModeName =
				(StringUtils.hasLength(sharedCacheModeName) ? sharedCacheModeName : DEFAULT_SHARED_CACHE_MODE_NAME);
	}

	public String getSharedCacheModeName() {
		return this.sharedCacheModeName;
	}

	public void setValidationModeName(String validationModeName) {
		this.validationModeName =
				(StringUtils.hasLength(validationModeName) ? validationModeName : DEFAULT_VALIDATION_MODE_NAME);
	}

	public String getValidationModeName() {
		return this.validationModeName;
	}


	/**
	 * Initialize this PersistenceUnitInfo with the LoadTimeWeaver SPI interface
	 * used by Spring to add instrumentation to the current class loader.
	 */
	public void init(LoadTimeWeaver loadTimeWeaver) {
		Assert.notNull(loadTimeWeaver, "LoadTimeWeaver must not be null");
		this.loadTimeWeaver = loadTimeWeaver;
		this.classLoader = loadTimeWeaver.getInstrumentableClassLoader();
	}

	/**
	 * Initialize this PersistenceUnitInfo with the current class loader
	 * (instead of with a LoadTimeWeaver).
	 */
	public void init(ClassLoader classLoader) {
		Assert.notNull(classLoader, "ClassLoader must not be null");
		this.classLoader = classLoader;
	}


	/**
	 * This implementation returns the LoadTimeWeaver's instrumentable ClassLoader,
	 * if specified.
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * This implementation delegates to the LoadTimeWeaver, if specified.
	 */
	@Override
	public void addTransformer(ClassTransformer classTransformer) {
		if (this.loadTimeWeaver == null) {
			throw new IllegalStateException("Cannot apply class transformer without LoadTimeWeaver specified");
		}
		this.loadTimeWeaver.addTransformer(new ClassFileTransformerAdapter(classTransformer));
	}

	/**
	 * This implementation delegates to the LoadTimeWeaver, if specified.
	 */
	@Override
	public ClassLoader getNewTempClassLoader() {
		ClassLoader tcl = (this.loadTimeWeaver != null ? this.loadTimeWeaver.getThrowawayClassLoader() :
				new SimpleThrowawayClassLoader(this.classLoader));
		String packageToExclude = getPersistenceProviderPackageName();
		if (packageToExclude != null && tcl instanceof DecoratingClassLoader) {
			((DecoratingClassLoader) tcl).excludePackage(packageToExclude);
		}
		return tcl;
	}

}

/**
 * Simple adapter that implements the <code>java.lang.instrument.ClassFileTransformer</code>
 * interface based on a JPA ClassTransformer which a JPA PersistenceProvider asks the
 * PersistenceUnitInfo to install in the current runtime.
 *
 * @author Rod Johnson
 * @since 2.0
 * @see javax.persistence.spi.PersistenceUnitInfo#addTransformer(javax.persistence.spi.ClassTransformer)
 */
class ClassFileTransformerAdapter implements ClassFileTransformer {

	private static final Log logger = LogFactory.getLog(ClassFileTransformerAdapter.class);

	private final ClassTransformer classTransformer;


	public ClassFileTransformerAdapter(ClassTransformer classTransformer) {
		Assert.notNull(classTransformer, "ClassTransformer must not be null");
		this.classTransformer = classTransformer;
	}


	public byte[] transform(
			ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) {

		try {
			byte[] transformed = this.classTransformer.transform(
					loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
			if (transformed != null && logger.isDebugEnabled()) {
				logger.debug("Transformer of class [" + this.classTransformer.getClass().getName() +
						"] transformed class [" + className + "]; bytes in=" +
						classfileBuffer.length + "; bytes out=" + transformed.length);
			}
			return transformed;
		}
		catch (ClassCircularityError ex) {
			logger.error("Error weaving class [" + className + "] with " +
					"transformer of class [" + this.classTransformer.getClass().getName() + "]", ex);
			throw new IllegalStateException("Could not weave class [" + className + "]", ex);
		}
		catch (Throwable ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Error weaving class [" + className + "] with " +
						"transformer of class [" + this.classTransformer.getClass().getName() + "]", ex);
			}
			// The exception will be ignored by the class loader, anyway...
			throw new IllegalStateException("Could not weave class [" + className + "]", ex);
		}
	}


	@Override
	public String toString() {
		return "Standard ClassFileTransformer wrapping JPA transformer: " + this.classTransformer;
	}

}