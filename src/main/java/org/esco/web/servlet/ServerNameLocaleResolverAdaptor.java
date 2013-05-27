/**
 * Copyright 2013 Recia <ent@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esco.web.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

/**
 * Locale Resolver Adaptor which adapt a backing Locale Resolver 
 * by adding the server name as variant.
 * 
 * @author GIP RECIA 2013 - Maxime BOSSARD.
 *
 */
public class ServerNameLocaleResolverAdaptor implements LocaleResolver {

	/** Attribute key indexing the Locale. */
	public static final String LOCALE_ATTRIBUTE_KEY = "LOCALE_ATTRIBUTE_KEY";
	
	/** Locale Resolver. */
	private LocaleResolver backingLocaleResolver;

	/** Do we store the locale in session ? */
	private boolean storeLocaleInSession = false;
	
	@Override
	public Locale resolveLocale(final HttpServletRequest request) {
		// Check if the locale was stored in session
		Locale locale = this.retrieveLocaleFromSession(request);
		if (locale == null) {
			final Locale resolvedLocale = this.backingLocaleResolver.resolveLocale(request);
			this.setLocale(request, null, resolvedLocale);
			
			// The locale in session may have changed
			locale = this.retrieveLocaleFromSession(request);
		}
		
		return (Locale) request.getAttribute(LOCALE_ATTRIBUTE_KEY);
	}

	@Override
	public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
		// Add the server name variant to the locale
		final String serverName = request.getServerName();
		Locale variantLocale = new Locale(locale.getLanguage(), locale.getCountry(), serverName);
		
		request.setAttribute(LOCALE_ATTRIBUTE_KEY, variantLocale);
		
		if (this.storeLocaleInSession) {
			// Store the locale in session
			request.getSession().setAttribute(LOCALE_ATTRIBUTE_KEY, variantLocale);
		}
	}

	protected Locale retrieveLocaleFromSession(final HttpServletRequest request) {
		Locale locale = null;
		if (this.storeLocaleInSession) {
			locale = (Locale) request.getSession().getAttribute(LOCALE_ATTRIBUTE_KEY);
		}
		
		return locale;
	}

	public LocaleResolver getBackingLocaleResolver() {
		return backingLocaleResolver;
	}

	public void setBackingLocaleResolver(LocaleResolver backingLocaleResolver) {
		this.backingLocaleResolver = backingLocaleResolver;
	}

	public boolean isStoreLocaleInSession() {
		return storeLocaleInSession;
	}

	public void setStoreLocaleInSession(boolean storeLocaleInSession) {
		this.storeLocaleInSession = storeLocaleInSession;
	}

}
