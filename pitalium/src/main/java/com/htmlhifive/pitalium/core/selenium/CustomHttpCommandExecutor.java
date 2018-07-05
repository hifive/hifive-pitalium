package com.htmlhifive.pitalium.core.selenium;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openqa.selenium.remote.DriverCommand.GET_ALL_SESSIONS;
import static org.openqa.selenium.remote.DriverCommand.NEW_SESSION;
import static org.openqa.selenium.remote.DriverCommand.QUIT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LocalLogs;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.NeedsLocalLogs;
import org.openqa.selenium.logging.profiler.HttpProfilerLogEntry;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandCodec;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.Dialect;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.HttpSessionId;
import org.openqa.selenium.remote.ProtocolHandshake;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.ResponseCodec;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpClient.Factory;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

import com.google.common.collect.ImmutableMap;

public class CustomHttpCommandExecutor implements CommandExecutor, NeedsLocalLogs {

	private static Factory defaultClientFactory;

	private final URL remoteServer;
	private final HttpClient client;
	private final HttpClient.Factory httpClientFactory;
	private final Map<String, CommandInfo> additionalCommands;
	protected CommandCodec<HttpRequest> commandCodec;
	protected ResponseCodec<HttpResponse> responseCodec;

	private LocalLogs logs = LocalLogs.getNullLogger();

	public CustomHttpCommandExecutor(URL addressOfRemoteServer) {
		this(ImmutableMap.of(), addressOfRemoteServer);
	}

	/**
	 * Creates an {@link HttpCommandExecutor} that supports non-standard {@code additionalCommands} in addition to the
	 * standard.
	 *
	 * @param additionalCommands additional commands to allow the command executor to process
	 * @param addressOfRemoteServer URL of remote end Selenium server
	 */
	public CustomHttpCommandExecutor(Map<String, CommandInfo> additionalCommands, URL addressOfRemoteServer) {
		this(additionalCommands, addressOfRemoteServer, getDefaultClientFactory());
	}

	public CustomHttpCommandExecutor(Map<String, CommandInfo> additionalCommands, URL addressOfRemoteServer,
			HttpClient.Factory httpClientFactory) {
		try {
			remoteServer = addressOfRemoteServer == null
					? new URL(System.getProperty("webdriver.remote.server", "http://localhost:4444/wd/hub"))
					: addressOfRemoteServer;
		} catch (MalformedURLException e) {
			throw new WebDriverException(e);
		}

		this.additionalCommands = additionalCommands;
		this.httpClientFactory = httpClientFactory;
		this.client = httpClientFactory.createClient(remoteServer);
	}

	private static synchronized Factory getDefaultClientFactory() {
		if (defaultClientFactory == null) {
			defaultClientFactory = new org.openqa.selenium.remote.internal.ApacheHttpClient.Factory();
		}
		return defaultClientFactory;
	}

	/**
	 * It may be useful to extend the commands understood by this {@code HttpCommandExecutor} at run time, and this can
	 * be achieved via this method. Note, this is protected, and expected usage is for subclasses only to call this.
	 *
	 * @param commandName The name of the command to use.
	 * @param info CommandInfo for the command name provided
	 */
	protected void defineCommand(String commandName, CommandInfo info) {
		checkNotNull(commandName);
		checkNotNull(info);
		commandCodec.defineCommand(commandName, info.getMethod(), info.getUrl());
	}

	public void setLocalLogs(LocalLogs logs) {
		this.logs = logs;
	}

	private void log(String logType, LogEntry entry) {
		logs.addEntry(logType, entry);
	}

	public URL getAddressOfRemoteServer() {
		return remoteServer;
	}

	public Response execute(Command command) throws IOException {
		if (command.getSessionId() == null) {
			if (QUIT.equals(command.getName())) {
				return new Response();
			}
			if (!GET_ALL_SESSIONS.equals(command.getName()) && !NEW_SESSION.equals(command.getName())) {
				throw new NoSuchSessionException("Session ID is null. Using WebDriver after calling quit()?");
			}
		}

		if (NEW_SESSION.equals(command.getName())) {
			if (commandCodec != null) {
				throw new SessionNotCreatedException("Session already exists");
			}
			ProtocolHandshake handshake = new ProtocolHandshake();
			log(LogType.PROFILER, new HttpProfilerLogEntry(command.getName(), true));
			ProtocolHandshake.Result result = handshake.createSession(client, command);
			Dialect dialect = result.getDialect();
			commandCodec = dialect.getCommandCodec();
			for (Map.Entry<String, CommandInfo> entry : additionalCommands.entrySet()) {
				defineCommand(entry.getKey(), entry.getValue());
			}
			responseCodec = dialect.getResponseCodec();
			log(LogType.PROFILER, new HttpProfilerLogEntry(command.getName(), false));
			return result.createResponse();
		}

		if (commandCodec == null || responseCodec == null) {
			throw new WebDriverException("No command or response codec has been defined. Unable to proceed");
		}

		HttpRequest httpRequest = commandCodec.encode(command);
		try {
			log(LogType.PROFILER, new HttpProfilerLogEntry(command.getName(), true));
			HttpResponse httpResponse = client.execute(httpRequest, true);
			log(LogType.PROFILER, new HttpProfilerLogEntry(command.getName(), false));

			Response response = responseCodec.decode(httpResponse);
			if (response.getSessionId() == null) {
				if (httpResponse.getTargetHost() != null) {
					response.setSessionId(HttpSessionId.getSessionId(httpResponse.getTargetHost()));
				} else {
					// Spam in the session id from the request
					response.setSessionId(command.getSessionId().toString());
				}
			}

			if (QUIT.equals(command.getName())) {
				this.client.close();
			}
			return response;
		} catch (UnsupportedCommandException e) {
			if (e.getMessage() == null || "".equals(e.getMessage())) {
				throw new UnsupportedOperationException(
						"No information from server. Command name was: " + command.getName(), e.getCause());
			}
			throw e;
		}
	}
}