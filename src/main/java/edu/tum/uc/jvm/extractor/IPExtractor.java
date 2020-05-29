package edu.tum.uc.jvm.extractor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IPExtractor implements IExtractor {
	private Map<String, String> _return;

	public IPExtractor() {
		_return = new HashMap<String, String>();
	}

	/**
	 * Extracts the file descriptor from object parameter obj
	 * and add it to the mapping
	 * @param obj
	 * @return
	 */
	@Override
	public Map<?,?> extract(Object obj) {
		_return.clear();
		String osSystem = System.getProperty("os.name");
		try {
			if ((obj instanceof Writer) || (obj instanceof Reader)) {
				java.util.List<java.lang.reflect.Field> oAttr = getAllFields(obj);
				Iterator<java.lang.reflect.Field> oIt = oAttr.iterator();
				while (oIt.hasNext()) {
					java.lang.reflect.Field oField = oIt.next();
					// Find attribute "fd" -> FileDescriptor of lock
					if ("out".equals(oField.getName().toLowerCase())) {
						oField.setAccessible(true);
						Object oOut = oField.get(obj);
						Iterator<java.lang.reflect.Field> oOutIt = getAllFields(oOut).iterator();
						while(oOutIt.hasNext()){
							java.lang.reflect.Field oOutField = oOutIt.next();
							if("lock".equals(oOutField.getName().toLowerCase())){
								oOutField.setAccessible(true);
								Object oOutLock = oOutField.get(oOut);
								Iterator<java.lang.reflect.Field> oOutLockIt = getAllFields(oOutLock).iterator();
								while(oOutLockIt.hasNext()){
									java.lang.reflect.Field oOutLockField = oOutLockIt.next();
									if("lock".equals(oOutLockField.getName().toLowerCase())){
										oOutLockField.setAccessible(true);
										Object oOutLockLock = oOutLockField.get(oOutLock);
										this.extract(oOutLockLock);										
									}
								}								
							}
						}
					}
				}
			}
			else if ((obj instanceof OutputStream) || (obj instanceof InputStream)) {
				java.util.List<java.lang.reflect.Field> oAttr = getAllFields(obj);
				Iterator<java.lang.reflect.Field> oIt = oAttr.iterator();
				while (oIt.hasNext()) {
					java.lang.reflect.Field oField = oIt.next();
					// Find attribute "fd" -> FileDescriptor of lock
					if ("impl".equals(oField.getName().toLowerCase())) {
						oField.setAccessible(true);
						Object oImpl = oField.get(obj);
							Iterator<java.lang.reflect.Field> oImplIt = getAllFields(oImpl).iterator();
							// Find attribue "int fd" -> contains
							// the file descriptor id from os
							while (oImplIt.hasNext()) {
								java.lang.reflect.Field oImplField = oImplIt.next();
								if ("port".equals(oImplField.getName().toLowerCase())) {
									oImplField.setAccessible(true);
									Object port = oImplField.get(oImpl);									
									_return.put("port", String.valueOf(port));
								}
								else if ("localport".equals(oImplField.getName().toLowerCase())) {
									oImplField.setAccessible(true);
									Object localport = oImplField.get(oImpl);									
									_return.put("localport", String.valueOf(localport));
								}
								else if ("address".equals(oImplField.getName().toLowerCase())) {
									oImplField.setAccessible(true);
									Object addressFields = oImplField.get(oImpl);		
									//2130706433 IPAddressUtil.
									Iterator<java.lang.reflect.Field> oImplAddressFieldIt = getAllFields(addressFields).iterator();
									while(oImplAddressFieldIt.hasNext()){
										java.lang.reflect.Field oImplAddressField = oImplAddressFieldIt.next();
										if("holder".equals(oImplAddressField.getName().toLowerCase())){
											oImplAddressField.setAccessible(true);
											Object holder = oImplAddressField.get(addressFields);
											Iterator<java.lang.reflect.Field> oImplAddressHolderFieldIt = getAllFields(holder).iterator();
											while(oImplAddressHolderFieldIt.hasNext()){
												java.lang.reflect.Field oImplAddressHolderField = oImplAddressHolderFieldIt.next();
												if("address".equals(oImplAddressHolderField.getName().toLowerCase())){
													oImplAddressHolderField.setAccessible(true);
													Object ipAddr = oImplAddressHolderField.get(holder);
													_return.put("ipAddress", Integer.toHexString((int) ipAddr));
												}
											}
										}
									}
								}
							}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _return;
	}

	// return all field from an object
	private List<Field> getAllFields(Object obj) {
		ArrayList<Field> _return = new ArrayList<Field>();
		Class<?> clazz = obj.getClass();
		while (clazz != null && clazz != Object.class) {
			for (Field f : clazz.getDeclaredFields())
				_return.add(f);
			clazz = clazz.getSuperclass();
		}
		return _return;
	}

}
