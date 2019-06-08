/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ReducedFM/src/Models/dnl/Sensor2.dnl
-2097161751
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

import com.ms4systems.devs.core.message.Message;
import com.ms4systems.devs.core.message.MessageBag;
import com.ms4systems.devs.core.message.Port;
import com.ms4systems.devs.core.message.impl.MessageBagImpl;
import com.ms4systems.devs.core.model.impl.AtomicModelImpl;
import com.ms4systems.devs.core.simulation.Simulation;
import com.ms4systems.devs.core.simulation.Simulator;
import com.ms4systems.devs.core.simulation.impl.SimulationImpl;
import com.ms4systems.devs.extensions.PhaseBased;
import com.ms4systems.devs.extensions.StateVariableBased;
import com.ms4systems.devs.helpers.impl.SimulationOptionsImpl;
import com.ms4systems.devs.simviewer.standalone.SimViewer;

@SuppressWarnings("unused")
public class Sensor2 extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_SENDDATA = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_MEASUREDATA = 1;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Depth sendData;
    protected Stimulus measureData;

    //ENDID
    String phase = "InitialState";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<Stimulus> inStimulus2 =
        addInputPort("inStimulus2", Stimulus.class);

    //ENDID
    //ID:INP:1
    public final Port<Depth> inDepth = addInputPort("inDepth", Depth.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Depth> outDepth = addOutputPort("outDepth", Depth.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public Sensor2() {
        this("Sensor2");
    }

    public Sensor2(String name) {
        this(name, null);
    }

    public Sensor2(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        passivateIn("InitialState");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("MeasureStimulus")) {
            getSimulator()
                .modelMessage("Internal transition from MeasureStimulus");

            //ID:TRA:MeasureStimulus
            holdIn("SendMessage2", 0.0);

            //ENDID
            return;
        }
        if (phaseIs("SendMessage1")) {
            getSimulator().modelMessage("Internal transition from SendMessage1");

            //ID:TRA:SendMessage1
            passivateIn("InitialState");

            //ENDID
            return;
        }
        if (phaseIs("SendMessage2")) {
            getSimulator().modelMessage("Internal transition from SendMessage2");

            //ID:TRA:SendMessage2
            passivateIn("InitialState");

            //ENDID
            return;
        }

        //passivate();
    }

    @Override
    public void externalTransition(double timeElapsed, MessageBag input) {
        currentTime += timeElapsed;
        // Subtract time remaining until next internal transition (no effect if sigma == Infinity)
        sigma -= timeElapsed;

        // Store prior data
        previousPhase = phase;
        previousSigma = sigma;

        // Fire state transition functions
        if (phaseIs("InitialState")) {
            if (input.hasMessages(inStimulus2)) {
                ArrayList<Message<Stimulus>> messageList =
                    inStimulus2.getMessages(input);

                holdIn("MeasureStimulus", 0.0);
                // Fire state and port specific external transition functions
                //ID:EXT:InitialState:inStimulus2
                measureData = (Stimulus) messageList.get(0).getData();
                sendData = new Depth(measureData.getValue());

                //ENDID
                // End external event code
                return;
            }
            if (input.hasMessages(inDepth)) {
                ArrayList<Message<Depth>> messageList =
                    inDepth.getMessages(input);

                holdIn("SendMessage1", 0.0);
                // Fire state and port specific external transition functions
                //ID:EXT:InitialState:inDepth
                sendData = (Depth) messageList.get(0).getData();

                //ENDID
                // End external event code
                return;
            }
        }
    }

    @Override
    public void confluentTransition(MessageBag input) {
        // confluentTransition with internalTransition first (by default)
        internalTransition();
        externalTransition(0, input);
    }

    @Override
    public Double getTimeAdvance() {
        return sigma;
    }

    @Override
    public MessageBag getOutput() {
        MessageBag output = new MessageBagImpl();

        if (phaseIs("SendMessage1")) {
            // Output event code
            //ID:OUT:SendMessage1
            output.add(outDepth, sendData);

            //ENDID
            // End output event code
        }
        if (phaseIs("SendMessage2")) {
            // Output event code
            //ID:OUT:SendMessage2
            output.add(outDepth, sendData);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);
        Sensor2 model = new Sensor2();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("Sensor2 Simulation", model, options);
            sim.startSimulation(0);
            sim.simulateIterations(Long.MAX_VALUE);
        } else { // Use SimViewer
            SimViewer viewer = new SimViewer();
            viewer.open(model, options);
        }
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // Getter/setter for sendData
    public void setSendData(Depth sendData) {
        propertyChangeSupport.firePropertyChange("sendData", this.sendData,
            this.sendData = sendData);
    }

    public Depth getSendData() {
        return this.sendData;
    }

    // End getter/setter for sendData

    // Getter/setter for measureData
    public void setMeasureData(Stimulus measureData) {
        propertyChangeSupport.firePropertyChange("measureData",
            this.measureData, this.measureData = measureData);
    }

    public Stimulus getMeasureData() {
        return this.measureData;
    }

    // End getter/setter for measureData

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "sendData", "measureData" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { sendData, measureData };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Depth.class, Stimulus.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_SENDDATA:
                setSendData((Depth) value);
                return;

            case ID_MEASUREDATA:
                setMeasureData((Stimulus) value);
                return;

            default:
                return;
        }
    }

    // Convenience functions
    protected void passivate() {
        passivateIn("passive");
    }

    protected void passivateIn(String phase) {
        holdIn(phase, Double.POSITIVE_INFINITY);
    }

    protected void holdIn(String phase, Double sigma) {
        this.phase = phase;
        this.sigma = sigma;
        getSimulator()
            .modelMessage("Holding in phase " + phase + " for time " + sigma);
    }

    protected static File getModelsDirectory() {
        URI dirUri;
        File dir;
        try {
            dirUri = Sensor2.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                Sensor2.class.getResource(".").toString());
        }
        boolean foundModels = false;
        while (dir != null && dir.getParentFile() != null) {
            if (dir.getName().equalsIgnoreCase("java") &&
                  dir.getParentFile().getName().equalsIgnoreCase("models")) {
                return dir.getParentFile();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(
            "Could not find Models directory from model path: " +
            dirUri.toASCIIString());
    }

    protected static File getDataFile(String fileName) {
        return getDataFile(fileName, "txt");
    }

    protected static File getDataFile(String fileName, String directoryName) {
        File modelDir = getModelsDirectory();
        File dir = new File(modelDir, directoryName);
        if (dir == null) {
            throw new RuntimeException("Could not find '" + directoryName +
                "' directory from model path: " + modelDir.getAbsolutePath());
        }
        File dataFile = new File(dir, fileName);
        if (dataFile == null) {
            throw new RuntimeException("Could not find '" + fileName +
                "' file in directory: " + dir.getAbsolutePath());
        }
        return dataFile;
    }

    protected void msg(String msg) {
        getSimulator().modelMessage(msg);
    }

    // Phase display
    public boolean phaseIs(String phase) {
        return this.phase.equals(phase);
    }

    public String getPhase() {
        return phase;
    }

    public String[] getPhaseNames() {
        return new String[] {
            "InitialState", "MeasureStimulus", "SendMessage1", "SendMessage2"
        };
    }
}
