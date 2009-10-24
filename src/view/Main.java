package view;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Parser;
import controller.Graph;
import controller.Search;

import model.Edge;
import model.Node;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class Main { //extends org.eclipse.swt.widgets.Composite {


	private Shell shell;
	private Menu mainMenu;
	private MenuItem fileMenuItem;
	private Menu fileMenu;
	private MenuItem openFileMenuItem;
	private MenuItem exitMenuItem;
	private MenuItem helpMenuItem;
	private Menu helpMenu;
	private MenuItem contentsMenuItem;
	private MenuItem aboutMenuItem;
	private Composite groupComposite;
	private Composite graphComposite;
	
	private Node nodeSource;
	private Node nodeDest;
	private int metricValue;
	
	private boolean astar = false;
	private boolean width =false;
	
	private String fileName;
	private Parser parser = new Parser(); 
	private Graph graph = new Graph();
	private Combo source;
	private Combo destination;
	private Combo metric;
	private ArrayList<Edge> list = null;
	private java.awt.Frame graphFrame;
	private java.awt.Panel panel;
	
	private static final String[] metrics = {"Dist�ncia", "Tempo de Retardo", 
	     "Hop Count", "Largura de Banda"};
	
	
	public Main(Composite parent, int style) {
		this.shell = parent.getShell();
		initGUI();
	}
	
	
	private void initGUI() {
		try{
			//this.setSize(new org.eclipse.swt.graphics.Point(800,600));
			//this.setLayout(new GridLayout(2,true));
			
			mainMenu = new Menu(shell, SWT.BAR);
			shell.setMenuBar(mainMenu);
				fileMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
				fileMenuItem.setText("Arquivo");
				fileMenu = new Menu(fileMenuItem);
					openFileMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
					openFileMenuItem.setText("Abrir");
					openFileMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							openFile();
						}
					});

					exitMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
					exitMenuItem.setText("Sair");
					exitMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							shell.dispose();
						}
					});

				fileMenuItem.setMenu(fileMenu);	
				
				helpMenuItem = new MenuItem(mainMenu, SWT.CASCADE);
				helpMenuItem.setText("Ajuda");
					helpMenu = new Menu(helpMenuItem);
						contentsMenuItem = new MenuItem(helpMenu, SWT.CASCADE);
						contentsMenuItem.setText("Conte�do");
						contentsMenuItem.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent evt) {
								final Shell dialog = new Shell(shell);
							    dialog.setText("Conte�do");
							    dialog.setSize(300, 150);
							    dialog.setLayout(new RowLayout(SWT.VERTICAL));
							    new Label(dialog, SWT.NONE).setText("Este trabalho cont�m a implementa��o\n" +
							    		"dos algoritmos A* e Busca em Largura para a \nsolu��o do problema de " +
							    		"roteamento \nem redes de computadores.\n");
							    Button btnOk = new Button(dialog, SWT.PUSH);
								btnOk.setText("Ok");
								btnOk.addListener(SWT.Selection, new Listener() {
									public void handleEvent(Event event) {
										shell.dispose();
									}
								});
								dialog.open();
							}
						});
						
						aboutMenuItem = new MenuItem(helpMenu, SWT.CASCADE);
						aboutMenuItem.setText("Sobre");
						aboutMenuItem.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(SelectionEvent evt) {
								final Shell dialog = new Shell(shell);
							    dialog.setText("Sobre");
							    dialog.setSize(350, 190);
							    dialog.setLayout(new RowLayout(SWT.VERTICAL));
							    new Label(dialog, SWT.NONE).setText("Trabalho de Intelig�ncia Artificial - 1� Bimestre\n\n" +
							    		"Alunos:\n" + "Igor Henrique da Cruz\n" + "Newton Muchael Jos�\n" + 
							    		"Saulo Campos Nunes de Souza\n");
							    Button btnOk = new Button(dialog, SWT.PUSH);
								btnOk.setText("Ok");
								btnOk.addListener(SWT.Selection, new Listener() {
									public void handleEvent(Event event) {
										dialog.dispose();
									}
								});
							    dialog.open();
							}
						});
						
				helpMenuItem.setMenu(helpMenu);

						
				
			groupComposite = new Composite(shell, SWT.NONE);
			//groupComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			groupComposite.setLayout(new FillLayout());
			
			Composite fakeComposite = new Composite(groupComposite, SWT.BEGINNING);
			//groupComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			fakeComposite.setLayout(new RowLayout(SWT.BEGINNING));
			
			Group groupRouter = new Group(fakeComposite, SWT.SHADOW_IN);
			groupRouter.setText("Roteadores");
			groupRouter.setLayout(new RowLayout(SWT.VERTICAL));
				new Label(groupRouter, SWT.NONE).setText("Origem");
				source = new Combo(groupRouter, SWT.DROP_DOWN);
				source.addSelectionListener(new SelectionListener() {
				      public void widgetSelected(SelectionEvent e) {
				    	  nodeSource = graph.getNodeByName(source.getText());
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				        
				        }
				});
				new Label(groupRouter, SWT.NONE).setText("Destino");
				destination = new Combo(groupRouter, SWT.DROP_DOWN);
				destination.addSelectionListener(new SelectionListener() {
				      public void widgetSelected(SelectionEvent e) {
				    	  nodeDest = graph.getNodeByName(destination.getText());
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				        
				        }
				});
				
			Group groupMetric = new Group(fakeComposite, SWT.SHADOW_IN);
			groupMetric.setText("M�trica");
			groupMetric.setLayout(new RowLayout(SWT.VERTICAL));
				metric = new Combo(groupMetric, SWT.DROP_DOWN);
				metric.setItems(metrics);
				metric.addSelectionListener(new SelectionListener() {
				      public void widgetSelected(SelectionEvent e) {
				    	  for (int i = 0; i < metrics.length; i++){
				    		  if (metrics[i].equalsIgnoreCase(metric.getText())){
				    			  metricValue = i;
				    		  }
				    	  }
				    	  
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				        
				        }
				});
					
			Group groupAlg = new Group(fakeComposite, SWT.SHADOW_IN);
			groupAlg.setText("Algoritmo");
			groupAlg.setLayout(new FillLayout(SWT.HORIZONTAL));
				Button btnAce = new Button(groupAlg, SWT.RADIO);
				btnAce.setText("A*");
				btnAce.addSelectionListener(new SelectionListener() {
				      public void widgetSelected(SelectionEvent e) {
				    	  astar = true;
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				    	  astar = true;
				      }
				});
				Button btnwidth = new Button(groupAlg, SWT.RADIO);
				btnwidth.setText("Largura");
				btnwidth.addSelectionListener(new SelectionListener() {
				      public void widgetSelected(SelectionEvent e) {
				    	  width = true;
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				          width = true;
				      }
				});
				
			Button btnSearch = new Button(fakeComposite, SWT.PUSH);
			btnSearch.setText("Procurar");
			btnSearch.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					
					Search.estadoDeEnlace(graph, metricValue);
					
//					list = new ArrayList<Edge>();
//					
//					System.out.println(astar);
//					if (astar == true){
//						list = Search.AStar(nodeSource, nodeDest, graph, metricValue);
//					}
//					if (width == true){
//						list = Search.width(nodeSource, nodeDest, graph, metricValue);
//					}
//					System.out.println("Tamanho da lista: " + list.size());
//					for (Edge name : list){
//						System.out.println(name.getSrc().getName() + "->" + name.getDst().getName());
//					}
//					String tmppath = "tmppath.xml";
//					
//					try {
//						System.out.println("antes metricvalue-> " + metricValue);
//						parser.GraphToXml(graph, list, metricValue, tmppath);
//						//panel = new java.awt.Panel(new java.awt.BorderLayout());
//						graphFrame.remove(panel);
//						
//						panel = new java.awt.Panel(new java.awt.BorderLayout());						
//					    panel.add(GraphViewEdgeDecoratorV2.demo(tmppath, "name", graphComposite.getSize().x, graphComposite.getSize().x));
//					    
//					    list = null;
//					   	    
//					    graphFrame.add(panel);
//					    graphFrame.repaint();
//					    graphFrame.setVisible(true);
//					    
//					    
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					//list.add(graph.getEdgelist().get(16));
//					//list.add(graph.getEdgelist().get(16));
//					//list.add(graph.getEdgelist().get(16));
//					
					
				}
			});
			
			graphComposite = new Composite(shell, SWT.EMBEDDED);
			//graphComposite.setLayout(new GridLayout());
			graphComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			//graphComposite.setSize(500, 350);
			
			
			graphFrame = SWT_AWT.new_Frame(graphComposite);
			
			Composite hidden = new Composite(shell, SWT.NONE);
			
			Group groupSubtitle = new Group(shell, SWT.SHADOW_IN);
			groupSubtitle.setText("Legenda");
			groupSubtitle.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			Group groupSubRouter = new Group(groupSubtitle, SWT.SHADOW_IN);
			groupSubRouter.setText("Roteadores");
			groupSubRouter.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			CLabel rotUp = new CLabel(groupSubRouter, SWT.LEFT);
			rotUp.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			rotUp.setText("   ");
			CLabel up = new CLabel(groupSubRouter, SWT.LEFT);
			up.setText("Up");
			
			CLabel rotDown = new CLabel(groupSubRouter, SWT.LEFT);
			rotDown.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			rotDown.setText("   ");
			CLabel down = new CLabel(groupSubRouter, SWT.LEFT);
			down.setText("Down");
			
			Group groupSubLink = new Group(groupSubtitle, SWT.SHADOW_IN);
			groupSubLink.setText("Link");
			groupSubLink.setLayout(new RowLayout(SWT.HORIZONTAL));
			
			CLabel rotAvailable = new CLabel(groupSubLink, SWT.LEFT);
			rotAvailable.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			rotAvailable.setText("   ");
			CLabel available = new CLabel(groupSubLink, SWT.LEFT);
			available.setText("Dispon�vel");
			
			CLabel rotUnAvailable = new CLabel(groupSubLink, SWT.LEFT);
			rotUnAvailable.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			rotUnAvailable.setText("   ");
			CLabel unAvailable = new CLabel(groupSubLink, SWT.LEFT);
			unAvailable.setText("Indispon�vel");
			
			CLabel rotPath = new CLabel(groupSubLink, SWT.LEFT);
			rotPath.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			rotPath.setText("   ");
			CLabel path = new CLabel(groupSubLink, SWT.LEFT);
			path.setText("Caminho");
			
			
			
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
		
	
	public void openFile()
	{

		try {
			FileDialog dlg = new FileDialog(shell, SWT.OPEN);
			dlg.setFilterNames(new String[] {"txt", "Todos os arquivos (*.*)" });
			dlg.setFilterExtensions(new String[] {"*.txt", "*.*" });
			dlg.setText("Abrir Arquivo...");
			fileName = dlg.open();
			if (fileName != null) {

				String infile = "config.xml";
				
				parser.TextToGraph(fileName, graph);
				
				parser.GraphToXml(graph, list, 0, infile);
				
				source.setItems(graph.getNodeNames());
				destination.setItems(graph.getNodeNames());
				
				if (panel != null){
					graphFrame.remove(panel);
					panel = null;
				}
				
				panel = new java.awt.Panel(new java.awt.BorderLayout());
			    panel.add(GraphViewEdgeDecoratorV2.demo(infile, "name", graphComposite.getSize().x, graphComposite.getSize().y));

			    //panel.add(teste);
			    //panel.setSize(500, 350);
			    graphFrame.add(panel);
			    //graphFrame = teste;
			    panel.setVisible(true);
			    graphFrame.setVisible(true);
			    //graphFrame.setSize(500, 350);
			    
			    
			}else
				System.out.println("Cancelar");
		}catch (Exception e){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
			messageBox.setMessage("Erro ao abrir o arquivo" );
		}

	}
	
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setText("Trabalho de Comunicação de Dados");
		shell.setMaximized(true);
		Main inst = new Main(shell, SWT.NULL);
		//Point size = inst.getSize();
		GridLayout layout = new GridLayout(2,false);
		
		shell.setLayout(layout);
		shell.layout();
		/*if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}*/
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}


}
