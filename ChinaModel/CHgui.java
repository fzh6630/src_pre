package ChinaModel;

// libraries includes

import sim.portrayal.simple.*;
//import org.jfree.data.xy.XYSeries;    	// the data series we'll add to
import org.jfree.data.xy.*;
import sim.util.media.chart.TimeSeriesChartGenerator;	// the charting facility
import sim.engine.*;
import java.awt.Color;
import javax.swing.JFrame;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;
import sim.portrayal.geo.*;
//import sim.portrayal.network.stats.*;

// base class for the graphical simulation of Model

public class CHgui extends GUIState {

	public static final int TILE = 0;

	public static final int MESH = 1;

	public static final int NOZ = 2;

	public int mapmode = MESH;

	//
	// atributes
	//
	public TimeSeriesChartGenerator chart;

	// Each series of points graphed in the chart must be created
	public XYSeries seriesEmployed;

	public XYSeries seriesRural;

	// UI for the grid

	public Display2D agentsDisplay;

	public JFrame agentsDisplayFrame;

	public SparseGridPortrayal2D agentsPortrayal;

	public Display2D patchesDisplay;

	public JFrame patchesDisplayFrame;

	public SparseGridPortrayal2D patchesPortrayal;

	// UI for the network
	public Display2D networkDisplay;

	public JFrame networkDisplayFrame;

	public NetworkPortrayal2D networkPortrayal;

	//UI for the GIS map
	public Display2D gisDisplay;

	public JFrame gisDisplayFrame;

	public GeomVectorFieldPortrayal gisPortrayal;

	//UI for the GIS grid map
	public Display2D gisGridDisplay;

	public JFrame gisGridDisplayFrame;

	public FastValueGridPortrayal2D gisGridPortrayal;

	//QuadPortrayal quadP = null;
	// pointer to the model
	public CHModel model;

	public SimpleColorMap agentColorMap = new SimpleColorMap();

	public SimpleColorMap patchColorMap = new SimpleColorMap();


	//
	// methods
	//
	// base construtor

	public CHgui() {

		// builds model and portrayals
		super(new CHModel(System.currentTimeMillis(), 1));
		//agentsPortrayal = new SparseGridPortrayal2D();
		patchesPortrayal = new SparseGridPortrayal2D();
		networkPortrayal = new NetworkPortrayal2D();
		gisPortrayal = new GeomVectorFieldPortrayal();
		gisGridPortrayal = new FastValueGridPortrayal2D();

		model = (CHModel) state;

	}


	// standard construtor 

	public CHgui(SimState state) {

		// builds model and portrayals
		super(state);
		//agentsPortrayal = new SparseGridPortrayal2D();
		patchesPortrayal = new SparseGridPortrayal2D();
		networkPortrayal = new NetworkPortrayal2D();
		gisPortrayal = new GeomVectorFieldPortrayal();
		gisGridPortrayal = new FastValueGridPortrayal2D();

		model = (CHModel) state;

	}

	/* // main method
	 * public static void main (String[] args) {
	 *
	 * // builds model
	 * CHgui m = new CHgui();
	 *
	 * // adds a console
	 * Console c = new Console(m);
	 * c.setVisible(true);
	 * }
	 */

	public static void main(String[] args) {
		CHgui china = new CHgui(new CHModel(System.currentTimeMillis(), 1));
		Console c = new Console(china);
		c.setVisible(true);
	}


	// starts the simulation

	public void start() {

		super.start();
		setupPortrayals(); // set up portrayals

		//CHART start and step
		chart.removeAllSeries();

		// Initialize each series
		seriesEmployed = new org.jfree.data.xy.XYSeries("Number Employed",
														false);

		// add each series to the chart object                          
		chart.addSeries(seriesEmployed, null);

		scheduleImmediateRepeat(true, new Steppable() {
			public void step(SimState state) {

				CHModel model = (CHModel) state;
                   // at this stage we're adding data to our chart.  We
				// need an X value and a Y value.  The X
				// value is the schedule's timestamp.  The Y value
				// is whatever data you're extracting from your 
				// simulation. 

				// assign the data values to simple variables
				double x = state.schedule.time();

				//Choose linear or log data
				double y0 = model.employedCount;

				// now add the data
				seriesEmployed.add(x, y0, true);

				//Set the network Portrayal
				// every edge with the same color
				networkPortrayal.setPortrayalForAll(
						new SimpleEdgePortrayal2D(Color.red, Color.red, null)
				);
//                   // Set the Agent Portrayal
//				// sets the color for each agent acording to its wage 
//
//				for (int i = 0; i < model.numberOfAgents; i++) {
//    //                 agentsPortrayal.setPortrayalForObject(model.agents[i],
//					//                         //new OvalPortrayal2D(Color.MAGENTA));
//					//                         new OvalPortrayal2D(
//					//                         agentColorMap.getColor(model.agents[i].wage)));
//					agentsPortrayal.setPortrayalForObject(model.agents[i],
//														  new OvalPortrayal2D(Color.PINK));
//					;
//				}

				//Set the Patch portrayal
				for (int xx = 0; xx < (model.gridWidth()); xx++) {

					for (int yy = 0; yy < (model.gridHeight()); yy++) {

						patchesPortrayal.setPortrayalForObject(
								model.patches[xx][yy], new RectanglePortrayal2D(
										patchColorMap.getColor(
												model.patches[xx][yy].province().provinceName().provinceNum))
						);

					}
				}

				gisPortrayal.setField(model.provinceVectorField);
				gisPortrayal.setPortrayalForAll(new GeomPortrayal(Color.CYAN, true));

				gisGridPortrayal.setField(model.gridField.getGrid());
				gisGridPortrayal.setMap(new SimpleColorMap(0, 32, Color.black, Color.white));

			}
		});
	}


	// builds and loads the graphical components of the simulation

	public void load(SimState state) {

		super.load(state);
		// we now have new grids; set up the portrayals to reflect that
		setupPortrayals();
	}


	// links the objects simulation to the graphical components

	public void setupPortrayals() {

		agentColorMap.setLevels(0.0, model.meanWage * 10, Color.white, Color.orange);
		patchColorMap.setLevels(0.0, 32.0, Color.white, Color.red);

//		//AGENTS PORTRAYAL
//		agentsPortrayal.setField(((CHModel) state).lattice);
//		for (int i = 0; i < model.numberOfAgents; i++) {
//
////                 agentsPortrayal.setPortrayalForObject(model.agents[i],
////                         //new OvalPortrayal2D(Color.MAGENTA));
////                         new OvalPortrayal2D(
////                         agentColorMap.getColor(model.agents[i].wage)));
//			agentsPortrayal.setPortrayalForObject(model.agents[i],
//												  new OvalPortrayal2D(Color.PINK));
//
//                         //agentsPortrayal.setPortrayalForObject(model.agents[i],
//			//new ConePortrayal3D(cm.getColor(model.agents[i].wage)));
//		}

		//PATCHES PORTRAYAL
		patchesPortrayal.setField(((CHModel) state).lattice);
		for (int xx = 0; xx < (model.gridWidth() - 1); xx++) {

			for (int yy = 0; yy < (model.gridHeight() - 1); yy++) {
				
				Patch p = model.patches[xx][yy];
				
				assert(p != null) : "A Patch cannot be null";
				
//				assert(p.province() != null): "A Patch's province cannot be null";
				
//				assert(p.province().provinceName() != null): "A Patche's province name cnanot be null";				
				
				
				patchesPortrayal.setPortrayalForObject(
						p, new RectanglePortrayal2D(patchColorMap.getColor(p.getColorLevel())
				));

			}
		}

		//NETWORK PORTRAYAL
		networkPortrayal.setField(((CHModel) state).world);
		// every edge with the same color
		networkPortrayal.setPortrayalForAll(
				new SimpleEdgePortrayal2D(Color.red, Color.red, null)
		);
		//GIS PORTRAYAL
		gisPortrayal.setField(model.provinceVectorField);
		gisPortrayal.setPortrayalForAll(new GeomPortrayal(Color.CYAN, true));

		gisGridPortrayal.setField(model.gridField.getGrid());
		gisGridPortrayal.setMap(new SimpleColorMap(0, 32, Color.black, Color.white));

		// 2D reschedule the displays
//		agentsDisplay.reset();
//		agentsDisplay.repaint();
		patchesDisplay.reset();
		patchesDisplay.repaint();

		//3D version
		//agentsDisplay.reset();
		//agentsDisplay.createSceneGraph();
		//patchesDisplay.reset();
		//patchesDisplay.createSceneGraph();
		networkDisplay.reset();
		networkDisplay.repaint();

//        gisPortrayal.reset();
//        gisPortrayal.repaint();
//        
		gisGridDisplay.reset();
		gisGridDisplay.setBackdrop(Color.BLUE);
		gisGridDisplay.repaint();

		gisDisplay.reset();
		gisDisplay.setBackdrop(Color.WHITE);
		gisDisplay.repaint();
	}


	// builds the graphical components

	public void init(Controller c) {

		super.init(c);

		// Make the Display2D.  We'll have it display stuff later.
//		agentsDisplay
//				= new Display2D(300, 300, this);
//		// attach the portrayals
//		agentsDisplay.attach(agentsPortrayal, "Agents");
//		agentsDisplayFrame = agentsDisplay.createFrame();
//		// register the frame so it appears in the "Display" list
//		c.registerFrame(agentsDisplayFrame);
//		agentsDisplayFrame.setVisible(true);

		// Make the Display2D.  We'll have it display stuff later.
		patchesDisplay
				= new Display2D(300, 300, this);
		patchesDisplay.attach(patchesPortrayal, "Patches");
		patchesDisplayFrame = patchesDisplay.createFrame();
		// register the frame so it appears in the "Display" list
		c.registerFrame(patchesDisplayFrame);
		patchesDisplayFrame.setVisible(true);

		// Make the Network Display2D.  We'll have it display stuff later.
		networkDisplay
				= new Display2D(300, 300, this);
		networkDisplay.attach(networkPortrayal, "Network");
		networkDisplayFrame = networkDisplay.createFrame();
		c.registerFrame(networkDisplayFrame);
		networkDisplayFrame.setVisible(true);

		// Make the GIS Display2D.  We'll have it display stuff later.
		gisDisplay
				= new Display2D(300, 300, this);
		gisDisplay.attach(gisPortrayal, "GIS Map");
		gisDisplayFrame = gisDisplay.createFrame();
		c.registerFrame(gisDisplayFrame);
		gisDisplayFrame.setVisible(true);

		// Make the GIS GRID Display2D.  We'll have it display stuff later.
		gisGridDisplay
				= new Display2D(300, 300, this);
		gisGridDisplay.attach(gisGridPortrayal, "GIS Map");
		gisGridDisplayFrame = gisGridDisplay.createFrame();
		c.registerFrame(gisGridDisplayFrame);
		gisGridDisplayFrame.setVisible(true);

		// specify the backdrop color - what gets painted behind the displays
//		agentsDisplay.setBackdrop(Color.black);
		patchesDisplay.setBackdrop(Color.black);
		networkDisplay.setBackdrop(Color.black);
		gisDisplay.setBackdrop(Color.white);
		gisGridDisplay.setBackdrop(Color.blue);

		/// CHART
		chart = new sim.util.media.chart.TimeSeriesChartGenerator();
		//chart.setTitle("");
		chart.setRangeAxisLabel(" Agent Action  ");
		chart.setDomainAxisLabel("Time (Steps)");
		JFrame frame = chart.createFrame(this);
		// perhaps you might move the chart to where you like.
		frame.show();
		frame.pack();
		c.registerFrame(frame);
	}


	// quits the simulations

	public void quit() {

		super.quit();
		model.finish();

//		if (agentsDisplayFrame != null) {
//			agentsDisplayFrame.dispose();
//		}
//		agentsDisplayFrame = null;  // let gc
//		agentsDisplay = null;       // let gc

		if (patchesDisplayFrame != null) {
			patchesDisplayFrame.dispose();
		}
		patchesDisplayFrame = null;  // let gc
		patchesDisplay = null;       // let gc

		if (networkDisplayFrame != null) {
			networkDisplayFrame.dispose();
		}
		networkDisplayFrame = null;  // let gc
		networkDisplay = null;       // let gc

		if (gisDisplayFrame != null) {
			gisDisplayFrame.dispose();
		}
		gisDisplayFrame = null;  // let gc
		gisDisplayFrame = null;       // let gc

		if (gisGridDisplayFrame != null) {
			gisGridDisplayFrame.dispose();
		}
		gisGridDisplayFrame = null;  // let gc
		gisGridDisplayFrame = null;       // let gc
	}


	// name of the model

	public static String getName() {

		return "Migration in China";
	}


	// for debug (?)

	public Object getSimulationInspectedObject() {

		return state;
	}
}
