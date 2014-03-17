package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Florian Kelbert
 *
 */
class ConnectionPool {
	private static final int DEFAULT_MAX_ENTRIES = 20;

//	private static final ConnectionPool _instance = new ConnectionPool(DEFAULT_MAX_ENTRIES);

	private static final PoolMap pool = new PoolMap(DEFAULT_MAX_ENTRIES);

//	private ConnectionPool() {
//		this(DEFAULT_MAX_ENTRIES);
//	}
//
//	private ConnectionPool(int maxEntries) {
//		pool ;
//	}
//
//	static ConnectionPool getInstance() {
//		return _instance;
//	}


	static AbstractConnection obtainConnection(AbstractConnection connection) throws IOException {
		if (connection == null) {
			throw new NullPointerException("No connection provided.");
		}

		synchronized (pool) {
			InUse inuse = null;

			while (inuse != InUse.NO) {
				inuse = pool.get(connection);

				if (inuse == null) {
					// connection is not known
					pool.put(connection, InUse.NO);
					connection.connect();
					inuse = InUse.NO;
				}
				else if (inuse == InUse.YES) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			pool.put(connection, InUse.YES);
		}

		return connection;
	}

	static void releaseConnection(AbstractConnection connection) throws IOException {
		synchronized (pool) {
			if (pool.get(connection) == null) {
				// It may be the case that the connection has been removed from the pool
				// while it was in use. In this case we do not add the new state but disconnect() silently.
				connection.disconnect();
			}
			else {
				pool.put(connection, InUse.NO);
				pool.notifyAll();
			}
		}
	}


	enum InUse {
		YES,
		NO;
	}


	private static class PoolMap implements Map<AbstractConnection, InUse> {
		private final int _maxEntries;

		private final Map<AbstractConnection, InUse> _backMap;

		public PoolMap(int maxEntries) {
			_maxEntries = maxEntries;
			_backMap = Collections.synchronizedMap(new LinkedHashMap<AbstractConnection, InUse>(maxEntries, 0.75f, true) {
				private static final long serialVersionUID = -3139938026277477159L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<AbstractConnection,InUse> eldest) {
					boolean result = size() > _maxEntries;
					if (result) {
						synchronized (pool) {
							// When removing an entry, we need to disconnect the connection.
							// Yet, we only do this if the connection is currently not used.
							// If the connection is currently used, it will be disconnect as
							// soon as it is released, cf. releaseConnection().
							if (eldest.getValue() == InUse.NO) {
								eldest.getKey().disconnect();
							}
						}
					}
					return result;
				}

			});
		}


		@Override
		public int size() {
			return _backMap.size();
		}

		@Override
		public boolean isEmpty() {
			return _backMap.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return _backMap.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return _backMap.containsValue(value);
		}

		@Override
		public InUse get(Object key) {
			return _backMap.get(key);
		}

		@Override
		public InUse put(AbstractConnection key, InUse value) {
			return _backMap.put(key, value);
		}

		@Override
		public InUse remove(Object key) {
			return _backMap.remove(key);
		}

		@Override
		public void putAll(Map<? extends AbstractConnection, ? extends InUse> m) {
			_backMap.putAll(m);
		}

		@Override
		public void clear() {
			_backMap.clear();
		}

		@Override
		public Set<AbstractConnection> keySet() {
			return _backMap.keySet();
		}

		@Override
		public Collection<InUse> values() {
			return _backMap.values();
		}

		@Override
		public Set<java.util.Map.Entry<AbstractConnection, InUse>> entrySet() {
			return _backMap.entrySet();
		}
	}
}
