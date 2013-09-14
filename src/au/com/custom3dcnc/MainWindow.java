/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import au.com.custom3dcnc.actions.ActionsUtil;
import au.com.custom3dcnc.actions.ExecuteFavourite;
import au.com.custom3dcnc.actions.GCodeParserEnum;
import au.com.custom3dcnc.actions.GenerateGCode;
import au.com.custom3dcnc.actions.SendData;
import au.com.custom3dcnc.actions.ZProbe;
import au.com.custom3dcnc.converter.ConverterWindow;
import au.com.custom3dcnc.converter.ConvertersEnum;
import j.extensions.comm.SerialComm;
import java.io.BufferedInputStream;
import javafx.geometry.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.GroupBuilder;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Serial Comm - https://code.google.com/p/serial-comm/downloads/list
 *
 * @author dutoitk
 */
public class MainWindow {

    private final TextArea console = new TextArea();
    private final TextField boardName = new TextField("");
    private final ComboBox comboBoxGcodeParser = new ComboBox();
    private final Label currentProfileNameLabel = new Label();
    private final ProgressBar progressBar = new ProgressBar();
    private static final MainWindow mainWindow = new MainWindow();
    private final TabPane tabPane = new TabPane();
    private String previousPortList = "";
    private String previousFavourites = "";

    private MainWindow() {
    }

    public static MainWindow getInstance() {
        return mainWindow;
    }

    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem properties = new MenuItem("Application properties");
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                PropertiesWindow.getInstance().setup().show();
            }
        });

        MenuItem configurationPy = new MenuItem("Edit configuration.py");
        configurationPy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DynamicPropertiesWindow.getInstance().setup(AppConfig.getInstance().getEtchConfigFile()).showAndWait();
                try {
                    ActionsUtil.fixPathVariable(new File(AppConfig.getInstance().getEtchConfigFile()).getCanonicalFile());
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        MenuItem gerber2GcodeConfig = new MenuItem("Edit pygerber2gcode_cui_MOD.conf");
        gerber2GcodeConfig.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DynamicPropertiesWindow.getInstance().setup(AppConfig.getInstance().getGerber2GcodeConfigFile()).showAndWait();
                try {
                    ActionsUtil.fixPathVariable(new File(AppConfig.getInstance().getGerber2GcodeConfigFile()).getCanonicalFile());
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });
        menuFile.getItems().addAll(properties, configurationPy, gerber2GcodeConfig, exit);

        menuBar.getMenus().addAll(menuFile, setupToolsMenu(), setupFavouritesMenu());

        return menuBar;
    }

    public void setup(final Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        BorderPane borderPane = new BorderPane();
        final Scene scene = new Scene(borderPane, 800, bounds.getHeight() - 50);

        primaryStage.setTitle("Cyclone PCB Factory GUI");
        primaryStage.setScene(scene);
        console.setMinHeight(300);

        final FileChooser fileChooser = new FileChooser();
        //setup top
        VBox top = new VBox();
        top.getChildren().add(setupMenuBar());
        borderPane.setTop(top);

        //setup center
        final VBox centerVBox = new VBox(10);
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        centerVBox.getChildren().addAll(gridPane, tabPane);
        borderPane.setCenter(centerVBox);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        int row = 1;
        int col = 1;
        final AppConfig appConfig = AppConfig.getInstance();
        //current profile
        final Label currentProfileLabel = new Label("Current profile");
        gridPane.add(currentProfileLabel, col, row);
        currentProfileNameLabel.setText(appConfig.getLastProfileName());
        gridPane.add(currentProfileNameLabel, col + 1, row);
        Button profileNameBrowse = createButtonProfileBrowse(fileChooser, appConfig, primaryStage);
        gridPane.add(profileNameBrowse, col + 2, row++);

        //location of the gerber2gcode config file
        final Label boardNameLabel = new Label("Board name");
        gridPane.add(boardNameLabel, col, row);
        boardName.setText(appConfig.getLastBoardName());
        boardName.setMinWidth(100);
        gridPane.add(boardName, col + 1, row);
        Button boardNameBrowse = createButtonBoardNameBrowse(fileChooser, appConfig, primaryStage);
        gridPane.add(boardNameBrowse, col + 2, row++);

        final Label gcodeParserLabel = new Label("GCode Parser");
        gridPane.add(gcodeParserLabel, col, row);

        comboBoxGcodeParser.setValue(GCodeParserEnum.PYTHON_PARSER);
        ArrayList<GCodeParserEnum> gcodeParserList = new ArrayList<>();
        Collections.addAll(gcodeParserList, GCodeParserEnum.values());
        comboBoxGcodeParser.getItems().addAll(gcodeParserList);
        gridPane.add(comboBoxGcodeParser, col + 1, row++);

        //display progress bar text top
        GridPane progressBarTopGrid = new GridPane();
        String topSpacer = "                 ";
        Button buttonGerber2GCode = createButtonGerber2GCode(scene);

        progressBarTopGrid.add(buttonGerber2GCode, 0, 0);
        progressBarTopGrid.add(new Text(topSpacer), 1, 0);
        Button sendEtchData = createButtonSendEtchData(scene);

        progressBarTopGrid.add(sendEtchData, 2, 0);
        progressBarTopGrid.add(new Text(topSpacer), 3, 0);
        progressBarTopGrid.add(new Label("Send etch data 3 |"), 4, 0);
        progressBarTopGrid.add(new Text(topSpacer), 5, 0);
        Button buttonSendEdge = createButtonSendEdge(scene);
        progressBarTopGrid.add(buttonSendEdge, 6, 0);
        gridPane.add(progressBarTopGrid, col + 1, row++);

        //display progress bar
        Label progressLabel = new Label("Progress");
        gridPane.add(progressLabel, col, row);

        progressBar.setMinSize(550, 20);
        progressBar.setProgress(ProgressBarEnum.NONE.getProgress());
        gridPane.add(progressBar, col + 1, row++);

        //display progress bar text bottom
        GridPane progressBarBottomGrid = new GridPane();
        String bottomSpacer = "                       ";
        progressBarBottomGrid.add(new Text(bottomSpacer), 0, 0);
        Button buttonZProbe = createButtonZProbe(scene);

        progressBarBottomGrid.add(buttonZProbe, 1, 0);
        progressBarBottomGrid.add(new Text(bottomSpacer), 2, 0);
        progressBarBottomGrid.add(new Label("Send etch data 2 |"), 3, 0);
        progressBarBottomGrid.add(new Text(bottomSpacer), 4, 0);
        Button buttonSendDrillData = createButtonSendDrillData(scene);
        progressBarBottomGrid.add(buttonSendDrillData, 5, 0);
        gridPane.add(progressBarBottomGrid, col + 1, row++);


        //test 3d
        Line line = LineBuilder.create().startX(46.419736).startY(57.047235).endX(37.265053).endY(57.047235).startX(30.368202).startY(63.944085).endX(30.369065).endY(63.945428). build();
        GroupBuilder.create().children(line).translateX(250).translateY(250).build();
        gridPane.add(line, col + 1, row++);



        //setup bottom/console
        VBox bottom = new VBox();
        bottom.getChildren().add(console);
        borderPane.setBottom(bottom);
    }

    private Timeline createTab(final String tabName, final String fileName, final int fitWidth, final int pollingCycle) {
        class InnerClassVariables {

            long lastModifiedTime;
            double previousX;
            double previousY;
        }

        final InnerClassVariables innerClassVariables = new InnerClassVariables();

        //setup image viewer
        final ImageView imageView = new ImageView();
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView);

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setContent(stackPane);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(fitWidth);

        imageView.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY();
                double currentFitWidth = imageView.getFitWidth();
                if (deltaY < 0) {
                    imageView.setFitWidth(currentFitWidth - 100);
                } else {
                    imageView.setFitWidth(currentFitWidth + 100);
                }
            }
        });

        final Timeline pollImage = new Timeline(new KeyFrame(Duration.seconds(0.1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String incompleteFileName = fileName.replace(".png", "_incomplete.png");
                            File incompleteFile = new File(incompleteFileName);
                            //only try to load the image when the incomplete file isn't there
                            if (!incompleteFile.exists()) {
                                File file = new File(AppConfig.getInstance().getGerberOutputDir() + "/" + fileName);
                                if (file.exists()) {
                                    //make sure the file has changed
                                    if (innerClassVariables.lastModifiedTime != file.lastModified()) {
                                        //try to solve memory leak
                                        Image image = imageView.getImage();
                                        image = null;
                                        imageView.setImage(null);
                                        System.gc();

                                        innerClassVariables.lastModifiedTime = file.lastModified();
                                        try (InputStream inputStream = new FileInputStream(file)) {
                                            try (BufferedInputStream bin = new BufferedInputStream(inputStream)) {
                                                image = new Image(bin);
                                            }
                                        }
                                        imageView.setImage(image);
                                    }
                                }
                            }

                        } catch (Exception ex) {
                            //consume this exception
                        }
                    }
                });
            }
        }));
        pollImage.setCycleCount(pollingCycle);
        pollImage.play();

        //setup top toolbar
        BorderPane borderPane = new BorderPane();
        HBox hboxToolbar = new HBox(10);
        hboxToolbar.setPadding(new Insets(10));
        borderPane.setTop(hboxToolbar);
        Button buttonZoomIn = createButtonZoomIn(imageView);
        Button buttonZoomOut = createButtonZoomOut(imageView);
        hboxToolbar.getChildren().addAll(buttonZoomIn, buttonZoomOut);

        //setup center
        borderPane.setCenter(scrollPane);
        Tab tab = new Tab(tabName);
        tab.setContent(borderPane);
        tabPane.getTabs().addAll(tab);

        //select the new tab
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);

        return pollImage;
    }

    private Button createButtonSendDrillData(final Scene scene) {
        final Button buttonSendDrillData = new Button("Send drill data |");
        buttonSendDrillData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                ProcessStepsWindow.getInstance().setup(console).showAndWait();

                startJob(buttonSendDrillData, scene);
                final TimeLineWrapper timeLineWrapper = new TimeLineWrapper();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //drill data
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapper.timeline = createTab(SendActionEnum.SHOW_DRILL.getDescription(),
                                        boardName.getText() + SendActionEnum.SHOW_DRILL.getFileName(), 600, Timeline.INDEFINITE);
                            }
                        });

                        SendData.getInstance().execute(console, SendActionEnum.SHOW_DRILL);
                        endJob(buttonSendDrillData, scene, ProgressBarEnum.DRILL_DATA);
                        timeLineWrapper.timeline.stop();
                    }
                };
                new Thread(runnable).start();
            }
        });
        return buttonSendDrillData;
    }

    private Button createButtonZProbe(final Scene scene) {
        final Button buttonZProbe = new Button("Z-Probe |");
        buttonZProbe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                startJob(buttonZProbe, scene);
                final TimeLineWrapper timeLineWrapperZprobe = new TimeLineWrapper();
                final TimeLineWrapper timeLineWrapperZprobeInterpolated = new TimeLineWrapper();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapperZprobe.timeline = createTab("zprobe", boardName.getText() + "_Z_probing.png", 600, Timeline.INDEFINITE);
                                timeLineWrapperZprobeInterpolated.timeline = createTab("zprobe interpolated", boardName.getText() + "_Z_probing_interpolated.png", 600, Timeline.INDEFINITE);

                            }
                        });

                        ZProbe.getInstance().execute(console);
                        endJob(buttonZProbe, scene, ProgressBarEnum.Z_PROBE);
                        timeLineWrapperZprobe.timeline.stop();
                        timeLineWrapperZprobeInterpolated.timeline.stop();
                    }
                };
                new Thread(runnable).start();
            }
        });
        return buttonZProbe;
    }

    private Button createButtonSendEdge(final Scene scene) {
        final Button buttonSendEdge = new Button("Send edge |");
        buttonSendEdge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                ProcessStepsWindow.getInstance().setup(console).showAndWait();

                startJob(buttonSendEdge, scene);
                final TimeLineWrapper timeLineWrapper = new TimeLineWrapper();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //drill data
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapper.timeline = createTab(SendActionEnum.SHOW_EDGE.getDescription(),
                                        boardName.getText() + SendActionEnum.SHOW_EDGE.getFileName(), 600, Timeline.INDEFINITE);
                            }
                        });

                        SendData.getInstance().execute(console, SendActionEnum.SHOW_EDGE);
                        endJob(buttonSendEdge, scene, ProgressBarEnum.EDGE_DATA);
                        timeLineWrapper.timeline.stop();
                    }
                };
                new Thread(runnable).start();
            }
        });
        return buttonSendEdge;
    }

    private Button createButtonSendEtchData(final Scene scene) {
        final Button buttonSendEtchData = new Button("Send etch data |");
        buttonSendEtchData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                ProcessStepsWindow.getInstance().setup(console).showAndWait();

                startJob(buttonSendEtchData, scene);
                final TimeLineWrapper timeLineWrapper = new TimeLineWrapper();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //etch data
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapper.timeline = createTab(SendActionEnum.SHOW_ETCH.getDescription(),
                                        boardName.getText() + SendActionEnum.SHOW_ETCH.getFileName(), 600, Timeline.INDEFINITE);
                            }
                        });

                        SendData.getInstance().execute(console, SendActionEnum.SHOW_ETCH);
                        progressBar.setProgress(ProgressBarEnum.ETCH_DATA.getProgress());
                        timeLineWrapper.timeline.stop();

                        //etch2 data
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapper.timeline = createTab(SendActionEnum.SHOW_ETCH_2.getDescription(),
                                        boardName.getText() + SendActionEnum.SHOW_ETCH_2.getFileName(), 600, Timeline.INDEFINITE);
                            }
                        });

                        SendData.getInstance().execute(console, SendActionEnum.SHOW_ETCH_2);
                        progressBar.setProgress(ProgressBarEnum.ETCH_DATA_2.getProgress());
                        timeLineWrapper.timeline.stop();

                        //etch 3 data
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timeLineWrapper.timeline = createTab(SendActionEnum.SHOW_ETCH_3.getDescription(),
                                        boardName.getText() + SendActionEnum.SHOW_ETCH_3.getFileName(), 600, Timeline.INDEFINITE);
                            }
                        });

                        SendData.getInstance().execute(console, SendActionEnum.SHOW_ETCH_3);
                        endJob(buttonSendEtchData, scene, ProgressBarEnum.ETCH_DATA_3);
                        timeLineWrapper.timeline.stop();
                    }
                };
                new Thread(runnable).start();
            }
        });
        return buttonSendEtchData;
    }

    private Button createButtonGerber2GCode(final Scene scene) {
        final Button buttonGerber2GCode = new Button("Gerber2GCode |");
        buttonGerber2GCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                startJob(buttonGerber2GCode, scene);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //setup profile
                        AppConfig appConfig = AppConfig.getInstance();
                        Properties gerber2GcodeProperties = Util.readProperties(appConfig.getGerber2GcodeConfigFile());
                        Properties profileProperties = Util.readProperties(appConfig.getLastProfileName());
                        Set keys = profileProperties.keySet();
                        for (Object key : keys) {
                            String value = (String) profileProperties.get(key);
                            gerber2GcodeProperties.setProperty((String) key, value);
                        }
                        Util.saveProperties(appConfig.getGerber2GcodeConfigFile(), gerber2GcodeProperties);

                        GCodeParserEnum selectedGCodeParser = (GCodeParserEnum) comboBoxGcodeParser.getValue();
                        selectedGCodeParser.getGenerateGCodeParser().execute(boardName.getText(), console);
//                        GenerateGCode.getInstance().execute(boardName.getText(), console);
                        //GenerateVisolateGCode.getInstance().execute(boardName.getText(), console);
                        endJob(buttonGerber2GCode, scene, ProgressBarEnum.GERBER_2_GCODE);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                createTab("gcode", boardName.getText() + "_viewGcode.png", 760, 1);
                            }
                        });

                    }
                };
                new Thread(runnable).start();
            }
        });
        return buttonGerber2GCode;
    }

    private Button createButtonProfileBrowse(final FileChooser fileChooser, final AppConfig appConfig, final Stage primaryStage) {
        final Button profileBrowse = new Button("...");
        profileBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                try {
                    fileChooser.getExtensionFilters().clear();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Profile", "*.properties"));
                    File initialDir = new File(appConfig.getProfileDir()).getCanonicalFile();
                    if (initialDir.exists()) {
                        fileChooser.setInitialDirectory(initialDir);
                    }

                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        String filePath = file.getPath();
                        String newProfile = filePath.replace(appConfig.getProfileDir(), "");
                        currentProfileNameLabel.setText(newProfile);
                        appConfig.setLastProfileName(newProfile);;
                        appConfig.saveProperties();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return profileBrowse;
    }

    private Button createButtonBoardNameBrowse(final FileChooser fileChooser, final AppConfig appConfig, final Stage primaryStage) {
        final Button boardNameBrowse = new Button("...");
        boardNameBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Board name", "*.drl"));
                fileChooser.setInitialDirectory(new File(appConfig.getGerberInputDir()));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    String filename = file.getName();
                    String board = Util.getOnlyFilename(filename);
                    boardName.setText(board);
                    appConfig.setLastBoardName(board);
                    appConfig.saveProperties();
                }
            }
        });
        return boardNameBrowse;
    }

    private Button createButtonZoomIn(final ImageView imageView) {
        final Button buttonZoomIn = new Button("Zoom +");
        buttonZoomIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double currentFitWidth = imageView.getFitWidth();
                        imageView.setFitWidth(currentFitWidth + 100);
                    }
                });
            }
        });
        return buttonZoomIn;
    }

    private Button createButtonZoomOut(final ImageView imageView) {
        final Button buttonZoomOut = new Button("Zoom -");
        buttonZoomOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double currentFitWidth = imageView.getFitWidth();
                        imageView.setFitWidth(currentFitWidth - 100);
                    }
                });
            }
        });
        return buttonZoomOut;
    }

    private void startJob(final Button button, final Scene scene) {
        console.clear();
        button.setDisable(true);
    }

    private void endJob(final Button button, final Scene scene, final ProgressBarEnum progressBarProgress) {
        progressBar.setProgress(progressBarProgress.getProgress());
        button.setDisable(false);
    }

    private Menu setupFavouritesMenu() {
        final Menu menuFavourites = new Menu("Favourites");
        Timeline pollFavourites = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the favourites and populate the screen
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Properties properties = Util.readProperties(AppConfig.getInstance().getEtchConfigFile());
                        Object favPath = properties.get("favouritesPath");
                        if (favPath != null) {
                            //only go on if the property exists
                            String favouritesPath = (String) favPath;
                            favouritesPath = favouritesPath.replace("\"", "");

                            File favouritesPathDir = new File(favouritesPath);
                            if (favouritesPathDir.exists()) {
                                //make sure the path exists
                                String currentFavList = "";
                                Collection<MenuItem> newMenuList = new ArrayList<>();

                                File[] fileList = favouritesPathDir.listFiles();
                                int length = fileList.length;
                                for (int i = 0; i < length; i++) {
                                    File file = fileList[i];
                                    if (file.isFile()) {
                                        //get each file in the path
                                        String onlyFileName = Util.getOnlyFilename(file.getName());
                                        currentFavList = currentFavList + onlyFileName;
                                        final MenuItem menuItem = new MenuItem(onlyFileName);
                                        menuItem.setOnAction(new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent actionEvent) {
                                                MenuItem selectedMenuItem = (MenuItem) actionEvent.getSource();
                                                final String menuItemText = selectedMenuItem.getText();
                                                console.clear();

                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ExecuteFavourite.getInstance().execute(console, menuItemText);


                                                    }
                                                };
                                                new Thread(runnable).start();
                                            }
                                        });

                                        newMenuList.add(menuItem);
                                    }
                                }

                                //check to see if the files have changed and only populate again if there was a change
                                if (!previousFavourites.equals(currentFavList)) {
                                    previousFavourites = currentFavList;
                                    menuFavourites.getItems().clear();
                                    menuFavourites.getItems().addAll(newMenuList);
                                }
                            }
                        }
                    }
                });
            }
        }));
        pollFavourites.setCycleCount(Timeline.INDEFINITE);
        pollFavourites.play();
        return menuFavourites;
    }

    private Menu setupProfilesMenu() {
        final Menu menuFavourites = new Menu("Profiles");
        Timeline pollFavourites = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //load the favourites and populate the screen
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Properties properties = Util.readProperties(AppConfig.getInstance().getEtchConfigFile());
                        Object favPath = properties.get("favouritesPath");
                        if (favPath != null) {
                            //only go on if the property exists
                            String favouritesPath = (String) favPath;
                            favouritesPath = favouritesPath.replace("\"", "");

                            File favouritesPathDir = new File(favouritesPath);
                            if (favouritesPathDir.exists()) {
                                //make sure the path exists
                                String currentFavList = "";
                                Collection<MenuItem> newMenuList = new ArrayList<>();

                                File[] fileList = favouritesPathDir.listFiles();
                                int length = fileList.length;
                                for (int i = 0; i < length; i++) {
                                    File file = fileList[i];
                                    if (file.isFile()) {
                                        //get each file in the path
                                        String onlyFileName = Util.getOnlyFilename(file.getName());
                                        currentFavList = currentFavList + onlyFileName;
                                        final MenuItem menuItem = new MenuItem(onlyFileName);
                                        menuItem.setOnAction(new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent actionEvent) {
                                                MenuItem selectedMenuItem = (MenuItem) actionEvent.getSource();
                                                final String menuItemText = selectedMenuItem.getText();
                                                console.clear();

                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ExecuteFavourite.getInstance().execute(console, menuItemText);


                                                    }
                                                };
                                                new Thread(runnable).start();
                                            }
                                        });

                                        newMenuList.add(menuItem);
                                    }
                                }

                                //check to see if the files have changed and only populate again if there was a change
                                if (!previousFavourites.equals(currentFavList)) {
                                    previousFavourites = currentFavList;
                                    menuFavourites.getItems().clear();
                                    menuFavourites.getItems().addAll(newMenuList);
                                }
                            }
                        }
                    }
                });
            }
        }));
        pollFavourites.setCycleCount(Timeline.INDEFINITE);
        pollFavourites.play();
        return menuFavourites;
    }

    private Menu setupToolsMenu() {
        Menu menuTools = new Menu("Tools");
        AppConfig appConfig = AppConfig.getInstance();
        String disableCommChecking = appConfig.getDisableCommChecking();
        if (disableCommChecking.equals("false")) {
            final Menu serialPort = new Menu("Serial Port");
            Timeline pollCommPorts = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //load the comm ports and populate the screen
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            SerialComm[] portList = SerialComm.getCommPorts();
                            Collection<MenuItem> newMenuList = new ArrayList<>();
                            String currentPortList = "";
                            for (SerialComm serialCommItem : portList) {
                                final MenuItem menuItem = new MenuItem(serialCommItem.getSystemPortName() + " - " + serialCommItem.getDescriptivePortName());
                                currentPortList = currentPortList + serialCommItem.getSystemPortName();
                                newMenuList.add(menuItem);
                            }

                            //check to see if the ports have changed and only populate again if there was a change
                            if (!previousPortList.equals(currentPortList)) {
                                previousPortList = currentPortList;
                                serialPort.getItems().clear();
                                serialPort.getItems().addAll(newMenuList);
                            }
                        }
                    });
                }
            }));
            pollCommPorts.setCycleCount(Timeline.INDEFINITE);
            pollCommPorts.play();
            menuTools.getItems().add(serialPort);
        }

        Menu menuConvert = new Menu("Convert");
        final ConvertersEnum[] converters = ConvertersEnum.values();
        final SimpleDataWrapper simpleDataWrapper = new SimpleDataWrapper();
        int length = converters.length;
        for (int i = 0; i < length; i++) {
            simpleDataWrapper.intValue = i;
            final MenuItem menuItemConvert = new MenuItem(converters[i].getDescription());
            menuItemConvert.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    ConverterWindow.getInstance().setup(converters[simpleDataWrapper.intValue]).show();
                }
            });

            menuConvert.getItems().add(menuItemConvert);
        }

        menuTools.getItems().add(menuConvert);
        return menuTools;
    }

    class TimeLineWrapper {

        Timeline timeline;
    }

    class SimpleDataWrapper {

        int intValue;
    }
}
