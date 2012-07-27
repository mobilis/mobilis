package de.tudresden.inf.rn.mobilis.xmpp.server;

import de.tudresden.inf.rn.mobilis.xmpp.beans.XMPPBean;

public class BeanHelper {
	
	public static XMPPBean CreateErrorBean(XMPPBean inBean, String errorType,
			String errorCondition, String errorText){
		XMPPBean out = inBean.clone();
		
		out.setTo(inBean.getFrom());
		out.setFrom(inBean.getTo());
		out.setType(XMPPBean.TYPE_ERROR);
		out.setId( inBean.getId() );
		
		out.errorType = errorType;
		out.errorCondition = errorCondition;
		out.errorText = errorText;
		
		return out;
	}

	public static XMPPBean CreateResultBean(XMPPBean inBean, XMPPBean resultBean){		
		resultBean.setTo(inBean.getFrom());
		resultBean.setFrom(inBean.getTo());
		resultBean.setType(XMPPBean.TYPE_RESULT);
		resultBean.setId(inBean.getId());
		
		return resultBean;
	}
}
