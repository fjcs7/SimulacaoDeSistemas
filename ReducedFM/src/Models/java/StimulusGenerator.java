/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ReducedFM/src/Models/dnl/StimulusGenerator.dnl
-823836751
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Random;

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
public class StimulusGenerator extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_MEASUREDATA = 0;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Stimulus measureData;

    //ENDID
    String phase = "InitialEvent";
    String previousPhase = null;
    Double sigma = 0.0;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Stimulus> outStimulus1 =
        addOutputPort("outStimulus1", Stimulus.class);

    //ENDID
    //ID:OUTP:1
    public final Port<Stimulus> outStimulus2 =
        addOutputPort("outStimulus2", Stimulus.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public StimulusGenerator() {
        this("StimulusGenerator");
    }

    public StimulusGenerator(String name) {
        this(name, null);
    }

    public StimulusGenerator(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        holdIn("InitialEvent", 0.0);

        // Initialize Variables
        //ID:INIT
        measureData = new Stimulus(randomNextMesure());

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("InitialEvent")) {
            getSimulator().modelMessage("Internal transition from InitialEvent");

            //ID:TRA:InitialEvent
            holdIn("SendToSensor1", 0.0);

            //ENDID
            return;
        }
        if (phaseIs("SendToSensor1")) {
            getSimulator().modelMessage("Internal transition from SendToSensor1");

            //ID:TRA:SendToSensor1
            holdIn("SendToSensor2", 1.0);

            //ENDID
            // Internal event code
            //ID:INT:SendToSensor1
            Random generator = new Random();
            measureData = new Stimulus(randomNextMesure());

            //ENDID
            // End internal event code
            return;
        }
        if (phaseIs("SendToSensor2")) {
            getSimulator().modelMessage("Internal transition from SendToSensor2");

            //ID:TRA:SendToSensor2
            holdIn("InitialEvent", 0.0);

            //ENDID
            // Internal event code
            //ID:INT:SendToSensor2
            Random generator = new Random();
            measureData = new Stimulus(randomNextMesure());

            //ENDID
            // End internal event code
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

        if (phaseIs("SendToSensor1")) {
            // Output event code
            //ID:OUT:SendToSensor1
            output.add(outStimulus1, measureData);

            //ENDID
            // End output event code
        }
        if (phaseIs("SendToSensor2")) {
            // Output event code
            //ID:OUT:SendToSensor2
            output.add(outStimulus2, measureData);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    //ID:CUST:0
    int randomNextMesure() {
        Random generator = new Random();
        return generator.nextInt(10);
    }

    //ENDID

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);
        StimulusGenerator model = new StimulusGenerator();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("StimulusGenerator Simulation", model,
                    options);
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
        return new String[] { "measureData" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { measureData };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Stimulus.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

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
            dirUri = StimulusGenerator.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                StimulusGenerator.class.getResource(".").toString());
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
        return new String[] { "InitialEvent", "SendToSensor1", "SendToSensor2" };
    }
}
