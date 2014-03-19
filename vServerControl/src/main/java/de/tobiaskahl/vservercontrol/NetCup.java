package de.tobiaskahl.vservercontrol;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.tobiaskahl.vservercontrol.exceptions.IncorrectUserDataException;
import de.tobiaskahl.vservercontrol.exceptions.LeseException;

/**
 * Anwendungsweiter Direktor für die deligierung von Datenzugriffen.
 *
 * @author Tobias Kahl (mail@tobiaskahl.de)
 */
public class NetCup implements HosterAPI {

    private static final String LOGGER_TAG = "NetCup";
    private static final String NAMESPACE = "http://enduser.service.web.vcp.netcup.de/";
    private static final String URL = "https://www.vservercontrolpanel.de/WSEndUser";

    public NetCup() {
        super();
    }

    protected String getUrl(){
        return URL;
    }

    private final Object callWebservice(final SoapObject soapObject, final String methode) throws LeseException {
        try {
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);
            final HttpTransportSE httpTransportSE = new HttpTransportSE(getUrl());
            httpTransportSE.call(NAMESPACE + methode, envelope);
            return envelope.getResponse();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    private final SoapObject getAnfrageSoapObject(final String methode) {
        return new SoapObject(NAMESPACE, methode);
    }

    private final String getBytesAsString(final long bytes) {
        final long kiloBytes = bytes / 1024;
        final long megaBytes = kiloBytes / 1024;
        final long gigaBytes = megaBytes / 1024;
        if (gigaBytes > 0) {
            return gigaBytes + " GB";
        }
        if (megaBytes > 0) {
            return megaBytes + " MB";
        }
        if (kiloBytes > 0) {
            return kiloBytes + " KB";
        }
        return bytes + " B";
    }

    @Override
    public final List<String> getVServerIPs(final String username, final String password,
                                            final String vserverName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerIPs(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerIPs";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            final Object response = callWebservice(soapObject, METHODE);
            //
            final ArrayList<String> liste = new ArrayList<String>();
            if (response instanceof SoapPrimitive) {
                liste.add(response.toString());
            } else if (response instanceof Vector) {
                @SuppressWarnings("unchecked")
                final Vector<SoapPrimitive> vector = (Vector<SoapPrimitive>) response;
                for (final SoapPrimitive ip : vector) {
                    liste.add(ip.toString());
                }
            }
            return liste;
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final String getVServerLoad(final String username, final String password, final String vserverName)
            throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerLoad(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerLoad";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public String getVServerNickname(final String username, final String password, final String vserverName)
            throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerNickname(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerNickname";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vservername", vserverName);
            Object object = callWebservice(soapObject, METHODE);
            if (object instanceof SoapPrimitive) {
                final SoapPrimitive response = (SoapPrimitive) object;
                return response.toString();
            }
            return "";
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final String getVServerProcesses(final String username, final String password,
                                            final String vserverName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerProcesses(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerProcesses";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final String getVServerState(final String username, final String password, final String vserverName)
            throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerState(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerState";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final String getVServerTrafficOfDay(final String username, final String password,
                                               final String vserverName, final int year, final int month, final int day) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerTrafficOfDay(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\", year=\"" + year + "\", month=\"" + month + "\", day=\""
                    + day + "\"");
            //
            final String METHODE = "getVServerTrafficOfDay";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            soapObject.addProperty("year", year);
            soapObject.addProperty("month", month);
            soapObject.addProperty("day", day);
            final SoapObject response = (SoapObject) callWebservice(soapObject, METHODE);
            final long byteIn = Long.parseLong(response.getPropertyAsString("in"));
            final long byteOut = Long.parseLong(response.getPropertyAsString("out"));
            final long byteTotal = Long.parseLong(response.getPropertyAsString("total"));
            final StringBuilder builder = new StringBuilder();
            builder.append("In: ");
            builder.append(getBytesAsString(byteIn));
            builder.append(", Out: ");
            builder.append(getBytesAsString(byteOut));
            builder.append(", Total: ");
            builder.append(getBytesAsString(byteTotal));
            return builder.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final List<String> getVServers(final String username, final String password)
            throws IncorrectUserDataException, LeseException {
        Log.i(LOGGER_TAG, "getVServers(username=\"" + username + "\", password=\"" + password + "\"");
        //
        final String METHODE = "getVServers";
        final SoapObject soapObject = getAnfrageSoapObject(METHODE);
        soapObject.addProperty("loginName", username);
        soapObject.addProperty("password", password);
        final Object response = callWebservice(soapObject, METHODE);
        //
        final ArrayList<String> liste = new ArrayList<String>();
        if (response instanceof SoapPrimitive) {
            final SoapPrimitive primitive = (SoapPrimitive) response;
            if (primitive.getAttributeCount() == 0) {
                final String stringResult = primitive.toString();
                if (stringResult.equals("wrong password")) {
                    throw new IncorrectUserDataException(username);
                } else if (stringResult.contains("error")) {
                    throw new LeseException(primitive.toString());
                } else if (stringResult.startsWith("v")) {
                    liste.add(stringResult);
                }
            }
        } else if (response instanceof SoapObject) {
            final SoapObject object = (SoapObject) response;
            for (int i = 0; i < object.getPropertyCount(); i++) {
                liste.add(object.getPropertyAsString(i));
            }
        }
        return liste;
    }

    @Override
    public final String getVServerTrafficOfMonth(final String username, final String password,
                                                 final String vserverName, final int year, final int month) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerTrafficOfMonth(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\", year=\"" + year + "\", month=\"" + month + "\"");
            //
            final String METHODE = "getVServerTrafficOfMonth";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            soapObject.addProperty("year", year);
            soapObject.addProperty("month", month);
            final SoapObject response = (SoapObject) callWebservice(soapObject, METHODE);
            final long byteIn = Long.parseLong(response.getPropertyAsString("in"));
            final long byteOut = Long.parseLong(response.getPropertyAsString("out"));
            final long byteTotal = Long.parseLong(response.getPropertyAsString("total"));
            final StringBuilder builder = new StringBuilder();
            builder.append("In: ");
            builder.append(getBytesAsString(byteIn));
            builder.append(", Out: ");
            builder.append(getBytesAsString(byteOut));
            builder.append(", Total: ");
            builder.append(getBytesAsString(byteTotal));
            return builder.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final String getVServerUptime(final String username, final String password, final String vserverName)
            throws LeseException {
        try {
            Log.i(LOGGER_TAG, "getVServerUptime(username=\"" + username + "\", password=\"" + password
                    + "\", vserverName=\"" + vserverName + "\"");
            //
            final String METHODE = "getVServerUptime";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", vserverName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString();
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final boolean restart(String username, String password, String verserName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "restart(server=\"" + username + "\", password=\"" + password + "\" vserverName" + verserName + "\"");
            //
            final String METHODE = "vServerACPIReboot";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", verserName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString().equalsIgnoreCase("true");
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final boolean start(String username, String password, String verserName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "start(server=\"" + username + "\", password=\"" + password + "\" vserverName" + verserName + "\"");
            //
            final String METHODE = "vServerStart";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", verserName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString().equalsIgnoreCase("true");
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final boolean stop(String username, String password, String verserName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "stop(server=\"" + username + "\", password=\"" + password + "\" vserverName" + verserName + "\"");
            //
            final String METHODE = "vServerACPIShutdown";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", verserName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString().equalsIgnoreCase("true");
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final boolean reset(String username, String password, String verserName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "reset(server=\"" + username + "\", password=\"" + password + "\" vserverName" + verserName + "\"");
            //
            final String METHODE = "vServerReset";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", verserName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString().equalsIgnoreCase("true");
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }

    @Override
    public final boolean powerOff(String username, String password, String verserName) throws LeseException {
        try {
            Log.i(LOGGER_TAG, "powerOff(username=\"" + username + "\", password=\"" + password + "\" vserverName" + verserName + "\"");
            //
            final String METHODE = "vServerPoweroff";
            final SoapObject soapObject = getAnfrageSoapObject(METHODE);
            soapObject.addProperty("loginName", username);
            soapObject.addProperty("password", password);
            soapObject.addProperty("vserverName", verserName);
            final SoapPrimitive response = (SoapPrimitive) callWebservice(soapObject, METHODE);
            return response.toString().equalsIgnoreCase("true");
        } catch (final Throwable e) {
            throw new LeseException(e);
        }
    }
}