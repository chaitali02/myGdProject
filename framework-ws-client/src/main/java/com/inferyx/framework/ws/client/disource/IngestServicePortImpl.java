
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.inferyx.framework.ws.client.disource;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.1
 * 2019-04-12T07:17:56.015+05:30
 * Generated source version: 3.3.1
 *
 */

@javax.jws.WebService(
                      serviceName = "IngestServiceService",
                      portName = "IngestServicePort",
                      targetNamespace = "http://ingestion.ws.com/",
                      wsdlLocation = "http://localhost:9000/Ingest?wsdl",
                      endpointInterface = "com.inferyx.framework.ws.client.disource.IngestService")

public class IngestServicePortImpl implements IngestService {

    private static final Logger LOG = Logger.getLogger(IngestServicePortImpl.class.getName());

    /* (non-Javadoc)
     * @see com.inferyx.framework.ws.client.disource.IngestService#stream()*
     */
    public java.util.List<java.lang.String> stream() {
        LOG.info("Executing operation stream");
        try {
            java.util.List<java.lang.String> _return = new java.util.ArrayList<java.lang.String>();
            java.lang.String _returnVal1 = "_returnVal71519012";
            _return.add(_returnVal1);
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
