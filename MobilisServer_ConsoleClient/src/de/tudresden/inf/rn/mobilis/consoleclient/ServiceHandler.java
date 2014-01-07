package de.tudresden.inf.rn.mobilis.consoleclient;

import de.tudresden.inf.rn.mobilis.consoleclient.helper.StatusInformation;
import de.tudresden.inf.rn.mobilis.consoleclient.helper.StatusType;
import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;
import de.tudresden.inf.rn.mobilis.xmpp.beans.deployment.PrepareServiceUploadBean;
import de.tudresden.inf.rn.mobilis.xmpp.mxj.BeanSenderReceiver;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * @author cmdaltent
 *
 */
public class ServiceHandler extends Observable {

    private Controller _controller;

    public ServiceHandler(final Controller controller) {
        this(controller, null);
    }

    public ServiceHandler(final Controller controller, Observer observer) {
        _controller = controller;
        if (observer != null) addObserver(observer);
    }

    public void sendFile(String filepath, boolean autoDeploy, boolean singleMode){
        File file = new File(filepath);

        setChanged();
        if(file.exists()){
            if (sendPrepareFile(file.getName(), autoDeploy, singleMode)) {
                boolean success = _controller.getConnection().transmitFile(
                        file,
                        "",
                        _controller.getSettings().getMobilisDeploymentJid() );

                if( success )
                    notifyObservers(new StatusInformation(StatusType.INFORMATION, "File transfer successful"));
                else notifyObservers(new StatusInformation(StatusType.ERROR, "File transfer not successful"));
            } else  notifyObservers(new StatusInformation(StatusType.ERROR, "Could not prepare file upload"));
        } else notifyObservers(new StatusInformation(StatusType.WARNING, "No such file was found."));
    }
    private boolean sendPrepareFile(String filename, boolean autoDeploy, boolean singleMode){
        PrepareServiceUploadBean bean = new PrepareServiceUploadBean(filename);
        bean.setTo( _controller.getSettings().getMobilisDeploymentJid() );
        bean.autoDeploy = autoDeploy;
        bean.singleMode = singleMode;
        bean.setType( XMPPBean.TYPE_SET );

        BeanSenderReceiver<PrepareServiceUploadBean, PrepareServiceUploadBean> bsr = new BeanSenderReceiver<PrepareServiceUploadBean, PrepareServiceUploadBean>(_controller.getConnection().getXMPPConnection());
        XMPPBean result = bsr.exchange(bean, new PrepareServiceUploadBean(), 0);
        return result != null && result.getType() != XMPPBean.TYPE_ERROR && ((PrepareServiceUploadBean) result).AcceptServiceUpload;
    }
}
