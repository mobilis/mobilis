package de.inf.tudresden.rn.mobilis.mxaonfire.service;

import java.util.ArrayList;

import de.tudresden.inf.rn.mobilis.mxa.services.callbacks.IFileAcceptCallback;
import de.tudresden.inf.rn.mobilis.mxa.services.parcelable.ByteStream;

public class FileTransferManager {
	private static FileTransferManager instance= new FileTransferManager();
	public static FileTransferManager get()
	{
		return instance;
	}
	
	private ArrayList<FileTransferDescriber> mList= new ArrayList<FileTransferDescriber>(); 
	
	public void insert(IFileAcceptCallback cb, ByteStream f, String id)
	{
		FileTransferDescriber ftd=new FileTransferDescriber();
		ftd.mFile=f;
		ftd.mFileAcceptCallback=cb;
		ftd.mStreamID=id;
		mList.add(ftd);
	}
	
	public FileTransferDescriber getFileTransfer()
	{
		if (mList.size()==0) return null;
		FileTransferDescriber result=mList.get(0);
		if (result!=null) mList.remove(0);
		return result;
	}
}
