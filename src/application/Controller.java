package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Controller {

	public static final String EVENT_TYPE_CLICK = "click";
	@FXML
	private CheckBox copb;
	private FileChooser dc = new FileChooser();
	private WebEngine engine;
	private GlobalFlags gfl;
	@FXML
	private TextField imagePath;
	@FXML
	private ImageView imagePrev;
	@FXML
	private ImageView img_response;
	private ImageDeliverer imgD;
	double ixOffset = 0;
	double iyOffset = 0;
	private String lastFileDir;
	@FXML
	private Button loadbtn;
	private ChangeListener<State> mylistener;
	private HTMLParser myparser;
	private Post myPost;
	private Thread parseT;
	private Task<Void> parseTask;
	private PostParser pp;
	private ProxyList proxL;
	@FXML
	private Button reset;
	@FXML 
	private AnchorPane root;
	final WebView smallView = new WebView();
	@FXML 
	private TextField threadID;
	private Timer timer;
	@FXML 
	private TextArea txt_comment;
	@FXML
	private TextField txt_email;
	@FXML 
	private PasswordField txt_name = new PasswordField();
	@FXML 
	private TextField txt_subject;
	@FXML
	private WebView view;
	@FXML
	private void closeStage() {
		MyPostHandler mph = new MyPostHandler();
		mph.giveThreadID(threadID.getText());
		try {
			mph.saveList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.gc();
		Platform.exit();
		System.exit(0);
	}	
	@FXML
	protected void initialize() {
		proxL = ProxyList.getInstance();
		imgD = ImageDeliverer.getInstance();
		MyPostHandler mph = new MyPostHandler();
		mph.loadList();
		threadID.setText(mph.getThreadID());
		final Popup popup = new Popup();
		gfl = GlobalFlags.getInstance();
		lastFileDir = System.getProperty("user.dir");
		root = new AnchorPane();
		parseTask = new Task<Void>(){
			@Override
			protected Void call() {
				pp.parse(threadID.getText());
				return null;
			}};
			parseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t)
				{	
					updateView();  
					System.gc();
				}
			});
			assert txt_name != null : "fx:id=\"txt_name\" was not injected: check your FXML file 'Layout.fxml'.";
			pp = new PostParser();
			parseT = new Thread(parseTask);
			timer = new Timer();
			timer.schedule(
					new TimerTask() {
						@Override
						public void run() {
							if (!gfl.isParserWorking()) {
								System.out.println("Automated update task run.");
								if (!gfl.isParserWorking()) {	
									reload();}}
						}
					}, 120000, 60000);
			engine = view.getEngine();
			engine.loadContent("Content goes here");
			txt_comment.setText("");
			final Button close = new Button();
			close.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					popup.hide();	
				}

			});

			mylistener = new ChangeListener<Worker.State>() {
				@Override
				public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, Worker.State oldState, Worker.State newState) {
					if (newState == Worker.State.SUCCEEDED) {
						EventListener listener = new EventListener() {

							@Override
							public void handleEvent(org.w3c.dom.events.Event evt) {
								String domEventType = evt.getType();
								if (domEventType.equals(EVENT_TYPE_CLICK)) {
									if (((Element)evt.getTarget()).getAttribute("name") != null) {
										if (!((Element)evt.getTarget()).getAttribute("class").equalsIgnoreCase("crosslink")) {
											txt_comment.setText(txt_comment.getText() + ">>" + ((Element)evt.getTarget()).getAttribute("name") + "\n");
											txt_comment.positionCaret(txt_comment.getLength());
											txt_comment.requestFocus();
										} else {
											if  (((Element)evt.getTarget()).getAttribute("class").equals("crosslink")) {
												engine.executeScript("scrollToElement("+((Element)evt.getTarget()).getAttribute("name")+");");


											}
										}
									} 
									if (((Element)evt.getTarget()).getAttribute("class") != null) {
										if (((Element)evt.getTarget()).getAttribute("class").equals("image")) {
											String imglink = ((Element)evt.getTarget()).getAttribute("alt");
											imgD.storeStringImage(imglink);
											try {
												showImage();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								} 
							}
						};

						Document doc = engine.getDocument();
						NodeList nodeList = doc.getElementsByTagName("a");
						for (int i = 0; i < nodeList.getLength(); i++) {
							((EventTarget) nodeList.item(i)).removeEventListener(EVENT_TYPE_CLICK, listener, false);
							((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
						}
					}
				}
			};
			copb.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> ov,
						Boolean old_val, Boolean new_val) {
					System.out.println(new_val);
					pp.setCop(new_val); 
				}
			});
			copb.setSelected(true);
			pp.setCop(true); 
	}

	@FXML
	private synchronized void reload() {
		System.gc();
		if (!threadID.getText().equals("") && (threadID.getLength() == 7)) {
			if (!gfl.isParserWorking()) {	
				try {
					parseT.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				parseTask = new Task<Void>(){
					@Override
					protected Void call() {
						pp.parse(threadID.getText());
						return null;
					}};
					parseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent t)
						{	
							System.out.println("Parsing succeeded!");
							updateView();  
							System.gc();
						}
					});
					parseT = new Thread(parseTask);
					System.out.println("reloading...");
					try {
						if (!gfl.isParserWorking()) {
							parseT.start();}
					}   catch (NullPointerException e) {
						System.out.println("Error in PostParser!");
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Error in PostParser");
					}

			}} 
		
	}

	@FXML
	private void resetPost() {
		imagePath.setText(myPost.getImage());
		txt_comment.setText(myPost.getBody());
		txt_subject.setText(myPost.getSubject());
		txt_email.setText(myPost.getEmail());
		String str = "file:/" + imagePath.getText();
		str = str.replaceAll("\\\\", "/");
		Image fuckyou = new Image(str,260,200,true,true);
		imagePrev.setPreserveRatio(true);
		imagePrev.setImage(fuckyou);
		reset.setDisable(true);
	}


	@FXML
	private void setImageDirectory() {
		dc.setTitle("Browse Image Directory");
		dc.setInitialDirectory(new File(lastFileDir));
		File dir;
		FileChooser.ExtensionFilter fil = new FileChooser.ExtensionFilter("Image Files", "*.jpeg", "*.jpg","*.gif", "*.bmp", "*.png");
		dc.getExtensionFilters().add(fil);
		if ((dir = dc.showOpenDialog(null)) != null) {
			imagePath.setText(dir.getAbsolutePath());
			lastFileDir = dir.getParent();
			String os = System.getProperty("os.name").toLowerCase();
			String str = "";
			if (os.equals("linux")) {str = "file:" + dir.getAbsolutePath();} else
			{str = "file:/" + dir.getAbsolutePath();}
			str = str.replaceAll("\\\\", "/");
			Image fuckyou = new Image(str,260,200,true,true);
			imagePrev.setPreserveRatio(true);
			imagePrev.setImage(fuckyou);
		}

	}


	@FXML
	private void showCaptcha() throws IOException {
		URL whatismyip = new URL("http://icanhazip.com/");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String remoteAddr = in.readLine();
		in.close();
		proxL.add(new Proxy(remoteAddr));
		AnchorPane info;
		try  {
			info =(AnchorPane) FXMLLoader.load(getClass().getResource("proxy.fxml"));
			Stage infoStage = new Stage();
			infoStage.setTitle("Challenge");
			infoStage.initStyle(StageStyle.UNDECORATED);
			infoStage.setScene(new Scene(info,720,240));
			infoStage.getIcons().add(new Image("20160222031515.png"));
			infoStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void showImage() throws IOException {
		AnchorPane image;
		try  {
			image =(AnchorPane) FXMLLoader.load(getClass().getResource("image.fxml"));
			Stage infoStage = new Stage();
			infoStage.setTitle("Image");
			infoStage.initStyle(StageStyle.DECORATED);
			infoStage.setScene(new Scene(image,720,720));
			infoStage.getIcons().add(new Image("20160222031515.png"));
			image.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					ixOffset = event.getSceneX();
					iyOffset = event.getSceneY();
				}
			});
			image.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					infoStage.setX(event.getScreenX() - ixOffset);
					infoStage.setY(event.getScreenY() - iyOffset);
				}
			});
			infoStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void tryToPost() throws IOException {
		//Save the post! Maybe It fails!
		myPost = new Post(txt_subject.getText(),txt_name.getText(), txt_comment.getText(), txt_email.getText(), "0000000", Long.toString(System.currentTimeMillis()), imagePath.getText(), null,"", "", false);
		LinkedHashMap<Object, Object> params = new LinkedHashMap<Object, Object>();
		params.put("thread", threadID.getText());
		params.put("firstname", "");
		params.put("board", "b");
		params.put("name", txt_name.getText());
		params.put("email", txt_email.getText());
		params.put("subject", txt_subject.getText());
		params.put("password", "lolno");
		params.put("body", txt_comment.getText());  
		new Poster(params,imagePath.getText(),threadID.getText());
		imagePath.setText("");
		txt_comment.setText("");
		imagePrev.setImage(null);
		reset.setDisable(false);
	}

	private synchronized void updateView() {
		if (!gfl.isViewUpdaterWorking()) {
			gfl.setViewUpdaterWorking(true);
			myparser = new HTMLParser();
			try {
				String content = myparser.parsePosts();
				engine = view.getEngine();
				engine.loadContent(content);
				engine.getLoadWorker().stateProperty().removeListener(mylistener);
				engine.getLoadWorker().stateProperty().addListener(mylistener);
				content = null;}
			catch (NullPointerException e) {
				System.out.println("Error in HTMLParser");
			}
			view.setPrefWidth(860.0);
			myparser = null;
			gfl.setViewUpdaterWorking(false);
		}
	}
}

