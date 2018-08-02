package de.mainzelliste.paths.backend;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mainzelliste.paths.configuration.AnonymousClient;
import de.mainzelliste.paths.configuration.Authentication;
//import de.pseudonymisierung.mainzelliste.SubnetUtils;
//import de.pseudonymisierung.mainzelliste.Servers.Server;

public class Authenticator {
	/**
	 * Represents one registered client.
	 */
	class Client {
		/** The apiKey by which this client authenticates itself. */
		String apiKey;
		/** The permissions of this client. */
		Set<String> permissions;
		/** Whether this client has all permissions */
		boolean allPermissions;
	}

	private HashMap<String, Client> clients = new HashMap<>();
	private Client anonymousClient = new Client();

	public Authenticator(Authentication config) {
		for (de.mainzelliste.paths.configuration.Client clientConfig : config.getClient()) {
			Client thisClient = new Client();
			thisClient.apiKey = clientConfig.getApiKey();
			thisClient.permissions = new HashSet(clientConfig.getPermissions());
			thisClient.allPermissions = (clientConfig.getAllPermissions() != null);
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
	 * @param req
	 *            The injected HTTPServletRequest.
	 * @param permission
	 *            The permission to check (name of a path).
	 */
	public void checkPermission(HttpServletRequest req, String permission) {
		// First, check if anonymous access is possible for the requested permission
		if (this.anonymousClient.allPermissions || (this.anonymousClient.permissions != null && this.anonymousClient.permissions.contains(permission)))
			return;

		@SuppressWarnings("unchecked")
		Set<String> perms = (Set<String>) req.getSession(true).getAttribute("permissions");

		if (perms == null) { // Login
			String apiKey = req.getHeader("apiKey");
			Client client = clients.get(apiKey);

			if (client == null) {
				// logger.info("No server found with provided API key " + apiKey);
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
						.entity("Please supply your API key in HTTP header field 'apiKey'.").build());
			}

			// if(!clients.allowedRemoteAdresses.contains(req.getRemoteAddr())){
			// boolean addressInRange = false;
			// for (SubnetUtils thisAddressRange : server.allowedRemoteAdressRanges) {
			// try {
			// if (thisAddressRange.getInfo().isInRange(req.getRemoteAddr())) {
			// addressInRange = true;
			// break;
			// }
			// } catch (IllegalArgumentException e) {
			// // Occurs if an IPv6 address was transmitted
			// logger.error("Could not parse IP address " + req.getRemoteAddr(), e);
			// break;
			// }
			// }
			// if (!addressInRange) {
			// logger.info("IP address " + req.getRemoteAddr() + " rejected");
			// throw new WebApplicationException(Response
			// .status(Status.UNAUTHORIZED)
			// .entity(String.format("Rejecting your IP address %s.", req.getRemoteAddr()))
			// .build());
			// }
			// }

			perms = client.permissions;
			req.getSession().setAttribute("permissions", perms);
			// logger.info("Server " + req.getRemoteHost() + " logged in with permissions "
			// + Arrays.toString(perms.toArray()) + ".");
		}

		if (!perms.contains(permission)) { // Check permission
			// logger.info("Access from " + req.getRemoteHost() + " is denied since they
			// lack permission " + permission + ".");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
					.entity("Your permissions do not allow the requested access.").build());
		}
	}

}
