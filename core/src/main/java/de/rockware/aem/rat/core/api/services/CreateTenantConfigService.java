package de.rockware.aem.rat.core.api.services;

/**
 * Created by diwakar on 26/01/16.
 * This Interface is used to configure the start and end level of the de.rockware.aem.tenant services.
 */
public interface CreateTenantConfigService {

    /**
     * Getter for this param
     * @return	param value
     */
	int getStartLevel();

    /**
     * Getter for this param
     * @return	param value
     */
	int getEndLevel();

    /**
     * Getter for this param
     * @return	param value
     */
	int getReadAccessLevel();



	/**
	 * Check if we need to do something on the given level.
	 * @param level	level
	 * @return	true if yes
	 */
	boolean isValidLevel(int level);

	/**
	 * Check if we only have read access here.
	 * @param level	current level
	 * @return	read access level
	 */
	boolean isReadAccessLevel(int level);



}
