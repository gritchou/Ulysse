package org.qualipso.factory.workflow.hook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class ProcessNVUtil {
	private static Logger LOG = Logger.getLogger(ProcessNVUtil.class.getName());

	// temporary constants
	public final static char PATH_DELIM = '/';
	public static String PROJECTVALIDATED = "projectValidated";
	public static String PATH = "path";
	public static String NAME = "name";
	public static String SUMMARY = "summary";
	public static String LICENCE = "licence";


//	public static enum WriterDecisionValues {
//		ASSIGN("assign"), TRASH("trash"), ENTERCOMMENT("comment"), DELIVER(
//				"deliver"), ;
//
//		private final String keyword;
//
//		private WriterDecisionValues(final String keyword) {
//			this.keyword = keyword;
//		}
//
//		public String getKeyword() {
//			return this.keyword;
//		}
//	};
//
//	public final static String COLLABORATOR = "collaborator";
//	public final static String VALIDATOR = "validator";
//	public final static String DEVELOPER = "developer";
//	public final static String FUNCTIONALMANAGER = "functionalmanager";
//	public final static String INTEGRATOR = "integrator";
//	public final static String QUALITYMANAGER = "qualitymanager";
//	public final static String SUPPORT = "support";
//	public final static String TECHNICALMANAGER = "technicalmanager";
//	public final static String MEMBER = "member";
//	public final static String CUSTOMER = "customer";
//	public final static String ADMINISTRATOR = "administrator";
//	public final static String MANAGER = "manager";
//	public final static String OWNER = "owner";
//	public final static String PROJETDIRECTOR = "projetdirector";
//	public final static String PROJETMANAGER = "projectmanager";
//
//	// validation step
//	public final static String VALIDATORCURRENTCOMMENT = "validatorCurrentComment";
//	public final static String VALIDATORGLOBALCOMMENT = "validatorGlobalComment";
//	public final static String WRITERPERFASSIGNVAR = "writerPerfAssignVar";
	public final static String SERVICECHOICEPERFASSIGNVAR = "serviceChoicePerfAssignVar";
//	public final static String VALIDATORINITIALLIST = "validatorInitialList";
//	public final static String ISVALIDATORITERATION = "isValidatorIteration";
//	public final static String VALIDATORREMAINIGITERATION = "validatorRemainingIteration";
//	public final static String VALIDATORDECISION = "validatorDecision";
//	public final static String ISVALIDATIONREFUSED = "isValidationRefused";
//
//	public static enum ValidatorDecisionValues {
//		TRASH("trash"), ACCEPTVALIDATION("acceptValidation"), REFUSEVALIDATION(
//				"refuseValidation"), ASSIGN("assign");
//
//		private final String keyword;
//
//		private ValidatorDecisionValues(final String keyword) {
//			this.keyword = keyword;
//		}
//
//		public String getkeyword() {
//			return this.keyword;
//		}
//	};
//
//	public static String getCommentSeparator(final String globalComment,
//			final String taskUser) {
//		String separator = "";
//		if (globalComment.equals("")) {
//			separator = "-------------------------------------------\n";
//		} else {
//			separator = "\n-------------------------------------------\n";
//		}
//		final DateFormat dateFormat = new SimpleDateFormat(
//				"yyyy/MM/dd HH:mm:ss");
//		final Date date = new Date();
//		separator += "Date: " + dateFormat.format(date);
//		separator += "\n" + "Sender: " + taskUser + "\n";
//
//		return separator;
//	}
//
//	/**
//	 * @param List
//	 *            comma separated list of string
//	 * @return List of String
//	 */
//	public static List<String> getListfromString(final String list) {
//		final ArrayList<String> ar = new ArrayList<String>();
//		final StringTokenizer stringTokenizer = new StringTokenizer(list, ",");
//		while (stringTokenizer.hasMoreTokens()) {
//			final String userId = (String) stringTokenizer.nextElement();
//			ar.add(userId.trim());
//		}
//		return ar;
//	}

}
