package de.tum.in.i22.uc.cm.factories;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PxpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PxpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;

public interface ClientHandlerFactory {
	Any2PdpClient createPdpClientHandler(Location location);
	Any2PipClient createPipClientHandler(Location location);
	Any2PmpClient createPmpClientHandler(Location location);
	Any2PxpClient createPxpClientHandler(Location location);

	Pep2PipClient createPep2PipClient(Location location);
	Pdp2PipClient createPdp2PipClient(Location location);
	Pip2PipClient createPip2PipClient(Location location);
	Pmp2PipClient createPmp2PipClient(Location location);

	Pep2PdpClient createPep2PdpClient(Location location);
	Pmp2PdpClient createPmp2PdpClient(Location location);

	Pip2PmpClient createPip2PmpClient(Location location);
	Pmp2PmpClient createPmp2PmpClient(Location location);

	Pdp2PxpClient createPdp2PxpClient(Location location);
}