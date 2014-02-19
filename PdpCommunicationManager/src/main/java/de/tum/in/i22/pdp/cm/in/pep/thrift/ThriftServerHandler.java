package de.tum.in.i22.pdp.cm.in.pep.thrift;

import java.io.EOFException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;

public class ThriftServerHandler extends ClientConnectionHandler implements
		PDPThriftConnector.Iface {

	private static RequestHandler requestHandler = RequestHandler.getInstance();
	private static final String IP = "localhost";
	private static int PORT = 8090;

	public ThriftServerHandler(int pepPort) {
		// TODO Auto-generated constructor stub
		// we should start it on this port
		super(null);
		PORT = pepPort;
		requestHandler = RequestHandler.getInstance();

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ThriftServer: " + IP + ":" + PORT;
	}

	private static final Logger _logger = Logger
			.getLogger(ThriftServerHandler.class);

	private IResponse processEvent(IEvent ev) {
		if (ev == null)
			return null;
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();

		Object responseObj;
		try {
			requestHandler.addEvent(ev, this);
			responseObj = waitForResponse();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			_logger.error("Communication error. Returning null");
			e1.printStackTrace();
			return null;
		}

		_logger.trace("Response received");

		if (responseObj instanceof IResponse) {
			IResponse response = (IResponse) responseObj;
			return response;
		} else {
			_logger.error("Response is not an instance of IResponse. returning null");
			throw new RuntimeException("IResponse type expected for "
					+ responseObj);
		}
	}

	@Override
	/****
	 * Async events are not blocking on the PEP side, therefore they can only be actual events.
	 * 
	 */
	public void processEventAsync(Event e) throws TException {
		if (e == null) {
			_logger.error("received null event, replying with null response");
			return;
		}

		IEvent ev = new EventBasic(e.name, e.parameters, true);
		_logger.trace("PDP received asynchronous thrift event " + e.name);
		processEvent(ev);
		return;
	}

	@Override
	/****
	 * Async events are not blocking on the PEP side, therefore they can only be actual events.
	 * As of now, we assume the sync (=blocking) events are ONLY DESIRED events, but for synchronization issues it may be the case that we need to make also the actual events synchronous.
	 * Note that, however, the events are processed in the same order in which they are received by the queue.
	 * This means that even if the actual event is not blocking, all the actual events will be processed in the same order they have been received and in case of a desired event, th event won't be allowed to be exectued until all the async events before have been processed
	 */
	public Response processEventSync(Event e) throws TException {
		if (e == null) {
			_logger.error("received null event, replying with null response");
			return null;
		}

		IEvent ev = new EventBasic(e.name, e.parameters, false);
		_logger.trace("PDP received synchronous thrift event " + e.name);

		IResponse response = processEvent(ev);

		_logger.trace("Response to return: " + response);

		Response finalThriftResponse;

		switch (response.getAuthorizationAction().getEStatus()) {
		case INHIBIT:
			finalThriftResponse = new Response(StatusType.INHIBIT);
			break;
		case ALLOW:
			finalThriftResponse = new Response(StatusType.ALLOW);

			// TODO: This needs to be changed. The correct behavior in theory is
			// that the PEP re-send another actual event when things actually
			// happen. For the time being, we pretend everything goes fine when
			// we allow and thus we notify the actual event to the pip.
			_logger.trace("Event " + e.name+" is allowed. Let's notify the PIP about it.");
			throwAwayResponse();
			processEvent(new EventBasic(e.name, e.parameters, true));
			break;
		case MODIFY:
			// TODO: Add modification action cause it is not supported on
			// the PEP side yet
			finalThriftResponse = new Response(StatusType.MODIFY);
			break;
		default:
			finalThriftResponse = new Response(StatusType.ERROR);
			finalThriftResponse.setComment("Error. Answer is " + response);
		}
		throwAwayResponse();
		return finalThriftResponse;
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		_logger.debug("Thrift doProcessing invoked");
		
		// TODO Auto-generated method stub

	}
}