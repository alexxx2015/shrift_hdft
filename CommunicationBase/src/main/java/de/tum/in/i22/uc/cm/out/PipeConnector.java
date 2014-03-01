package de.tum.in.i22.uc.cm.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.google.protobuf.MessageLite;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipeConnector implements IFastConnector {

	private static final Logger _logger = Logger.getLogger(PipeConnector.class);

	private final File _inPipe;
	private final File _outPipe;
	private OutputStream _outputStream;
 	private InputStream _inputStream;

 	public PipeConnector(File inPipe, File outPipe) {
		_inPipe = inPipe;
		_outPipe = outPipe;
	}

	@Override
	public void connect() throws Exception {
		_logger.debug("Establish connection to pipes " + _inPipe + " and " + _outPipe);

		try {
			_logger.debug("Get i/o streams.");
			_outputStream = new BufferedOutputStream(new FileOutputStream(_outPipe));
			_inputStream = new BufferedInputStream(new FileInputStream(_inPipe));
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	@Override
	public void disconnect() {
		_logger.info("Tear down the connection");
		try {
			_inputStream.close();
			_outputStream.close();
		_logger.info("Connection closed!");
	} catch (IOException e) {
		_logger.error("Error occurred when closing the connection.", e);
	}
	}

	/**
	 * This method is currently not used. The idea was to use it instead of
	 *  Google Protocol Buffer method writeDelimitedTo().
	 * It first writes the operation type (one byte), then the size of the message as 32 bit int
	 *  and then it writes the message bytes. The message size always takes 32 bits. WriteDelimitedTo()
	 *  method uses compact representation of int.
	 * @param operationType
	 * @param messages
	 * @throws IOException
	 */
	protected void sendData(byte operationType, MessageLite... messages)
			throws IOException {
		_logger.trace("Write operation type. Byte representation: " + operationType);
		getOutputStream().write(operationType);
		sendData(messages);
		getOutputStream().flush();
	}

	private void sendData(MessageLite... messages) throws IOException {
		_logger.trace("Send GPB message/s");
		if (messages != null && messages.length > 0) {
			_logger.trace("Num of messages" + messages.length);
			for (int i = 0; i < messages.length; i++) {
				int messageSize = messages[i].getSerializedSize();
				_logger.trace("Message size to send: " + messageSize);
				OutputStream out = getOutputStream();
				writeInt(out, messageSize);
				out.write(messages[i].toByteArray());
			}
		}
	}

	/**
	 * Currently not used.
	 * Writes 4 bytes (int as 4 bytes, Big Endian format, most significant byte first)
	 * @param out OutputStream where the data will be written
	 * @param value int value
	 * @throws IOException
	 */
	private void writeInt(OutputStream out, int value)
			throws IOException {

		_logger.trace("Writing int value (" + value + ") as 4 bytes in Big Endian format");

		int ibyte;
        ibyte = ((value >>> 24) & 0xff); out.write(ibyte);
        ibyte = ((value >>> 16) & 0xff); out.write(ibyte);
        ibyte = ((value >>> 8) & 0xff); out.write(ibyte);
        ibyte = (value & 0xff); out.write(ibyte);
	}

	protected OutputStream getOutputStream() {
		return _outputStream;
	}

	protected InputStream getInputStream() {
		return _inputStream;
	}
}
