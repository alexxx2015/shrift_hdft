package de.tum.in.i22.uc.pdp.handlers.pip;

import java.io.IOException;
import java.net.Socket;

public class PipClientTcpConnectionHandler extends PipClientConnectionHandler {
	private final Socket _socket;

	public PipClientTcpConnectionHandler(Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
		_socket = socket;
	}

	@Override
	protected void disconnect() {
		if (_socket != null) {
			try {
				_socket.close();
			} catch (IOException e) {
				_logger.warn("Unable to close socket", e);
				e.printStackTrace();
			}
		}
	}
}