package de.mainzelliste.paths.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import de.mainzelliste.paths.configuration.AnonymousClient;
import de.mainzelliste.paths.configuration.Authentication;
import de.mainzelliste.paths.configuration.Restrictions;
import de.mainzelliste.paths.configuration.Restrictions.Restriction.InputRestriction;

/**
 * Utility class for access control (authentication, authorization).
 */
public class Authenticator {
	/**
	 * Represents one registered client.
	 */
	class Client {
		/** The apiKey by which this client authenticates itself. */
		String apiKey;
		/** The permissions of this client. */
		Set<String> permissions;
		/** Restrictions on inputs, ordered by path and input. */
		Map<Pair<String, String>, Predicate<String>> inputChecks = new HashMap<>();
		/** Whether this client has all permissions */
		boolean allPermissions;
	}

	private HashMap<String, Client> clients = new HashMap<>();
	private Client anonymousClient = new Client();

	/**
	 * Create instance from given configuration.
	 * 
	 * @param config Configuration (typically de-serialized from XML).
	 */
	public Authenticator(Authentication config) {
		for (de.mainzelliste.paths.configuration.Client clientConfig : config.getClient()) {
			// Transforms configuration into internal format for more efficient access
			Client thisClient = new Client();
			thisClient.apiKey = clientConfig.getApiKey();
			thisClient.permissions = new HashSet<>(clientConfig.getPermissions());
			thisClient.allPermissions = (clientConfig.getAllPermissions() != null);
			// Iterate over restrictions (e.g. which input is permissable on a given client,
			// path and field)
			for (Restrictions thisRestriction : clientConfig.getRestrictions()) {
				String thisPathName = thisRestriction.getRestriction().getPath();
				if (thisRestriction.getRestriction().getInputRestriction() != null) {
					// Iterate over input restrictions (i.e. which values are permitted)
					for (InputRestriction inputRestr : thisRestriction.getRestriction().getInputRestriction()) {
						String thisInput = inputRestr.getInput();
						// Functional interface String -> boolean implements input restrictions
						Predicate<String> thisInputCheck = null;
						if (inputRestr.getPermissibleValues() != null) {
							thisInputCheck = s -> inputRestr.getPermissibleValues().getValue().contains(s);
						} else if (inputRestr.getRegex() != null) {
							Pattern p = Pattern.compile(inputRestr.getRegex());
							thisInputCheck = s -> p.matcher(s).matches();
						}
						if (thisInputCheck != null)
							thisClient.inputChecks.put(new ImmutablePair<String, String>(thisPathName, thisInput),
									thisInputCheck);
					}

				}
			}
			clients.put(clientConfig.getApiKey(), thisClient);
		}
		AnonymousClient anonymousClient = config.getAnonymous();
		if (anonymousClient != null) {
			if (anonymousClient.getAllPermissions() != null)
				this.anonymousClient.allPermissions = true;
			else
				this.anonymousClient.permissions = new HashSet<>(anonymousClient.getPermissions());
		}
	}

	/**
	 * Check whether a client is authorized for a request according to the
	 * authentication/authorization configuration. If anonymous access to the
	 * requested action (permission) is configured, no actual authentication occurs.
	 *
	 * If access is denied, an appropriate WebApplicationException is thrown.
	 *
	 * @param req        The injected HTTPServletRequest.
	 * @param permission The permission to check (name of a path).
	 */
	public void checkPermission(HttpServletRequest req, String permission) {
		// First, check if anonymous access is possible for the requested
		// permission
		if (this.anonymousClient.allPermissions
				|| (this.anonymousClient.permissions != null && this.anonymousClient.permissions.contains(permission)))
			return;

		@SuppressWarnings("unchecked")
		Set<String> perms = (Set<String>) req.getSession(true).getAttribute("permissions");

		if (perms == null) { // Login
			Client client = getClient(req);
			perms = client.permissions;
			req.getSession().setAttribute("permissions", perms);
		}

		if (!perms.contains(permission)) { // Check permission
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
					.entity("Your permissions do not allow the requested access.").build());
		}
	}

	/**
	 * Check restrictions on a request. Restrictions may exist on what input values
	 * a given client can use on a given path. For example, a pseudonymization
	 * service might restrict the types of IDs that a particular client can
	 * retrieve.
	 * 
	 * If access is denied, an appropriate WebApplicationException is thrown.
	 * 
	 * @param req  The HttpServletRequest.
	 * @param path The called path.
	 * @param data Input data, pre-parsed by
	 *             {@link PathBackend#unmarshalToStrings(String)}.
	 */
	public void checkRestrictions(HttpServletRequest req, String path, Map<String, String> data) {
		Client client = getClient(req);
		for (String input : data.keySet()) {
			String value = data.get(input);
			if (!client.inputChecks.getOrDefault(new ImmutablePair<String, String>(path, input), s -> true)
					.test(data.get(input))) {
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
						.entity(String.format("Value '%s' not allowed for input field '%s'!", value, input)).build());
			}
		}
	}

	/**
	 * Get the client (internal representation) from a request.
	 */
	private Client getClient(HttpServletRequest req) {
		String apiKey = req.getHeader("apiKey");
		Client client = clients.get(apiKey);

		if (client == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
					.entity("Please supply your API key in HTTP header field 'apiKey'.").build());
		}
		return client;
	}
}