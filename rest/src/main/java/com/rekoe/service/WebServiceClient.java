package com.rekoe.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Document;

public class WebServiceClient {

	private final static Log log = Logs.get();

	String nameSpace = "";
	String wsdlUrl = "";
	String serviceName = "";
	String portName = "";
	String responseName = "";
	String elementName = "";
	int timeout = 6000;

	public WebServiceClient(String nameSpace, String wsdlUrl, String serviceName, String portName, String element, String responseName) {
		this.nameSpace = nameSpace;
		this.wsdlUrl = wsdlUrl;
		this.serviceName = serviceName;
		this.portName = portName;
		this.elementName = element;
		this.responseName = responseName;
	}

	public String sendMessage(HashMap<String, String> inMsg, String... names) throws Exception {
		try {
			SOAPMessage msg = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
			msg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
			SOAPBody body = envelope.getBody();
			QName ename = new QName(nameSpace, elementName, "soapenv");
			final SOAPBodyElement ele = body.addBodyElement(ename);
			if (Lang.isEmptyArray(names)) {
				for (Map.Entry<String, String> entry : inMsg.entrySet()) {
					ele.addChildElement(new QName(nameSpace, entry.getKey())).setValue(entry.getValue());
				}
			} else {
				Lang.each(names, new Each<String>() {
					@Override
					public void invoke(int index, String name, int length) throws ExitLoop, ContinueLoop, LoopException {
						try {
							if (StringUtils.isNotBlank(inMsg.get(name))) {
								ele.addChildElement(new QName(nameSpace, name)).setValue(inMsg.get(name));
							} else {
								System.err.println(name);
							}
						} catch (SOAPException e) {
							log.error(e);
						}
					}
				});
			}
			Document docc = body.getOwnerDocument();
			String xml = toStringFromDoc(docc);
			Header header = Header.create();
			header.set("SOAPAction", this.wsdlUrl + "/" + this.elementName);
			header.set("Content-Type", "text/xml; charset=UTF-8");
			Response response = Http.post3(this.wsdlUrl, xml, header, this.timeout);
			Document doc = Lang.xmls().parse(response.getStream());
			String ret = doc.getElementsByTagName(responseName).item(0).getTextContent();
			return ret;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	public static String toStringFromDoc(Document document) {
		StringWriter strWtr = new StringWriter();
		StreamResult strResult = new StreamResult(strWtr);
		TransformerFactory tfac = TransformerFactory.newInstance();
		try {
			javax.xml.transform.Transformer t = tfac.newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(document.getDocumentElement()), strResult);
		} catch (Exception e) {
			log.error(e);
		} finally {
			Streams.safeClose(strWtr);
		}
		return strResult.getWriter().toString();
	}

}
