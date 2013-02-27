package de.inf.tudresden.rn.mobilis.mxaonfire.service;

import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.ByteStream;

public class FileTransferDescriber {

	public IFileAcceptCallback mFileAcceptCallback;
	public ByteStream mFile;
	public String mStreamID;
}
