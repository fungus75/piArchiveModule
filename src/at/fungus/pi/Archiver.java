package at.fungus.pi;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;

// vgl: https://blogs.sap.com/2015/01/29/create-sap-pi-adapter-modules-in-ejb-30-standard/

import javax.ejb.Stateless;

import com.sap.aii.af.lib.mp.module.Module;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.aii.af.lib.mp.module.ModuleData;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.aii.af.lib.mp.module.ModuleHome;
import com.sap.aii.af.lib.mp.module.ModuleLocal;
import com.sap.aii.af.lib.mp.module.ModuleLocalHome;
import com.sap.aii.af.lib.mp.module.ModuleRemote;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.MessagePropertyKey;
import com.sap.engine.interfaces.messaging.api.Payload;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

/**
 * Session Bean implementation class Archiver
 * 
 * followed pretty much the description from https://blogs.sap.com/2015/01/29/create-sap-pi-adapter-modules-in-ejb-30-standard/
 */

@Stateless(name="ArchiverBean")
@Local(value={ModuleLocal.class})
@Remote(value={ModuleRemote.class})
@LocalHome(value=ModuleLocalHome.class)
@RemoteHome(value=ModuleHome.class)
public class Archiver implements Module {

	private AuditAccess audit;
	
    /**
     * Default constructor. 
     */
    public Archiver() {
    }
    
    /**
     * returns the part within given begin/end-Characters.
     * Example: fullstring = "abc[123]", beginChar='[', endChar=']' ==> returns 123
     * 
     * @param fullString
     * @param beginChar
     * @param endChar
     * @return
     * @throws ModuleException
     */
    public String getEnclosedPart(String fullString, char beginChar, char endChar) throws ModuleException {
    	int pos=fullString.indexOf(beginChar);
    	if (pos<0) return null;
    	
		int end=fullString.indexOf(endChar,pos);
		if (end<pos) throw new ModuleException("Faulty Filename String: no closing found '"+endChar+"' ");

		return fullString.substring(pos+1, end).trim();
    }
    
    /**
     * Replaces the given placeholder in the input-string with the given dateformat (date=now)
     * 
     * @param input
     * @param placeholder
     * @param dateformat
     * @return the new string, input will not be changed
     * @see replaceWithDateformat(String input, String placeholder, String dateformat, Date date)
     */
    public String replaceWithDateformat(String input, String placeholder, String dateformat) {
    	return replaceWithDateformat(input, placeholder, dateformat, new Date());
    }
    
    
    /**
     * Replaces the given placeholder in the input-string with the given dateformat 
     * 
     * @param input
     * @param placeholder
     * @param dateformat
     * @param date the wanted date
     * @return the new string, input will not be changed
     * @see replaceWithDateformat(String input, String placeholder, String dateformat)
     */
    public String replaceWithDateformat(String input, String placeholder, String dateformat, Date date) {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateformat);
    	String dateString = simpleDateFormat.format(new Date());
    	return input.replaceAll(placeholder, dateString);
    }
    
    /**
     * replaces placeholders in the filename string with current values
     * this function is realized as standalone function to have better testing/debugging-posibilities
     * than as PI-Module
     * 
     * The parameter should be exactly the same as for the standard SAP archiving module.
     * 
     * {@link https://help.sap.com/viewer/22e34f550ba84f20b35b7652ba94ef9c/1.0.5/en-US/ce5c9a811c25452681888ddb139b2702.html}
     * 
     * @param fileName
     * @param parameter
     * @return
     * @throws ModuleException
     */
    private String getRealFilename(String fileName, RealFilenameParameters parameter) throws ModuleException {
    	fileName=fileName.replaceAll("%id", parameter.msgId);
    	fileName=replaceWithDateformat(fileName, "%TS", "yyyy.MM.dd_HH-mm-ss.SSS");
    	fileName=fileName.replaceAll("%fp", parameter.fromParty);
    	fileName=fileName.replaceAll("%tp", parameter.toParty);
    	fileName=fileName.replaceAll("%fs", parameter.fromService);
    	fileName=fileName.replaceAll("%ts", parameter.toService);
    	
    	String dynString;
    	while ((dynString=getEnclosedPart(fileName, '[', ']')) !=null) {
			// last slash is the delimiter for NameSpace and Name
			int lastSlash=dynString.lastIndexOf('/');
			if (lastSlash<0) throw new ModuleException("Faulty Filename String: no slash found '/'");
			
			String dynNameSpace=dynString.substring(0, lastSlash);
			String dynName=dynString.substring(lastSlash+1);
			
			if (audit!=null) audit.addAuditLogEntry(parameter.messageKey, AuditLogStatus.SUCCESS, "Found DynConfig: "+dynName+", Namespace: "+dynNameSpace);
			String dynValue=parameter.message.getMessageProperty(new MessagePropertyKey(dynName, dynNameSpace));
			if (dynValue==null) dynValue="";
			if (audit!=null) audit.addAuditLogEntry(parameter.messageKey, AuditLogStatus.SUCCESS, " DynConfig has Value: ["+dynValue+"]");
			fileName=fileName.replaceAll("\\["+dynString+"\\]", dynValue);
    	}
    	
    	String dateString;
    	while ((dateString=getEnclosedPart(fileName, '{', '}')) !=null) {
    		if (audit!=null) audit.addAuditLogEntry(parameter.messageKey, AuditLogStatus.SUCCESS, "Found Dateformat: "+dateString);
			fileName=replaceWithDateformat(fileName, "\\{"+dateString+"\\}", dateString);
    	}

    	return fileName;
    	
    }
    
    
    @Override
    public ModuleData process(ModuleContext moduleContext, ModuleData inputModuleData) throws ModuleException {
    	
    	Message msg = (Message)inputModuleData.getPrincipalData();
    	MessageKey mk = msg.getMessageKey();
    	audit.addAuditLogEntry(mk, AuditLogStatus.SUCCESS, "ArchiverBean V 1.0");
    	String archiveBaseDirString=moduleContext.getContextData("archiveBaseDir")+"/";
    	File archiveBaseDir=new File(archiveBaseDirString);
    	if (!archiveBaseDir.isDirectory()) archiveBaseDir.mkdirs();	// try to create directory if not isDirectory
    	
    	// get Filename and replace as it does the standard-archiver-module
    	String fileName=moduleContext.getContextData("archiveFile")+".arc";
    	
    	// do some replacements
    	RealFilenameParameters rfnParams=new RealFilenameParameters();
    	rfnParams.msgId      =msg.getMessageId();
    	rfnParams.fromParty  =msg.getFromParty().getName();
    	rfnParams.toParty    =msg.getToParty().getName();
    	rfnParams.fromService=msg.getFromService().getName();
    	rfnParams.toService  =msg.getToService().getName();
    	rfnParams.messageKey =mk;
    	rfnParams.message    =msg;
    	fileName=getRealFilename(fileName, rfnParams);    	
    	
		
    	// make Filename unique
    	File archiveFile=new File(archiveBaseDirString+fileName);
    	int makeUniqueIndex=0;
    	while (archiveFile.exists() && makeUniqueIndex<1000) {
    		makeUniqueIndex++;
    		archiveFile=new File(archiveBaseDirString+fileName+"."+makeUniqueIndex);
    	}
    	
    	// do we have an unique Filename?
    	if (archiveFile.exists()) throw new ModuleException("Not able to find an unique archive filename");
    	
    	// write content to file and close file
    	Payload payload = msg.getMainPayload();
    	try {
			FileOutputStream fos = new FileOutputStream(archiveFile);
			fos.write(payload.getContent());
			fos.flush();
			fos.close();
			audit.addAuditLogEntry(mk, AuditLogStatus.SUCCESS, "Payload written to "+archiveFile.getAbsolutePath());
		} catch (Exception e) {
			throw new ModuleException("error in initialiseResources():"+e.getMessage());
		}
    	
    	
    	return inputModuleData;
    }
    
    @PostConstruct
    public void initialiseResources() {
    	try {
    		audit=PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
    	} catch (Exception e) {
			throw new RuntimeException("error in initialiseResources():"+e.getMessage());
		}
    }
    
    @PreDestroy
    public void releaseResources() {
    	// release any resources here
    }

    
    /* just for testing/debugging
    public static void main(String[] args) throws ModuleException {
    	RealFilenameParameters rfnParams=new RealFilenameParameters();
    	rfnParams.msgId      ="asf34afasaffas3442";
    	rfnParams.fromParty  ="testPartyFrom";
    	rfnParams.toParty    ="testPartyTo";
    	rfnParams.fromService="testServiceFrom";
    	rfnParams.toService  ="testServiceTo";
    	rfnParams.messageKey =null;
    	rfnParams.message    =new JustForDebugMessage();
    	
    	Archiver x=new Archiver();
    	
    	String fileName=x.getRealFilename("start-%id-%TS-%fp-%tp-%fs-%ts-[http://sap.com/file/FileName]-{HH}-ende", rfnParams); 
    	System.out.println(fileName);

    }
    */
    
    
}
