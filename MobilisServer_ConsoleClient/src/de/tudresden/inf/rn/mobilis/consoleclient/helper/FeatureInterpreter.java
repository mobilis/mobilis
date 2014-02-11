package de.tudresden.inf.rn.mobilis.consoleclient.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cmdaltent
 */
public class FeatureInterpreter {

    private String _featureString;
    private Map<String,String> _features;

    public FeatureInterpreter(final String featureString)
    {
        if ("".equalsIgnoreCase(featureString)) return;

        _featureString = featureString;
        evaluate();
    }
    private void evaluate() {
        String[] components = _featureString.split(",");
        _features = new HashMap<>(components.length);
        for (String component : components) {
            String[] keyValue = component.split("=");
            if (keyValue.length != 2) continue;
            _features.put(keyValue[0], keyValue[1]);
        }
    }

    public String get_featureString() {
        return _featureString;
    }

    public Map<String, String> get_features() {
        return _features;
    }

    public void set_featureString(String _featureString) {
        this._featureString = _featureString;
    }

    public void set_features(Map<String, String> _features) {
        this._features = _features;
    }
}
