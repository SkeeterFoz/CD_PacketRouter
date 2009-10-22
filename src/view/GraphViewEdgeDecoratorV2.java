package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;


import javax.swing.JPanel;


import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 * The prefuse GraphView demo with some additional edge rendering and decorations
 * Not particular nice, but hopefully a base for further improvements.
 * Requires the file socialnetRelations.xml
 * 
 * search for MAD(most amazing discovery) to find the relevant changes of the original demo
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */
public class GraphViewEdgeDecoratorV2 extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    
    private static int sizeX;
    private static int sizeY;

    private Visualization m_vis;

    //MAD - the name of the decorator group
    private static final String EDGE_DECORATORS = "edgeDeco";

    public GraphViewEdgeDecoratorV2(Graph g, String label) {

	// create a new, empty visualization for our data
	m_vis = new Visualization();

	// --------------------------------------------------------------------
	// set up the renderers
	
	LabelRenderer tr = new LabelRenderer();
	tr.setRoundedCorner(8, 8);

	//MAD - making the edges fat
	EdgeRenderer edgeR = new EdgeRenderer(Constants.EDGE_TYPE_LINE,
		Constants.EDGE_ARROW_FORWARD);
	edgeR.setDefaultLineWidth(7);

	DefaultRendererFactory drf = new DefaultRendererFactory(tr,edgeR);

	m_vis.setRendererFactory(drf);

	// --------------------------------------------------------------------
	// register the data with a visualization

	// adds graph to visualization and sets renderer label field
	setGraph(g, label);

	/*
	 * MAD - defining the decorator schema and
	 * adding the edge decorator group to the visualization using the edge decorator schema
	 */

	Schema EDGE_DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema(); 

	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); //noninteractive 
	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgba(100,100,100,200)); 
	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",11)); // and not too big

	m_vis.addDecorators(EDGE_DECORATORS, edges, EDGE_DECORATOR_SCHEMA);


	// fix selected focus nodes
	TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
	focusGroup.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
	    {
		for ( int i=0; i<rem.length; ++i )
		    ((VisualItem)rem[i]).setFixed(false);
		for ( int i=0; i<add.length; ++i ) {
		    ((VisualItem)add[i]).setFixed(false);
		    ((VisualItem)add[i]).setFixed(true);
		}
		if ( ts.getTupleCount() == 0 ) {
		    ts.addTuple(rem[0]);
		    ((VisualItem)rem[0]).setFixed(false);
		}
		m_vis.run("draw");
	    }
	});



	// --------------------------------------------------------------------
	// create actions to process the visual data

	int hops = 30;
	final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

	
//	ColorAction fill = new ColorAction(nodes, 
//		VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
//	fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
//	fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));

	int[] palette = new int[] { ColorLib.rgb(255, 180, 180), ColorLib.rgb(190, 190, 255) };
	ActionList draw = new ActionList();
	draw.add(filter);
//	draw.add(fill);
	
	draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
	
	draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
	draw.add(new DataColorAction(nodes, "state", Constants.NOMINAL, VisualItem.FILLCOLOR, palette));
	
	draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));

	//MAD - some edge stroke coloring
	ColorAction edgeStrokeColor = new ColorAction(edges, 
		VisualItem.STROKECOLOR, ColorLib.gray(200));

	edgeStrokeColor.add(ExpressionParser.predicate("waycolor == 'color1'"), ColorLib.rgba(0,0,255,100));
	edgeStrokeColor.add(ExpressionParser.predicate("waycolor == 'color2'"), ColorLib.rgba(255,0,0,100));
	edgeStrokeColor.add(ExpressionParser.predicate("waycolor == 'color3'"), ColorLib.rgba(0,255,0,100));

	draw.add(edgeStrokeColor);

	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(new ForceDirectedLayout(graph));
	//animate.add(fill);

	//MAD - adding the edge layout to the animate action
	animate.add(new EdgeLayout(EDGE_DECORATORS));

	animate.add(new RepaintAction());

	// finally, we register our ActionList with the Visualization.
	// we can later execute our Actions by invoking a method on our
	// Visualization, using the name we've chosen below.
	m_vis.putAction("draw", draw);
	m_vis.putAction("layout", animate);

	m_vis.runAfter("draw", "layout");


	// --------------------------------------------------------------------
	// set up a display to show the visualization

	Display display = new Display(m_vis);
	display.setSize(sizeX,sizeY);
	display.pan(sizeX/2, sizeY/2);
	display.setForeground(Color.GRAY);
	display.setBackground(Color.WHITE);
	
	// main display controls
	display.addControlListener(new FocusControl(1));
	display.addControlListener(new DragControl());
	display.addControlListener(new PanControl());
	display.addControlListener(new ZoomControl());
	display.addControlListener(new WheelZoomControl());
	display.addControlListener(new ZoomToFitControl());
	display.addControlListener(new NeighborHighlightControl());

	// --------------------------------------------------------------------        
	// launch the visualization

	// now we run our action list
	m_vis.run("draw");

	add(display);
    }

    public void setGraph(Graph g, String label) {
	// update labeling
	DefaultRendererFactory drf = (DefaultRendererFactory)
	m_vis.getRendererFactory();
	((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);

	// update graph
	m_vis.removeGroup(graph);
	VisualGraph vg = m_vis.addGraph(graph, g);
	m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
	VisualItem f = (VisualItem)vg.getNode(0);
	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
	f.setFixed(false);
	
	//MAD - ADDING the renderer for the edges to the default renderer factory 

	drf.add(new InGroupPredicate(EDGE_DECORATORS), new LabelRenderer("weight"));


    }
    

    public static JPanel demo() {
	return demo((String)null, "label", 500, 500);
    }

    public static JPanel demo(String datafile, String label, int x, int y) {
	Graph g = null;
	sizeX = x;
	sizeY = y;
	if ( datafile == null ) {
	    g = GraphLib.getGrid(15,15);
	    label = "label";
	} else {
	    try {
		g = new GraphMLReader().readGraph(datafile);
	    } catch ( Exception e ) {
		e.printStackTrace();
		System.exit(1);
	    }
	}
	return demo(g, label);
    }

	public static JPanel demo(Graph g, String label) {
	final GraphViewEdgeDecoratorV2 view = new GraphViewEdgeDecoratorV2(g, label);

	// set up menu
	
	// launch window
	JPanel panel = new JPanel();
	//panel.setVisible(true);	
	view.m_vis.run("layout");
	panel = view;

	return panel;
    }


    // ------------------------------------------------------------------------

    /**
     * Swing menu action that loads a graph into the graph viewer.
     */ 

    /*
     * MAD - the layout for the edges which places the relation type
     * in the middle of the edge
     */

    class EdgeLayout extends Layout {
	public EdgeLayout(String group) {
	    super(group);
	}
	public void run(double frac) {
	    Iterator iter = m_vis.items(m_group);
	    while ( iter.hasNext() ) {
		DecoratorItem decorator = (DecoratorItem)iter.next();
		VisualItem decoratedItem = decorator.getDecoratedItem();

		if (decoratedItem.isVisible()) {
		    
		    Rectangle2D bounds = decoratedItem.getBounds();

		    double x = bounds.getCenterX();
		    double y = bounds.getCenterY();

		    setX(decorator, null, x);
		    setY(decorator, null, y);
		} 
		decorator.setVisible(decoratedItem.isVisible());

	    }
	}
    }


} // end of class GraphView
