
package com.inferyx.framework.ws.client.disource;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.inferyx.framework.ws.client.disource package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Stream_QNAME = new QName("http://ingestion.ws.com/", "stream");
    private final static QName _StreamResponse_QNAME = new QName("http://ingestion.ws.com/", "streamResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.inferyx.framework.ws.client.disource
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Stream }
     * 
     */
    public Stream createStream() {
        return new Stream();
    }

    /**
     * Create an instance of {@link StreamResponse }
     * 
     */
    public StreamResponse createStreamResponse() {
        return new StreamResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Stream }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Stream }{@code >}
     */
    @XmlElementDecl(namespace = "http://ingestion.ws.com/", name = "stream")
    public JAXBElement<Stream> createStream(Stream value) {
        return new JAXBElement<Stream>(_Stream_QNAME, Stream.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StreamResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link StreamResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ingestion.ws.com/", name = "streamResponse")
    public JAXBElement<StreamResponse> createStreamResponse(StreamResponse value) {
        return new JAXBElement<StreamResponse>(_StreamResponse_QNAME, StreamResponse.class, null, value);
    }

}
