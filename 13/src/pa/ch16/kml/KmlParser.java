package pa.ch16.kml;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import pa.ch16.Ch16;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very basic KML parser to meet the need of the emulator control panel.
 * <p/>
 * It parses basic Placemark information.
 */
public class KmlParser {
    private final static String NS_KML_2 =
            "http://earth.google.com/kml/2.";  //$NON-NLS-1$

    private final static String NODE_PLACEMARK = "Placemark"; //$NON-NLS-1$
    private final static String NODE_NAME = "name"; //$NON-NLS-1$
    private final static String NODE_COORDINATES = "coordinates"; //$NON-NLS-1$

    private final static Pattern sLocationPattern =
            Pattern.compile("([^,]+),([^,]+)(?:,([^,]+))?");

    private static SAXParserFactory sParserFactory;

    static {
        sParserFactory = SAXParserFactory.newInstance();
        sParserFactory.setNamespaceAware(true);
    }

    private KmlHandler mHandler;
    private InputStream mInput;

    public boolean isEmpty() {
        return (mHandler == null) || mHandler.isEmpty;
    }

    /**
     * Handler for the SAX parser.
     */
    private static class KmlHandler extends DefaultHandler {
        private boolean isEmpty = true;

        // --------- parsed data ---------
        List<WayPoint> mWayPoints;

        // --------- state for parsing ---------
        WayPoint mCurrentWayPoint;
        final StringBuilder mStringAccumulator = new StringBuilder();

        boolean mSuccess = true;

        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes)
                throws SAXException
        {
            isEmpty = false;

            // we only care about the standard GPX nodes.
            try {
                if (uri.startsWith(NS_KML_2)) {
                    if (NODE_PLACEMARK.equals(localName)) {
                        if (mWayPoints == null) {
                            mWayPoints = new ArrayList<WayPoint>();
                        }

                        Log.d(Ch16.LOG_TAG, "new waypoint");

                        mCurrentWayPoint = new WayPoint();
                        mWayPoints.add(mCurrentWayPoint);
                    }
                }
            } finally {
                // no matter the node, we empty the StringBuilder accumulator
                // when we start a new node.
                mStringAccumulator.setLength(0);
            }
        }

        /**
         * Processes new characters for the node content. The characters are
         * simply stored, and will be processed when {@link #endElement(String,
         * String, String)} is called.
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException
        {
            mStringAccumulator.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException
        {
            if (uri.startsWith(NS_KML_2)) {
                if (NODE_PLACEMARK.equals(localName)) {
                    mCurrentWayPoint = null;
                } else if (NODE_NAME.equals(localName)) {
                    if (mCurrentWayPoint != null) {
                        mCurrentWayPoint.setName(mStringAccumulator.toString());
                        Log.d(Ch16.LOG_TAG, "waypoint set name: " +
                                mCurrentWayPoint.getName());

                    }
                } else if (NODE_COORDINATES.equals(localName)) {
                    if (mCurrentWayPoint != null) {
                        parseLocation(mCurrentWayPoint,
                                mStringAccumulator.toString());
                    }
                }
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            mSuccess = false;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            mSuccess = false;
        }

        /**
         * Parses the location string and store the information into a
         * {@link LocationPoint}.
         * @param locationNode the {@link LocationPoint} to receive the
         *  location data.
         * @param location The string containing the location info.
         */
        private void parseLocation(LocationPoint locationNode,
                                   String location)
        {
            Matcher m = sLocationPattern.matcher(location);
            if (m.matches()) {
                try {
                    double longitude = Double.parseDouble(m.group(1));
                    double latitude = Double.parseDouble(m.group(2));
                    locationNode.setLocation(longitude, latitude);

                    if (m.groupCount() == 3) {
                        // looks like we have elevation data.
                        locationNode.
                                setElevation(Double.parseDouble(m.group(3)));
                    }
                } catch (NumberFormatException e) {
                    // wrong data, do nothing.
                }
            }
        }

        WayPoint[] getWayPoints() {
            if (mWayPoints != null) {
                return mWayPoints.toArray(new WayPoint[mWayPoints.size()]);
            }

            return new WayPoint[]{};
        }

        boolean getSuccess() {
            return mSuccess;
        }
    }

    /**
     * Creates a new GPX parser for a file specified by its full path.
     * @param input InputStream of the GPX file to parse.
     */
    public
    KmlParser(InputStream input) {
        mInput = input;
    }

    /**
     * Parses the GPX file.
     * @return <code>true</code> if success.
     */
    public boolean parse() {
        try {
            SAXParser parser = sParserFactory.newSAXParser();

            mHandler = new KmlHandler();

            parser.parse(new InputSource(new InputStreamReader(mInput)),
                    mHandler);

            return mHandler.getSuccess();
        } catch (ParserConfigurationException e) {
            Log.d(Ch16.LOG_TAG, e.getMessage());
        } catch (SAXException e) {
            Log.d(Ch16.LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(Ch16.LOG_TAG, e.getMessage());
        }

        return false;
    }

    /**
     * Returns the parsed {@link WayPoint} objects, or <code>null</code> if
     * none were found (or if the parsing failed.
     */
    public WayPoint[] getWayPoints() {
        if (mHandler != null) {
            return mHandler.getWayPoints();
        }

        return null;
    }
}
