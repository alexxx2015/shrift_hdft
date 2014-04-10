package de.tum.in.i22.uc.thrift.client;

import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.client.Any2PxpClient;
import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.Pdp2PipClient;
import de.tum.in.i22.uc.cm.client.Pdp2PxpClient;
import de.tum.in.i22.uc.cm.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.client.Pep2PipClient;
import de.tum.in.i22.uc.cm.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.client.Pip2PmpClient;
import de.tum.in.i22.uc.cm.client.Pmp2PdpClient;
import de.tum.in.i22.uc.cm.client.Pmp2PipClient;
import de.tum.in.i22.uc.cm.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;


public class ThriftClientFactory implements ClientHandlerFactory {

	@Override
	public Any2PdpClient createPdpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PipClient createPipClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PmpClient createPmpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PxpClient createPxpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PxpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pep2PipClient createPep2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPep2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pdp2PipClient createPdp2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdp2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pip2PipClient createPip2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPip2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PipClient createPmp2PipClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pep2PdpClient createPep2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPep2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PdpClient createPmp2PdpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pip2PmpClient createPip2PmpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPip2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pmp2PmpClient createPmp2PmpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmp2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Pdp2PxpClient createPdp2PxpClient(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdp2PxpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}
}