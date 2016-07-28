package de.tum.in.i22.ucwebmanager.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.BlackAndWhiteList;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.UcConfig;
import edu.tum.uc.jvm.Instrumentor;
public class InstrumentationView extends VerticalLayout implements View{
	App app;
	File file;
	String appName, arg0, arg1, arg2;
	String blackL = "black list", whiteL = "white list",
			blackListName = "/blacklist.list", whiteListName = "/whitelist.list";
	private int appId;
	private TextArea textArea;
	private final Table gridBlackList, gridWhiteList;
	private Button btnrun;
	private TextField txtSrcFolder, txtDestFolder;
	private ComboBox cmbReportFile;
	public InstrumentationView() {
		Label lab = new Label("Instrumentation");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		lab.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		//Combobox
		cmbReportFile = new ComboBox("Select Report File");
//		blackbox = new TableGrid("Black Box", "blackbox");
//		whitebox = new TableGrid("White box", "whitebox");
		gridBlackList = new Table("Black List");
		gridBlackList.addContainerProperty("black list", TextField.class, null);
		gridBlackList.setPageLength(5);
		gridBlackList.setWidth("100%");
		gridBlackList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					gridBlackList.select(event.getItemId());
				}
			}

		});

		gridBlackList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = gridBlackList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					gridBlackList.getItem(newItemId)
							.getItemProperty("black list").setValue(t);
				} else if (action.getCaption() == "Delete") {
					gridBlackList.removeItem(target);
				}
			}
		});
		gridWhiteList = new Table("White List");
		gridWhiteList.addContainerProperty("white list", TextField.class, null);
		gridWhiteList.setPageLength(5);
		gridWhiteList.setWidth("100%");
		gridWhiteList.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
					gridWhiteList.select(event.getItemId());
				}
			}

		});

		gridWhiteList.addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { new Action("New"), new Action("Delete") };
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action.getCaption() == "New") {
					Object newItemId = gridWhiteList.addItem();
					TextField t = new TextField();
					t.setWidth("100%");
					gridWhiteList.getItem(newItemId)
							.getItemProperty("white list").setValue(t);
				} else if (action.getCaption() == "Delete") {
					gridWhiteList.removeItem(target);
				}
			}
		});
		textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setVisible(false);
		txtSrcFolder = new TextField("Source Folder");
		txtSrcFolder.setWidth("100%");
//		txtSrcFolder.setReadOnly(true);
		
		txtDestFolder = new TextField("Destination Folder");
		txtDestFolder.setWidth("100%");
//		txtDestFolder.setReadOnly(true);
		
		btnrun = new Button("Run");
		btnrun.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
//				System.out.println(getReportFileFromComboBox());
				try {
					String hashCode = app.getHashCode();
					arg0 = FileUtil.getPathCode(hashCode);
					String reportFolder = (String) cmbReportFile.getValue();
					String reportFile = FileUtil.getPathOutput(hashCode) + "/" + reportFolder + "/report.xml";
					
					arg1 = FileUtil.getPathInstrumentationOfApp(app.getHashCode()) + "/" + reportFolder;
					File f = new File(arg1);
					f.mkdirs();
					
					// create blacklist.list
					List<String> bl = readDataFromTable(gridBlackList, blackL);
					BlackAndWhiteList.saveAndWrite(bl, arg1 + blackListName);
					
					//create whitelist.list
					List<String> wl = readDataFromTable(gridWhiteList, whiteL);
					BlackAndWhiteList.saveAndWrite(wl, arg1 + whiteListName);
					//create uc.config
					UcConfig uc = new UcConfig();
					uc.create(reportFile, arg1 + blackListName, arg1 + whiteListName);
					uc.save(arg1 + "/uc.config");
					
					arg2 = arg1 + "/uc.config";
					System.out.println(arg0);
					System.out.println(arg1);
					System.out.println(arg2);
					Instrumentor.main(new String[]{arg0, arg1, arg2});
				} catch (IOException | IllegalClassFormatException e) {
					e.printStackTrace();
				}
				
			}
		});
		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		
		fl.addComponent(cmbReportFile);
		fl.addComponent(textArea);
		fl.setSizeFull();
		fl.addComponent(textArea);
		fl.addComponent(gridBlackList);
		fl.addComponent(gridWhiteList);
		fl.addComponent(txtSrcFolder);
		fl.addComponent(txtDestFolder);
		fl.addComponent(btnrun);
		
		parent.addComponent(fl);
		
		parent.setMargin(true);
		addComponent(parent);
	}
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		fillBlackAndWhiteList();
		if (event.getParameters() != null) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			for (String msg : msgs) {
				appId = 0;
				if (msg != null && msg != "") {
					appId = Integer.parseInt(msg);
					System.out.println("enter Instrumentation view " + appId);

					try {
						app = AppDAO.getAppById(appId);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
					if (app != null) {
						appName = app.getName();
						fillComboBox(app);
						fillSrcAndDest(app);
					}

				}
			}
		}
	}
	 private String readXmlFile(File file){
			String xml = "";
			try (BufferedReader br = new BufferedReader(new FileReader(file))){
				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					System.out.println(sCurrentLine);
					xml = xml + "\n" + sCurrentLine;
				} 
			}catch (IOException e) {
				e.printStackTrace();
			}
			return xml;
		}
	 private void fillComboBox(App app){
		 File staticAnalysisOutput = new File(FileUtil.getPathOutput(app.getHashCode()));
		 ArrayList<String> names = new ArrayList<String>(Arrays.asList(staticAnalysisOutput.list()));
		 for (String name : names) cmbReportFile.addItem(name);
	 }
	 private void fillSrcAndDest(App app){
		 String txtSrcFolder = FileUtil.getPathCode(app.getHashCode());
		 String txtDestFolder = FileUtil.getPathInstrumentationOfApp(app.getHashCode());
		 this.txtSrcFolder.setReadOnly(false);
		 this.txtSrcFolder.setValue(txtDestFolder);
		 this.txtSrcFolder.setReadOnly(true);
		 this.txtDestFolder.setReadOnly(false);
		 this.txtDestFolder.setValue(txtSrcFolder);
		 this.txtDestFolder.setReadOnly(true);
		 
	 }
	 private String getReportFileFromComboBox(){
		 String reportFile = "/report.xml";
		 return FileUtil.getPathOutput(app.getHashCode()) + "/" + cmbReportFile.getValue() + reportFile;
	 }
	 private String generateUcConfigFile(){
		 
		 
		 return null;
	 }
	 private void fillBlackAndWhiteList(){
		 List<String> blackList = BlackAndWhiteList.read(FileUtil.getPathBlackAndWhiteList() + blackListName);
		 List<String> whiteList = BlackAndWhiteList.read(FileUtil.getPathBlackAndWhiteList() + whiteListName);
		 fillTable(blackList, gridBlackList, "black list");
		 fillTable(whiteList, gridWhiteList, "white list");
	 }
	 
	 private void fillTable(List<String> list, Table t, String property){
		 for (String s : list){
			 TextField txt = new TextField("textfield");
			 txt.setValue(s);
			 txt.setWidth(24.6f, ComboBox.UNITS_EM);
			 Object newItemId = t.addItem();
			Item row = t.getItem(newItemId);
			row.getItemProperty(property).setValue(txt);
			t.addItem(new Object[] { txt }, newItemId);
		 } 
	 }
	 private List<String> readDataFromTable(Table t, String property){
		 List<String> list = new ArrayList<String>();
		 int newItemId = t.size();
		 for (int i = 1; i <= newItemId; i++) {
				Item row = t.getItem(i);
				TextField temp = new TextField();
				temp = (TextField) row.getItemProperty(property).getValue();
				list.add(temp.getValue());
		 }
		 return list;
	 }
//	 private void initTable(Table table, String name, String property){
//		 table = new Table("ClassPath");
//			table.addContainerProperty("Classpath", TextField.class, null);
//			table.setPageLength(5);
//			table.setWidth("100%");
//			table.addItemClickListener(new ItemClickListener() {
//				public void itemClick(ItemClickEvent event) {
//					if (event.getButton() == MouseEventDetails.MouseButton.RIGHT) {
//						table.select(event.getItemId());
//					}
//				}
//
//			});
//
//			table.addActionHandler(new Action.Handler() {
//				public Action[] getActions(Object target, Object sender) {
//					return new Action[] { new Action("New"), new Action("Delete") };
//				}
//
//				@Override
//				public void handleAction(Action action, Object sender, Object target) {
//					if (action.getCaption() == "New") {
//						Object newItemId = table.addItem();
//						TextField t = new TextField();
//						t.setWidth("100%");
//						table.getItem(newItemId)
//								.getItemProperty("Classpath").setValue(t);
//					} else if (action.getCaption() == "Delete") {
//						table.removeItem(target);
//					}
//				}
//			});
//	 }
}