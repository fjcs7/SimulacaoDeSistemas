/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/HumanBody/src/Models/dnl/ActionBody.dnl
-2039610725
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
public class ActionBody extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_SENDFOOD = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_MEASUREHUNGER = 1;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Food sendFood;
    protected Hunger measureHunger;

    //ENDID
    String phase = "initialEvent";
    String previousPhase = null;
    Double sigma = 1.0;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<Hunger> inHunger = addInputPort("inHunger", Hunger.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Food> outFood = addOutputPort("outFood", Food.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public ActionBody() {
        this("ActionBody");
    }

    public ActionBody(String name) {
        this(name, null);
    }

    public ActionBody(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        holdIn("initialEvent", 1.0);

        // Initialize Variables
        //ID:INIT
        sendFood = new Food(new Integer(0));
        measureHunger = new Hunger(new Integer(10));

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("initialEvent")) {
            getSimulator().modelMessage("Internal transition from initialEvent");

            //ID:TRA:initialEvent
            holdIn("isStarving", 1.0);

            //ENDID
            return;
        }
        if (phaseIs("isStarving")) {
            getSimulator().modelMessage("Internal transition from isStarving");

            //ID:TRA:isStarving
            passivateIn("isHunger");

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
        if (phaseIs("isHunger")) {
            if (input.hasMessages(inHunger)) {
                ArrayList<Message<Hunger>> messageList =
                    inHunger.getMessages(input);

                holdIn("isStarving", 1.0);

                // Fire state and port specific external transition functions
                //ID:EXT:isHunger:inHunger
                for (int i = 0; i < messageList.size(); i++) {
                    Hunger valueReceived =
                        (Hunger) messageList.get(i).getData();
                    measureHunger = valueReceived;
                    System.out.println("Value: " + valueReceived.getValue());
                }

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

        if (phaseIs("isStarving")) {
            // Output event code
            //ID:OUT:isStarving
            sendFood = new Food(new Integer(0));
            if (measureHunger.getValue() >= 10) {
                sendFood = new Food(new Integer(1));
            }
            output.add(outFood, sendFood);

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
        ActionBody model = new ActionBody();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("ActionBody Simulation", model, options);
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

    // Getter/setter for sendFood
    public void setSendFood(Food sendFood) {
        propertyChangeSupport.firePropertyChange("sendFood", this.sendFood,
            this.sendFood = sendFood);
    }

    public Food getSendFood() {
        return this.sendFood;
    }

    // End getter/setter for sendFood

    // Getter/setter for measureHunger
    public void setMeasureHunger(Hunger measureHunger) {
        propertyChangeSupport.firePropertyChange("measureHunger",
            this.measureHunger, this.measureHunger = measureHunger);
    }

    public Hunger getMeasureHunger() {
        return this.measureHunger;
    }

    // End getter/setter for measureHunger

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "sendFood", "measureHunger" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { sendFood, measureHunger };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Food.class, Hunger.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_SENDFOOD:
                setSendFood((Food) value);
                return;

            case ID_MEASUREHUNGER:
                setMeasureHunger((Hunger) value);
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
            dirUri = ActionBody.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                ActionBody.class.getResource(".").toString());
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
        return new String[] { "initialEvent", "isStarving", "isHunger" };
    }
}
