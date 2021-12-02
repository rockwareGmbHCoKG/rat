package de.rockware.aem.rat.core.impl.services;

import com.day.cq.commons.Externalizer;

import de.rockware.aem.rat.core.api.services.InstanceService;

import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Set;

/**
 * Default implementation. Check runmodes and more.
 */
@Component(name = "Rockware default Instance Service", immediate = true)
public class DefaultInstanceService implements InstanceService {

		@Reference
		private SlingSettingsService settings;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAuthor() {
			return hasRunModeValue(Externalizer.AUTHOR);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPublish() {
			return hasRunModeValue(Externalizer.PUBLISH);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isLocal() {
			return hasRunModeValue(Externalizer.LOCAL);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasRunModeValue(String value) {
			Set<String> runModes = settings.getRunModes();
			return runModes.contains(value);
		}

		@Override
		public String getInstanceRunMode() {
			if(isLocal()) {
				return Externalizer.LOCAL;
			}
			if(isAuthor()) {
				return Externalizer.AUTHOR;
			}
			if(isPublish()) {
				return Externalizer.PUBLISH;
			}
			return settings.getRunModes().iterator().next();
		}

}
