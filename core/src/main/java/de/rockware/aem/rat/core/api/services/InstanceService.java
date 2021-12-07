package de.rockware.aem.rat.core.api.services;

/**
 * Helper Service that holds some information about the AEM instance.
 * Created by ogebert on 28.01.16.
 */
public interface InstanceService {

	/**
	 * Is this an author instance?
	 *
	 * @return <code>true</code> if yes, <code>false</code> if no.
	 */
	boolean isAuthor();

	/**
	 * Is this a publish instance?
	 *
	 * @return <code>true</code> if yes, <code>false</code> if no.
	 */
	boolean isPublish();

	/**
	 * Is this a instance running locally?
	 *
	 * @return <code>true</code> if yes, <code>false</code> if no.
	 */
	boolean isLocal();

	/**
	 * check run mode value.
	 *
	 * @param value
	 *            value to check
	 * @return true if runmode is set
	 */
	boolean hasRunModeValue(String value);
}
