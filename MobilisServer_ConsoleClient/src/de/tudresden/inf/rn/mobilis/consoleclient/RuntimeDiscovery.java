package de.tudresden.inf.rn.mobilis.consoleclient;

import de.tudresden.inf.rn.mobilis.consoleclient.helper.FeatureInterpreter;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;

import java.util.Iterator;
import java.util.Map;

/**
 * @author cmdaltent
 */
public class RuntimeDiscovery {

    private Connection _connection;
    private String _jid;

    private Map<String, String> _features;

    public RuntimeDiscovery(final Connection connection, final String jid)
    {
        if (connection == null || jid == null) return;

        _connection = connection;
        _jid = jid;
    }

    public void performRuntimeDiscovery()
    {
        ServiceDiscoveryManager manager = ServiceDiscoveryManager.getInstanceFor(_connection.getXMPPConnection());

        DiscoverInfo discoverInfo;
        try {
            discoverInfo = manager.discoverInfo(_jid);
        } catch (XMPPException e) {
            e.printStackTrace();
            return;
        }

        Iterator<DiscoverInfo.Feature> features = discoverInfo.getFeatures();
        while (features.hasNext())
        {
            DiscoverInfo.Feature feature = features.next();
            String variable = feature.getVar();
            Map<String, String> featureMap = (new FeatureInterpreter(variable)).get_features();
            if (!featureMap.isEmpty())
            {
                _features = featureMap;
            }
        }
    }

    public Boolean isJavaRuntime()
    {
        return _features.get("servicelanguage").equalsIgnoreCase("java");
    }
}
