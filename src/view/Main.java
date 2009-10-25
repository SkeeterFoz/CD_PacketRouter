package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Vector;

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
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class Main {


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
	
	private List listDown;
	private String[] textListDown;
	private List listUp;
	private String[] textListUp;
	
	private boolean astar = false;
	private boolean width = false;
	
	private String fileName;
	private Parser parser = new Parser(); 
	private Graph graph = new Graph();
	private Combo source;
	private Combo destination;
	private Combo metric;
	private ArrayList<Edge> list = null;
	private ArrayList<Node> nodeList;
	private java.awt.Frame graphFrame;
	private java.awt.Panel panel;
	
	private Tree tree;
	private TreeItem item =  null;
	private TreeItem subItem;
	private TreeItem subsubItem;
	private Table table;
	private TableItem tableItem;
	
	private Timer timer;
	
	private static final String[] metrics = {"Distância", "Tempo de Retardo", 
	     "Hop Count", "Largura de Banda"};
	
	
	public Main(Composite parent, int style) {
		this.shell = parent.getShell();
		initGUI();
	}
	
	
	private void initGUI() {
		try{

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
										dialog.dispose();
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
			groupComposite.setLayout(new FillLayout());
			
			Composite fakeComposite = new Composite(groupComposite, SWT.BEGINNING);
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
			groupMetric.setText("Métrica");
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
				    	  refreshGraph();
				    	  
				      }
				      public void widgetDefaultSelected(SelectionEvent e) {
				        
				        }
				});
			
				Group groupConfig = new Group(fakeComposite, SWT.SHADOW_IN);
				groupConfig.setText("Configuração");
				groupConfig.setLayout(new RowLayout(SWT.HORIZONTAL));
				
					Group groupDown = new Group(groupConfig, SWT.SHADOW_IN);
					groupDown.setText("Down");
					groupDown.setLayout(new RowLayout(SWT.HORIZONTAL));
					
					listDown = new List(groupDown, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
					listDown.setSize(100, 60);
					
					listDown.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							textListDown = listDown.getSelection();
						}
					});
					
					Composite buttonComposite = new Composite(groupConfig, SWT.BEGINNING);
					buttonComposite.setLayout(new RowLayout(SWT.VERTICAL));
					
					Button btnDown = new Button(buttonComposite, SWT.PUSH);
					btnDown.setText(">>");
					
					btnDown.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							try{
								for (int i = 0; i < textListDown.length; i++){
									listUp.add(textListDown[i]);
									listDown.remove(textListDown[i]);
									for (Node node:nodeList){
								    	if (node.getName().equalsIgnoreCase(textListDown[i])){
								    		node.setState(true);
								    	}
								    }
								}
							textListDown = null;
							graph.setNodelist(nodeList);
							refreshGraph();
							}catch(Exception e){
								System.out.println(e.getMessage());
							}
						}
					});
					
					Button btnUp = new Button(buttonComposite, SWT.PUSH);
					btnUp.setText("<<");
					btnUp.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							try{
								for (int i = 0; i < textListUp.length; i++){
									listDown.add(textListUp[i]);
									listUp.remove(textListUp[i]);
									for (Node node:nodeList){
								    	if (node.getName().equalsIgnoreCase(textListUp[i])){
								    		node.setState(false);
								    	}
								    }
								}
							textListDown = null;
							graph.setNodelist(nodeList);
							refreshGraph();
							}catch(Exception e){
								System.out.println(e.getMessage());
							}
						}
					});
					
					Group groupUp = new Group(groupConfig, SWT.SHADOW_IN);
					groupUp.setText("Up");
					groupUp.setLayout(new RowLayout(SWT.HORIZONTAL));
					
					listUp = new List(groupUp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);  
					listUp.setSize(100, 60);
	
					listUp.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							textListUp = listUp.getSelection();
						}
					});
					
			Group groupUhet = new Group(fakeComposite, SWT.SHADOW_IN);
			groupUhet.setText("Tabela de Roteamento");
		    groupUhet.setLayout(new RowLayout(SWT.HORIZONTAL));
		    
			    tree = new Tree(groupUhet, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			    TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
			    column1.setWidth(200);
			    
				
			    //TODO TABELAS
/*			    for (int i = 0; i < 10; i++) {
				      tableItem = new TableItem(table, SWT.NONE);
				      for (int j = 0; j < 3; j++) {
				        tableItem.setText(j, "rot" + i + "," + j);
				      }
				    }
			    for (int i = 0, n = table.getColumnCount(); i < n; i++) {
				      table.getColumn(i).pack();
				    }*/
			    
			    table = new Table(groupUhet, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
				    for (int i = 0; i < 3; i++) {
				      TableColumn column = new TableColumn(table, SWT.NONE);
				      column.setWidth(60);
				    }
				   
				    
				
				
			/*Button btnSearch = new Button(fakeComposite, SWT.PUSH);
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
//					
//					refreshGraph();					
				}
			});*/
			
			graphComposite = new Composite(shell, SWT.EMBEDDED);
			graphComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
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
			available.setText("Disponível");
			
			CLabel rotUnAvailable = new CLabel(groupSubLink, SWT.LEFT);
			rotUnAvailable.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			rotUnAvailable.setText("   ");
			CLabel unAvailable = new CLabel(groupSubLink, SWT.LEFT);
			unAvailable.setText("Indisponível");
			
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
				
				nodeList = graph.getNodelist();
				
				destination.setItems(graph.getNodeNames());
				
				
				
				if (panel != null){
					graphFrame.remove(panel);
					panel = null;
				}
				
				panel = new java.awt.Panel(new java.awt.BorderLayout());
			    panel.add(GraphViewEdgeDecoratorV2.demo(infile, "name", graphComposite.getSize().x, graphComposite.getSize().y));		 
			    
			    for (Node node:nodeList){
			    	if (node.isState()){
			    		listUp.add(node.getName());
			    	}else{
			    		listDown.add(node.getName());
			    	}
			    }
			    
			    
			    ArrayList<String> saList = getSaList(nodeList);
			    for (String in:saList){
			    	item = new TreeItem(tree, SWT.NONE);
			    	item.setText(new String[] { in });
				    for (Node node:nodeList){
				    	if (item.getText().equalsIgnoreCase(node.getSa())){
				    		subItem = new TreeItem(item, SWT.NONE);
					        subItem.setText(new String[] { node.getName() });
				    	}
				    	//TODO SUBSUBITENS - TABELAS
				    }
			    }
			    
			    
			    graphFrame.add(panel);
			    panel.setVisible(true);
			    graphFrame.setVisible(true);
			    
			    timer = new Timer();
			    timer.schedule(new Search(), 0, 30000);
			    
			    
			}else
				System.out.println("Cancelar");
		}catch (Exception e){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
			messageBox.setMessage("Erro ao abrir o arquivo" );
		}

	}
	
	public void refreshGraph(){
		try {
			String tmppath = "tmppath.xml";
			parser.GraphToXml(graph, list, metricValue, tmppath);
			
			graphFrame.remove(panel);
			graphFrame.dispose();
			
			panel = new java.awt.Panel(new java.awt.BorderLayout());						
		    panel.add(GraphViewEdgeDecoratorV2.demo(tmppath, "name", graphComposite.getSize().x, graphComposite.getSize().x));
		    
		    list = null;
		   	    
		    graphFrame.add(panel);
		    graphFrame.repaint();
		    graphFrame.setVisible(true);
		    
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> getSaList(ArrayList<Node> nodes){
		ArrayList<String> saList = new ArrayList<String>();
		
		for (Node node:nodes){
			if (saList.contains(node.getSa()) == false){
				saList.add(node.getSa());
			}
		}
		return saList;
	}
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setText("Trabalho de Comunicação de Dados - Simulação de Roteamento de Pacotes em Rede");
		shell.setMaximized(true);
		Main inst = new Main(shell, SWT.NULL);
		GridLayout layout = new GridLayout(2,false);
		
		shell.setLayout(layout);
		shell.layout();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}


}
