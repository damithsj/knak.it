package org.wso2.carbon.esb.connector;

import ifs.fnd.ap.*;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.esb.connector.util.constants;
import org.wso2.carbon.esb.connector.util.ifsUtil;
import org.wso2.carbon.esb.connector.util.resultPayloadCreate;
import org.wso2.carbon.esb.connector.util.xmlUtil;

public class plsqlBaseMethod extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        String plsqlPackage = xmlUtil.getElementFromXmlRequest(messageContext, constants.PLSQLPACKAGE);
        PlsqlBaseMethodType methodType = ifsUtil.getPlsqlBaseMethodType(messageContext);
        String methodName = xmlUtil.getElementFromXmlRequest(messageContext,constants.METHODNAME);

        if (methodName == constants.NULL) methodName = methodType.getDefaultMethodName();

        PlsqlBaseMethodAction methodAction = ifsUtil.getPlsqlBaseMethodAction(messageContext);
        Record recordAttr = ifsUtil.getRecordAttr(messageContext);

        try {
            Server srv = ifsUtil.connect(messageContext);
            PlsqlBaseMethodCommand cmd = new PlsqlBaseMethodCommand(srv, methodType, plsqlPackage, methodName, recordAttr, methodAction);
            cmd.execute();

            RecordCollection recCollection = new RecordCollection();
            recCollection.add(recordAttr);

            OMElement resultOM = xmlUtil.generateResultXML(recCollection);
            resultPayloadCreate.preparePayload(messageContext, resultOM);

        } catch (APException e) {
            //log.error("error while executing PL/SQL Base Method. Error:" + e.getMessage());
            //throw new SynapseException("error while executing PL/SQL Base Method", e);
            OMElement errorOM = xmlUtil.generateErrorXML("APException", e.getMessage());
            resultPayloadCreate.preparePayload(messageContext, errorOM);
        }


    }
}
