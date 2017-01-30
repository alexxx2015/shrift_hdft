package de.tum.in.i22.ucwebmanager.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.gwt.thirdparty.guava.common.io.Files;
import com.vaadin.annotations.Push;
import com.vaadin.data.Item;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.tum.in.i22.ucwebmanager.Configuration;
import de.tum.in.i22.ucwebmanager.DB.App;
import de.tum.in.i22.ucwebmanager.DB.AppDAO;
import de.tum.in.i22.ucwebmanager.FileUtil.FileUtil;
import de.tum.in.i22.ucwebmanager.FileUtil.MD5Checksum;
import de.tum.in.i22.ucwebmanager.Status.Status;
import de.tum.in.i22.ucwebmanager.analysis.Analyser;
import de.tum.in.i22.ucwebmanager.dashboard.DashboardViewType;
import de.tum.in.i22.ucwebmanager.deploy.DeployManager;

@Push()
public class MainView extends VerticalLayout implements View {
	Grid grid = new Grid();
	Upload upload;
	File fileTmp;
	App app;
	String appName;
	String hashCodeOfApp;
	Map<Integer, String> map = new HashMap<Integer, String>();
    final List<Integer> coords = new ArrayList<>();
	public MainView() {

		Label lab = new Label("Main view");
		lab.setSizeUndefined();
		lab.addStyleName(ValoTheme.LABEL_H1);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	// .../src/main/webapp	
		//System.out.println(basepath);
		FileResource res = new FileResource(new File(basepath
				+ "/img/tum_logo.png"));
		Image image = new Image(null, res);
		image.setHeight("100%");
		image.setWidth("100%");
		
		
		HorizontalLayout horLayout = new HorizontalLayout();
		//horLayout.setMargin(true);
		horLayout.addComponent(lab);
		horLayout.addComponent(image);
		// Upload
		Upload.Receiver receiver = createReceiver();
		upload = new Upload("Upload your App", receiver);
		upload.addStyleName("myCustomUpload");
		upload.addSucceededListener(new SucceededListener() {

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				try {
					hashCodeOfApp = MD5Checksum.getMD5Checksum(fileTmp.getPath());
					if (!isAppAlreadyExisting(hashCodeOfApp))
						createFolderAndSaveApp(appName);
					else {
						showNotification("App already uploaded!");
						fileTmp.delete();
					}
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// Table
		grid.setSizeFull();
		fillGrid();
		gridClickListener();
		addComponent(horLayout);
		addComponent(upload);
		addComponent(grid);
	}
	
	private void configureIntegerField(final TextField integerField) {
        integerField.setConverter(Integer.class);
        integerField.addValidator(new IntegerRangeValidator("only integer, 0-500", 0, 500));
        integerField.setRequired(true);
        integerField.setImmediate(true);
    }
	
	@Override
	public void enter(ViewChangeEvent event) {

		if (!event.getParameters().equals("")) {
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			
			if (msgs[0].equals(DashboardViewType.STATANALYSIS.getViewName())) {
				int appId = 0;
				String configFile = "";
				for (int i = 1; i < msgs.length; i++){
					if (i == 1) {
						appId = Integer.parseInt(msgs[i]);
					}
					else configFile = msgs[i];
				}
				// start static analysis, update start time
				try {
					App app = AppDAO.getAppById(appId);
					Analyser analyser = new Analyser(app, configFile);
					analyser.start();
					if (!app.getStatus().equals(Status.INSTRUMENTATION)) {
						app.setStatus(Status.STATICANALYSIS.getStage());
						AppDAO.updateStatus(app, Status.STATICANALYSIS.getStage());
						updateStatus(app);
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
			else if (msgs[0].equals(DashboardViewType.INSTRUMENT.getViewName())) {
				int appId = Integer.parseInt(msgs[1]);
				
				try {
					App app = AppDAO.getAppById(appId);
					app.setStatus(Status.INSTRUMENTATION.getStage());
					AppDAO.updateStatus(app, Status.INSTRUMENTATION.getStage());
					updateStatus(app);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
			// map.put(appId, inputStream);
			
		}

	}
	
	private boolean isAppAlreadyExisting(String appHashcode) {
		String appFolderPath = FileUtil.Dir.APPS.getDir()+ "/" + String.valueOf(appHashcode);
		File appDir = new File(appFolderPath);
		return appDir.exists();
	}
	
	private void showNotification(String message) {
		Notification notification = new Notification("Message box");
		notification.setDescription(message);
        notification.setHtmlContentAllowed(true);
        notification.setStyleName("tray dark small closable login-help");
        notification.setPosition(Position.BOTTOM_RIGHT);
        notification.setDelayMsec(5000);
        notification.show(Page.getCurrent());
	}

	private void createFolderAndSaveApp(String fileName) throws IOException, SQLException {
//		String path = Configuration.WebAppRoot + "/apps/"+ String.valueOf(hashCodeOfApp)+"/";
		
		String path = FileUtil.Dir.APPS.getDir()+ "/" + String.valueOf(hashCodeOfApp);
		System.out.println(path);
		File dirCode = new File(path + FileUtil.Dir.CODE.getDir());
		File dirOutput = new File(path +  FileUtil.Dir.JOANAOUTPUT.getDir());
		File dirConfig = new File(path + FileUtil.Dir.JOANACONFIG.getDir());
		File dirInstrusment = new File(path + FileUtil.Dir.INSTRUMENTATION.getDir());
		File dirRuntime = new File(path + FileUtil.Dir.RUNTIME.getDir());
		boolean success = dirCode.mkdirs()&& dirOutput.mkdirs() && dirConfig.mkdirs()
							&& dirInstrusment.mkdirs() && dirRuntime.mkdirs();
		if (success) {
			
			File fileSave = new File(dirCode, fileName);
			Files.move(fileTmp, fileSave);
			fileTmp.delete();
			FileUtil.unzipFile(dirCode, fileName);
			try {			
				// create App
				app = new App( fileName, hashCodeOfApp, Status.NONE.getStage());
				//saveToDB(fileName, hashCode, path, Status.NONE.getStage());
				AppDAO.saveToDB(app);
				updateTable(app);
				new Notification("Success!",
						"<br/>File uploaded, Folder created, saved to DB!",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Directory is not created");
		}
	}

	private Upload.Receiver createReceiver() {
		Upload.Receiver receiver = new Upload.Receiver() {
			FileOutputStream fos = null;

			@Override
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					appName = filename;
					fileTmp = new File(Configuration.WebAppRoot + "/apps/tmp/"
							+ filename);
					fos = new FileOutputStream(fileTmp);
				} catch (IOException e) {

					e.printStackTrace();
					return null;
				}
				return fos;
			}

		};
		return receiver;
	}

	private void fillGrid() {
		grid.setEditorEnabled(false);
		grid.setSelectionMode(SelectionMode.SINGLE);
		SingleSelectionModel selection = (SingleSelectionModel) grid.getSelectionModel();
		grid.addColumn("ID", Integer.class);
		grid.getColumn("ID").setMaximumWidth(50).setEditable(false);
		grid.addColumn("Name", String.class);
		grid.addColumn("Hash Code",String.class);
		grid.addColumn("Status",String.class);
		grid.getColumn("Status").setMaximumWidth(60);
		grid.addColumn("Static Analysis", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId(); // get the selected rows id
			Item item = grid.getContainerDataSource().getItem(selected);
			String status = item.getItemProperty("Status").getValue().toString();
			// Send a message to static analyser view with the id of app
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.STATANALYSIS.getViewName() +
						"/" + item.getItemProperty("ID").getValue().toString());
			
		}));
		grid.addColumn("SA execute time", String.class);
		grid.addColumn("Instrumentation", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId();
			Item item = grid.getContainerDataSource().getItem(selected);
			String stat = item.getItemProperty("Status").getValue().toString();
			if (!stat.equals(Status.NONE.getStage())){
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.INSTRUMENT.getViewName() +
						"/" + item.getItemProperty("ID").getValue().toString());
			}
			else {
				new Notification("Error!",
						"<br/>Action not allowed",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			}
		}));
		grid.addColumn("Execute time", String.class);
		grid.addColumn("Deployment", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId();
			Item item = grid.getContainerDataSource().getItem(selected);
			UI.getCurrent().getNavigator().navigateTo(DashboardViewType.DEPLOYMENT.getViewName() +
					"/" + item.getItemProperty("ID").getValue().toString());
		}));
		grid.addColumn("Run time", String.class).setRenderer(new ButtonRenderer(e->{
			Object selected =  e.getItemId(); // get the selected rows id
			Item item = grid.getContainerDataSource().getItem(selected);
			String stat = item.getItemProperty("Status").getValue().toString();
			if (stat.equals(Status.INSTRUMENTATION.getStage())){
				UI.getCurrent().getNavigator().navigateTo(DashboardViewType.RUNTIME.getViewName() +
						"/" + item.getItemProperty("ID").getValue().toString());
			}
			else {
				new Notification("Error!",
						"<br/>Action not allowed",
						Notification.Type.WARNING_MESSAGE, true).show(Page.getCurrent());
			}
		}));
		grid.sort("ID", SortDirection.ASCENDING);
		List<App> allApp = AppDAO.getAllApps();
		for (App a : allApp) {
			updateTable(a);
		}
//		grid.getContainerDataSource()
	}
	private Item findRow(int id){
		for (Object rowID : grid.getContainerDataSource().getItemIds()) {
			Item item = grid.getContainerDataSource().getItem(rowID);
			if (id == Integer.parseInt(item.getItemProperty("ID").getValue().toString()))
				return item;
		}
		return null;
	}

	private void updateTable(App app) {
		grid.addRow(app.getId(),app.getName(), app.getHashCode(), app.getStatus(), "Go", "", "Go", "", "Go", "Go");
	}
	
	private void updateStatus (App app) {
		Item row = findRow(app.getId());
		row.getItemProperty("Status").setValue(app.getStatus());
		//Because of a bug, the grid is not automatically refreshed
		//Call clearSortOrder is a workaround
		grid.clearSortOrder();
	}
	
	private void updateStartTimeTable(int appId){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
		Date date = new Date();
		String stringDate = dateFormat.format(date);
		System.out.println(stringDate);
		SingleSelectionModel m  = (SingleSelectionModel) grid.getSelectionModel();
	}
	private void gridClickListener(){
//		int size = grid.getContainerDataSource().size();
//		for (int i = 0; i < size; )
		grid.addSelectionListener(selectionEvent -> { // Java 8
		    // Get selection from the selection model
		    Object selected = ((SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
		    if (selected != null){
		    	int appId =  (Integer) grid.getContainerDataSource().getItem(selected).getItemProperty("ID").getValue();
//		    	System.out.println(map.get(appId));
		        Notification.show(map.get(appId));
		    }
		    else
		        Notification.show("Nothing selected");
		});
	}
}