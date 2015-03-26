package main;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransitionBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

class MouseHoverAnimation implements EventHandler<MouseEvent> {
	static final Duration ANIMATION_DURATION = new Duration(500);
	static final double ANIMATION_DISTANCE = 0.15;
	private double cos;
	private double sin;
	private PieChart chart;

	public MouseHoverAnimation(PieChart.Data d, PieChart chart) {
		this.chart = chart;
		double start = 0;
		double angle = calcAngle(d);
		for (PieChart.Data tmp : chart.getData()) {
			if (tmp == d) {
				break;
			}
			start += calcAngle(tmp);
		}

		cos = Math.cos(Math.toRadians(0 - start - angle / 2));
		sin = Math.sin(Math.toRadians(0 - start - angle / 2));
	}

	@Override
	public void handle(MouseEvent arg0) {
		Node n = (Node) arg0.getSource();

		double minX = Double.MAX_VALUE;
		double maxX = Double.MAX_VALUE * -1;

		for (PieChart.Data d : chart.getData()) {
			minX = Math.min(minX, d.getNode().getBoundsInParent().getMinX());
			maxX = Math.max(maxX, d.getNode().getBoundsInParent().getMaxX());
		}

		double radius = maxX - minX;
		TranslateTransitionBuilder.create()
				.toX((radius * ANIMATION_DISTANCE) * cos)
				.toY((radius * ANIMATION_DISTANCE) * sin)
				.duration(ANIMATION_DURATION).node(n).build().play();
	}

	private static double calcAngle(PieChart.Data d) {
		double total = 0;
		for (PieChart.Data tmp : d.getChart().getData()) {
			total += tmp.getPieValue();
		}

		return 360 * (d.getPieValue() / total);
	}
}

class MouseExitAnimation implements EventHandler<MouseEvent> {
	@Override
	public void handle(MouseEvent event) {
		TranslateTransitionBuilder.create().toX(0).toY(0)
				.duration(new Duration(500)).node((Node) event.getSource())
				.build().play();
	}
}

public class control implements Initializable {
	
	

	ObservableList listviewadder1 = FXCollections.observableArrayList();
	ObservableList listviewadder2 = FXCollections.observableArrayList();
	ObservableList<PieChart.Data> pieChartData = FXCollections
			.observableArrayList();
	@FXML
	private PieChart pp;
	@FXML
	private TextField directory;
	@FXML
	private Button oo;
	@FXML
	private Button home;
	@FXML
	private Button back;
	@FXML
	private Label totalsize;
	@FXML
	private Label freespace;
	@FXML
	private Label diskspace;
	@FXML
	private Label usedspace;
	@FXML
	private ListView<String> mylistnames;
	@FXML
	private ListView<String> mylistfiles;
	@FXML
	private Button browse;
	DecimalFormat df = new DecimalFormat("#.0000");	
	static String sizesymbol;
	String[] names;
	long[] sizes;

	
	void scrollname(ScrollBar x)
	{
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pp.setLabelsVisible(true);
		pp.setLabelLineLength(3);
		pp.setLegendSide(Side.BOTTOM);
		pp.setLegendVisible(true);
		pp.setVisible(true);
		directory.setText("D:\\");	
		filelist(directory.getText());
		update();
		act();
	}

	public double getKBytes(long x) {
		return x / 1000.0;
	}

	public double getMBytes(long x) {
		return x / 1000000.0;
	}

	public double getGBytes(long x) {
		return x / 1000000000.0;
	}

	public double sizing(long x) {
		if (x >= 1000000000) {
			sizesymbol = "GB";
			return getGBytes(x);
		} else if (x >= 1000000) {
			sizesymbol = "MB";
			return getMBytes(x);
		} else if (x >= 1000) {
			sizesymbol = "KB";
			return getKBytes(x);
		} else {
			sizesymbol = "B";
			return x / 1.0;
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////
	public void filelist(String directory) {
		File currentDir = new File(directory);
		File[] files = currentDir.listFiles();

		names = new String[files.length];
		sizes = new long[files.length];

		for (int i = 0; i < files.length; i++) {
			sizes[i] = fileSize(files[i], null);
			if (files[i].isDirectory()) {
				names[i] = "(FOLDER) " + files[i].getName();
			} else
				names[i] = files[i].getName();
		}

	}

	public static long fileSize(File dir, TreeItem<File> parent) {
		long x = 0;
		TreeItem<File> root = new TreeItem<>(dir);
		root.setExpanded(true);
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					x += fileSize(file, root);
				} else {
					root.getChildren().add(new TreeItem<>(file));
					x += file.length();
				}
			}
			if (parent == null) {
			} else {
				parent.getChildren().add(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return dir.length();
		}
		return x;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	public void act() {
		pieChartData.clear();

		for (int y = 0; y < sizes.length; y++)
			pieChartData.add(new PieChart.Data(names[y], sizes[y]));

		pp.setData(pieChartData);

		// --------------------------------------------------------------------

		class MouseHoverAnimation implements EventHandler<MouseEvent> {

			final Duration ANIMATION_DURATION = new Duration(500);
			final double ANIMATION_DISTANCE = 0.15;
			private double xcos;
			private double ysin;
			private PieChart chart;

			public MouseHoverAnimation(PieChart.Data d, PieChart chart) {
				this.chart = chart;
				double start = 0;
				double angle = calcAngle(d);
				for (PieChart.Data tmp : chart.getData()) {
					if (tmp == d) {
						break;
					}
					start += calcAngle(tmp);
				}
				xcos = Math.cos(Math.toRadians(0 - angle - start) / 2);
				ysin = Math.sin(Math.toRadians(0 - angle - start) / 2);
			}

			@Override
			public void handle(MouseEvent arg0) {
				Node n = (Node) arg0.getSource();
				double minX = Double.MAX_VALUE;
				double maxX = Double.MAX_VALUE * -1;
				for (PieChart.Data d : chart.getData()) {
					minX = Math.min(minX, d.getNode().getBoundsInParent()
							.getMinX());
					maxX = Math.max(maxX, d.getNode().getBoundsInParent()
							.getMaxX());
				}

				double radius = (maxX - minX) * 0.15;
				TranslateTransitionBuilder
						.create()
						.toX((pp.getLayoutX() + radius * ANIMATION_DISTANCE)
								* ysin)
						.toY((pp.getLayoutY() + radius * ANIMATION_DISTANCE)
								* xcos).duration(ANIMATION_DURATION).node(n)
						.build().play();
			}

			private double calcAngle(PieChart.Data d) {
				double total = 0;
				for (PieChart.Data tmp : d.getChart().getData()) {
					total += tmp.getPieValue();
				}
				return 360 * (d.getPieValue() / total);
			}
		}
		class MouseExitAnimation implements EventHandler<MouseEvent> {
			@Override
			public void handle(MouseEvent event) {
				TranslateTransitionBuilder.create().toX(0).toY(0)
						.duration(new Duration(500))
						.node((Node) event.getSource()).build().play();
			}
		}

		// -----------------------------------------------------------------------------
		for (PieChart.Data d : pieChartData) {
			d.getNode().setOnMouseEntered(new MouseHoverAnimation(d, pp));
			d.getNode().setOnMouseExited(new MouseExitAnimation());

		}

	}

	public void update() {
		filelist(directory.getText());
		listviewadder1 = FXCollections.observableArrayList(names);
		mylistnames.setItems(listviewadder1);
		mylistnames.autosize();
		listviewadder2 = FXCollections.observableArrayList(sizes);
		long total=0;
		File cc=new File(directory.getText());
		for(int x=0;x<sizes.length;x++)
			total+=sizes[x];
		totalsize.setText("Directory Size: "+df.format(sizing(total))+ sizesymbol);
		freespace.setText("Free space Size:	"+df.format(sizing(cc.getUsableSpace()))+ sizesymbol);
		usedspace.setText("Used space Size:	"+df.format(sizing(cc.getTotalSpace()-cc.getUsableSpace()))+ sizesymbol);	
		diskspace.setText("Disk space Size:	"+df.format(sizing(cc.getTotalSpace()))+ sizesymbol);
		String[] newsizes = new String[sizes.length];
		totalsize.autosize();
		freespace.autosize();
		usedspace.autosize();
		diskspace.autosize();
		for (int i = 0; i < sizes.length; i++)
			newsizes[i] = Double
					.toString((double) Math.round(sizing(sizes[i])))
					+ " "
					+ sizesymbol
					+"		["
					+df.format(sizes[i]*100/(total*1.0))
					+'%'
					+']';
		listviewadder2 = FXCollections.observableArrayList(newsizes);
		mylistfiles.setItems(listviewadder2);
		mylistfiles.autosize();
	}

	public void openfile(ActionEvent event) {
		Stage arg0 = null;
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Browse directory");
		fileChooser.setInitialDirectory(new File("/home"));

		directory.setText(fileChooser.showDialog(arg0).getAbsolutePath());
		update();
		act();
	}

	public void goback() {
		directory.setText(directory.getText().substring(0,
				directory.getText().lastIndexOf('/')));
		update();
		act();
	}

	public void selectlist(MouseEvent click)
	{

		String temp = directory.getText()
				+ "/"
				+ mylistnames.getSelectionModel().getSelectedItem()
						.substring(9);

		if (click.getButton() == MouseButton.PRIMARY)
		{
			if(click.getClickCount() == 1 )
					mylistfiles.getSelectionModel().select(mylistnames.getSelectionModel().getSelectedIndex());
			else if(click.getClickCount() == 2)
					if (new File(temp).isDirectory()) 
				{
				directory.setText(temp);
				update();
				act();
				}
		}
		

	}				
	
	public void selectlist2(MouseEvent click)
	{

		if (click.getButton() == MouseButton.PRIMARY && click.getClickCount() == 1 )
				mylistnames.getSelectionModel().select(mylistfiles.getSelectionModel().getSelectedIndex());
			
		

	}
	public void refr() {
		filelist(directory.getText());
		update();
		act();

	}
	public void homefun(ActionEvent event)
	{
		directory.setText("/home");
		update();
		act();
	}

}