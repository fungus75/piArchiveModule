package at.fungus.pi;

// this class is just used for debugging Archiver.java outside of sap PI


import java.util.Iterator;
import java.util.Set;

import com.sap.engine.interfaces.messaging.api.AckType;
import com.sap.engine.interfaces.messaging.api.Action;
import com.sap.engine.interfaces.messaging.api.DeliverySemantics;
import com.sap.engine.interfaces.messaging.api.ErrorInfo;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageClass;
import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.Party;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.Service;
import com.sap.engine.interfaces.messaging.api.TextPayload;
import com.sap.engine.interfaces.messaging.api.XMLPayload;
import com.sap.engine.interfaces.messaging.api.exception.InvalidParamException;
import com.sap.engine.interfaces.messaging.api.exception.PayloadFormatException;

public class JustForDebugMessage implements Message {

	@Override
	public void addAttachment(Payload attachment) throws PayloadFormatException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int countAttachments() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorInfo createErrorInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Payload createPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextPayload createTextPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLPayload createXMLPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action getAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Payload getAttachment(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getAttachmentIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCorrelationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeliverySemantics getDeliverySemantics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLPayload getDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorInfo getErrorInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Party getFromParty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service getFromService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Payload getMainPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageClass getMessageClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageDirection getMessageDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessageId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageKey getMessageKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessageProperty(MessagePropertyKey key) {
		// TODO Auto-generated method stub
		return "testpir_super_2019.csv";
	}

	@Override
	public String getMessageProperty(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MessagePropertyKey> getMessagePropertyKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRefToMessageId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequenceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSerializationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTimeReceived() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTimeSent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Party getToParty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service getToService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAck(AckType ackType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAckRequested() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAckRequested(AckType ackType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttachment(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMessageProperty(MessagePropertyKey key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCorrelationId(String id) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDeliverySemantics(DeliverySemantics ds) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDescription(String description) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocument(XMLPayload document) throws PayloadFormatException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setErrorInfo(ErrorInfo errorInfo) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMainPayload(Payload payload) throws PayloadFormatException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMessageProperty(MessagePropertyKey key, String value) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMessageProperty(String namespace, String name, String value) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRefToMessageId(String id) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSequenceId(String id) throws InvalidParamException {
		// TODO Auto-generated method stub
		
	}

}
