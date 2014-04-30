package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.io.File;

/**
 * @author cmdaltent
 */
public class IFFReaderFactory {

    private String _interfaceFile;

    public IFFReaderFactory(final String interfaceFile)
    {
        if (interfaceFile == null) throw new IllegalArgumentException("Interface File is not allowed to be null.");

        _interfaceFile = interfaceFile;
    }

    public IFFReader getIFFReader()
    {
        String[] fileNameComponents = _interfaceFile.split("\\.");
        if (fileNameComponents.length < 2) return null;

        String fileExtensions = fileNameComponents[fileNameComponents.length-1];

        if (fileExtensions.equalsIgnoreCase("msdl"))
            return new MSDLReader();
        else if (fileExtensions.equalsIgnoreCase("xpd"))
            return new XPDReader();
        else return null;
    }

}
