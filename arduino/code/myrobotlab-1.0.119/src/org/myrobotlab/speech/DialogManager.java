/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package org.myrobotlab.speech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import org.myrobotlab.framework.Service;
import org.myrobotlab.logging.LoggerFactory;
import org.slf4j.Logger;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

/**
 * The DialogManager is a component that is used to manage speech dialogs. A
 * speech dialog is represented as a graph of dialog nodes. The dialog manager
 * maintains an active node. When a node is active it is directing the
 * recognition process. Typically a dialog node will define the current active
 * grammar. The recognition result is typically used to direct the dialog
 * manager to select the next active node. An application can easily customize
 * the behavior at each active node.
 */
public class DialogManager implements Configurable {

	/**
	 * Represents a node in the dialog
	 */
	class DialogNode {
		private DialogNodeBehavior behavior;
		private String name;

		/**
		 * Creates a dialog node with the given name an application behavior
		 * 
		 * @param name
		 *            the name of the node
		 * 
		 * @param behavior
		 *            the application behavor for the node
		 * 
		 */
		DialogNode(String name, DialogNodeBehavior behavior) {
			this.behavior = behavior;
			this.name = name;
		}

		/**
		 * Enters the node, prepares it for recognition
		 */
		void enter() throws IOException {
			trace("Entering " + name);
			behavior.onEntry();
			behavior.onReady();
		}

		/**
		 * Exits the node
		 */
		void exit() {
			trace("Exiting " + name);
			behavior.onExit();
		}

		public DialogManager getDialogManager() {
			return DialogManager.this;
		}

		/**
		 * Returns the JSGF Grammar for the dialog manager that contains this
		 * node
		 * 
		 * @return the grammar
		 */
		public JSGFGrammar getGrammar() {
			return grammar;
		}

		/**
		 * Gets the name of the node
		 * 
		 * @return the name of the node
		 */
		public String getName() {
			return name;
		}

		/**
		 * Initializes the node
		 */

		void init() {
			behavior.onInit(this);
		}

		/**
		 * Performs recognition at the node.
		 * 
		 * @return the result tag
		 */
		String recognize() throws GrammarException {
			trace("Recognize " + name);
			Result result = recognizer.recognize();
			return behavior.onRecognize(result);
		}

		/**
		 * Traces a message
		 * 
		 * @param msg
		 *            the message to trace
		 */
		public void trace(String msg) {
			DialogManager.this.trace(msg);
		}
	}

	public final static Logger log = LoggerFactory.getLogger(DialogManager.class);

	/**
	 * The property that defines the name of the grammar component to be used by
	 * this dialog manager
	 */
	@S4Component(type = JSGFGrammar.class)
	public final static String PROP_JSGF_GRAMMAR = "jsgfGrammar";

	/**
	 * The property that defines the name of the microphone to be used by this
	 * dialog manager
	 */
	@S4Component(type = Microphone.class)
	public final static String PROP_MICROPHONE = "microphone";

	/**
	 * The property that defines the name of the recognizer to be used by this
	 * dialog manager
	 */
	@S4Component(type = Recognizer.class)
	public final static String PROP_RECOGNIZER = "recognizer";
	// ------------------------------------
	// Configuration data
	// ------------------------------------
	private JSGFGrammar grammar;
	private Logger logger;
	private Recognizer recognizer;

	private Microphone microphone;
	// ------------------------------------
	// local data
	// ------------------------------------
	private DialogNode initialNode;
	private Map<String, DialogNode> nodeMap = new HashMap<String, DialogNode>();

	private String name;

	/**
	 * Adds a new node to the dialog manager. The dialog manager maintains a set
	 * of dialog nodes. When a new node is added the application specific beh
	 * 
	 * @param name
	 *            the name of the node
	 * @param behavior
	 *            the application specified behavior for the node
	 */
	public void addNode(String name, DialogNodeBehavior behavior) {
		DialogNode node = new DialogNode(name, behavior);
		putNode(node);
	}

	/**
	 * Gets the recognizer and the dialog nodes ready to run
	 * 
	 * @throws IOException
	 *             if an error occurs while allocating the recognizer.
	 */
	public void allocate() throws IOException {
		recognizer.allocate();

		for (DialogNode node : nodeMap.values()) {
			node.init();
		}
	}

	/**
	 * Releases all resources allocated by the dialog manager
	 */
	public void deallocate() {
		recognizer.deallocate();
	}

	/**
	 * Issues an error message
	 * 
	 * @param s
	 *            the message
	 */
	private void error(String s) {
		System.out.println("Error: " + s);
	}

	public void generateGrammarFiles(Service myService) {
		/*
		 * HashMap<String, ServiceEntry> services =
		 * myService.getHostCFG().getServiceMap();
		 * org.myrobotlab.service.Runtime.getlo Iterator<String> it =
		 * services.keySet().iterator();
		 * 
		 * // this.getClass().getClassLoader().getResource(grammarLocation)
		 * 
		 * // create service grammar file BufferedWriter g = null; try { g = new
		 * BufferedWriter(new FileWriter("service.gram"));
		 * g.write("#JSGF V1.0;\n\n"); g.write("grammar service;\n\n");
		 * g.write("public <command> = \n");
		 * 
		 * while (it.hasNext()) { String serviceName = it.next(); g.write("\t\t"
		 * + serviceName + "\t\t" + "{ goto_" + serviceName + " } "); if
		 * (it.hasNext()) { g.write("|\n"); } else { g.write(";"); } }
		 * g.flush(); g.close();
		 * 
		 * } catch (IOException e) { //
		 * log.error("could not create grammar file service.gram");
		 * e.printStackTrace(); return; }
		 * 
		 * String serviceName = null; try {
		 * 
		 * // create all the service commands it = services.keySet().iterator();
		 * while (it.hasNext()) { serviceName = it.next();
		 * 
		 * HashMap<String, MethodEntry> methods =
		 * myService.getHostCFG().getMethodMap(serviceName); g = new
		 * BufferedWriter(new FileWriter(serviceName + ".gram"));
		 * g.write("#JSGF V1.0;\n\n"); g.write("grammar " + serviceName +
		 * ";\n\n"); g.write("public <command> = \n");
		 * 
		 * Iterator<String> mit = methods.keySet().iterator(); String mString =
		 * ""; while (mit.hasNext()) { String methodName = mit.next();
		 * MethodEntry me = methods.get(methodName);
		 * 
		 * if (me.parameterTypes == null || me.parameterTypes.length != 0) {
		 * continue; }
		 * 
		 * mString += "\t\t" + me.name + " ";
		 * 
		 * } mString = mString.replace(" ", "|\n"); mString =
		 * mString.substring(0, mString.length() - 2); mString += ";";
		 * log.info(mString); g.write(mString); g.flush(); g.close();
		 * 
		 * } } catch (IOException e) { //
		 * Log.error("could not create grammar file " + serviceName + //
		 * ".gram"); e.printStackTrace(); }
		 */
	}

	/**
	 * Returns the name of this component
	 * 
	 * @return the name of the component.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the dialog node with the given name
	 * 
	 * @param name
	 *            the name of the node
	 */
	private DialogNode getNode(String name) {
		return nodeMap.get(name);
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	/**
	 * Invokes the dialog manager. The dialog manager begin to process the
	 * dialog states starting at the initial node. This method will not return
	 * until the dialog manager is finished processing states
	 */
	public void go() {
		DialogNode lastNode = null;
		DialogNode curNode = initialNode;

		try {
			if (microphone.startRecording()) {
				while (true) {

					if (curNode != lastNode) {
						if (lastNode != null) {
							lastNode.exit();
						}
						curNode.enter();
						lastNode = curNode;
					}
					String nextStateName = curNode.recognize();
					if (nextStateName == null || nextStateName.isEmpty()) {
						continue;
					} else {
						DialogNode node = nodeMap.get(nextStateName);
						if (node == null) {
							warn("Can't transition to unknown state " + nextStateName);
						} else {
							curNode = node;
						}
					}
				}
			} else {
				error("Can't start the microphone");
			}
		} catch (GrammarException ge) {
			error("grammar problem in state " + curNode.getName() + ' ' + ge);
		} catch (IOException ioe) {
			error("problem loading grammar in state " + curNode.getName() + ' ' + ioe);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util
	 * .props.PropertySheet)
	 */
	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// logger = ps.getLogger();
		grammar = (JSGFGrammar) ps.getComponent(PROP_JSGF_GRAMMAR);
		microphone = (Microphone) ps.getComponent(PROP_MICROPHONE);
		recognizer = (Recognizer) ps.getComponent(PROP_RECOGNIZER);
	}

	/**
	 * Puts a node into the node map
	 * 
	 * @param node
	 *            the node to place into the node map
	 */
	private void putNode(DialogNode node) {
		nodeMap.put(node.getName(), node);
	}

	/**
	 * Sets the name of the initial node for the dialog manager
	 * 
	 * @param name
	 *            the name of the initial node. Must be the name of a previously
	 *            added dialog node.
	 */
	public void setInitialNode(String name) {
		if (getNode(name) == null) {
			throw new IllegalArgumentException("Unknown node " + name);
		}
		initialNode = getNode(name);
	}

	/**
	 * Sets the recognizer
	 * 
	 * @param recognizer
	 *            the recognizer
	 */
	public void setRecognizer(Recognizer recognizer) {
		this.recognizer = recognizer;
	}

	/**
	 * Issues a tracing message
	 * 
	 * @parma s the message
	 */
	private void trace(String s) {
		logger.info(s);
	}

	/**
	 * Issues a warning message
	 * 
	 * @param s
	 *            the message
	 */
	private void warn(String s) {
		System.out.println("Warning: " + s);
	}
}

/**
 * Provides the default behavior for dialog node. Applications will typically
 * extend this class and override methods as appropriate
 */
class DialogNodeBehavior {
	private DialogManager.DialogNode node;

	/**
	 * Retrieves the grammar associated with this ndoe
	 * 
	 * @return the grammar
	 */
	public JSGFGrammar getGrammar() {
		return node.getGrammar();
	}

	/**
	 * Returns the name for this node
	 * 
	 * @return the name of the node
	 */
	public String getName() {
		return node.getName();
	}

	/**
	 * Retrieves the rule parse for the given result
	 * 
	 * @param the
	 *            recognition result
	 * @return the rule parse for the result
	 * @throws GrammarException
	 *             if there is an error while parsing the result
	 */
	RuleParse getRuleParse(Result result) throws GrammarException {
		String resultText = result.getBestFinalResultNoFiller();
		RuleGrammar ruleGrammar = (RuleGrammar) getGrammar().getRuleGrammar(); // FIXME
		RuleParse ruleParse = ruleGrammar.parse(resultText, null);
		return ruleParse;
	}

	/**
	 * Gets a space delimited string of tags representing the result
	 * 
	 * @param result
	 *            the recognition result
	 * @return the tag string
	 * @throws GrammarException
	 *             if there is an error while parsing the result
	 */
	String getTagString(Result result) throws GrammarException {
		RuleParse ruleParse = getRuleParse(result);
		if (ruleParse == null)
			return null;
		String[] tags = ruleParse.getTags();
		if (tags == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String tag : tags)
			sb.append(tag).append(' ');
		return sb.toString().trim();
	}

	/**
	 * Called when this node becomes the active node
	 */
	public void onEntry() throws IOException {
		trace("Entering " + getName());
	}

	/**
	 * Called when this node is no lnoger the active node
	 */
	public void onExit() {
		trace("Exiting " + getName());
	}

	/**
	 * Called during the initialization phase
	 * 
	 * @param node
	 *            the dialog node that the behavior is attached to
	 */
	public void onInit(DialogManager.DialogNode node) {
		this.node = node;
	}

	/**
	 * Called when this node is ready to perform recognition
	 */
	public void onReady() {
		trace("Ready " + getName());
	}

	/*
	 * Called with the recognition results. Should return a string representing
	 * the name of the next node.
	 */
	public String onRecognize(Result result) throws GrammarException {
		String tagString = getTagString(result);
		trace("Recognize result: " + result.getBestFinalResultNoFiller());
		trace("Recognize tag   : " + tagString);
		return tagString;
	}

	/**
	 * Returns the string representation of this object
	 * 
	 * @return the string representation of this object
	 */
	@Override
	public String toString() {
		return "Node " + getName();
	}

	/**
	 * Outputs a trace message
	 * 
	 * @param the
	 *            trace message
	 */
	void trace(String msg) {
		node.trace(msg);
	}
}
