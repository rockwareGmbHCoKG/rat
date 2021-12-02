package de.rockware.aem.rat.core.impl.services;

import org.apache.jackrabbit.oak.commons.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

import java.util.Dictionary;
import java.util.HashMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author  diwakar
 * 
 * Implements CreateTenantGroupsService 
 * 
 * this class provides methods to manipulate all groups 
 */

@Component(label = "Rockware AEM - Create Tenant Groups Interface", policy = ConfigurationPolicy.OPTIONAL, metatype = true, immediate = true, description = "Implementation of the create de.rockware.aem.tenant groups interface for AEM Tenant..")
public class CreateTenantGroupsServiceImpl implements CreateTenantGroupsService {
	
	private static final Logger logger = getLogger(CreateTenantGroupsServiceImpl.class);

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Readers Group", boolValue = true, description = "Create a readers group for each content page between start and end level. The newly created readers group will be added as member to parent page's readers group.")
	public static final String PROP_CREATE_GROUP_READERS = "create.de.rockware.aem.tenant.createGroupReaders";

	/**
	 * Configuration property.
	 */
	@Property(label = "Readers Group Name", value = "readers", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_READERS = "create.de.rockware.aem.tenant.groupNameReaders";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Global Readers Group", boolValue = true, description = "Create a global readers group and add it as a member to each readers group created.")
	public static final String PROP_CREATE_GROUP_GLOBAL_READERS = "create.de.rockware.aem.tenant.createGroupGlobalReaders";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Readers Group Name", value = "global-readers", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_GLOBAL_READERS = "create.de.rockware.aem.tenant.groupNameGlobalReaders";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Editors Group", boolValue = true, description = "Create a editors group for each content page between start and end level. The newly created editors group will be added as member to parent page's readers group.")
	public static final String PROP_CREATE_GROUP_EDITORS = "create.de.rockware.aem.tenant.createGroupEditors";

	/**
	 * Configuration property.
	 */
	@Property(label = "Editors Group Name", value = "editors", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_EDITORS = "create.de.rockware.aem.tenant.groupNameEditors";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Global Editors Group", boolValue = true, description = "Create a global editors group and add it as a member to each editors group created.")
	public static final String PROP_CREATE_GROUP_GLOBAL_EDITORS = "create.de.rockware.aem.tenant.createGroupGlobalEditors";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Editors Group Name", value = "global-editors", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_GLOBAL_EDITORS = "create.de.rockware.aem.tenant.groupNameGlobalEditors";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Publishers Group", boolValue = true, description = "Create a publishers group for each content page between start and end level. The newly created publishers group will be added as member to parent page's readers group.")
	public static final String PROP_CREATE_GROUP_PUBLISHERS = "create.de.rockware.aem.tenant.createGroupPublishers";

	/**
	 * Configuration property.
	 */
	@Property(label = "Publishers Group Name", value = "publishers", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_PUBLISHERS = "create.de.rockware.aem.tenant.groupNamePublishers";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Global Publishers Group", boolValue = true, description = "Create a global publishers group and add it as a member to each publishers group created.")
	public static final String PROP_CREATE_GROUP_GLOBAL_PUBLISHERS = "create.de.rockware.aem.tenant.createGroupGlobalPublishers";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Publishers Group Name", value = "global-publishers", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_GLOBAL_PUBLISHERS = "create.de.rockware.aem.tenant.groupNameGlobalPublishers";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create User Admins Group", boolValue = true, description = "Create a user admin group for each content page between start and end level. The newly created publishers group will be added as member to parent page's readers group.")
	public static final String PROP_CREATE_GROUP_USER_ADMINS = "create.de.rockware.aem.tenant.createGroupUserAdmins";

	/**
	 * Configuration property.
	 */
	@Property(label = "User Admins Group Name", value = "useradmins", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_USER_ADMINS = "create.de.rockware.aem.tenant.groupNameUserAdmins";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Global User Admins Group", boolValue = true, description = "Create a global user admins group and add it as a member to each user admins group created.")
	public static final String PROP_CREATE_GROUP_GLOBAL_USER_ADMINS = "create.de.rockware.aem.tenant.createGroupGlobalUserAdmins";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global User Admins Group Name", value = "global-useradmins", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_GLOBAL_USER_ADMINS = "create.de.rockware.aem.tenant.groupNameGlobalUserAdmins";

	/**
	 * Configuration property.
	 */
	@Property(label = "Create Global Support Group", boolValue = true, description = "Create a global support group with extended rights and add it as a member to each publishers group created.")
	public static final String PROP_CREATE_GROUP_GLOBAL_SUPPORT = "create.de.rockware.aem.tenant.createGroupGlobalSupport";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Support Group Name", value = "global-support", description = "The given group name will be prefixed / suffixed with path and / or instance name.")
	public static final String PROP_GROUP_NAME_GLOBAL_SUPPORT = "create.de.rockware.aem.tenant.groupNameGlobalSupport";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Name Prefix", value = "a", description = "All group names will be prefixed with this string.")
	public static final String PROP_GROUP_NAME_PREFIX = "create.de.rockware.aem.tenant.groupNamePrefix";

	/**
	 * Configuration property.
	 */
	@Property(label = "Global Name Suffix", value = "", description = "All group names will be suffixed with this string.")
	public static final String PROP_GROUP_NAME_SUFFIX = "create.de.rockware.aem.tenant.groupNameSuffix";

	/**
	 * Configuration property.
	 */
	@Property(label = "Group Name Separator", value = ".", description = "This letter or string should not be part of your standard page names as it is needed to split the group name into its parts.")
	public static final String PROP_GROUP_NAME_SEPARATOR = "create.de.rockware.aem.tenant.groupNameSeparator";

	/**
	 * Configuration property.
	 */
	@Property(label = "Inherit Read", boolValue = false, description = "If set to true, you are allowed to read all descendant nodes. If set to false you may only read descendant nodes if the page is on the last relevant level.")
	public static final String PROP_READ_INHERITANCE = "create.de.rockware.aem.tenant.readInheritance";

	/**
	 * Configuration property.
	 */
	@Property(label = "Toplevel Readers", value = "toplevel-readers", description = "This group gains read access to the toplevel folders.")
	public static final String PROP_TOP_LEVEL_READERS_NAME = "create.de.rockware.aem.tenant.toplevel.readers";

	/**
	 * Activates the service.
	 *
	 * @param context
	 *            ComponentContext
	 */
	@Activate
	public void activate(ComponentContext context) {
		groupNamesMap = new HashMap<>();
		localGroupNamesMap = new HashMap<>();
		globalGroupNamesMap = new HashMap<>();
		logger.info("Activating Tenant Groups API service.");
		Dictionary<String, Object> dictionary = context.getProperties();
		createReadersGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_READERS), false);
		readersGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_READERS), "readers");
		if (createReadersGroup) {
			localGroupNamesMap.put(GroupType.READER, readersGroupName);
		}
		createGlobalReadersGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_GLOBAL_READERS), false);
		globalReadersGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_GLOBAL_READERS), "global-readers");
		if (createGlobalReadersGroup) {
			globalGroupNamesMap.put(GroupType.GLOBAL_READER, globalReadersGroupName);
		}

		createEditorsGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_EDITORS), false);
		editorsGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_EDITORS), "editors");
		if (createEditorsGroup) {
			localGroupNamesMap.put(GroupType.EDITOR, editorsGroupName);
		}
		createGlobalEditorsGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_GLOBAL_EDITORS), false);
		globalEditorsGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_GLOBAL_EDITORS), "global-editors");
		if (createGlobalEditorsGroup) {
			globalGroupNamesMap.put(GroupType.GLOBAL_EDITOR, globalEditorsGroupName);
		}

		createPublishersGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_PUBLISHERS), false);
		publishersGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_PUBLISHERS), "publishers");
		if (createPublishersGroup) {
			localGroupNamesMap.put(GroupType.PUBLISHER, publishersGroupName);
		}
		createGlobalPublishersGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_GLOBAL_PUBLISHERS), false);
		globalPublishersGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_GLOBAL_PUBLISHERS), "global-publishers");
		if (createGlobalPublishersGroup) {
			globalGroupNamesMap.put(GroupType.GLOBAL_PUBLISHER, globalPublishersGroupName);
		}

		createUserAdminsGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_USER_ADMINS), false);
		userAdminsGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_USER_ADMINS), "useradmins");
		if (createUserAdminsGroup) {
			localGroupNamesMap.put(GroupType.USER_ADMIN, userAdminsGroupName);
		}
		createGlobalUserAdminsGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_GLOBAL_USER_ADMINS), false);
		globalUserAdminsGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_GLOBAL_USER_ADMINS), "global-useradmins");
		if (createGlobalUserAdminsGroup) {
			globalGroupNamesMap.put(GroupType.GLOBAL_USER_ADMIN, globalUserAdminsGroupName);
		}

		createGlobalSupportGroup = PropertiesUtil.toBoolean(dictionary.get(PROP_CREATE_GROUP_GLOBAL_SUPPORT), false);
		globalSupportGroupName = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_GLOBAL_SUPPORT), "global-support");
		if (createGlobalSupportGroup) {
			globalGroupNamesMap.put(GroupType.GLOBAL_SUPPORT, globalSupportGroupName);
		}
		readInheritance = PropertiesUtil.toBoolean(dictionary.get(PROP_READ_INHERITANCE), false);
		topLevelReadersGroupName = PropertiesUtil.toString(dictionary.get(PROP_TOP_LEVEL_READERS_NAME), "toplevel-readers");

		groupNamePrefix = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_PREFIX), "a");
		groupNameSuffix = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_SUFFIX), "");
		groupNameSeparator = PropertiesUtil.toString(dictionary.get(PROP_GROUP_NAME_SEPARATOR), ".");
		groupNamesMap.put(GroupType.READER, readersGroupName);
		groupNamesMap.put(GroupType.GLOBAL_READER, globalReadersGroupName);
		groupNamesMap.put(GroupType.EDITOR, editorsGroupName);
		groupNamesMap.put(GroupType.GLOBAL_EDITOR, globalEditorsGroupName);
		groupNamesMap.put(GroupType.PUBLISHER, publishersGroupName);
		groupNamesMap.put(GroupType.GLOBAL_PUBLISHER, globalPublishersGroupName);
		groupNamesMap.put(GroupType.USER_ADMIN, userAdminsGroupName);
		groupNamesMap.put(GroupType.GLOBAL_USER_ADMIN, globalUserAdminsGroupName);
		groupNamesMap.put(GroupType.GLOBAL_SUPPORT, globalSupportGroupName);
		groupNamesMap.put(GroupType.TOPLEVEL_READER, topLevelReadersGroupName);

		String readInheritGroupName = "read-inherit";
		groupNamesMap.put(GroupType.READ_INHERIT, readInheritGroupName);
		localGroupNamesMap.put(GroupType.READ_INHERIT, readInheritGroupName);
	}
	
	@Deactivate
	public void deavtivate(ComponentContext context){
		logger.info("Deactivating Create Tenant Groups Service in context {}", context.toString());
	}
	
	private boolean createReadersGroup;

	private String readersGroupName;

	private boolean createGlobalReadersGroup;

	private String globalReadersGroupName;

	private boolean createEditorsGroup;

	private String editorsGroupName;

	private boolean createGlobalEditorsGroup;

	private String globalEditorsGroupName;

	private boolean createPublishersGroup;

	private String publishersGroupName;

	private boolean createGlobalPublishersGroup;

	private String globalPublishersGroupName;

	private boolean createUserAdminsGroup;

	private String userAdminsGroupName;

	private boolean createGlobalUserAdminsGroup;

	private String globalUserAdminsGroupName;

	private boolean createGlobalSupportGroup;

	private String globalSupportGroupName;

	private String groupNamePrefix;

	private String groupNameSuffix;

	private String groupNameSeparator;

	private boolean readInheritance;

	private String topLevelReadersGroupName;

	private Map<GroupType, String> groupNamesMap;

	private Map<GroupType, String> globalGroupNamesMap;

	private Map<GroupType, String> localGroupNamesMap;

	@Override
	public boolean isCreateReadersGroup() {
		return createReadersGroup;
	}

	@Override
	public String getReadersGroupName() {
		return readersGroupName;
	}

	@Override
	public boolean isCreateGlobalReadersGroup() {
		return createGlobalReadersGroup;
	}

	@Override
	public String getGlobalReadersGroupName() {
		return globalReadersGroupName;
	}

	@Override
	public boolean isCreateEditorsGroup() {
		return createEditorsGroup;
	}

	@Override
	public String getEditorsGroupName() {
		return editorsGroupName;
	}

	@Override
	public boolean isCreateGlobalEditorsGroup() {
		return createGlobalEditorsGroup;
	}

	@Override
	public String getGlobalEditorsGroupName() {
		return globalEditorsGroupName;
	}

	@Override
	public boolean isCreatePublishersGroup() {
		return createPublishersGroup;
	}

	@Override
	public String getPublishersGroupName() {
		return publishersGroupName;
	}

	@Override
	public boolean isCreateGlobalPublishersGroup() {
		return createGlobalPublishersGroup;
	}

	@Override
	public String getGlobalPublishersGroupName() {
		return globalPublishersGroupName;
	}

	@Override public boolean isCreateUserAdminsGroup() {
		return createUserAdminsGroup;
	}

	@Override public String getUserAdminsGroupName() {
		return userAdminsGroupName;
	}

	@Override public boolean isCreateGlobalUserAdminsGroup() {
		return createGlobalUserAdminsGroup;
	}

	@Override public String getGlobalUserAdminsGroupName() {
		return globalUserAdminsGroupName;
	}

	@Override
	public boolean isCreateGlobalSupportGroup() {
		return createGlobalSupportGroup;
	}

	@Override
	public String getGlobalSupportGroupName() {
		return globalSupportGroupName;
	}

	@Override
	public String getGroupNamePrefix() {
		return groupNamePrefix;
	}

	@Override
	public String getGroupNameSuffix() {
		return groupNameSuffix;
	}

	@Override
	public String getGroupNameSeparator() {
		return groupNameSeparator;
	}

	@Override
	public boolean isReadInheritance() {
		return readInheritance;
	}

	@Override
	public Map<GroupType, String> getGlobalGroupNamesMap() {
		return globalGroupNamesMap;
	}

	@Override
	public Map<GroupType, String> getLocalGroupNamesMap() {
		return localGroupNamesMap;
	}

	@Override
	public String getTopLevelReadersGroupName() {
		return topLevelReadersGroupName;
	}

	@Override
	public String computeGroupName(GroupType type, String path) {
		StringBuilder groupName = new StringBuilder();
		if (StringUtils.isNotEmpty(path)) {
			if (!StringUtils.isEmpty(groupNamePrefix)) {
				groupName.append(groupNamePrefix).append(".");
			}
			String groupPath = StringUtils.replace(StringUtils.substringAfter(path, "/content/"), "/", groupNameSeparator);
			groupName.append(groupPath).append(".");
		}
		groupName.append(groupNamesMap.get(type));
		return groupName.toString();
	}
	
}
